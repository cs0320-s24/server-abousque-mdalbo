package edu.brown.cs.student.main.server.csv.searching;

import edu.brown.cs.student.main.server.csv.parsing.CsvParser;
import edu.brown.cs.student.main.server.csv.parsing.FactoryFailureException;
import edu.brown.cs.student.main.server.csv.parsing.examplecreatorfromrows.StringListRowConverter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** A class to handle searching through the contents of a CSV file. */
public class Searcher {
  private boolean initialized;
  private HashMap<String, Integer> columnHeaderToIndex = null;
  private List<List<String>> fullCsvContents;
  private List<List<String>> csvContents;
  private int numCols;

  /**
   * Primary constructor for the Searcher class. Parses user specified CSV file for future
   * searching.
   *
   * @param pathFromData the path from ./data/ to the CSV file of interest
   * @param headersIncluded true if the first row of the CSV at ./data/pathFromData is a header,
   *     false otherwise
   * @throws IllegalArgumentException if passed an invalid filepath or headersIncluded but csv is
   *     empty, or if CSV contains rows of inconsistent lengths
   * @throws IOException if a line is encountered in the CSV than cannot be read
   * @throws FactoryFailureException if a CSV row is unable to be converted into a List of Strings
   */
  public Searcher(String pathFromData, boolean headersIncluded)
      throws IllegalArgumentException, IOException, FactoryFailureException {
    initialize(pathFromData, headersIncluded);
  }

  /**
   * Alternate constructor for the Searcher class.
   *
   * <p>initialize method MUST be called before calling any of this Searcher's other methods.
   */
  public Searcher() {
    this.initialized = false;
  }

  /**
   * Indicates whether this Searcher has been initialized (given a CSV file to work with) yet.
   *
   * @return true if this has been initialized, false otherwise
   */
  public boolean isInitialized() {
    return this.initialized;
  }

  /**
   * Initializes (or re-initializes) this Searcher to the contents of a specific CSV file.
   *
   * <p>Also sets this Searcher's "initialized" field to true.
   *
   * @param pathFromData the path from ./data/ to the CSV file of interest
   * @param headersIncluded true if the first row of the CSV at ./data/pathFromData is a header,
   *     false otherwise
   * @throws IllegalArgumentException if passed an invalid filepath or headersIncluded but csv is
   *     empty, or if CSV contains rows of inconsistent lengths
   * @throws IOException if a line is encountered in the CSV than cannot be read
   * @throws FactoryFailureException if a CSV row is unable to be converted into a List of Strings
   */
  public void initialize(String pathFromData, boolean headersIncluded)
      throws IllegalArgumentException, IOException, FactoryFailureException {
    CsvParser<List<String>> csvParser = generateCsvParser(pathFromData);
    this.fullCsvContents = csvParser.parse();
    if (this.fullCsvContents.isEmpty()) {
      throw new IllegalArgumentException("User provided empty file.");
    }
    this.numCols = this.fullCsvContents.get(0).size();
    this.checkConsistentRows();

    if (headersIncluded) {
      this.columnHeaderToIndex = extractHeaders(this.fullCsvContents.get(0));
      this.csvContents = new ArrayList<>();
      this.csvContents.addAll(fullCsvContents);
      this.csvContents.remove(0); // header row is not part of the CSV contents
    } else {
      // fullCsvContents and csvContents should point to the same list
      this.csvContents = this.fullCsvContents;
    }
    this.initialized = true;
  }

  /**
   * Generates a CSVParser where rows are lists of Strings, error checking for invalid filepath.
   *
   * @param pathFromData the path from ./data/ to the CSV file of interest
   * @return the new CSVParser for the CSV contents at ./data/pathFromData
   * @throws IllegalArgumentException for an empty or invalid CSV path from ./data/, or if user is
   *     attempting to exit data with ../filepath
   */
  private CsvParser<List<String>> generateCsvParser(String pathFromData)
      throws IllegalArgumentException {
    if (pathFromData == null || pathFromData.isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid empty CSV path. Please try again with a valid path from ./data/");
    }

    if (pathFromData.startsWith("..")) {
      // user is attempting to exit data directory illegally
      throw new IllegalArgumentException("Illegal attempt to exit secure data directory.");
    }

    FileReader reader;
    try {
      reader = new FileReader("data/" + pathFromData);
    } catch (FileNotFoundException exn) {
      throw new IllegalArgumentException(
          "Invalid CSV path. Please try again with a valid path from ./data/");
    }
    return new CsvParser<>(reader, new StringListRowConverter());
  }

  /**
   * Validates that CSV contents contains rows of the same size.
   *
   * @throws IllegalArgumentException if a row is encountered with a different length than the first
   *     row of the CSV
   */
  private void checkConsistentRows() throws IllegalArgumentException {
    for (List<String> row : this.fullCsvContents) {
      if (row.size() != this.numCols) {
        throw new IllegalArgumentException(
            "Encountered a row with length inconsistent to first row of CSV: " + row);
      }
    }
  }

  /**
   * Creates a map from headers to their corresponding index for efficient lookup.
   *
   * @param headerRow containing the names of columns (lowercase) in order from 1st column to nth
   *     column
   * @return the updated
   */
  private HashMap<String, Integer> extractHeaders(List<String> headerRow) {
    HashMap<String, Integer> mapper = new HashMap<>();
    for (int i = 0; i < headerRow.size(); i++) {
      mapper.put(headerRow.get(i).toLowerCase(), i);
    }
    return mapper;
  }

  /**
   * Extracts the column index from user provided String and error checks as needed.
   *
   * @param columnOfInterest the int column index or String column name of interest
   * @return the Integer index of columnOfInterest
   * @throws IllegalArgumentException if columnOfInterest is a String name in a Searcher provided a
   *     CSV file without a header
   * @throws IndexOutOfBoundsException if columnOfInterest as Integer lays outside the file's range
   *     or as a column name (String) does not exist
   */
  private Integer identifyColumnIndex(String columnOfInterest)
      throws IllegalArgumentException, IndexOutOfBoundsException {
    Integer colIndex;
    try {
      colIndex = Integer.valueOf(columnOfInterest);
      if (colIndex < 0 || colIndex > this.numCols) {
        throw new IndexOutOfBoundsException(
            "User provided column index outside of the range of the file.");
      }
    } catch (NumberFormatException exn) {
      // cannot be interpreted as Integer => column provided must be a name rather than an integer
      if (this.columnHeaderToIndex == null) {
        throw new IllegalArgumentException(
            "User provided column name but previously indicated that columns are not named.");
      } else if (this.columnHeaderToIndex.containsKey(columnOfInterest.toLowerCase())) {
        colIndex = this.columnHeaderToIndex.get(columnOfInterest.toLowerCase());
      } else {
        throw new IndexOutOfBoundsException("User provided column name not present in file.");
      }
    }
    return colIndex;
  }

  /**
   * Extracts the CSV contents.
   *
   * @return the csv contents associated with this Searcher
   * @throws IllegalStateException if called prior to initializing this via initialize method
   */
  public List<List<String>> getCsvContents() throws IllegalStateException {
    if (!this.initialized) {
      throw new IllegalStateException(
          "Attempted to retrieve CSV contents before Searcher initialized.");
    }
    List<List<String>> copy = new ArrayList<>();
    for (List<String> row : this.csvContents) {
      copy.add(new ArrayList<>(row));
    }
    return copy;
  }

  /**
   * Extracts the full CSV contents (including header).
   *
   * <p>In the case where the csv doesn't have a header, this is the exact same as getCsvContents.
   *
   * @return the full csv contents associated with this Searcher
   * @throws IllegalStateException if called prior to initializing this via initialize method
   */
  public List<List<String>> getFullCsv() throws IllegalStateException {
    if (!this.initialized) {
      throw new IllegalStateException(
          "Attempted to retrieve CSV contents before Searcher initialized.");
    }
    List<List<String>> copy = new ArrayList<>();
    for (List<String> row : this.fullCsvContents) {
      copy.add(new ArrayList<>(row));
    }
    return copy;
  }

  /**
   * Extracts the mapping of column names to indices.
   *
   * @return the HashMap of column names to their indices
   * @throws IllegalStateException if called prior to initializing this via initialize method
   */
  public HashMap<String, Integer> getHeaders() throws IllegalStateException {
    if (!this.initialized) {
      throw new IllegalStateException(
          "Attempted to retrieve CSV headers before Searcher initialized.");
    }
    return new HashMap<String, Integer>(this.columnHeaderToIndex);
  }

  /**
   * Searches for a target value in a specific column of CSV contents. A cell (String) in the
   * specified column is considered a match if and only if [lowercase version of
   * cell].equals([lowercase version of target]).
   *
   * @param target the value to search for
   * @param columnOfInterest specifying the column where target should be searched for (in String
   *     form); can be either: 1) the integer column number of the column, 2) column name if the
   *     original CSV was provided with headers
   * @return a List of the rows whose columnOfInterest matched target
   * @throws IllegalArgumentException if columnOfInterest is a String name in a Searcher provided a
   *     CSV file without a header, or provided empty/null arg
   * @throws IndexOutOfBoundsException if columnOfInterest as Integer lays outside the file's range
   *     or as a column name (String) does not exist
   * @throws IllegalStateException if called prior to initializing this via initialize method
   */
  public List<List<String>> search(String target, String columnOfInterest)
      throws IllegalArgumentException, IndexOutOfBoundsException, IllegalStateException {
    if (!this.initialized) {
      throw new IllegalStateException("Attempted to search before Searcher initialized.");
    } else if (columnOfInterest == null || columnOfInterest.isEmpty()) {
      throw new IllegalArgumentException(
          "Search passed a null column of interest. "
              + "Please try again with non-null column of interest.");
    } else if (target == null || target.isEmpty()) {
      throw new IllegalArgumentException(
          "Search passed a null target. Please try again with non-null target.");
    }

    Integer colIndex = this.identifyColumnIndex(columnOfInterest);
    List<List<String>> matchingRows = new ArrayList<>();
    String lowercaseTarget = target.toLowerCase();
    for (List<String> row : this.csvContents) {
      if (row.get(colIndex).toLowerCase().equals(lowercaseTarget)) {
        matchingRows.add(new ArrayList<>(row));
      }
    }
    return matchingRows;
  }

  /**
   * Searches for a target value in all columns of CSV contents. A cell (String) in any column is
   * considered a match if and only if [lowercase version of cell].equals([lowercase version of
   * target]).
   *
   * @param target the value to search for
   * @return a List of the rows where any column matched target
   * @throws IllegalArgumentException if given a null or empty target
   * @throws IllegalStateException if called prior to initializing this via initialize method
   */
  public List<List<String>> search(String target)
      throws IllegalArgumentException, IllegalStateException {
    if (!this.initialized) {
      throw new IllegalStateException("Attempted to search before Searcher initialized.");
    } else if (target == null || target.isEmpty()) {
      throw new IllegalArgumentException(
          "Search passed a null target. Please try again with non-null target.");
    }

    List<List<String>> matchingRows = new ArrayList<>();
    String lowercaseTarget = target.toLowerCase();
    for (List<String> row : this.csvContents) {
      for (String cell : row) {
        if (cell.toLowerCase().equals(lowercaseTarget)) {
          matchingRows.add(new ArrayList<>(row));
          break; // short-circuit row to save some time
        }
      }
    }
    return matchingRows;
  }
}
