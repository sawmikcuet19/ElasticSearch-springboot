package com.sawmik.elastic_search.controller.location;

import com.sawmik.elastic_search.dto.location.LocationRequest;
import com.sawmik.elastic_search.entity.Location;
import com.sawmik.elastic_search.service.location.LocationSearchService;
import com.sawmik.elastic_search.service.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LocationSearchService locationSearchService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Location> create(@RequestBody LocationRequest request) {
        return ResponseEntity.ok(locationService.save(toLocation(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getById(@PathVariable String id) {
        return locationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Location> update(@PathVariable String id, @RequestBody LocationRequest request) {
        Location location = toLocation(request);
        location.setId(id);
        return ResponseEntity.ok(locationService.save(location));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        locationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> findByType(@PathVariable String type) {
        return ResponseEntity.ok(locationService.findByType(type));
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<Location>> findByCountry(@PathVariable String country) {
        return ResponseEntity.ok(locationService.findByCountry(country));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Location>> findByCity(@PathVariable String city) {
        return ResponseEntity.ok(locationService.findByCity(city));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Location>> findNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "10km") String distance) {
        return ResponseEntity.ok(locationSearchService.findNearby(lat, lon, distance));
    }

    @GetMapping("/bounding-box")
    public ResponseEntity<List<Location>> findByBoundingBox(
            @RequestParam double topLat,
            @RequestParam double topLon,
            @RequestParam double bottomLat,
            @RequestParam double bottomLon) {
        return ResponseEntity.ok(locationSearchService.findByBoundingBox(topLat, topLon, bottomLat, bottomLon));
    }

    private Location toLocation(LocationRequest request) {
        Location location = new Location();
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setType(request.getType());
        location.setCountry(request.getCountry());
        location.setCity(request.getCity());
        if (request.getCoordinates() != null) {
            location.setCoordinates(new GeoPoint(request.getCoordinates().getLat(), request.getCoordinates().getLon()));
        }
        return location;
    }
}
