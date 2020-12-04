package hrds.k.biz.algorithms.helper;


import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;


/**
 * Represents a unique column combination.
 *
 * @author Jakob Zwiener
 */
@JsonTypeName("UniqueColumnCombination")
public class UniqueColumnCombination {

  private static final long serialVersionUID = -8782723135088616653L;

  protected ColumnCombination columnCombination;

  protected UniqueColumnCombination() {
    this.columnCombination = new ColumnCombination();
  }

  /**
   * Constructs a {@link UniqueColumnCombination} from a number of {@link ColumnIdentifier}s.
   *
   * @param columnIdentifier the column identifier comprising the column combination
   */
  public UniqueColumnCombination(ColumnIdentifier... columnIdentifier) {
    this.columnCombination = new ColumnCombination(columnIdentifier);
  }

  /**
   * Constructs a {@link UniqueColumnCombination} from a {@link ColumnCombination}.
   *
   * @param columnCombination a supposedly unique column combination
   */
  public UniqueColumnCombination(ColumnCombination columnCombination) {
    this.columnCombination = columnCombination;
  }

  /**
   * @return the column combination
   */
  public ColumnCombination getColumnCombination() {
    return columnCombination;
  }

  public void setColumnCombination(ColumnCombination columnCombination) {
    this.columnCombination = columnCombination;
  }

  /*@Override
  @XmlTransient
  public void sendResultTo(OmniscientResultReceiver resultReceiver)
    throws CouldNotReceiveResultException, ColumnNameMismatchException {
    resultReceiver.receiveResult(this);
  }*/

  @Override
  public String toString() {
    return columnCombination.toString();
  }

  /**
   * Encodes the unique column combination as string with the given mappings.
   * @param tableMapping the table mapping
   * @param columnMapping the column mapping
   * @return the string
   */
  public String toString(Map<String, String> tableMapping, Map<String, String> columnMapping) {
    return this.columnCombination.toString(tableMapping, columnMapping);
  }

  /**
   * Creates a unique column combination from the given string using the given mapping.
   * @param tableMapping the table mapping
   * @param columnMapping the column mapping
   * @param str the string
   * @return a unique column combination
   */
  public static UniqueColumnCombination fromString(Map<String, String> tableMapping, Map<String, String> columnMapping, String str)
    throws NullPointerException, IndexOutOfBoundsException {
    return new UniqueColumnCombination(ColumnCombination.fromString(tableMapping, columnMapping, str));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime
      * result
      + ((columnCombination == null) ? 0 : columnCombination
      .hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UniqueColumnCombination other = (UniqueColumnCombination) obj;
    if (columnCombination == null) {
      if (other.columnCombination != null) {
        return false;
      }
    } else if (!columnCombination.equals(other.columnCombination)) {
      return false;
    }
    return true;
  }

}
