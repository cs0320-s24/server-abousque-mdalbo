package edu.brown.cs.student.main.server;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class CensusHandler implements Route {
    CensusHandler(){
        Map<String,Object> stateToNums = null;
        try {
            stateToNums = queryStateNumbers();
        } catch(IOException e){
            System.out.println("boooo");
        } catch(URISyntaxException e) {
            System.out.println("syntax exception");
        } catch (InterruptedException e){
            System.out.println("INTERRUPTION!");
        }
        System.out.println(stateToNums.get("Rhode Island"));
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        //link to get all state codes:https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
        String state = request.queryParams("state");
        String county = request.queryParams("county");
        Map<String, Object> responseMap = new HashMap<>();
        return null;
    }
    public Map<String,Object> queryStateNumbers() throws URISyntaxException, IOException,InterruptedException {
        HttpRequest getStateNums = HttpRequest.newBuilder()
                .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
                .GET()
                .build();
        HttpResponse<String> stateNumsResponse = HttpClient.newBuilder().build()
                .send(getStateNums, HttpResponse.BodyHandlers.ofString());
        return CensusAPIUtilities.deserializeNums(stateNumsResponse.body());
    }
}
