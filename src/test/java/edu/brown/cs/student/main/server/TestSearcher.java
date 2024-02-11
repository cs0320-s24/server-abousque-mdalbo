package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import edu.brown.cs.student.main.server.csv.parsing.FactoryFailureException;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

/** A class for unit testing of the functionality of the Searcher class. */
public class TestSearcher {
  private final List<String> postSecondaryEduColumns =
      List.of(
          "IPEDS Race",
          "ID Year",
          "Year",
          "ID University",
          "University",
          "Completions",
          "Slug University",
          "share",
          "Sex",
          "ID Sex");

  /** Basic unit test of extractHeaders method in Searcher. */
  @Test
  public void testHeaderMapping() {
    Searcher censusSearcher = null;
    try {
      censusSearcher = new Searcher("census/postsecondary_education.csv", true);
    } catch (IOException | IllegalArgumentException | FactoryFailureException ffExn) {
      fail();
    }
    HashMap<String, Integer> colToIndex = censusSearcher.getHeaders();

    for (int i = 0; i < this.postSecondaryEduColumns.size(); i++) {
      assertEquals(i, colToIndex.get(this.postSecondaryEduColumns.get(i).toLowerCase()));
    }
  }

  /** Basic unit test ensuring header row isn't in csvContents. */
  @Test
  public void testContents() {
    Searcher censusSearcher = null;
    try {
      censusSearcher = new Searcher("census/postsecondary_education.csv", true);
    } catch (IOException | IllegalArgumentException | FactoryFailureException ffExn) {
      fail();
    }
    List<List<String>> contents = censusSearcher.getCsvContents();
    List<String> firstRow =
        List.of(
            "Asian",
            "2020",
            "2020",
            "217156",
            "Brown University",
            "214",
            "brown-university",
            "0.069233258",
            "Men",
            "1");
    assertNotEquals(this.postSecondaryEduColumns, contents.get(0));
    assertEquals(firstRow, contents.get(0));
  }

  /** Test exception thrown when file is empty. */
  @Test
  public void testEmptyFile() {
    assertThrows(IllegalArgumentException.class, () -> new Searcher("empty.csv", false));
    assertThrows(IllegalArgumentException.class, () -> new Searcher("empty.csv", true));
  }

  /** Test exception thrown when providing Searcher with empty/null path. */
  @Test
  public void testEmptyPath() {
    // null paths
    assertThrows(IllegalArgumentException.class, () -> new Searcher(null, true));
    assertThrows(IllegalArgumentException.class, () -> new Searcher(null, false));

    // empty paths
    assertThrows(IllegalArgumentException.class, () -> new Searcher("", true));
    assertThrows(IllegalArgumentException.class, () -> new Searcher("", false));
  }

  /**
   * Test exception thrown when providing Searcher with an invalid path from data and/or a file that
   * is out of scope.
   */
  @Test
  public void testNonExistentPath() {
    assertThrows(IllegalArgumentException.class, () -> new Searcher("fish.csv", true));
    assertThrows(IllegalArgumentException.class, () -> new Searcher("fish.csv", false));
  }

  /** Test exception thrown when an invalid path from data routes out of the secure data folder. */
  @Test
  public void testLoopholeIllegalPath() {
    assertThrows(IllegalArgumentException.class, () -> new Searcher("../out_of_scope.csv", true));
    assertThrows(IllegalArgumentException.class, () -> new Searcher("../out_of_scope.csv", false));
  }

  /**
   * Test exception thrown when search given empty search.
   *
   * @throws IOException if an unexpected other error occurs
   * @throws FactoryFailureException if an unexpected other error occurs
   */
  @Test
  public void testEmptySearch() throws IOException, FactoryFailureException {
    Searcher searcher = new Searcher("census/income_by_race.csv", true);
    assertThrows(IllegalArgumentException.class, () -> searcher.search(null, "Year"));
    assertThrows(IllegalArgumentException.class, () -> searcher.search(null));
    assertThrows(IllegalArgumentException.class, () -> searcher.search("", "Year"));
    assertThrows(IllegalArgumentException.class, () -> searcher.search(""));
  }

  /** Test searching by index for present targets. */
  @Test
  public void testSearchByIndex() {
    try {
      // CSV with headers
      Searcher starSearcher = new Searcher("stars/ten-star.csv", true);
      List<List<String>> expectedSolRow = List.of(List.of("0", "Sol", "0", "0", "0"));

      assertEquals(expectedSolRow, starSearcher.search("Sol", "1"));
      assertEquals(expectedSolRow, starSearcher.search("sol", "1"));

      // CSV without headers
      Searcher starSearcherNoHeaders = new Searcher("stars/ten-star_no_header.csv", false);
      assertEquals(expectedSolRow, starSearcherNoHeaders.search("Sol", "1"));
      assertEquals(expectedSolRow, starSearcherNoHeaders.search("sol", "1"));
    } catch (IllegalArgumentException
        | IndexOutOfBoundsException
        | IOException
        | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test searching by column name for present targets. */
  @Test
  public void testSearchByColName() {
    try {
      // target only present in column of interest
      Searcher starSearcher = new Searcher("stars/ten-star.csv", true);
      List<List<String>> expectedSolRow = List.of(List.of("0", "Sol", "0", "0", "0"));

      assertEquals(expectedSolRow, starSearcher.search("Sol", "ProperName"));
      assertEquals(expectedSolRow, starSearcher.search("sol", "ProperName"));
      assertEquals(expectedSolRow, starSearcher.search("Sol", "propername"));
      assertEquals(expectedSolRow, starSearcher.search("sol", "propername"));

      Searcher eduSearcher = new Searcher("census/postsecondary_education.csv", true);
      List<List<String>> expectedRowsWomen =
          List.of(
              List.of(
                  "Asian",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "235",
                  "brown-university",
                  "0.076027176",
                  "Women",
                  "2"),
              List.of(
                  "Black or African American",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "95",
                  "brown-university",
                  "0.03073439",
                  "Women",
                  "2"),
              List.of(
                  "Native Hawaiian or Other Pacific Islanders",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "4",
                  "brown-university",
                  "0.00129408",
                  "Women",
                  "2"),
              List.of(
                  "Hispanic or Latino",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "207",
                  "brown-university",
                  "0.066968619",
                  "Women",
                  "2"),
              List.of(
                  "Two or More Races",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "85",
                  "brown-university",
                  "0.027499191",
                  "Women",
                  "2"),
              List.of(
                  "American Indian or Alaska Native",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "7",
                  "brown-university",
                  "0.002264639",
                  "Women",
                  "2"),
              List.of(
                  "Non-resident Alien",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "281",
                  "brown-university",
                  "0.090909091",
                  "Women",
                  "2"),
              List.of(
                  "White",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "660",
                  "brown-university",
                  "0.213523132",
                  "Women",
                  "2"));

      assertEquals(expectedRowsWomen, eduSearcher.search("Women", "Sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("women", "Sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("Women", "sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("women", "sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("2", "ID Sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("2", "ID Sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("2", "id sex"));
      assertEquals(expectedRowsWomen, eduSearcher.search("2", "id sex"));

      // target present in col of interest AND other column
      Searcher nameSearcher = new Searcher("value_multiple_columns.csv", true);
      List<List<String>> firstNameHarry = List.of(List.of("Harry", "Potter", "56"));

      assertEquals(firstNameHarry, nameSearcher.search("Harry", "first name"));
      assertEquals(firstNameHarry, nameSearcher.search("harry", "first name"));
      assertEquals(firstNameHarry, nameSearcher.search("Harry", "First name"));
      assertEquals(firstNameHarry, nameSearcher.search("harry", "First Name"));
    } catch (IllegalArgumentException
        | IndexOutOfBoundsException
        | IOException
        | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test searching all columns for a present target. */
  @Test
  public void testSearchAllCols() {
    try {
      // value present in one column (but an unspecified column)
      Searcher eduSearcher = new Searcher("census/postsecondary_education.csv", true);
      List<List<String>> expectedRowsWomen =
          List.of(
              List.of(
                  "Asian",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "235",
                  "brown-university",
                  "0.076027176",
                  "Women",
                  "2"),
              List.of(
                  "Black or African American",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "95",
                  "brown-university",
                  "0.03073439",
                  "Women",
                  "2"),
              List.of(
                  "Native Hawaiian or Other Pacific Islanders",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "4",
                  "brown-university",
                  "0.00129408",
                  "Women",
                  "2"),
              List.of(
                  "Hispanic or Latino",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "207",
                  "brown-university",
                  "0.066968619",
                  "Women",
                  "2"),
              List.of(
                  "Two or More Races",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "85",
                  "brown-university",
                  "0.027499191",
                  "Women",
                  "2"),
              List.of(
                  "American Indian or Alaska Native",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "7",
                  "brown-university",
                  "0.002264639",
                  "Women",
                  "2"),
              List.of(
                  "Non-resident Alien",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "281",
                  "brown-university",
                  "0.090909091",
                  "Women",
                  "2"),
              List.of(
                  "White",
                  "2020",
                  "2020",
                  "217156",
                  "Brown University",
                  "660",
                  "brown-university",
                  "0.213523132",
                  "Women",
                  "2"));

      assertEquals(expectedRowsWomen, eduSearcher.search("Women"));
      assertEquals(expectedRowsWomen, eduSearcher.search("women"));
      assertEquals(expectedRowsWomen, eduSearcher.search("2"));
      assertEquals(expectedRowsWomen, eduSearcher.search("2"));

      // value present multiple times, in different columns
      Searcher nameSearcher = new Searcher("value_multiple_columns.csv", true);
      List<List<String>> harries =
          List.of(List.of("Harry", "Potter", "56"), List.of("John", "Harry", "12"));

      assertEquals(harries, nameSearcher.search("Harry"));
      assertEquals(harries, nameSearcher.search("harry"));
    } catch (IllegalArgumentException
        | IndexOutOfBoundsException
        | IOException
        | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test empty results for target present in full CSV but not present in column of interest. */
  @Test
  public void testValuePresentWrongColumn() {
    try {
      Searcher nameSearcher = new Searcher("value_multiple_columns.csv", true);
      assertEquals(0, nameSearcher.search("Fish", "first name").size());
      assertEquals(0, nameSearcher.search("fish", "first name").size());
      assertEquals(0, nameSearcher.search("Fish", "First Name").size());
      assertEquals(0, nameSearcher.search("fish", "First Name").size());
    } catch (IllegalArgumentException
        | IndexOutOfBoundsException
        | IOException
        | FactoryFailureException exn) {
      fail();
    }
  }

  /** Test empty results for target not present in full CSV at all. */
  @Test
  public void testValueNotPresent() {
    try {
      Searcher nameSearcher = new Searcher("value_multiple_columns.csv", true);

      assertEquals(0, nameSearcher.search("noname", "first name").size());
      assertEquals(0, nameSearcher.search("noname", "last name").size());
      assertEquals(0, nameSearcher.search("noname", "age").size());

      assertEquals(0, nameSearcher.search("noname", "0").size());
      assertEquals(0, nameSearcher.search("noname", "1").size());
      assertEquals(0, nameSearcher.search("noname", "2").size());

      assertEquals(0, nameSearcher.search("noname").size());
    } catch (IllegalArgumentException
        | IndexOutOfBoundsException
        | IOException
        | FactoryFailureException exn) {
      fail();
    }
  }

  /**
   * Test exception thrown when a user wrongly attempts to search in a column by name, but the
   * original file did not provide column names.
   *
   * @throws IOException if an unexpected other error occurs
   * @throws FactoryFailureException if an unexpected other error occurs
   */
  @Test
  public void testSearchByStringNoHeader() throws IOException, FactoryFailureException {
    Searcher searcher = new Searcher("stars/ten-star_no_header.csv", false);
    assertThrows(
        IllegalArgumentException.class, () -> searcher.search("Proxima Centauri", "ProperName"));
    assertThrows(
        IllegalArgumentException.class, () -> searcher.search("87666", "totally made up col"));
  }

  /**
   * Test exception thrown when the search method that takes 2 arguments is given a nonexistent
   * column to search in.
   *
   * @throws IOException if an unexpected other error occurs
   * @throws FactoryFailureException if an unexpected other error occurs
   */
  @Test
  public void testSearch1GivenNonExistentColumn() throws IOException, FactoryFailureException {
    Searcher searcher = new Searcher("census/postsecondary_education.csv", true);
    assertThrows(IndexOutOfBoundsException.class, () -> searcher.search("2020", "nonexistent"));
    assertThrows(
        IndexOutOfBoundsException.class,
        () -> searcher.search("brown-university", "slug university ")); // extra space
  }

  /**
   * Test search method error handling of an Integer index out of bounds of file columns.
   *
   * @throws IOException if an unexpected other error occurs
   * @throws FactoryFailureException if an unexpected other error occurs
   */
  @Test
  public void testSearchGivenOutOfBoundsIndex() throws IOException, FactoryFailureException {
    Searcher noHeaderSearcher = new Searcher("stars/ten-star_no_header.csv", false);
    Searcher yesHeaderSearcher = new Searcher("census/postsecondary_education.csv", true);

    assertThrows(IndexOutOfBoundsException.class, () -> noHeaderSearcher.search("Sol", "-1"));
    assertThrows(IndexOutOfBoundsException.class, () -> yesHeaderSearcher.search("Asian", "-1"));
    assertThrows(IndexOutOfBoundsException.class, () -> noHeaderSearcher.search("-0.36132", "5"));
    assertThrows(IndexOutOfBoundsException.class, () -> yesHeaderSearcher.search("85", "10"));
  }

  /**
   * Test exception thrown when CSV contents are not rectangular, that is to say that 1 or more rows
   * are inconsistent in lengths.
   */
  @Test
  public void testInconsistentColumnCount() {
    assertThrows(
        IllegalArgumentException.class, () -> new Searcher("inconsistent_row_lengths.csv", false));
  }

  /** Test getFullCsv. */
  @Test
  public void testCsvContentsGetters() {
    Searcher censusSearcher = null;
    try {
      censusSearcher = new Searcher("census/postsecondary_education.csv", true);
    } catch (IOException | IllegalArgumentException | FactoryFailureException ffExn) {
      fail();
    }
    List<List<String>> fullContents = censusSearcher.getFullCsv();
    List<String> firstRow =
        List.of(
            "Asian",
            "2020",
            "2020",
            "217156",
            "Brown University",
            "214",
            "brown-university",
            "0.069233258",
            "Men",
            "1");
    assertEquals(this.postSecondaryEduColumns, fullContents.get(0));
    assertEquals(firstRow, fullContents.get(1));
  }
}
