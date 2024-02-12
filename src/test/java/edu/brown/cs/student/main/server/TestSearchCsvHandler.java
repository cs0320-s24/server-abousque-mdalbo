package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import edu.brown.cs.student.main.server.csv.SearchCsvHandler;
import edu.brown.cs.student.main.server.csv.ViewCsvHandler;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** A class for unit testing of the functionality of the SearchCsvHandler class. */
public class TestSearchCsvHandler {
  private final Searcher csvSearcher = new Searcher();
  private LoadCsvHandler loadcsv;
  private ViewCsvHandler viewcsv;
  private SearchCsvHandler searchcsv;

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  private final Type listListStringObject =
      Types.newParameterizedType(List.class, List.class, String.class);
  private JsonAdapter<List<List<String>>> csvContentsAdapter;

  @BeforeEach
  public void setup() {
    this.loadcsv = new LoadCsvHandler(this.csvSearcher);
    this.viewcsv = new ViewCsvHandler(this.csvSearcher);
    this.searchcsv = new SearchCsvHandler(this.csvSearcher);
    // Re-initialize parser, state, etc. for every test method
    Spark.get("/loadcsv", this.loadcsv);
    Spark.get("/searchcsv", this.searchcsv);
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    csvContentsAdapter = moshi.adapter(listListStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to the loadcsv endpoint.
   *
   * <p>Adapted from February 8, 2024 cs32 livecode.
   *
   * <p>The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @param args the arguments to pass to the endpoint, following a "?"
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequestLoadCsv(String args) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/loadcsv?" + args);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper to start a connection to the searchcsv endpoint.
   *
   * <p>Adapted from February 8, 2024 cs32 livecode.
   *
   * <p>The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequestSearchCsv() throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/searchcsv");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper to make working with a large test suite easier: if an error, print more info.
   *
   * <p>Adapted from February 8, 2024 cs32 livecode.
   *
   * @param body the body of the json response
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body);
    }
  }

  /**
   * Helper to load a csv with its handler and verify success.
   *
   * @param path the path from data/ to the csv to load
   * @param headersIncluded a boolean describing if the csv file at path has a header row
   * @throws IOException if unable to connect to Server
   */
  public void loadAndVerifySuccess(String path, boolean headersIncluded) throws IOException {
    HttpURLConnection loadConnection =
        tryRequestLoadCsv("filepath=" + path + "&headersIncluded=" + headersIncluded);
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("success", responseBody.get("result"));
  }

  /**
   * Test error when searching a CSV before loading one.
   *
   * @throws IOException if unable to connect to Server
   */
  @Test
  public void testSearchBeforeLoad() throws IOException {
    HttpURLConnection searchConnection = tryRequestSearchCsv();
    assertEquals(200, searchConnection.getResponseCode());

    Map<String, Object> viewResponseBody =
        adapter.fromJson(new Buffer().readFrom(searchConnection.getInputStream()));
    showDetailsIfError(viewResponseBody);
    assertEquals("error_bad_json", viewResponseBody.get("result"));
    assertEquals(
        "Attempted to searchcsv before loading in a csv with loadcsv.",
        viewResponseBody.get("error message"));

    searchConnection.disconnect(); // close gracefully
  }
}
