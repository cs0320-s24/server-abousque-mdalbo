package edu.brown.cs.student.main.server.acs;

import java.util.HashMap;
import java.util.Map;

/** A proxy class wrapping an AcsDatasource in a cache. */
public class CachedAcsAPI implements AcsDatasource {

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param state the State of interest
   * @param county the County of interest in state
   * @return a response Map describing the results of this query
   */
  public Map<String, Object> queryBroadband(String state, String county) {
    // TODO: implement
    return new HashMap<String, Object>();
  }
}
