package edu.brown.cs.student.main.server.acs;

import java.util.HashMap;
import java.util.Map;

public class MockAcsAPI implements AcsDatasource {

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param stateCode the State of interest
   * @param countyCode the County of interest in state
   * @return a response Map describing the results of this query
   */
  public Map<String, Object> queryBroadband(String stateCode, String countyCode) {
    // TODO: implement
    return new HashMap<String, Object>();
  }
}
