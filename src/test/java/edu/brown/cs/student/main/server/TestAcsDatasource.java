package edu.brown.cs.student.main.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.testng.AssertJUnit.assertFalse;

import edu.brown.cs.student.main.server.acs.AcsDatasource;
import edu.brown.cs.student.main.server.acs.CachedAcsApi;
import edu.brown.cs.student.main.server.acs.CensusApi;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * These tests are unit tests for the needed functionality fo the APIDatasource. There are two
 * different results that can happen from inputting the county and state, given we've already
 * verified that the county is in the state and has a number: either we have the broadband data
 * usage or the data isn't stored. I test each outcome.
 */
public class TestAcsDatasource {
  /** Tests what happens when the inputted county and state are valid. */
  @Test
  public void testValidCountyAndState() {
    AcsDatasource ds = new CensusApi();
    Map<String, Object> map = ds.queryBroadband("06", "025");
    assertEquals(map.size(), 2);
    assertTrue(map.containsKey("date accessed"));
    assertEquals(map.get("Broadband Use"), "87.8");
  }

  /** Tests when there is no available data for a county. */
  @Test
  public void testNoAvailableData() {
    AcsDatasource ds = new CensusApi();
    Map<String, Object> map = ds.queryBroadband("06", "021");
    System.out.println(map);
    assertEquals(map.size(), 1);
  }

  /**
   * Example of User Story 3. Caches max 12 results for 2 minutes.
   *
   * <p>Tests the cache storing system which employs a proxy censusApi. It should have the same date
   * accessed time within the specified time of minutes kept, but it should be different when over
   * that time limit.
   *
   * @throws InterruptedException if a thread is unexpectedly interrupted
   */
  @Test
  public void testCacheStoring() throws InterruptedException {
    AcsDatasource ds = new CensusApi();
    AcsDatasource wrap = new CachedAcsApi(ds, 20, 1);
    Map<String, Object> firstQuery = wrap.queryBroadband("06", "025");
    Thread.sleep(500);
    Map<String, Object> secondQuery = wrap.queryBroadband("06", "025");
    assertEquals(secondQuery, firstQuery);
    Thread.sleep(70000);
    Map<String, Object> queryAfterLongPause = wrap.queryBroadband("06", "025");
    assertFalse(queryAfterLongPause.equals(firstQuery));
  }

  /**
   * Example of User Story 3. Caches max 12 results for 2 minutes.
   *
   * <p>Tests the cache storing system which employs a proxy censusApi. It should have the same date
   * accessed time within the specified time of minutes kept, but it should be different when over
   * that time limit.
   *
   * @throws InterruptedException if a thread is unexpectedly interrupted
   */
  @Test
  public void testCacheStoring2() throws InterruptedException {
    AcsDatasource ds = new CensusApi();
    AcsDatasource wrap = new CachedAcsApi(ds, 12, 2);
    Map<String, Object> firstQuery = wrap.queryBroadband("06", "025");
    Thread.sleep(60000);
    Map<String, Object> secondQuery = wrap.queryBroadband("06", "025");
    assertEquals(firstQuery, secondQuery);
    Thread.sleep(60050);
    Map<String, Object> queryAfterLongPause = wrap.queryBroadband("06", "025");
    assertFalse(queryAfterLongPause.equals(firstQuery));
  }
}
