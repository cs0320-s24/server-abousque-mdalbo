package edu.brown.cs.student.main.server.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CensusAPIUtilities {
  /**
   * We can use this helper method for deserializing both county to numbers and state to numbers.
   *
   * @param jsonStateNums
   * @return
   */
  public static Map<String, Object> deserializeNums(String jsonStateNums) {
    try {
      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      System.out.println(jsonStateNums);
      Map<String, Object> StatesToNums = adapter.fromJson(jsonStateNums);
      return StatesToNums;
    } catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, Object>();
    }
  }
}
