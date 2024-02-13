package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.acs.AcsDatasource;
import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.acs.MockAcsAPI;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

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
}
