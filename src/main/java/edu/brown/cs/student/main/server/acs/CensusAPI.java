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

public class CensusAPI implements AcsDatasource {

    public Map<String,Object> queryBroadband(String stateCode, String countyCode, String state, String county) {
        String stats = new String();
        try {
            stats = queryCountyStats(stateCode, countyCode);
        } catch (Exception e) {
            System.out.println("error querying county stats");
            return null;
        }
        Map<String,Object> responseMap = new HashMap<String,Object>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        responseMap.put("date accessed", dtf.format(now));
        responseMap.put("County", county);
        responseMap.put("State", state);
        responseMap.put("Broadband Use", stats);
        return responseMap;
    }
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
