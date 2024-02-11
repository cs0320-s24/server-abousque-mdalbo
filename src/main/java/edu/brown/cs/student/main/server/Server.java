package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.server.acs.CensusHandler;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import spark.Spark;

import static spark.Spark.after;

public class Server {
    public static void main(String[] args) {
        int port = 3232;
        Searcher csvSearcher;

        Spark.port(port);
        after(
                (request, response) -> {
                    response.header("Access-Control-Allow-Origin", "*");
                    response.header("Access-Control-Allow-Methods", "*");
                });

        // /loadcsv?filepath=String&headersIncluded=boolean
        // somehow put in instructions that filepath should be path from data folder?

        Spark.get("/broadband",new CensusHandler());
        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started at http://localhost:" + port);
    }
}
