package edu.brown.cs.student.main.server.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The censusAPIUtilities class provides useful utilities such as deserializing to deal with json
 * returns from the ACS govt API.
 */
public class CensusApiUtilities {
  /**
   * We can use this helper method for deserializing both county to numbers and state to numbers.
   * Need to combine these to make it better code.
   *
   * @param jsonStateNums the json representing the state to state number
   * @return A map from State name to state number
   */
  public static Map<String, String> deserializeStates(String jsonStateNums) {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class);
      List<List<String>> statesToNums = adapter.fromJson(jsonStateNums);
      Map<String, String> map = new HashMap();
      for (int i = 1; i < statesToNums.size(); i++) {
        map.put(statesToNums.get(i).get(0), statesToNums.get(i).get(1));
      }
      return map;
    } catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, String>();
    }
  }

  /**
   * Helper function to deserialize counties by using an adapter to convert into a list of list of
   * strings, and then looping through and putting the county, county number pairs into a map to be
   * returned.
   *
   * @param jsonStateNums pass in the json string that represents the counties to numbers
   * @return Map from county name to county number
   * @throws IOException to be caught by handler
   */
  public static Map<String, String> deserializeCounties(String jsonStateNums) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<List> adapter = moshi.adapter(List.class);
    List<List<String>> statesToNums = adapter.fromJson(jsonStateNums);
    Map<String, String> map = new HashMap();
    for (int i = 1; i < statesToNums.size(); i++) {
      map.put(statesToNums.get(i).get(0), statesToNums.get(i).get(2));
    }
    return map;
  }

  /**
   * The deserialize broadband helper method is used to take a json file and get the broadband use.
   * It returns an empty string if there is an error deserializing.
   *
   * @param jsonCountyInfo pass in the json representing the county information including the
   *     broadband use
   * @return returns a string containing the broadband usage
   */
  public static String deserializeBroadband(String jsonCountyInfo) {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class);
      List<List<String>> countyInfo = adapter.fromJson(jsonCountyInfo);
      return countyInfo.get(1).get(1);
    } catch (IOException e) {
      e.printStackTrace();
      return new String();
    }
  }
}
