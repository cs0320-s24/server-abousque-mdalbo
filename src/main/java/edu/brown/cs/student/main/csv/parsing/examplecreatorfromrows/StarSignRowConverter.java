package edu.brown.cs.student.main.csv.parsing.examplecreatorfromrows;

import edu.brown.cs.student.main.csv.parsing.CreatorFromRow;
import edu.brown.cs.student.main.csv.parsing.FactoryFailureException;
import java.util.List;

/** A class for converting a CSV row into a StarSign instance. */
public class StarSignRowConverter implements CreatorFromRow<StarSign> {

  private final StringListRowConverter stringConverter = new StringListRowConverter();

  /**
   * Generates a StarSign representation of a row.
   *
   * @param row of raw CSV data in the form of a List of Strings
   * @return the StarSign version of row
   * @throws FactoryFailureException if the given row cannot be converted into a StarSign
   */
  @Override
  public StarSign create(List<String> row) throws FactoryFailureException {
    List<String> splitRow = stringConverter.create(row);
    if (splitRow.size() != 2) {
      throw new FactoryFailureException(
          "StarSignRowConverter: " + "Incorrect number of args provided for StarSign.", row);
    }

    StarSign converted;
    try {
      converted = new StarSign(splitRow.get(1), splitRow.get(0));
    } catch (IllegalArgumentException exn) {
      throw new FactoryFailureException("StarSignRowConverter: " + exn, row);
    }
    return converted;
  }
}
