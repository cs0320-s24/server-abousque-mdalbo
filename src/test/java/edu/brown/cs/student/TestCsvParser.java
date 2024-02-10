package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import edu.brown.cs.student.main.parsing.CsvParser;
import edu.brown.cs.student.main.parsing.FactoryFailureException;
import edu.brown.cs.student.main.parsing.examplecreatorfromrows.StarSign;
import edu.brown.cs.student.main.parsing.examplecreatorfromrows.StarSignRowConverter;
import edu.brown.cs.student.main.parsing.examplecreatorfromrows.StringListRowConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

/** A class for unit testing of the functionality of the CSVParser class. */
public class TestCsvParser {

  private final StringListRowConverter splitterStrings = new StringListRowConverter();
  private final StarSignRowConverter splitterStarSigns = new StarSignRowConverter();

  /** Unit test CSVParser with an empty CSV file. */
  @Test
  public void parseEmpty() {
    String emptyCsvLine = "";
    StringReader reader = new StringReader(emptyCsvLine);
    CsvParser<List<String>> parser = new CsvParser<>(reader, splitterStrings);

    List<List<String>> actual;
    try {
      actual = parser.parse();
      assertEquals(0, actual.size());
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Basic unit test to verify expected output of StringRowSplitter.create. */
  @Test
  public void testStringRowSplitter() {
    String singleLine = "woopie, pies, yum";
    StringReader reader = new StringReader(singleLine);
    BufferedReader bufReader = new BufferedReader(reader);
    String readerOutput = "";
    try {
      readerOutput = bufReader.readLine();
    } catch (IOException exn) {
      fail();
    }

    List<String> splitterOutput = splitterStrings.create(List.of(readerOutput));
    assertEquals(List.of("woopie", "pies", "yum"), splitterOutput);
  }

  /** Unit test CSVParser with a single line of simple CSV content. */
  @Test
  public void parseOneLineSimple() {
    String singleLine = "woopie, pies, yum";
    StringReader reader = new StringReader(singleLine);
    CsvParser<List<String>> parser = new CsvParser<>(reader, splitterStrings);
    List<List<String>> actual;
    try {
      actual = parser.parse();
      assertEquals(1, actual.size());
      assertEquals(List.of(List.of("woopie", "pies", "yum")), actual);
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Unit test CSVParser with a single line of more complex CSV content with quotes. */
  @Test
  public void parseOneLineQuotes() {
    String csv = "RI,White,\" $1,058.47 \",395773.6521, $1.00 ,75%";
    StringReader reader = new StringReader(csv);
    CsvParser<List<String>> parser = new CsvParser<>(reader, splitterStrings);
    List<List<String>> actual;
    try {
      actual = parser.parse();
      assertEquals(1, actual.size());
      assertEquals(
          List.of(List.of("RI", "White", "$1,058.47", "395773.6521", "$1.00", "75%")), actual);
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test CSVParser with multiple rows of CSV contents. */
  @Test
  public void testMultipleLines() {
    String csv =
        """
            RI,White," $1,058.47 ",395773.6521, $1.00 ,75%
            RI,Black, $770.26 ,30424.80376, $0.73 ,6%
            """;
    StringReader reader = new StringReader(csv);
    CsvParser<List<String>> parser = new CsvParser<>(reader, splitterStrings);
    List<List<String>> actual;
    try {
      actual = parser.parse();
      assertEquals(2, actual.size());
      assertEquals(
          List.of(
              List.of("RI", "White", "$1,058.47", "395773.6521", "$1.00", "75%"),
              List.of("RI", "Black", "$770.26", "30424.80376", "$0.73", "6%")),
          actual);
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test CSVParser with CSV row missing a cell. */
  @Test
  public void testMissingCell() {
    String csv =
        """
            RI,White," $1,058.47 ",395773.6521, $1.00 ,75%
            RI,Black, $770.26 ,, $0.73 ,6%
            """;
    StringReader reader = new StringReader(csv);
    CsvParser<List<String>> parser = new CsvParser<>(reader, splitterStrings);
    List<List<String>> actual;
    try {
      actual = parser.parse();
      assertEquals(2, actual.size());
      assertEquals(
          List.of(
              List.of("RI", "White", "$1,058.47", "395773.6521", "$1.00", "75%"),
              List.of("RI", "Black", "$770.26", "", "$0.73", "6%")),
          actual);
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test CSVParser with CSV rows of different lengths. */
  @Test
  public void testIrregularShape() {
    String csv =
        """
            RI,White," $1,058.47 ",395773.6521, $1.00 ,75%
            RI,Black, $770.26 , $0.73 ,6%
            """;
    StringReader reader = new StringReader(csv);
    CsvParser<List<String>> parser = new CsvParser<>(reader, splitterStrings);
    List<List<String>> actual;
    try {
      actual = parser.parse();
      assertEquals(2, actual.size());
      assertEquals(
          List.of(
              List.of("RI", "White", "$1,058.47", "395773.6521", "$1.00", "75%"),
              List.of("RI", "Black", "$770.26", "$0.73", "6%")),
          actual);
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test CSVParser with null converter. */
  @Test
  public void testNullConverter() {
    String singleLine = "woopie, pies, yum";
    StringReader reader = new StringReader(singleLine);
    assertThrows(IllegalArgumentException.class, () -> new CsvParser<>(reader, null));
  }

  /** Test CSVParser with null reader. */
  @Test
  public void testNullReader() {
    assertThrows(IllegalArgumentException.class, () -> new CsvParser<>(null, splitterStrings));
  }

  /** Test CSVParser with a different converter and correctly formatted inputs. */
  @Test
  public void testStarSignConverter() {
    String row = "Taurus,Albert";
    StringReader reader = new StringReader(row);
    CsvParser<StarSign> parser = new CsvParser<>(reader, splitterStarSigns);
    List<StarSign> actual;
    try {
      actual = parser.parse();
      assertEquals(1, actual.size());
      assert (actual.get(0).equals(new StarSign("Albert", "Taurus")));
    } catch (IOException | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test StarSignRowConverter with malformed CSV contents, prompting FactoryFailureException. */
  @Test
  public void testIOWrongNumArgs() {
    String row = "Gemini,Roberto,Nick";
    assertThrows(FactoryFailureException.class, () -> splitterStarSigns.create(List.of(row)));
  }

  /** Test CSVParser with malformed CSV contents, prompting FactoryFailureException. */
  @Test
  public void testFactoryFailureWrongNumArgs() {
    String row = "Gemini,Roberto,Nick";
    StringReader reader = new StringReader(row);
    CsvParser<StarSign> parser = new CsvParser<>(reader, splitterStarSigns);
    assertThrows(FactoryFailureException.class, parser::parse);
  }

  /**
   * Test StarSignRowConverter with null args (from malformed CSV contents), prompting
   * FactoryFailureException.
   */
  @Test
  public void testFFNullName() {
    String row = "Libra,";
    assertThrows(FactoryFailureException.class, () -> splitterStarSigns.create(List.of(row)));
  }

  /**
   * Test StarSignRowConverter with null args (from malformed CSV contents), prompting
   * FactoryFailureException.
   */
  @Test
  public void testFFNullSign() {
    String row = ",Bob";
    assertThrows(FactoryFailureException.class, () -> splitterStarSigns.create(List.of(row)));
  }
}
