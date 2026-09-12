package com.sawmik.elastic_search.controller.product;

import com.sawmik.elastic_search.service.product.ProductIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products/admin")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductIndexService productIndexService;

    @PostMapping("/index/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createIndex(@PathVariable String name) {
        productIndexService.createIndex(name);
        return ResponseEntity.ok(Map.of("message", "Index " + name + " created"));
    }

    @GetMapping("/index/{name}/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> indexExists(@PathVariable String name) {
        return ResponseEntity.ok(Map.of("exists", productIndexService.indexExists(name)));
    }

    @DeleteMapping("/index/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteIndex(@PathVariable String name) {
        productIndexService.deleteIndex(name);
        return ResponseEntity.ok(Map.of("message", "Index " + name + " deleted"));
    }

    @PostMapping("/index/{name}/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> refreshIndex(@PathVariable String name) {
        productIndexService.refreshIndex(name);
        return ResponseEntity.ok(Map.of("message", "Index " + name + " refreshed"));
    }
}
