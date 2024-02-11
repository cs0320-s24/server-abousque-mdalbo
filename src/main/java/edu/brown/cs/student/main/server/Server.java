package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import spark.Spark;

import static spark.Spark.after;

public class Server {
    static final int port = 3232;

    // TODO: Eventually should take an AcsDataSource (or whatever we end up naming the interface
    //  describing the census datasource to use) for mocking purposes in testing
    public Server() {
        Spark.port(this.port);
        after(
            (request, response) -> {
                response.header("Access-Control-Allow-Origin", "*");
                response.header("Access-Control-Allow-Methods", "*");
            });

        Spark.get("/broadband", new CensusHandler());

        // /loadcsv?filepath=String&headersIncluded=boolean
        // somehow add instructions that filepath should be path from data folder?
        Spark.get("/loadcsv", new LoadCsvHandler());

        Spark.awaitInitialization();
    }

    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Server started at http://localhost:" + port);
    }
}
