package edu.brown.cs.student.main.server.csv;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.csv.parsing.FactoryFailureException;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCsvHandler extends CsvHandler implements Route {

  /** Constructor for LoadCsvHandler class */
  public LoadCsvHandler() {
    // TODO: clean up (is this needed?)
    // could call super() constructor but super doesn't have one anyway so maybe this is unnecessary
  }

  /**
   * Interprets and executes user request.
   *
   * @param request the Request of the user
   * @param response the Response to the request // TODO ???? unused?
   * @return a serialized json describing the results of executing request
   */
  @Override
  public Object handle(Request request, Response response) {
    String filepath;
    boolean headersIncluded;

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    try {
      filepath = request.queryParams("filepath");
      headersIncluded = super.parseBoolean(request.queryParams("headersIncluded"));
      super.csvSearcher = new Searcher(filepath, headersIncluded);
    } catch (IllegalArgumentException iaExn) {
      if (iaExn
          .getMessage()
          .equals("Invalid CSV path. Please try again with a valid path from ./data/")) {
        // specific IllegalArgumentException stemming from nonexistent csv data source
        return adapter.toJson(super.mapErrorDatasource(request, iaExn));
      } else {
        // rest of IllegalArgumentExceptions thrown are due to a bad request
        return adapter.toJson(super.mapBadRequestError(request, iaExn));
      }
    } catch (IOException | FactoryFailureException exn) {
      // occurs when csv can be found but can't be parsed -> bad request
      return adapter.toJson(super.mapErrorDatasource(request, exn));
    }

    Map<String, Object> successResponseMap = new HashMap<>();
    successResponseMap.put("request", request.queryString());
    successResponseMap.put("result", "success");
    return adapter.toJson(successResponseMap);
  }
}
