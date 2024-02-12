package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import edu.brown.cs.student.main.server.csv.SearchCsvHandler;
import edu.brown.cs.student.main.server.csv.ViewCsvHandler;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import spark.Spark;

/** A class for running CSV searcher and ACS APIs. */
public class Server {
  static final int port = 3232;
  private final Searcher csvSearcher = new Searcher();

  /**
   * Constructor for the Server class.
   */
  // TODO: Server should take an AcsDataSource for mocking purposes in testing
  public Server() {
    Spark.port(this.port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // TODO: Should take AcsDatasource as arg to use for calling API
    Spark.get("/broadband", new CensusHandler());

    // E.g., http://localhost:3232/loadcsv?filepath=String&headersIncluded=boolean
    // TODO: somehow add instructions that filepath should be path from data folder?
    Spark.get("/loadcsv", new LoadCsvHandler(this.csvSearcher));

    // E.g., http://localhost:3232/viewcsv
    Spark.get("/viewcsv", new ViewCsvHandler(this.csvSearcher));

    /* E.g., http://localhost:3232/searchcsv?target=String
            http://localhost:3232/searchcsv?target=String&columnOfInterest=String
            http://localhost:3232/searchcsv?target=String&columnOfInterest=num
    */
    Spark.get("/searchcsv", new SearchCsvHandler(this.csvSearcher));

    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    Server server = new Server();
    System.out.println("Server started at http://localhost:" + port);
  }
}
