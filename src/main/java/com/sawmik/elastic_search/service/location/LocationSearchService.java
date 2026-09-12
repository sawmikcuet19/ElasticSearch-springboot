package com.sawmik.elastic_search.service.location;

import com.sawmik.elastic_search.entity.Location;
import com.sawmik.elastic_search.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationSearchService {

    private final LocationRepository locationRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<Location> findNearby(double lat, double lon, String distance) {
        return locationRepository.findByDistanceFrom(distance, lat, lon);
    }

    public List<Location> findByBoundingBox(double topLat, double topLon, double bottomLat, double bottomLon) {
        return locationRepository.findByBoundingBox(topLat, topLon, bottomLat, bottomLon);
    }

    public SearchHits<Location> searchWithGeoDistance(double lat, double lon, String distance) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.geoDistance(gd -> gd
                                .field("coordinates")
                                .distance(distance)
                                .location(loc -> loc.latlon(ll -> ll.lat(lat).lon(lon)))
                        ))
                ))
                .build();

        return elasticsearchOperations.search(query, Location.class);
    }

    public SearchHits<Location> searchWithGeoBoundingBox(double topLat, double topLon, double bottomLat, double bottomLon) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.geoBoundingBox(gb -> gb
                                .field("coordinates")
                                .boundingBox(bb -> bb
                                        .coords(cb -> cb
                                                .top(topLat)
                                                .bottom(bottomLat)
                                                .left(topLon)
                                                .right(bottomLon)
                                        )
                                )
                        ))
                ))
                .build();

        return elasticsearchOperations.search(query, Location.class);
    }

    public SearchHits<Location> searchWithGeoPolygon(List<double[]> points) {
        List<co.elastic.clients.elasticsearch._types.GeoLocation> geoLocations = points.stream()
                .map(p -> co.elastic.clients.elasticsearch._types.GeoLocation.of(gl -> gl
                        .latlon(ll -> ll.lat(p[0]).lon(p[1]))
                ))
                .toList();

        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.geoPolygon(gp -> gp
                                .field("coordinates")
                                .polygon(poly -> poly
                                        .points(geoLocations)
                                )
                        ))
                ))
                .build();

        return elasticsearchOperations.search(query, Location.class);
    }
}
