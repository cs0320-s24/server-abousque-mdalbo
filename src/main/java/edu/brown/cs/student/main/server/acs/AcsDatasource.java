package edu.brown.cs.student.main.server.acs;

import java.util.Map;

/** An interface describing a data source for ACS (Census) data. */
public interface AcsDatasource {

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param stateCode the ACS code of the State of interest
   * @param countyCode the ACS code of the County of interest in state
   * @return a response Map describing the results of this query
   */
  Map<String, Object> queryBroadband(String stateCode, String countyCode);
}
