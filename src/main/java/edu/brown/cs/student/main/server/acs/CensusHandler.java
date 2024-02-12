package edu.brown.cs.student.main.server.acs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class CensusHandler implements Route {
  private Map<String, String> stateToNums;
  private AcsDatasource datasource;

  public CensusHandler() {
    this.datasource = new CensusAPI();
    try {
      this.stateToNums = this.queryStateNumbers();
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
    String state = URLDecoder.decode(request.queryParams("state"), "UTF-8");
    String stateCode = this.stateToNums.get(state);
    Map<String, String> countyToInt = this.queryCountyNumbers(stateCode);
    String county = request.queryParams("county");
    String countyCode = countyToInt.get(county + ", " + state);
    Map<String, Object> responseMap =
        this.datasource.queryBroadband(stateCode, countyCode, state, county);
    return responseMap;
  }

  /**
   * The queryStateNumbers method is called in the CensusHandler constructor. It returns a map
   * between all the states and their corresponding numbers.
   *
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
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
    return CensusAPIUtilities.deserializeStates(stateNumsResponse.body());
  }

  /**
   * This method takes in the state number and then queries the census.gov api to get the codes of
   * the counties in that state. It returns a map of the county names to their corresponding
   * numbers.
   *
   * @param state
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
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
    return CensusAPIUtilities.deserializeCounties(countyNumsResponse.body());
  }
}
