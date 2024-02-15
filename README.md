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
proxy of the CensusApi class which simply wraps the
# Errors/Bugs

# Tests
We have several test suites which each test different pieces of functionality in our program:
- TestAcsDatasource: Unit tests for verifying functionality of AcsDatasources (including cached); this test suite covers User Story 3 applications as well
- TestCensusHandler: Integration tests for the /broadband endpoint using mocked data (MockAcsApi class as AcsDatasource)
- TestLoadCsvHandler: Integration tests for the /loadcsv endpoint
- TestViewCsvHandler: Integration tests for the /viewcsv endpoint, both before and after calling the other csv-related endpoints
- TestSearchCsvHandler: Integration tests for the /searchcsv endpoint, both before and after calling the other csv-related endpoints
- TestCsvParser: Unit tests for verifying functionality of CsvParser class and its main parse method (from CSV sprint)
- TestSearcher: Unit tests for verifying functionality of Searcher class (from CSV sprint)
  Please see the javadocs for the individual tests in these suites to see how each test ensures a specific part of the program works as intended.

# How To:
To compile and run our unit and integration tests, run either:
- mvn package
- mvn test

To compile and run our server (for use in User Stories 1-2), run the following in a bash terminal:
1. mvn package
2. ./run
   Then follow the instructions printed to the terminal.