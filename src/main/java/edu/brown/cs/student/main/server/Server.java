package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.acs.AcsDatasource;
import edu.brown.cs.student.main.server.acs.CensusAPI;
import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import edu.brown.cs.student.main.server.csv.SearchCsvHandler;
import edu.brown.cs.student.main.server.csv.ViewCsvHandler;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import spark.Spark;

/**
 * A class for running CSV searcher and ACS APIs. ACS API is cached in this implementation with
 * cached results held for 4 minutes.
 */
public class Server {
  static final int port = 3232;

  /**
   * Constructor for the Server class.
   *
   * @param acsDatasource the data source object to use for querying broadband data
   */
  public Server(AcsDatasource acsDatasource) {
    Spark.port(port);
    Searcher csvSearcher = new Searcher();
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("/broadband", new CensusHandler(acsDatasource));
    Spark.get("/loadcsv", new LoadCsvHandler(csvSearcher));
    Spark.get("/viewcsv", new ViewCsvHandler(csvSearcher));
    Spark.get("/searchcsv", new SearchCsvHandler(csvSearcher));

    Spark.awaitInitialization();
  }

  /**
   * The main method for running a cached server servicing the broadband, loadcsv, viewcsv, and
   * searchscv endpoints.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    new Server(new CensusAPI());
    System.out.println("Server started at http://localhost:" + port);
    System.out.println(
        """
        In a browser window:
        1. Call the loadcsv endpoint to load in a CSV:
        http://localhost:port/loadcsv?filepath=String&headersIncluded=boolean
        *Note: filepath is the filepath from ./data

        2. Call the viewcsv endpoint to view a CSV previously loaded with loadcsv:
        http://localhost:port/viewcsv

        3. Call the searchcsv endpoint to search a CSV previously loaded with loadcsv:
        http://localhost:port/searchcsv?target=String
        http://localhost:port/searchcsv?target=String&columnOfInterest=String
        http://localhost:port/searchcsv?target=String&columnOfInterest=num
        *Note: Results will reflect case-insensitive matches to target, and columnOfInterest can be
               column name if headersIncluded was true in loadcsv call or the column index.

        4. Call the broadband endpoint to query the ACS database for broadband access data:
        http://localhost:port/broadband?state=String&county=String
        *Note: States or counties with spaces in their name must use "%20" or "+" rather than " ".
        """);
  }
}
