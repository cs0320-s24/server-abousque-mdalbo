package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** A class for unit testing of the functionality of the LoadCsvHandler class. */
public class TestLoadCsvHandler {
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method
    Spark.get("/loadcsv", new LoadCsvHandler());
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params.
   *
   * <p>Adapted from February 8, 2024 cs32 livecode.
   *
   * <p>The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @param endpoint the endpoint to send a request to
   * @param args the arguments to pass to the endpoint, following a "?"
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String endpoint, String args) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + endpoint + "?" + args);
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
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }

  /**
   * Test error when no arguments given to endpoint.
   *
   * @throws IOException if unable to connect to Server
   */
  @Test
  public void testNoArgs() throws IOException {
    // no args
    HttpURLConnection connection = tryRequest("loadcsv", "");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_bad_request", responseBody.get("result"));
    connection.disconnect(); // close gracefully

    // filepath only, missing headersIncluded
    connection = tryRequest("loadcsv", "filepath=RI_City_Town.csv");
    assertEquals(200, connection.getResponseCode());

    responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_bad_request", responseBody.get("result"));
    connection.disconnect(); // close gracefully

    // headersIncluded only, missing filepath
    connection = tryRequest("loadcsv", "headersIncluded=false");
    assertEquals(200, connection.getResponseCode());

    responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_bad_request", responseBody.get("result"));
    connection.disconnect(); // close gracefully
  }

  /**
   * Test error when endpoint given bad filepath.
   *
   * @throws IOException if unable to connect to Server
   */
  @Test
  public void testBadFilepath() throws IOException {
    // nonexistent file
    HttpURLConnection connection =
        tryRequest("loadcsv", "filepath=nonexistent.csv&headersIncluded=true");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_datasource", responseBody.get("result"));
    connection.disconnect(); // close gracefully

    // file out of scope
    connection = tryRequest("loadcsv", "filepath=../out_of_scope.csv&headersIncluded=true");
    assertEquals(200, connection.getResponseCode());

    responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_bad_request", responseBody.get("result"));
    connection.disconnect(); // close gracefully
  }

  /**
   * Test error when endpoint given bad file (bad file contents that cannot be parsed for
   * searching).
   *
   * @throws IOException if unable to connect to Server
   */
  @Test
  public void testBadFileContents() throws IOException {
    // file with inconsistent row lengths
    HttpURLConnection connection =
        tryRequest("loadcsv", "filepath=inconsistent_row_lengths.csv&headersIncluded=false");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_bad_request", responseBody.get("result"));
    connection.disconnect(); // close gracefully

    // empty file
    connection = tryRequest("loadcsv", "filepath=empty.csv&headersIncluded=true");
    assertEquals(200, connection.getResponseCode());

    responseBody = adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error_bad_request", responseBody.get("result"));
    connection.disconnect(); // close gracefully
  }

  /**
   * Test successful connection.
   *
   * @throws IOException if unable to connect to Server
   */
  @Test
  public void testSuccess() throws IOException {
    HttpURLConnection connection =
        tryRequest("loadcsv", "filepath=RI_City_Town.csv&headersIncluded=true");
    assertEquals(200, connection.getResponseCode());

    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("success", responseBody.get("result"));
    connection.disconnect(); // close gracefully
  }
}
