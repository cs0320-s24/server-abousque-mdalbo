# Project Details
### Project name: Server
### Team members:
1. mdalbo
2. abousque

### Total estimated time to complete the project:
10 hrs

### Link to repo:
https://github.com/cs0320-s24/server-abousque-mdalbo

# Design Choices:

The main logic our program stems from the server class where four handlers
are created. One handles broadcom requests and the others handle requests to load, search, and
view CSV files.

All the handlers implement the route class which enforces them to have a handle method which is
used to handle requests to the server.

The CensusHandler works by first storing a map that consists of all the states and their associated
numbers used for looking things up in the ACS government API. We do this every time the server is
started. Then, once a request is sent to /broadband we find the associated state using the map
generated at the initialization of the handler. We then use this state number to get the county
to number json from the government API which we parse into a map. Once we have these two pieces of
information, we call an AcsDatasource class which sends a request to the government api given the
state number and county number. There are several different CensusApis that implement the
AcsDatasource interface and therefore define the getBroadcom method which returns the time the data
was received from the government API, and the Broadband data. The CachedAcsApi is essentially a
proxy of the CensusApi class which simply wraps the CensusApi class and checks whether the query is
included in the cache, if not it sends a request to the census government server to get this data.
We send all the requests to the census government api as an http request,and the returned data is
in the json format. The census api utilities class takes care of turning this data and putting it
into maps for use by our program. We then send the data back to the user as a json file that
includes the state, county, date accessed, and Broadband.

CensusHandler was chosen to take an instance of a AcsDatasource in its constructor to allow us to
keep the handler functionality separate from the datasource. Ultimately, for this project
that meant that we could use the CensusHandler class to query from the non-cached CensusApi class,
CachedAcsApi class, or MockAcsApi class. We created the AcsDatasource interface with the sole
required method queryBroadband to abstract the functionality within the Server from the specific
way that broadband would be queried (e.g., for testing vs. real user calls).

We used the MockAcsApi class with the CensusHandler for unit testing the handler. The MockAcsApi
constructor takes a Map<String, Object> object toReturn which allows the user (testing) to
specify whatever constant result they would like to see in a successful example query.

We use CachedAcsApi wrapped around CensusApi in our Server to enable cached queries from the
real ACS API. We chose to make these 2 separate classes with User Story 3 in mind to allow a
backend developer wanting to query the ACS API to decide if they wanted caching at all, and if so
then they would have the ability to specify caching settings of how many results to keep and for how
long in constructor arguments depending on their use case. The user of the CachedAcsApi can use our
CensusApi while setting the cache time and amount of storage limits.

Our CachedAcsApi uses google's guava library as opposed to a homemade Map cache for both time and
space optimization.

The LoadCsvHandler, ViewCsvHandler, and SearchCsvHandler classes all implement Route as stated above.
They also each extend the abstract CsvHandler class, which was designed to allow the different 
csv-related handlers to share some helper methods in a clean and hopefully easily interpretable way.
LoadCsvHandler is associated with the "/loadcsv" endpoint, whereas ViewCsvHandler is associated with
the "/viewcsv" endpoint, and SearchCsvHandler is associated with the "searchcsv" endpoint. Their 3
constructors each take a Searcher csvSearcher reference to allow the three endpoints to have a
shared state (the object associated with the CSV contents of interest); this was decided to make
it possible to load in a CSV with loadcsv and then view or search that same CSV using the
other endpoints while maintaining the specification of one handler class per endpoint. 
LoadCsvHandler creates an instance of a Searcher from the Searcher class, whereas ViewCsvHandler
uses primarily the getFullCsv method from the Searcher class, and SearchCsvHandler uses primarily
the two overloaded search methods from the Searcher class.

The Searcher class wraps around a CsvParser from that class to enable searching of a parsed CSV from
its filepath. The details of these classes was covered in the CSV sprint and specifically
@abousque's README, so we will omit that level of detail here.

# Errors/Bugs
There are no remaining known bugs in our program.

# Tests
We have several test suites which each test different pieces of functionality in our program:
- **TestAcsDatasource**: Unit tests for verifying functionality of AcsDatasources (including cached); this test suite covers User Story 3 applications as well
- **TestCensusHandler**: Integration tests for the /broadband endpoint using mocked data (MockAcsApi class as AcsDatasource)
- **TestLoadCsvHandler**: Integration tests for the /loadcsv endpoint
- **TestViewCsvHandler**: Integration tests for the /viewcsv endpoint, both before and after calling the other csv-related endpoints
- **TestSearchCsvHandler**: Integration tests for the /searchcsv endpoint, both before and after calling the other csv-related endpoints
- **TestCsvParser**: Unit tests for verifying functionality of CsvParser class and its main parse method (from CSV sprint)
- **TestSearcher**: Unit tests for verifying functionality of Searcher class (from CSV sprint)
Please see javadocs for the individual tests in these suites to see how each test ensures a specific part of the program works as intended.

# How To:
To compile and run our **unit and integration tests**, run either:
- mvn package
- mvn test

Note that some of these tests involve testing caching and therefore require wait statements. Expect running the test suite via either of these prompts to take about 3.5 minutes start to finish.

To compile and run our **server** (for use in User Stories 1-2), run the following in a bash terminal:
1. mvn package
2. ./run

Then, follow the instructions printed to the terminal. To end the program, use CTRL-C.