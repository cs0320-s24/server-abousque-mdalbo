package edu.brown.cs.student.main.server;

import spark.Spark;

import static spark.Spark.after;

public class Server {
    public static void main(String[] args) {
        int port = 3232;
        Spark.port(port);
        after(
                (request, response) -> {
                    response.header("Access-Control-Allow-Origin", "*");
                    response.header("Access-Control-Allow-Methods", "*");
                });
    }
}
