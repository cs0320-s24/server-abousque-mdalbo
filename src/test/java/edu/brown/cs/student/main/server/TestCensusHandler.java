package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.acs.AcsDatasource;
import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.acs.MockAcsApi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * A class for testing functionality of CensusHandler class and possible inputs These are
 * integration tests that test the whole functionality of broadband.
 */
public class TestCensusHandler {
  private CensusHandler censusHandler;
  private AcsDatasource AcsAPI;
  private Map<String, Object> successfulResponse;
  private JsonAdapter<Map<String, String>> adapter;
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, String.class);

  /** Sets up the server and passes in the mocked api before each test */
  @BeforeEach
  public void setup() {
    this.successfulResponse = new HashMap<>();
    this.successfulResponse.put("date accessed", "time");
    this.successfulResponse.put("Broadband Use", "87.0");
    this.AcsAPI = new MockAcsApi(this.successfulResponse);
    this.censusHandler = new CensusHandler(this.AcsAPI);
    Spark.get("/broadband", this.censusHandler);
    Spark.awaitInitialization();
    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(mapStringObject);
  }

  /** Breaks down the server after each test */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/broadband");
    Spark.awaitStop();
  }

  /**
   * Helper method to send a request to the server
   *
   * @param apiCall
   * @return
   * @throws IOException
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/broadband?" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests that a valid state and country has the correct output
   *
   * @throws IOException
   */
  @Test
  public void testValidStateAndCounty() throws IOException {
    HttpURLConnection connection = tryRequest("state=California&county=Yolo+County");
    assertEquals(200, connection.getResponseCode());
    Map<String, String> responseBody =
        this.adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals(responseBody.size(), 5);
    assertEquals(responseBody.get("result"), "success");
    assertEquals(responseBody.get("Broadband Use"), "87.0");
    assertEquals(responseBody.get("State"), "California");
  }

  /**
   * Integration test for when an invalid state is entered
   *
   * @throws IOException
   */
  @Test
  public void testInvalidState() throws IOException {
    HttpURLConnection connection = tryRequest("state=Mexico&county=Yolo+County");
    assertEquals(200, connection.getResponseCode());
    Map<String, String> responseBody =
        this.adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals(responseBody.size(), 1);
    assertEquals(responseBody.get("result"), "error_datasource");
  }

  /**
   * Tests what happens when the county entered is invalid
   *
   * @throws IOException
   */
  @Test
  public void testInvalidCounty() throws IOException {
    HttpURLConnection connection = tryRequest("state=California&county=Yo+Mama");
    assertEquals(200, connection.getResponseCode());
    Map<String, String> responseBody =
        this.adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals(responseBody.size(), 1);
    assertEquals(responseBody.get("result"), "error_datasource");
  }

  /**
   * This tests what happens when the state parameter isn't included correctly
   *
   * @throws IOException
   */
  @Test
  public void testIncorrectStateParameter() throws IOException {
    HttpURLConnection connection = tryRequest("sates=California&county=Yo+Mama");
    assertEquals(200, connection.getResponseCode());
    Map<String, String> responseBody =
        this.adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals(responseBody.size(), 1);
    assertEquals(
        responseBody.get("result"), "error_bad_request: make sure that you have a state parameter");
  }

  /**
   * Tests when the county parameter isn't included correctly
   *
   * @throws IOException
   */
  @Test
  public void testIncorrectCountyParameter() throws IOException {
    HttpURLConnection connection = tryRequest("state=California&countly=Yo+Mama");
    assertEquals(200, connection.getResponseCode());
    Map<String, String> responseBody =
        this.adapter.fromJson(new Buffer().readFrom(connection.getInputStream()));
    assertEquals(responseBody.size(), 1);
    assertEquals(
        responseBody.get("result"), "error_bad_request: make sure to have a county parameter");
  }
}
