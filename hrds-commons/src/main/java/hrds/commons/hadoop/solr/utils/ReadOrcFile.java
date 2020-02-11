package hrds.commons.hadoop.solr.utils;

import fd.ng.core.annotation.DocClass;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcInputFormat;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

@DocClass(desc = "读取orc文件类", author = "BY-HLL", createdate = "2020/1/14 0014 下午 03:21")
public class ReadOrcFile {

    @SuppressWarnings({"rawtypes"})
    public static RecordReader readFileData(Configuration config, Path filePath) {

        RecordReader reader = null;
        try {
            JobConf conf = new JobConf(config);
            InputFormat in = new OrcInputFormat();
            FileInputFormat.setInputPaths(conf, filePath);
            InputSplit[] splits = in.getSplits(conf, 1);
            reader = in.getRecordReader(splits[0], conf, Reporter.NULL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }
}
