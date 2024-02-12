package edu.brown.cs.student.main.server.acs;

import java.util.Map;

/** An interface describing a data source for ACS (Census) data. */
public interface AcsDatasource {

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param state the State of interest
   * @param county the County of interest in state
   * @return a response Map describing the results of this query
   */
  public Map<String, Object> queryBroadband(
      String stateCode, String countyCode, String state, String county);
}
