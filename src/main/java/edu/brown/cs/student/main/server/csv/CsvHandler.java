package edu.brown.cs.student.main.server.csv;

import edu.brown.cs.student.main.server.csv.searching.Searcher;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

public abstract class CsvHandler {
    protected static Searcher csvSearcher;

    /**
     * Converts a String to a boolean and throws exception if given something other than case-insensitive true or false.
     *
     * @param boolString the String to convert to a boolean
     * @return the boolean version of boolString
     * @throws IllegalArgumentException if boolString is neither "true" nor "false" (case-insensitive)
     */
    protected boolean parseBoolean(String boolString) throws IllegalArgumentException {
        if (boolString.equalsIgnoreCase("true")) {
            return true;
        } else if (boolString.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException("Cannot convert '" + boolString + "' to boolean.");
        }
    }

    /**
     * Creates a Map from labels to details for describing an error due to a bad request.
     *
     * @param request the Request that caused the exception
     * @param exn the Exception to report back to API caller
     * @return a Map describing the error
     */
    protected Map<String, Object> mapBadRequestError(Request request, Exception exn) {
        Map<String, Object> response = new HashMap<>();
        response.put("request", request.body());
        response.put("result", "error_bad_request");
        response.put("message", exn.getMessage());
        return response;
    }

    /**
     * Creates a Map from labels to details for describing an error due to an invalid data source.
     *
     * @param request the Request that caused the exception
     * @param exn the Exception to report back to API caller
     * @return a Map describing the error
     */
    protected Map<String, Object> mapErrorDatasource(Request request, Exception exn) {
        Map<String, Object> response = new HashMap<>();
        response.put("request", request.body());
        response.put("result", "error_datasource");
        response.put("message", exn.getMessage());
        return response;
    }
}
