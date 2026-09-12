package com.sawmik.elastic_search.controller.log;

import com.sawmik.elastic_search.entity.LogEntry;
import com.sawmik.elastic_search.service.log.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<LogEntry> create(@RequestBody LogEntry logEntry) {
        return ResponseEntity.ok(logService.save(logEntry));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntry> getById(@PathVariable String id) {
        return logService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<LogEntry>> getAll() {
        return ResponseEntity.ok(logService.findAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAll() {
        logService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<LogEntry>> findByLevel(@PathVariable String level) {
        return ResponseEntity.ok(logService.findByLevel(level));
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<LogEntry>> findByService(@PathVariable String service) {
        return ResponseEntity.ok(logService.findByService(service));
    }

    @GetMapping("/level/{level}/page")
    public ResponseEntity<Page<LogEntry>> findByLevelPaged(
            @PathVariable String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(logService.findByLevelPaged(level, page, size));
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<List<LogEntry>> searchByMessage(@PathVariable String query) {
        return ResponseEntity.ok(logService.searchByMessage(query));
    }

    @GetMapping("/errors")
    public ResponseEntity<List<LogEntry>> findErrors() {
        return ResponseEntity.ok(logService.findErrorResponses());
    }

    @GetMapping("/slow")
    public ResponseEntity<List<LogEntry>> findSlowRequests(
            @RequestParam(defaultValue = "1000") long minDurationMs) {
        return ResponseEntity.ok(logService.findSlowRequests(minDurationMs));
    }

    @GetMapping("/search/phrase/{phrase}")
    public ResponseEntity<List<LogEntry>> findByExactPhrase(@PathVariable String phrase) {
        return ResponseEntity.ok(logService.findByExactPhrase(phrase));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LogEntry>> bulkSave(@RequestBody List<LogEntry> logs) {
        return ResponseEntity.ok(logService.bulkSave(logs));
    }
}
