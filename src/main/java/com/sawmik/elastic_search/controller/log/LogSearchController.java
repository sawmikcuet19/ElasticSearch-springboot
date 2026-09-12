package com.sawmik.elastic_search.controller.log;

import com.sawmik.elastic_search.entity.LogEntry;
import com.sawmik.elastic_search.service.log.LogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogSearchController {

    private final LogSearchService logSearchService;

    @GetMapping("/search/bool")
    public ResponseEntity<SearchHits<LogEntry>> searchBool(
            @RequestParam String level,
            @RequestParam String service,
            @RequestParam(defaultValue = "400") int minResponseCode) {
        return ResponseEntity.ok(logSearchService.searchWithBoolQuery(level, service, minResponseCode));
    }

    @GetMapping("/aggregations")
    public ResponseEntity<SearchHits<LogEntry>> searchAggregations() {
        return ResponseEntity.ok(logSearchService.searchWithAggregations());
    }

    @GetMapping("/aggregations/histogram")
    public ResponseEntity<SearchHits<LogEntry>> searchHistogram() {
        return ResponseEntity.ok(logSearchService.searchByDateHistogram());
    }
}
