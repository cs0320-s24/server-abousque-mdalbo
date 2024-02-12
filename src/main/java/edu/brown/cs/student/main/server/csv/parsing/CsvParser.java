package edu.brown.cs.student.main.server.csv.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/** A class to parse CSV data associated with a Reader. */
public class CsvParser<T> {

  BufferedReader reader;
  CreatorFromRow<T> converter;

  /**
   * The single constructor for the CSVParser class.
   *
   * @param src a Reader to source the CSV contents from
   * @param converter describing how rows of the CSV should be translated into objects
   * @throws IllegalArgumentException if src or converter are null
   */
  public CsvParser(Reader src, CreatorFromRow<T> converter) throws IllegalArgumentException {
    if (src == null) {
      throw new IllegalArgumentException("CSV source must be nonnull.");
    } else {
      this.reader = new BufferedReader(src);
    }

    if (converter == null) {
      throw new IllegalArgumentException("CSV row converter must be nonnull.");
    } else {
      this.converter = converter;
    }
  }

  /**
   * Parses the CSV associated with this instance's reader.
   *
   * @return a List of the row-translations of the src CSV
   * @throws IOException if a line is encountered in the CSV than cannot be read by src
   * @throws FactoryFailureException if the provided converter is unable to convert a CSV row
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    List<T> parsed = new ArrayList<>();

    int currentRowIndex = 0;
    String currentRow;
    while (true) {
      try {
        currentRow = this.reader.readLine();
        if (currentRow == null) {
          break;
        }
        parsed.add(this.converter.create(List.of(currentRow)));
        currentRowIndex++;
      } catch (IOException ioExn) {
        throw new IOException("Unable to read CSV starting at line #" + currentRowIndex + ".");
      } catch (FactoryFailureException ffExn) {
        throw new FactoryFailureException(
            "Unable to convert CSV line #" + currentRowIndex + " to expected row type.", ffExn.row);
      }
    }
    return parsed;
  }
}
