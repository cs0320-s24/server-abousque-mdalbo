package edu.brown.cs.student.main.server.acs;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A proxy class wrapping an AcsDatasource in a cache. This should be used for querying the Census
 * (ACS) API with configurable cache.
 */
public class CachedAcsApi implements AcsDatasource {

  private final AcsDatasource wrappedApi;
  private final LoadingCache<String, Map<String, Object>> cache;

  /**
   * Proxy class: wrap an instance of Searcher (of any kind) and cache its results.
   *
   * @param toWrap the CensusAPI to wrap
   * @param maxSize the maximum number of queries that should be kept in the cache at a time
   * @param minutesKept the maximum time in minutes that a query should be kept in the cache
   */
  public CachedAcsApi(AcsDatasource toWrap, int maxSize, int minutesKept) {
    this.wrappedApi = toWrap;
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(minutesKept, TimeUnit.MINUTES)
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                new CacheLoader<>() {
                  /**
                   * How to load data into the cache from the ACS datasource.
                   *
                   * @param codes a String formatted "{countyCode},{stateCode}"
                   * @return a Map describing the query results
                   */
                  @Override
                  public Map<String, Object> load(String codes) {
                    String[] codesList = codes.split(",");
                    String countyCode = codesList[0];
                    String stateCode = codesList[1];
                    // If this isn't yet present in the cache, load it:
                    return wrappedApi.queryBroadband(stateCode, countyCode);
                  }
                });
  }

  /**
   * Queries the ACS broadband data for a specific state and county.
   *
   * @param stateCode the ACS code of the State of interest
   * @param countyCode the ACS code of the County of interest in state
   * @return a response Map describing the results of this query
   */
  public Map<String, Object> queryBroadband(String stateCode, String countyCode) {
    Map<String, Object> result = cache.getUnchecked(countyCode + "," + stateCode);
    return result;
  }
}
