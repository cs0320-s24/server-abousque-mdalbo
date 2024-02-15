package edu.brown.cs.student.main.server.csv;

import java.util.Map;

/** Abstract parent class for CSV endpoint handlers. */
public abstract class CsvHandler {

  /**
   * Converts a String to a boolean and throws exception if given something other than
   * case-insensitive true or false.
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
   * Adds error information for a bad request to response map.
   *
   * @param response the response Map to add error information to
   * @param exn the Exception to report back to API caller
   * @return a Map describing the error
   */
  protected Map<String, Object> mapBadRequestError(Map<String, Object> response, Exception exn) {
    response.put("result", "error_bad_request: " + exn.getMessage());
    return response;
  }

  /**
   * Adds error information for an invalid data source to response map.
   *
   * @param response the response Map to add error information to
   * @param exn the Exception to report back to API caller
   * @return a Map describing the error
   */
  protected Map<String, Object> mapErrorDatasource(Map<String, Object> response, Exception exn) {
    response.put("result", "error_datasource: " + exn.getMessage());
    return response;
  }
}
