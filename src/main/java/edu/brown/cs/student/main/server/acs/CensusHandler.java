package edu.brown.cs.student.main.server.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler for the /broadband endpoint. */
public class CensusHandler implements Route {
  private Map<String, String> stateToNums;
  private final AcsDatasource datasource;

  /**
   * Constructor for the CensusHandler class.
   *
   * @param acsDatasource the datasource to use to retrieve ACS data from
   * @throws URISyntaxException if unexpected error in setup
   * @throws IOException if unexpected error in setup
   * @throws InterruptedException if unexpected error in setup
   */
  public CensusHandler(AcsDatasource acsDatasource)
      throws IOException, URISyntaxException, InterruptedException {
    this.datasource = new CachedAcsApi(acsDatasource, 200, 4);
    this.stateToNums = this.queryStateNumbers();
  }

  /**
   * The handle method is called every time a request is sent to broadband. It gets the state
   * number, than county number and finally looks up the broadband use.
   *
   * @param request the Request of the user
   * @param response the Response to the request, unused in this implementation
   * @return a serialized json describing the results of executing request
   */
  @Override
  public Object handle(Request request, Response response) {
    // link to get all state codes:https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> errorMap = new HashMap<>();
    errorMap.put("endpoint", "broadband");
    String state = request.queryParams("state");
    errorMap.put("state", state);
    if (state == null) {
      errorMap.put("result", "error_bad_request: Make sure that you have a state parameter.");
      return adapter.toJson(errorMap);
    }
    try {
      state = URLDecoder.decode(state, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      errorMap.put("result", "error_bad_request");
      return adapter.toJson(errorMap);
    }
    Map<String, String> countyToInt;
    String stateCode = this.stateToNums.get(state);
    try {
      countyToInt = this.queryCountyNumbers(stateCode);
    } catch (IOException e) {
      errorMap.put("result", "error_datasource");
      return adapter.toJson(errorMap);
    } catch (URISyntaxException e) {
      errorMap.put("result", "error_bad_request");
      return adapter.toJson(errorMap);
    } catch (InterruptedException e) {
      errorMap.put("result", "error_datasource");
      return adapter.toJson(errorMap);
    }
    String county = request.queryParams("county");
    errorMap.put("county", county);
    if (county == null) {
      errorMap.put("result", "error_bad_request: Make sure to have a county parameter.");
      return adapter.toJson(errorMap);
    }
    try {
      county = URLDecoder.decode(county, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      errorMap.put("result", "error_bad_request");
      return adapter.toJson(errorMap);
    }
    String countyCode = countyToInt.get(county + ", " + state);
    if (countyCode == null) {
      errorMap.put("result", "error_datasource");
      return adapter.toJson(errorMap);
    }
    Map<String, Object> responseMap = this.datasource.queryBroadband(stateCode, countyCode);
    responseMap.put("endpoint", "broadband");
    responseMap.put("County", county);
    responseMap.put("State", state);
    if (responseMap.containsKey("result")) {
      return adapter.toJson(responseMap);
    }
    responseMap.put("result", "success");

    return adapter.toJson(responseMap);
  }

  /**
   * The queryStateNumbers method is called in the CensusHandler constructor. It returns a map
   * between all the states and their corresponding numbers.
   *
   * @return a Map from each state name to their corresponding ACS state number
   * @throws URISyntaxException if census API called with bad syntax
   * @throws IOException if unable to connect to census API
   * @throws InterruptedException if a thread is unexpectedly interrupted
   */
  public Map<String, String> queryStateNumbers()
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest getStateNums =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();
    HttpResponse<String> stateNumsResponse =
        HttpClient.newBuilder().build().send(getStateNums, HttpResponse.BodyHandlers.ofString());
    return CensusApiUtilities.deserializeStates(stateNumsResponse.body());
  }

  /**
   * This method takes in the state number and then queries the census.gov api to get the codes of
   * the counties in that state. It returns a map of the county names to their corresponding
   * numbers.
   *
   * @param state the String query representing the ACS state number of the State of interest
   * @return a Map of the county names (Strings) to their corresponding ACS county number
   * @throws URISyntaxException if census API called with bad syntax
   * @throws IOException if unable to connect to census API
   * @throws InterruptedException if a thread is unexpectedly interrupted
   */
  public Map<String, String> queryCountyNumbers(String state)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest getCountyNums =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                        + state))
            .GET()
            .build();
    HttpResponse<String> countyNumsResponse =
        HttpClient.newBuilder().build().send(getCountyNums, HttpResponse.BodyHandlers.ofString());
    return CensusApiUtilities.deserializeCounties(countyNumsResponse.body());
  }
}
