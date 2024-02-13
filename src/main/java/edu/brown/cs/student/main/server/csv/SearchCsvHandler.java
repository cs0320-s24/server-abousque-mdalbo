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

/** Handler for the searchcsv endpoint. */
public class SearchCsvHandler extends CsvHandler implements Route {
  private Searcher csvSearcher;

  /**
   * Constructor for the LoadCsvHandler class.
   *
   * @param csvSearcher a reference to where the csv file should be loaded into via a Searcher
   */
  public SearchCsvHandler(Searcher csvSearcher) {
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
    String target;
    String columnOfInterest;
    List<List<String>> searchResults;

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("endpoint", "searchcsv");

    if (!this.csvSearcher.isInitialized()) {
      responseMap.put("result", "error_bad_json");
      responseMap.put(
          "error message", "Attempted to searchcsv before loading in a csv with loadcsv.");
    } else {
      target = request.queryParams("target");
      responseMap.put("target", target);
      columnOfInterest = request.queryParams("columnOfInterest");
      responseMap.put("columnOfInterest", columnOfInterest);
      try {
        if (columnOfInterest == null) {
          searchResults = this.csvSearcher.search(target);
        } else {
          searchResults = this.csvSearcher.search(target, columnOfInterest);
        }
      } catch (IllegalArgumentException | IndexOutOfBoundsException exn) {
        return adapter.toJson(super.mapBadRequestError(responseMap, exn));
      }
      responseMap.put("data", searchResults);
      responseMap.put("result", "success");
    }
    return adapter.toJson(responseMap);
  }
}
