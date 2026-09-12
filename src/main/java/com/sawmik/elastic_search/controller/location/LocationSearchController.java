package com.sawmik.elastic_search.controller.location;

import com.sawmik.elastic_search.entity.Location;
import com.sawmik.elastic_search.service.location.LocationSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations/search")
@RequiredArgsConstructor
public class LocationSearchController {

    private final LocationSearchService locationSearchService;

    @GetMapping("/geo-distance")
    public ResponseEntity<SearchHits<Location>> searchGeoDistance(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5km") String distance) {
        return ResponseEntity.ok(locationSearchService.searchWithGeoDistance(lat, lon, distance));
    }

    @GetMapping("/geo-bounding-box")
    public ResponseEntity<SearchHits<Location>> searchGeoBoundingBox(
            @RequestParam double topLat,
            @RequestParam double topLon,
            @RequestParam double bottomLat,
            @RequestParam double bottomLon) {
        return ResponseEntity.ok(locationSearchService.searchWithGeoBoundingBox(topLat, topLon, bottomLat, bottomLon));
    }
}
