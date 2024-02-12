package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import edu.brown.cs.student.main.server.csv.ViewCsvHandler;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

/** A class for unit testing of the functionality of the LoadCsvHandler class. */
public class TestViewCsvHandler {
  private LoadCsvHandler lh;
  private ViewCsvHandler vh;
  private Searcher csvSearcher;

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  private final Type listListStringObject =
      Types.newParameterizedType(List.class, List.class, String.class);
  private JsonAdapter<List<List<String>>> csvContentsAdapter;

  @BeforeEach
  public void setup() {

    this.lh = new LoadCsvHandler(this.csvSearcher);
    this.vh = new ViewCsvHandler(this.csvSearcher);
    // Re-initialize parser, state, etc. for every test method
    Spark.get("/loadcsv", this.lh);
    Spark.get("/viewcsv", this.vh);
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    csvContentsAdapter = moshi.adapter(listListStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
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
   * Helper to start a connection to the viewcsv endpoint.
   *
   * <p>Adapted from February 8, 2024 cs32 livecode.
   *
   * <p>The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequestViewCsv() throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/viewcsv");
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
   * Test error when viewing a CSV before loading one.
   *
   * @throws IOException if unable to connect to Server
   */
  //  @Test
  //  public void testViewBeforeLoad() throws IOException {
  //    HttpURLConnection viewConnection = tryRequestViewCsv();
  //    assertEquals(200, viewConnection.getResponseCode());
  //
  //    Map<String, Object> viewResponseBody =
  //        adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));
  //    showDetailsIfError(viewResponseBody);
  //    assertEquals("error_bad_json", viewResponseBody.get("result"));
  //    assertEquals(
  //        "Attempted to viewcsv before loading in a csv with loadcsv.",
  //        viewResponseBody.get("message"));
  //
  //    viewConnection.disconnect(); // close gracefully
  //  }

  /**
   * Test successful viewcsv (after loadcsv).
   *
   * @throws IOException if unable to connect to Server
   */
  //  @Test
  //  public void testSuccess() throws IOException {
  //    // loadcsv
  //    HttpURLConnection loadConnection =
  //        tryRequestLoadCsv("filepath=value_multiple_columns.csv&headersIncluded=true");
  //    assertEquals(200, loadConnection.getResponseCode());
  //    Map<String, Object> responseBody =
  //        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
  //    showDetailsIfError(responseBody);
  //    assertEquals("success", responseBody.get("result"));
  //
  //    // viewcsv
  //    HttpURLConnection viewConnection = tryRequestViewCsv();
  //    assertEquals(200, viewConnection.getResponseCode());
  //    Map<String, Object> viewResponseBody =
  //        adapter.fromJson(new Buffer().readFrom(viewConnection.getInputStream()));
  //    showDetailsIfError(viewResponseBody);
  //    assertEquals("success", viewResponseBody.get("result"));
  //    assertEquals("viewcsv", viewResponseBody.get("endpoint"));
  //
  //    viewConnection.disconnect(); // close gracefully
  //    loadConnection.disconnect(); // close gracefully
  //  }
}
