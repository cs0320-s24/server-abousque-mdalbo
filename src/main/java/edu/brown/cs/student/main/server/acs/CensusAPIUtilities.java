package edu.brown.cs.student.main.server.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusAPIUtilities {
  /**
   * We can use this helper method for deserializing both county to numbers and state to numbers.
   * Need to combine these to make it better code
   *
   * @param jsonStateNums
   * @return
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

  public static Map<String, String> deserializeCounties(String jsonStateNums) {
    try {
      System.out.println("deserializing counties");
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class);
      List<List<String>> statesToNums = adapter.fromJson(jsonStateNums);
      Map<String, String> map = new HashMap();
      System.out.println(statesToNums.size());
      for (int i = 1; i < statesToNums.size(); i++) {
        map.put(statesToNums.get(i).get(0), statesToNums.get(i).get(2));
      }
      return map;
    } catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, String>();
    }
  }

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
