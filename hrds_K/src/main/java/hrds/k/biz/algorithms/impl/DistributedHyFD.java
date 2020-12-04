package hrds.k.biz.algorithms.impl;
/*
 * This is an optimized implementation,
 * modifications:
 * a) does not compute comparison suggestions,
 * b) Sampling phase: sample non-FDs from randomly partitioned data
 * c) switches between sampling and validation based on cost.
 * lmPDP
 */

import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.k.biz.algorithms.helper.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.util.OpenBitSet;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class DistributedHyFD implements Serializable {
	private static final MemoryGuardian memoryGuardian = new MemoryGuardian(true);

	private final static float efficiencyThreshold = 0.0001f;
	public static int maxLhsSize = -1;                // The lhss can become numAttributes - 1 large, but usually we are only interested in FDs with lhs < some threshold (otherwise they would not be useful for normalization, key discovery etc.)

	public static Dataset<Row> df = null;
	private static JavaRDD<ArrayList<Integer>> recodingDF = null;
	public static JavaSparkContext sc;
	public static String datasetFile = null;
	public static String outputFile = null;
	public static String[] columnNames = null;
	private static long numberTuples = 0;
	public static Integer numberAttributes = 0;
	public static int numPartitions = 55; // #of horizontal data partitions
	private static Map<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> cartesianPartition = null;
	private static int posInCartesianPartition = 0;
	public static int batchSize = 55;
	public static int validationBatchSize = 5000;
	private static FDSet negCover = null;
	private static FDTree posCover = null;
	private static HashMap<OpenBitSet, Integer> level0_unique = new HashMap<>();
	private static HashMap<OpenBitSet, Integer> level1_unique = new HashMap<>();
	private static int level = 0;
	private static int numNewNonFds = 0;
	private static long localComputationTime = 0;
	private static long dataShuffleTime = 0;
	private static Inductor inductor = null;

	public static void execute() throws IOException {
		long startTime = System.currentTimeMillis();
		executeHyFD();
		Logger.getInstance().writeln(" localComputationTime time(s): " + localComputationTime / 1000);
		Logger.getInstance().writeln(" dataShuffleTime time(s): " + dataShuffleTime / 1000);
		Logger.getInstance().writeln("Time: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
	}

	private static void executeHyFD() throws IOException {
		// Initialize
		long t1 = System.currentTimeMillis();
		Logger.getInstance().writeln("Loading data...");
		loadData();
		partitionData(numberAttributes);

		if (maxLhsSize < 0) maxLhsSize = numberAttributes - 1;
		//final int numRecords = new Long(numberTuples).intValue();

		// Broadcast the table
		int[][] table = new int[new Long(numberTuples).intValue()][numberAttributes];
		int currRow = 0;
//        List<Row> rowList = df.collectAsList();
		List<ArrayList<Integer>> rowList = recodingDF.collect();
		for (ArrayList<Integer> r : rowList) {
			for (int i = 0; i < numberAttributes; i++) {
				table[currRow][i] = new Long(r.get(i)).intValue();
			}
			currRow++;
		}
		final Broadcast<int[][]> b_table = sc.broadcast(table);
		long t2 = System.currentTimeMillis();
		dataShuffleTime += t2 - t1;

		negCover = new FDSet(numberAttributes, maxLhsSize);
		posCover = new FDTree(numberAttributes, maxLhsSize);
		posCover.addMostGeneralDependencies();
		inductor = new Inductor(negCover, posCover, memoryGuardian);
//        List<IntegerPair> comparisonSuggestions = new ArrayList<>();

		// Bootstrap with some non-FDs before starting the main algorithm
		for (int i = 0; i < 1; i++) {
			FDList newNonFds = enrichNegativeCover(b_table);
			long t3 = System.currentTimeMillis();
			inductor.updatePositiveCover(newNonFds);
			long t4 = System.currentTimeMillis();
			localComputationTime += t4 - t3;
		}

		while (!posCover.getLevel(level).isEmpty()/*!level1_unique.isEmpty()*/ && level <= numberAttributes) {
			validatePositiveCover(b_table);

			//FDList newNonFds = enrichNegativeCover(b_table);
			//Logger.getInstance().writeln(" #### of non FDs: " + newNonFds.size());

			/*// DEBUG
			Logger.getInstance().writeln(" Print NegCover: ");
			for(ObjectOpenHashSet<OpenBitSet> hs : negCover.getFdLevels()) {
				for(OpenBitSet b : hs) {
					for(int i = 0; i < posCover.getNumAttributes(); i++)
						if(b.get(i))
							System.out.print(i+" ");
					Logger.getInstance().writeln();
				}
			}*/

			//inductor.updatePositiveCover(newNonFds);
			//Logger.getInstance().writeln(" #### of FDs: " + posCover.writeFunctionalDependencies(new OpenBitSet(), buildColumnIdentifiers(), false));

		}
		negCover = null;

		// Output all valid FDs
		Logger.getInstance().writeln("Translating FD-tree into result format ...");

		FileSystem fs = FileSystem.get(URI.create(outputFile + "/part-00000"), ConfigReader.getConfiguration());
		OutputStreamWriter hdfsOutStream = new OutputStreamWriter(fs.create(new Path(outputFile + "/part-00000")), StandardCharsets.UTF_8);

		int numFDs = posCover.writeFunctionalDependencies(hdfsOutStream, new OpenBitSet(), buildColumnIdentifiers(), false);
		hdfsOutStream.close();
		Logger.getInstance().writeln("... done! (" + numFDs + " FDs)");
	}

	private static ObjectArrayList<ColumnIdentifier> buildColumnIdentifiers() {
		ObjectArrayList<ColumnIdentifier> columnIdentifiers = new ObjectArrayList<>(columnNames.length);
		for (String attributeName : columnNames)
			columnIdentifiers.add(new ColumnIdentifier(datasetFile, attributeName));
		return columnIdentifiers;
	}

	private static void loadData() {
		df.cache();
		numberTuples = df.count();
	}


	/**
	 * Random partitioning of data.
	 */
	private static void partitionData(Integer numberAttributes) {

		JavaRDD<Row> tableRDD = df.javaRDD();
		JavaRDD<ArrayList<String>> dataSet = tableRDD.map(row -> {
			ArrayList<String> strings = new ArrayList<>(row.size());
			for (int i = 0; i < row.size(); i++) {
				strings.add(String.valueOf(row.get(i)));
			}
			return strings;
		});
		ArrayList<HashMap<String, Integer>> cardinalityOfAttribute = dataSet.mapPartitions(iter -> {
			ArrayList<HashMap<String, Integer>> attributes = new ArrayList<>(numberAttributes);
			for (int i = 0; i < numberAttributes; i++) {
				attributes.add(new HashMap<>());
			}
			while (iter.hasNext()) {
				ArrayList<String> next = iter.next();
				if (next.size() != numberAttributes) throw new RuntimeException(
						"numberAttributes:" + numberAttributes + "," +
								" rowSize:" + next.size() + "," + " wrong data: " + next.toString());
				int i = 0;
				while (i < numberAttributes) {
					if (attributes.get(i).containsKey(next.get(i))) {
						attributes.get(i).put(next.get(i), attributes.get(i).get(next.get(i)) + 1);
					} else {
						attributes.get(i).put(next.get(i), 1);
					}
					i += 1;
				}
			}
			ArrayList<ArrayList<HashMap<String, Integer>>> tmp = new ArrayList<>();
			tmp.add(attributes);

			return tmp.iterator();
		}).reduce((x, y) -> {
			for (int i = 0; i < numberAttributes; i++) {
				for (Entry<String, Integer> elem : y.get(i).entrySet()) {
					String k = elem.getKey();
					Integer v = elem.getValue();
					if (x.get(i).containsKey(k))
						x.get(i).put(k, x.get(i).get(k) + v);
					else
						x.get(i).put(k, v);
				}
			}
			return x;
		});

		ArrayList<HashMap<String, Integer>> broadcast = new ArrayList<>();
		for (HashMap<String, Integer> f : cardinalityOfAttribute) {
			HashMap<String, Integer> map = new HashMap<>();
			int i = 0;
			for (String k : f.keySet()) {
				map.put(k, i);
				i += 1;
			}
			broadcast.add(map);
		}

		recodingDF = dataSet.mapPartitions(iter -> {
			ArrayList<ArrayList<Integer>> rows = new ArrayList<>();
			while (iter.hasNext()) {
				ArrayList<String> f = iter.next();
				ArrayList<Integer> n = new ArrayList<>(f.size());
				for (int i = 0; i < numberAttributes; i++) {
					n.add(broadcast.get(i).get(f.get(i)));
				}
				rows.add(n);
			}
			return rows.iterator();
		});
		recodingDF.cache();
		df.unpersist();

		final Broadcast<Integer> b_numPartitions = sc.broadcast(numPartitions);

		JavaPairRDD<Integer, Integer> rowMap = recodingDF.mapToPair(row -> new Tuple2<>((int) (Math.random() * b_numPartitions.value()), new Long(row.get(row.size() - 1)).intValue()));

//        JavaPairRDD<Integer, Integer> rowMap = tableRDD.mapToPair(
//                new PairFunction<Row, Integer, Integer>() {
//                    public Tuple2<Integer, Integer> call(Row r) {
//                        return new Tuple2<Integer, Integer>((int) (Math.random() * b_numPartitions.value()), new Long(r.getLong(r.size() - 1)).intValue());
//                    }
//                });
		JavaPairRDD<Integer, Iterable<Integer>> pairsPartition = rowMap.groupByKey();
		JavaPairRDD<Tuple2<Integer, Iterable<Integer>>, Tuple2<Integer, Iterable<Integer>>> joinedPairs = pairsPartition.cartesian(pairsPartition);
		joinedPairs = joinedPairs.filter((Function<Tuple2<Tuple2<Integer, Iterable<Integer>>, Tuple2<Integer, Iterable<Integer>>>, Boolean>) t -> t._1._1 <= t._2._1);
		// (1: {{1,2,3},{4,5,6}}; 2: {{1,2,3},{7,8,9}})
		cartesianPartition = joinedPairs.mapToPair((PairFunction<Tuple2<Tuple2<Integer, Iterable<Integer>>, Tuple2<Integer, Iterable<Integer>>>, Iterable<Integer>, Iterable<Integer>>) t -> new Tuple2<>(t._1._2, t._2._2)).zipWithIndex().mapToPair((PairFunction<Tuple2<Tuple2<Iterable<Integer>, Iterable<Integer>>, Long>, Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>>) t -> new Tuple2<>(t._2.intValue(), t._1)).collectAsMap();
    	
    	/*// DEBUG
    	Map<Integer, Tuple2<Iterable<Row>, Iterable<Row>>> tmp = cartesianPartition.collectAsMap();
    	for(Integer key: tmp.keySet()) {
    		Logger.getInstance().writeln(" ***** "+ key);
    		Logger.getInstance().writeln("LEFT:");
    		for(Row r : tmp.get(key)._1)
    			Logger.getInstance().writeln(r);
    		Logger.getInstance().writeln("RIGHT:");
    		for(Row r : tmp.get(key)._2)
    			Logger.getInstance().writeln(r);
    	}*/
	}

	private static FDList enrichNegativeCover(final Broadcast<int[][]> b_table) {
		Logger.getInstance().writeln("Enriching Negative Cover ... ");
		FDList newNonFds = new FDList(numberAttributes, negCover.getMaxDepth());
		runNext(newNonFds, negCover, batchSize, b_table);
		return newNonFds;
	}

	private static void runNext(FDList newNonFds, FDSet negCover, int batchSize, final Broadcast<int[][]> b_table) {
		int previousNegCoverSize = newNonFds.size();

		long numComparisons;

		final Broadcast<Integer> b_numberAttributes = sc.broadcast(numberAttributes);
		List<Tuple2<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>>> currentMap = new ArrayList<>();

		for (int i = posInCartesianPartition; i < posInCartesianPartition + batchSize; i++) {
			if (i >= cartesianPartition.size())
				break;
			currentMap.add(new Tuple2<>(i, cartesianPartition.get(i)));
		}
		JavaPairRDD<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> currentJobRDD = sc.parallelizePairs(currentMap, numPartitions);

		// DEBUG
    	/*Map<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> tmp = currentJobRDD.collectAsMap();
    	for(Integer key: tmp.keySet()) {
    		Logger.getInstance().writeln(" ***** "+ key);
    		Logger.getInstance().writeln("LEFT:");
    		for(Integer r : tmp.get(key)._1)
    			Logger.getInstance().writeln(r);
    		Logger.getInstance().writeln("RIGHT:");
    		for(Integer r : tmp.get(key)._2)
    			Logger.getInstance().writeln(r);
    	}*/

		JavaRDD<HashSet<OpenBitSet>> nonFDsHashetRDD = currentJobRDD.map(
				(Function<Tuple2<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>>, HashSet<OpenBitSet>>) t -> {
//                        long t1 = System.currentTimeMillis();
					HashSet<OpenBitSet> result_l = new HashSet<>();
					//FDTree negCoverTree = new FDTree(b_numberAttributes.value());
					Iterable<Integer> l1 = t._2._1;
					Iterable<Integer> l2 = t._2._2;
					for (int i1 : l1) {
						int[] r1 = b_table.value()[i1];
						for (int i2 : l2) {
							int[] r2 = b_table.value()[i2];
							OpenBitSet equalAttrs = new OpenBitSet(b_numberAttributes.value());
							for (int i = 0; i < b_numberAttributes.value(); i++) {
								long val1 = r1[i];
								long val2 = r2[i];
								if (val2 >= 0 && val1 == val2) {
									equalAttrs.set(i);
								}
							}
							result_l.add(equalAttrs);
						}
					}
//                        long t2 = System.currentTimeMillis();
//                        Logger.getInstance().writeln(t2 - t1);
					return result_l;
				});

		JavaRDD<OpenBitSet> nonFDsRDD = nonFDsHashetRDD.flatMap((FlatMapFunction<HashSet<OpenBitSet>, OpenBitSet>) HashSet::iterator).distinct();

		// DEBUG
		/*Logger.getInstance().writeln(" Print NegCover: ");
		List<OpenBitSet> l = nonFDsRDD.collect();
		for(OpenBitSet b : l) {
			for(int i = 0; i < numberAttributes; i++)
				if(b.get(i))
					System.out.print(i+" ");
			Logger.getInstance().writeln();
		}*/
		long t1 = System.currentTimeMillis();
		for (OpenBitSet nonFD : nonFDsRDD.collect()) {
			if (!negCover.contains(nonFD)) {
				//Logger.getInstance().writeln(" Add the above to negCover");
				OpenBitSet equalAttrsCopy = nonFD.clone();
				negCover.add(equalAttrsCopy);
				newNonFds.add(equalAttrsCopy);
			}
		}

		posInCartesianPartition = posInCartesianPartition + batchSize;

		int partitionSize = (int) (numberTuples / numPartitions);
		numComparisons = (long) partitionSize * partitionSize * batchSize;
		numNewNonFds = newNonFds.size() - previousNegCoverSize;
		float efficiency = (float) numNewNonFds / numComparisons;
		Logger.getInstance().writeln("new NonFDs: " + numNewNonFds + " # comparisons: " + numComparisons + " efficiency: " + efficiency + " efficiency threshold: " + efficiencyThreshold);
		long t2 = System.currentTimeMillis();
		localComputationTime += t2 - t1;
	}

	private static void validatePositiveCover(final Broadcast<int[][]> b_table) {
		int numAttributes = numberAttributes;
		long t1 = System.currentTimeMillis();

		List<FDTreeElementLhsPair> currentLevel;
		if (level == 0) {
			currentLevel = new ArrayList<>();
			currentLevel.add(new FDTreeElementLhsPair(posCover, new OpenBitSet(numAttributes)));
		} else {
			currentLevel = posCover.getLevel(level);
		}

		// Start the level-wise validation/discovery
//        int previousNumInvalidFds = 0;
		Logger.getInstance().write("\tLevel " + level + ": " + currentLevel.size() + " elements; ");

		// Validate current level
		Logger.getInstance().write("(V)");

		//ValidationResult validationResult = (this.executor == null) ? this.validateSequential(currentLevel, compressedRecords) : this.validateParallel(currentLevel, compressedRecords);
		// The cost check of Validation vs Sampling happens inside validateSpark
		//long t1 = System.currentTimeMillis();
		long t2 = System.currentTimeMillis();
		localComputationTime += t2 - t1;
		ValidationResult validationResult = validateSpark(currentLevel, b_table);

		if (validationResult == null)
			return;
		long t3 = System.currentTimeMillis();
		// If the next level exceeds the predefined maximum lhs size, then we can stop here
		/*if ((posCover.getMaxDepth() > -1) && (level >= posCover.getMaxDepth())) {
			int numInvalidFds = validationResult.invalidFDs.size();
			int numValidFds = validationResult.validations - numInvalidFds;
			Logger.getInstance().writeln("(-)(-); " + validationResult.intersections + " intersections; " + validationResult.validations + " validations; " + numInvalidFds + " invalid; " + "-" + " new candidates; --> " + numValidFds + " FDs");
			return;
		}*/

		// Add all children to the next level
		Logger.getInstance().write("(C)");

//		List<FDTreeElementLhsPair> nextLevel = new ArrayList<>();
		/*for (FDTreeElementLhsPair elementLhsPair : currentLevel) {
			FDTreeElement element = elementLhsPair.getElement();
			OpenBitSet lhs = elementLhsPair.getLhs();

			if (element.getChildren() == null)
				continue;
			
			for (int childAttr = 0; childAttr < numAttributes; childAttr++) {
				FDTreeElement child = element.getChildren()[childAttr];
				
				if (child != null) {
					OpenBitSet childLhs = lhs.clone();
					childLhs.set(childAttr);
					nextLevel.add(new FDTreeElementLhsPair(child, childLhs));
				}
			}
		}*/

		// Generate new FDs from the invalid FDs and add them to the next level as well
		Logger.getInstance().write("(G); ");

		int candidates = 0;
		for (FD invalidFD : validationResult.invalidFDs) {
			for (int extensionAttr = 0; extensionAttr < numAttributes; extensionAttr++) {
				OpenBitSet childLhs = extendWith(invalidFD.lhs, invalidFD.rhs, extensionAttr);
				if (childLhs != null) {
					FDTreeElement child = posCover.addFunctionalDependencyGetIfNew(childLhs, invalidFD.rhs);
					if (child != null) {
//						nextLevel.add(new FDTreeElementLhsPair(child, childLhs));
						candidates++;
					}
				}
			}
			
			/*if ((posCover.getMaxDepth() > -1) && (level >= posCover.getMaxDepth()))
				break;*/
		}

//        currentLevel = nextLevel;
		level0_unique = level1_unique;
		level1_unique = new HashMap<>();
		level++;
		int numInvalidFds = validationResult.invalidFDs.size();
		int numValidFds = validationResult.validations - numInvalidFds;
		Logger.getInstance().writeln(validationResult.intersections + " intersections; " + validationResult.validations + " validations; " + numInvalidFds + " invalid; " + candidates + " new candidates; --> " + numValidFds + " FDs");
//        previousNumInvalidFds = numInvalidFds;
		long t4 = System.currentTimeMillis();
		localComputationTime += t4 - t3;
	}

	private static ValidationResult validateSpark(List<FDTreeElementLhsPair> currentLevel, Broadcast<int[][]> b_table) {
		long t1 = System.currentTimeMillis();
		ValidationResult result = new ValidationResult();

		HashSet<OpenBitSet> uniquesToCompute = new HashSet<>();
//        ArrayList<int[]> combination_arr = new ArrayList<int[]>();
		int full = 0; // checks how many entries added to combination_arr
		int l = 0;

//        int l0_count = 0;
//        int l1_count = 0;
		// Find out what uniques to compute
		for (FDTreeElementLhsPair elementLhsPair : currentLevel) {
			FDTreeElement element = elementLhsPair.getElement();
			OpenBitSet lhs = elementLhsPair.getLhs();
			OpenBitSet rhs = element.getFds();
			if (!level0_unique.containsKey(lhs)) {
				uniquesToCompute.add(lhs);
//                l0_count++;
			}

			for (int rhsAttr = rhs.nextSetBit(0); rhsAttr >= 0; rhsAttr = rhs.nextSetBit(rhsAttr + 1)) {
				OpenBitSet lhsRhs = lhs.clone();
				lhsRhs.set(rhsAttr);
				uniquesToCompute.add(lhsRhs);
//                l1_count++;
			}
		}
		long t2 = System.currentTimeMillis();
		localComputationTime += t2 - t1;
		// Continue only if validation phase is cheaper, else return
		Logger.getInstance().writeln("\n Uniques to compute: " + uniquesToCompute.size() + " Sampling load: " + numberTuples / numPartitions);
		if (uniquesToCompute.size() > numberTuples / numPartitions) {
			FDList newNonFds = enrichNegativeCover(b_table);

			// Check if newNonFDs were zero then going to sampling phase use useless, go ahead with validation
			if (numNewNonFds != 0) {
				long t3 = System.currentTimeMillis();
				inductor.updatePositiveCover(newNonFds);
				long t4 = System.currentTimeMillis();
				localComputationTime += t4 - t3;
				return null;
			}
		}

		//Logger.getInstance().writeln(" Total uniques to compute l0 - l1: "+l0_count+" "+l1_count);
		// compute uniques in batches via spark
		LinkedList<OpenBitSet> batch = new LinkedList<>();
		for (OpenBitSet bs : uniquesToCompute) {
			//if(!level0_unique.containsKey(bs)) {
//            spComputation++;
			batch.add(bs);
			full++;
			//}

			if (full == validationBatchSize || l == uniquesToCompute.size() - 1) { // then process the batch
				Logger.getInstance().writeln("Running Spark job for batch size: " + batch.size());
				JavaRDD<OpenBitSet> combinationsRDD = sc.parallelize(batch);
				Map<String, Integer> map = generateStrippedPartitions(combinationsRDD, b_table);
				Iterator<Entry<String, Integer>> entry_itr = map.entrySet().iterator();
				long t3 = System.currentTimeMillis();
				while (entry_itr.hasNext()) {
					Entry<String, Integer> e = entry_itr.next();
					//Logger.getInstance().writeln(e.getKey() + " count: " + e.getValue());
					OpenBitSet combination = stringToBitset(e.getKey());
					if (combination.cardinality() == level + 1) {
						level1_unique.put(combination, e.getValue());
					} else if (combination.cardinality() == level) {
						level0_unique.put(combination, e.getValue());
					}
				}
				full = 0;
//                combination_arr = new ArrayList<int[]>();
				batch = new LinkedList<>();
				long t4 = System.currentTimeMillis();
				localComputationTime += t4 - t3;
			}
			l++;
		}

		/* Now we have all unique counts required to calculate FDs at this level (=key)
		 * Begin validating FDs, if something is not a FD add its specifications to the candidates
		 * After validation for this level then, assign level1_unique to level0_unique,
		 * and clear level1_unique for next iteration.
		 */
		long t5 = System.currentTimeMillis();
		for (FDTreeElementLhsPair elementLhsPair : currentLevel) {
			ValidationResult localResult = new ValidationResult();

			FDTreeElement element = elementLhsPair.getElement();
			OpenBitSet lhs = elementLhsPair.getLhs();
			OpenBitSet rhs = element.getFds();
			OpenBitSet validRhs = new OpenBitSet(numberAttributes);

			int rhsSize = (int) rhs.cardinality();
			localResult.validations = localResult.validations + rhsSize;

			for (int rhsAttr = rhs.nextSetBit(0); rhsAttr >= 0; rhsAttr = rhs.nextSetBit(rhsAttr + 1)) {
				OpenBitSet lhsNrhs = lhs.clone();
				lhsNrhs.set(rhsAttr);

				//Logger.getInstance().writeln(level0_unique.get(lhs) +" "+ level1_unique.get(lhsNrhs));
				if (/*level0_unique.containsKey(lhs) && level1_unique.containsKey(lhsNrhs) &&*/
						level0_unique.get(lhs).intValue() == level1_unique.get(lhsNrhs).intValue()) {
					validRhs.set(rhsAttr);
				} else {
					localResult.invalidFDs.add(new FD(lhs, rhsAttr));
				}
			}

			element.setFds(validRhs); // Sets the valid FDs in the FD tree
			localResult.intersections++;

			result.add(localResult);
		}
		long t6 = System.currentTimeMillis();
		localComputationTime += t6 - t5;
		return result;
	}

	private static Map<String, Integer> generateStrippedPartitions(JavaRDD<OpenBitSet> combinationsRDD, final Broadcast<int[][]> b_table) {
		final Broadcast<Integer> b_numberAttributes = sc.broadcast(numberAttributes);
		JavaPairRDD<String, Integer> attrSpRDD2 = combinationsRDD.mapToPair((PairFunction<OpenBitSet, String, Integer>) b -> {
//            long t1 = System.currentTimeMillis();
			HashSet<ArrayList<Integer>> hashSet = new HashSet<>();
			StringBuilder combination = new StringBuilder();
			int[][] table = b_table.value();
			for (int i = 0; i < b_numberAttributes.value(); i++) {
				if (b.get(i))
					combination.append("_").append(i);
			}
			for (int[] row : table) {
				ArrayList<Integer> value = new ArrayList<>();
				for (int j = 0; j < b_numberAttributes.value(); j++) {
					if (b.get(j))
						value.add(row[j]);
				}
				hashSet.add(value);
			}
//            long t2 = System.currentTimeMillis();
//            Logger.getInstance().writeln(t2 - t1);
			return new Tuple2<>(combination.toString(), hashSet.size());
		});
		return attrSpRDD2.collectAsMap();
	}

	private static OpenBitSet extendWith(OpenBitSet lhs, int rhs, int extensionAttr) {
		if (lhs.get(extensionAttr) ||                                            // Triviality: AA->C cannot be valid, because A->C is invalid
				(rhs == extensionAttr) ||                                            // Triviality: AC->C cannot be valid, because A->C is invalid
				posCover.containsFdOrGeneralization(lhs, extensionAttr) ||        // Pruning: If A->B, then AB->C cannot be minimal // TODO: this pruning is not used in the Inductor when inverting the negCover; so either it is useless here or it is useful in the Inductor?
				((posCover.getChildren() != null) && (posCover.getChildren()[extensionAttr] != null) && posCover.getChildren()[extensionAttr].isFd(rhs)))
			// Pruning: If B->C, then AB->C cannot be minimal
			return null;

		OpenBitSet childLhs = lhs.clone(); // TODO: This clone() could be avoided when done externally
		childLhs.set(extensionAttr);

		// TODO: Add more pruning here

		// if contains FD: element was a child before and has already been added to the next level
		// if contains Generalization: element cannot be minimal, because generalizations have already been validated
		if (posCover.containsFdOrGeneralization(childLhs, rhs))  // Pruning: If A->C, then AB->C cannot be minimal
			return null;

		return childLhs;
	}

	private static OpenBitSet stringToBitset(String str) {
		OpenBitSet bs = new OpenBitSet(numberAttributes);
		String[] splitArr = str.split("_");
		for (String aSplitArr : splitArr) {
			if (!aSplitArr.equals("")) {
				int pos = Integer.parseInt(aSplitArr);
				bs.set(pos);
			}
		}
		return bs;
	}

}
