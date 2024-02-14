package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.acs.AcsDatasource;
import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.acs.MockAcsAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * A class for testing functionality of CensusHandler class and possible inputs
 */
public class TestCensusHandler {
    private CensusHandler censusHandler;
    private AcsDatasource AcsAPI;
    @BeforeEach
    private void setup() {
        this.AcsAPI = new MockAcsAPI();
        this.censusHandler = new CensusHandler(this.AcsAPI);
        Spark.get("/broadband",this.censusHandler);
        Spark.awaitInitialization();
    }
    @AfterEach
    private void tearDown() {
        Spark.unmap("/broadband");
        Spark.awaitStop();
    }
    @Test
    private void testValidStateAndCounty() {

    }

    /**
     * This test checks
     */
    @Test
    private void testCountyWithNoData() {

    }
}
