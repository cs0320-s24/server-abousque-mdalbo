package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.csv.LoadCsvHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import spark.Spark;

/** A class for unit testing of the functionality of the LoadCsvHandler class. */
public class TestLoadCsvHandler {
    private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    private JsonAdapter<Map<String, Object>> adapter;

    @BeforeEach
    public void setup() {
        // Re-initialize parser, state, etc. for every test method

        // TODO: Use *MOCKED* data when in this test environment.

        Spark.get("/loadcsv", new LoadCsvHandler());
        Spark.awaitInitialization();

        Moshi moshi = new Moshi.Builder().build();
        adapter = moshi.adapter(mapStringObject);
    }

    @AfterEach
    public void tearDown() {
        Spark.unmap("/loadcsv");
        Spark.awaitStop();
    }

    /**
     * Helper to start a connection to a specific API endpoint/params
     * Adapted from February 8 cs32 livecode.
     *
     * The "throws" clause doesn't matter below -- JUnit will fail if an
     *     exception is thrown that hasn't been declared as a parameter to @Test.
     *
     * @param apiCall the call string, including endpoint
     *                (Note: this would be better if it had more structure!)
     * @return the connection for the given URL, just after connecting
     * @throws IOException if the connection fails for some reason
     */
    private HttpURLConnection tryRequest(String apiCall) throws IOException {
        // Configure the connection (but don't actually send a request yet)
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        // The request body contains a Json object
        clientConnection.setRequestProperty("Content-Type", "application/json");
        // We're expecting a Json object in the response body
        clientConnection.setRequestProperty("Accept", "application/json");

        clientConnection.connect();
        return clientConnection;
    }

  // http://localhost:3232/loadcsv (should error)

  // http://localhost:3232/loadcsv?filepath=empty.csv&headersIncluded=true

  // http://localhost:3232/loadcsv?filepath=RI_City_Town.csv&headersIncluded=true

}
