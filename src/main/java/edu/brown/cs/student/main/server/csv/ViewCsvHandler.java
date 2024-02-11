package edu.brown.cs.student.main.server.csv;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** A class to handle the viewcsv endpoint. */
public class ViewCsvHandler extends CsvHandler implements Route {

  /**
   * Interprets and executes user request.
   *
   * @param request the Request of the user
   * @param response the Response to the request // TODO ???? unused? but also unused in livecode
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

    if (CsvHandler.csvSearcher == null) {
      responseMap.put("result", "error_bad_json");
      responseMap.put("message", "Attempted to viewcsv before loading in a csv with loadcsv.");
    } else {
      responseMap.put("data", csvContentsAdapter.toJson(CsvHandler.csvSearcher.getFullCsv()));
      responseMap.put("result", "success");
    }
    return adapter.toJson(responseMap);
  }
}
