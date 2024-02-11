// package edu.brown.cs.student.main.server.csv;
//
// import edu.brown.cs.student.main.server.csv.parsing.FactoryFailureException;
// import
// edu.brown.cs.student.main.server.csv.parsing.examplecreatorfromrows.StringListRowConverter;
// import edu.brown.cs.student.main.server.csv.searching.Searcher;
//
// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.util.List;
//
/// ** The Main class of our project. This is where search execution begins. */
// public final class Main {
//
//  /**
//   * The initial method called when execution begins.
//   *
//   * @param args An array of command line arguments
//   */
//  public static void main(String[] args) {
//    new Main().run();
//  }
//
//  private Main() {}
//
//  /** Main method for running the searching described by User Story 1. */
//  private void run() {
//    final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
//    final StringListRowConverter rowConverter = new StringListRowConverter();
//
//    // Get user input for CSV data
//    String filepath;
//    String rawHeadersIncluded;
//    boolean headersIncluded;
//    try {
//      System.out.println("Welcome to CSV-searcher!");
//      System.out.println(
//          "\nTo begin, please enter the valid path from ./data/"
//              + " to the CSV file you would like to search:");
//      System.out.print("Path: ./data/");
//      filepath = inputReader.readLine();
//
//      System.out.println("\nDoes this CSV file contain a header row?");
//      System.out.print("(yes/no): ");
//      rawHeadersIncluded = inputReader.readLine();
//      if (rawHeadersIncluded.equalsIgnoreCase("yes")) {
//        headersIncluded = true;
//      } else if (rawHeadersIncluded.equalsIgnoreCase("no")) {
//        headersIncluded = false;
//      } else {
//        System.out.println("Provided invalid input. Please provide either \"yes\" or \"no\"");
//        return;
//      }
//    } catch (IOException exn) {
//      System.err.println("Error: Error accepting user input. Please try again.");
//      return;
//    }
//    Searcher searcher;
//    try {
//      searcher = new Searcher(filepath, headersIncluded);
//    } catch (IllegalArgumentException iaExn) {
//      System.err.println(iaExn.getMessage());
//      return;
//    } catch (IOException | FactoryFailureException otherExn) {
//      System.err.println("Error: " + otherExn.getMessage());
//      return;
//    }
//
//    System.out.println("Now, you can begin searching the CSV:");
//    while (true) {
//      System.out.println(
//          """
//              User Input Options:
//                1) To search for a value in specific column: target, column*
//                  *can be: (a) column index (e.g. 0 for leftmost column, 1 for next leftmost, etc)
//                           (b) column header
//                2) To search for a value in any column: target
//                3) To quit: QUIT
//              Note: Searches are not case sensitive, but will be an exact match otherwise.
//              """);
//
//      // READ
//      String rawInput = "";
//      try {
//        System.out.print("Your command:  ");
//        rawInput = inputReader.readLine();
//      } catch (IOException exn) {
//        System.err.println("Error: Error accepting user input. Please try again.");
//      }
//
//      // EVAL & PRINT
//      List<String> parsedInput = rowConverter.create(List.of(rawInput));
//      if (parsedInput.size() == 1) {
//        String target = parsedInput.get(0);
//        if (target.equalsIgnoreCase("quit")) {
//          return;
//        } else if (target.isEmpty()) {
//          System.out.println(
//              "Wrong number of arguments provided. Please try again with a valid command.");
//        } else {
//          System.out.println("Results of searching all columns for \"" + target + "\":");
//          printCsvContents(searcher.search(target));
//          System.out.println("End of search results.");
//        }
//      } else if (parsedInput.size() == 2) {
//        String target = parsedInput.get(0);
//        String col = parsedInput.get(1);
//        System.out.println("Results of searching column " + col + " for \"" + target + "\":");
//        printCsvContents(searcher.search(target, col));
//        System.out.println("End of search results.");
//      } else {
//        System.out.println(
//            "Wrong number of arguments provided. Please try again with a valid command.");
//      }
//      System.out.println("---------------------------\n");
//    }
//  }
//
//  /**
//   * Prints CSV contents to terminal.
//   *
//   * @param csvContents a List of the rows of the CSV contents to format and print to terminal
//   */
//  private static void printCsvContents(List<List<String>> csvContents) {
//    for (List<String> row : csvContents) {
//      System.out.println(row);
//    }
//  }
// }
