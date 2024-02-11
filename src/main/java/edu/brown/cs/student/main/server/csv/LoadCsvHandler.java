package edu.brown.cs.student.main.server.csv;

import edu.brown.cs.student.main.server.csv.parsing.FactoryFailureException;
import edu.brown.cs.student.main.server.csv.searching.Searcher;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;

public class LoadCsvHandler extends CsvHandler implements Route {
    private Searcher csvSearcher;

    /**
     * Constructor for LoadCsvHandler class
     *
     * Doesn't need to do anything? Not sure how to return errors from here w/o throwing...
     */
    public LoadCsvHandler() {
    }

    /*
    "error_bad_json" if the request was ill-formed;
    "error_bad_request" if the request was missing a needed field, or the field was ill-formed; and
    "error_datasource" if the given data source wasn't accessible (e.g., the file didn't exist or the ACS API returned an error for a given location).
     */


    @Override
    public Object handle(Request request, Response response) {
        String filepath;
        boolean headersIncluded;
        try {
            filepath = request.queryParams("filepath");
            headersIncluded = super.parseBoolean(request.queryParams("headersIncluded"));
            this.csvSearcher = new Searcher(filepath, headersIncluded);
        } catch (IllegalArgumentException iaExn) {
            if (iaExn.getMessage().equals("Invalid CSV path. Please try again with a valid path from ./data/")) {
                return super.sterilizeErrorDatasource(request, iaExn);
            } else {
                return super.sterilizeBadRequestError(request, iaExn);
            }
        } catch (IOException | FactoryFailureException exn) {
            return super.sterilizeErrorDatasource(request, exn);
        }

        // TODO return actual json
        return new Object();
    }

}
