package edu.brown.cs.student.main.server;

import spark.Request;

import java.util.HashMap;
import java.util.Map;

public class CsvHandler {
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
     * Sterilizes exception due to bad request to return as json.
     *
     * @param request, the Request that caused the exception
     * @param exn the Exception to report back to API caller
     * @return the sterilized Object version of exn
     */
    protected Object sterilizeBadRequestError(Request request, Exception exn) {
        Map<String, Object> response = new HashMap<>();
        response.put("request", request.body());
        response.put("result", "error_bad_request");
        response.put("message", exn.getMessage());
        return response;
    }

    /**
     * Sterilizes exception due to invalid data source to return as json.
     *
     * @param request, the Request that caused the exception
     * @param exn the Exception to report back to API caller
     * @return the sterilized Object version of exn
     */
    protected Object sterilizeErrorDatasource(Request request, Exception exn) {
        Map<String, Object> response = new HashMap<>();
        response.put("request", request.body());
        response.put("result", "error_datasource");
        response.put("message", exn.getMessage());
        return response;
    }
}
