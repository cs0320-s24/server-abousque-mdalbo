package edu.brown.cs.student.main.parsing.examplecreatorfromrows;

import edu.brown.cs.student.main.parsing.CreatorFromRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A class for translating a String representation of a CSV row into a more useful list of Strings.
 */
public class StringListRowConverter implements CreatorFromRow<List<String>> {

  /* The regex useful for splitting a String of comma separated values into a List<String> */
  static final Pattern REGEX_SPLIT_CSV_ROW =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * Generates a list-based representation of an inputted row. Example: ["sat, fri, tues"] =>
   * ["sat", "fri", "tues"]
   *
   * @param row a row of CSV data to split
   * @return representation of the row as a List of Strings, with comma-separated ones isolated
   */
  @Override
  public List<String> create(List<String> row) {
    List<String> formatted = new ArrayList<>();
    for (String r : row) {
      List<String> split = Arrays.asList(REGEX_SPLIT_CSV_ROW.split(r));
      split.replaceAll(StringListRowConverter::postprocess);
      formatted.addAll(split);
    }
    return formatted;
  }

  /**
   * Eliminate a single instance of leading or trailing double-quote, and replace pairs of double
   * quotes with singles. Adapted from the livecode example at: <a
   * href="https://github.com/cs0320/class-livecode/blob/main/old/F23/vignettes/csvRegex/src/test/java/TestRegex.java">...</a>.
   *
   * @param arg the string to process
   * @return the post-processed string
   */
  public static String postprocess(String arg) {
    return arg
        // Remove extra spaces at beginning and end of the line
        .trim()
        // Remove a beginning quote, if present
        .replaceAll("^\"", "")
        // Remove an ending quote, if present
        .replaceAll("\"$", "")
        // Replace double-double-quotes with double-quotes
        .replaceAll("\"\"", "\"")

        // ADDED: Second removal of surrounding spaces for additional case coverage
        .trim();
  }
}
