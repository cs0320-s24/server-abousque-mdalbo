package edu.brown.cs.student.main.server.acs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CensusHandler implements Route {
  private Map<String, String> stateToNums;

  public CensusHandler() {
    this.stateToNums = null;
    try {
      this.stateToNums = queryStateNumbers();
    } catch (IOException e) {
      System.out.println("boooo");
    } catch (URISyntaxException e) {
      System.out.println("syntax exception");
    } catch (InterruptedException e) {
      System.out.println("INTERRUPTION!");
    }
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // link to get all state codes:https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
    System.out.println("getting here");
    String state = URLDecoder.decode(request.queryParams("state"), "UTF-8");
    System.out.println("got state:" + state);
    String stateCode = this.stateToNums.get(state);
    System.out.println("got state");
    Map<String, String> countyToInt = this.queryCountyNumbers(stateCode);
    System.out.println("got county to int map");
    String county = request.queryParams("county");
    System.out.println(county);
    String countyCode = countyToInt.get(county + ", " + state);
    System.out.println(countyCode);
    String broadbandUse = queryCountyStats(stateCode, countyCode);
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("State", state);
    responseMap.put("County", county);
    responseMap.put("Broadband Usage", broadbandUse);
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    responseMap.put("date accessed", dtf.format(now));
    return responseMap;
  }

  public Map<String, String> queryStateNumbers()
      throws URISyntaxException, IOException, InterruptedException {
    HttpRequest getStateNums =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();
    HttpResponse<String> stateNumsResponse =
        HttpClient.newBuilder().build().send(getStateNums, HttpResponse.BodyHandlers.ofString());
    return CensusAPIUtilities.deserializeStates(stateNumsResponse.body());
  }

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
    return CensusAPIUtilities.deserializeCounties(countyNumsResponse.body());
  }

  public String queryCountyStats(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {
    System.out.println(
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
            + county
            + "&in=state:"
            + state);
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
