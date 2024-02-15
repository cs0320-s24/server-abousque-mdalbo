package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.acs.AcsDatasource;
import edu.brown.cs.student.main.server.acs.CensusAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

/**
 * These tests are unit tests for some of the needed functionality fo the APIDatasource. There are two different results
 * that can happen from inputting the county and state, given we've already verified that the county is in the state and
 * has a number: either we have the broadband data usage or the data isn't stored. I test each outcome.
 */
public class TestAcsDatasource {
    /**
     * Tests what happens when the inputted county and state are valid
     */
    @Test
    public void testValidCountyAndState() {
        AcsDatasource ds = new CensusAPI();
        Map<String,Object> map = ds.queryBroadband("06","025");
        assertEquals(map.size(),2);
        assertTrue(map.containsKey("date accessed"));
        assertEquals(map.get("Broadband Use"), "87.8");
    }

    /**
     * Tests when there is no available data for a county
     */
    @Test
    public void testNoAvailableData() {
        AcsDatasource ds = new CensusAPI();
        Map<String,Object> map = ds.queryBroadband("06","021");
        System.out.println(map);
        assertEquals(map.size(),1);
    }
}
