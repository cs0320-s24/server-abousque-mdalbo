package edu.brown.cs.student.main.server.csv;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** A class to handle the viewcsv endpoint. */
public class ViewCsvHandler extends CsvHandler implements Route {

  private Searcher csvSearcher;

  /**
   * Constructor for the LoadCsvHandler class
   *
   * @param csvSearcher a reference to where the csv file contents can be found via a Searcher
   */
  public ViewCsvHandler(Searcher csvSearcher) {
    this.csvSearcher = csvSearcher;
  }

  /**
   * Interprets and executes user request.
   *
   * @param request the Request of the user
   * @param response the Response to the request, unused in this implementation
   * @return a serialized json describing the results of executing request
   */
  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Type listListStringObject = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> csvContentsAdapter = moshi.adapter(listListStringObject);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("endpoint", "viewcsv");

    if (!this.csvSearcher.isInitialized()) {
      responseMap.put("result", "error_bad_json");
      responseMap.put(
          "error message", "Attempted to viewcsv before loading in a csv with loadcsv.");
    } else {
      responseMap.put(
          "data", postProcessJson(csvContentsAdapter.toJson(this.csvSearcher.getFullCsv())));
      responseMap.put("result", "success");
    }
    return adapter.toJson(responseMap);
  }

  /**
   * Cleans up json view of CSV by replacing any \" with a single ".
   *
   * @param original the String json that may contain \"
   * @return original with any cases of \" replaced with a single "
   */
  private String postProcessJson(String original) {
    return original.replaceAll("\\\"", "'");
  }
}
