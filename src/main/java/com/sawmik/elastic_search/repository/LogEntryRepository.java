package com.sawmik.elastic_search.repository;

import com.sawmik.elastic_search.entity.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogEntryRepository extends ElasticsearchRepository<LogEntry, String> {

    // Derived query methods
    List<LogEntry> findByLevel(String level);

    List<LogEntry> findByService(String service);

    List<LogEntry> findByLevelAndService(String level, String service);

    List<LogEntry> findByResponseCode(int responseCode);

    List<LogEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    Page<LogEntry> findByLevel(String level, Pageable pageable);

    long countByLevel(String level);

    long countByService(String service);

    long countByResponseCode(int responseCode);

    List<LogEntry> findByTraceId(String traceId);

    // Custom Elasticsearch queries
    @Query("""
        {
          "bool": {
            "must": [
              { "match": { "message": "?0" } }
            ]
          }
        }
    """)
    List<LogEntry> searchByMessage(String query);

    @Query("""
        {
          "bool": {
            "must": [
              { "term": { "level": "?0" } }
            ],
            "filter": [
              { "range": { "timestamp": { "gte": "?1", "lte": "?2" } } }
            ]
          }
        }
    """)
    List<LogEntry> findByLevelAndTimestampRange(String level, String start, String end);

    @Query("""
        {
          "bool": {
            "must": [
              { "range": { "responseCode": { "gte": 400 } } }
            ]
          }
        }
    """)
    List<LogEntry> findErrorResponses();

    @Query("""
        {
          "bool": {
            "must": [
              { "range": { "durationMs": { "gte": ?0 } } }
            ]
          }
        }
    """)
    List<LogEntry> findSlowRequests(long minDurationMs);

    @Query("""
        {
          "bool": {
            "must": [
              { "match_phrase": { "message": "?0" } }
            ]
          }
        }
    """)
    List<LogEntry> findByExactMessagePhrase(String phrase);
}
