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

/** A class to handle the loadcsv endpoint. */
public class LoadCsvHandler extends CsvHandler implements Route {

  private Searcher csvSearcher;

  /**
   * Constructor for the LoadCsvHandler class
   *
   * @param csvSearcher a reference to where the csv file should be loaded into via a Searcher
   */
  public LoadCsvHandler(Searcher csvSearcher) {
    this.csvSearcher = csvSearcher;
  }

  /**
   * Interprets and executes user request.
   *
   * @param request the Request of the user
   * @param response the Response to the request // TODO ???? unused? but also unused in livecode
   * @return a serialized json describing the results of executing request
   */
  @Override
  public Object handle(Request request, Response response) {
    String filepath;
    boolean headersIncluded;

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("endpoint", "loadcsv");

    try {
      filepath = request.queryParams("filepath");
      responseMap.put("filepath", filepath);
      String headersIncludedString = request.queryParams("headersIncluded");
      responseMap.put("headersIncluded", headersIncludedString);
      if (filepath == null | headersIncludedString == null) {
        throw new IllegalArgumentException(
            "Required argument " + filepath == null ? "filepath" : "headersIncluded" + " missing.");
      }
      headersIncluded = super.parseBoolean(headersIncludedString);

      this.csvSearcher = new Searcher(filepath, headersIncluded);
    } catch (IllegalArgumentException iaExn) {
      if (iaExn
          .getMessage()
          .equals("Invalid CSV path. Please try again with a valid path from ./data/")) {
        // specific IllegalArgumentException stemming from nonexistent csv data source
        return adapter.toJson(super.mapErrorDatasource(responseMap, iaExn));
      } else {
        // rest of IllegalArgumentExceptions thrown are due to a bad request
        return adapter.toJson(super.mapBadRequestError(responseMap, iaExn));
      }
    } catch (IOException | FactoryFailureException exn) {
      // occurs when csv can be found but can't be parsed -> bad request
      return adapter.toJson(super.mapErrorDatasource(responseMap, exn));
    }

    // success
    responseMap.put("result", "success");
    return adapter.toJson(responseMap);
  }
}
