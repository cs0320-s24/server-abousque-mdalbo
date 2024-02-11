package edu.brown.cs.student.main.server.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensusAPIUtilities {
  /**
   * We can use this helper method for deserializing both county to numbers and state to numbers.
   *
   * @param jsonStateNums
   * @return
   */
  public static Map<String, Integer> deserializeNums(String jsonStateNums) {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class);
      List<List<String>> statesToNums = adapter.fromJson(jsonStateNums);
      Map<String,Integer> map = new HashMap();
      for (int i =1;i<statesToNums.size();i++) {
        map.put(statesToNums.get(i).get(0),Integer.valueOf(statesToNums.get(i).get(1)));
      }
      return map;
    } catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, Integer>();
    }
  }
}
