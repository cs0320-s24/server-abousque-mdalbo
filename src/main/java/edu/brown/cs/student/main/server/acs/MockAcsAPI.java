package edu.brown.cs.student.main.server.acs;

import java.util.HashMap;
import java.util.Map;

/**
 * MockAcsApi is used to mimic the behaviour of the regular Census APis, but without actually
 * sending requests.
 */
public class MockAcsAPI implements AcsDatasource {

  private Map<String, Object> toReturn;
  public MockAcsAPI(Map<String, Object> toReturn) {
    this.toReturn = toReturn;
  }

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param stateCode the State of interest
   * @param countyCode the County of interest in state
   * @return a response Map describing the results of this query
   */
  public Map<String, Object> queryBroadband(String stateCode, String countyCode) {
    return this.toReturn;
  }
}
