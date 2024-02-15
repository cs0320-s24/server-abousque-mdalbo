package edu.brown.cs.student.main.server.acs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to query the Census (ACS) API. This should be used for querying the Census (ACS) API when
 * no caching of results is desired.
 */
public class CensusAPI implements AcsDatasource {

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param stateCode the ACS code of the State of interest
   * @param countyCode the ACS code of the County of interest in state
   * @return a response Map describing the results of this query
   */
  public Map<String, Object> queryBroadband(String stateCode, String countyCode) {
    String stats = new String();
    try {
      stats = queryCountyStats(stateCode, countyCode);
    } catch (Exception e) {
      Map<String,Object > errorMap = new HashMap<>();
      errorMap.put("result", "error_datasource: no available data for that county");
      return errorMap;
    }
    Map<String, Object> responseMap = new HashMap<>();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    responseMap.put("date accessed", dtf.format(now));
    responseMap.put("Broadband Use", stats);
    return responseMap;
  }

  /**
   * Queries the percentage broadband usage for a specific state and county in the US.
   *
   * @param state the ACS state code for the state of interest
   * @param county the ACS county code for the county of interest in state
   * @return a String with the County Broadband usage
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  private String queryCountyStats(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest getCountyInfo =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                        + county
                        + "&in=state:"
                        + state))
            .GET()
            .build();
    HttpResponse<String> countyInfoResponse =
        HttpClient.newBuilder().build().send(getCountyInfo, HttpResponse.BodyHandlers.ofString());
    return CensusAPIUtilities.deserializeBroadband(countyInfoResponse.body());
  }
}
