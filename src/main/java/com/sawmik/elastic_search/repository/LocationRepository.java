package com.sawmik.elastic_search.repository;

import com.sawmik.elastic_search.entity.Location;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface LocationRepository extends ElasticsearchRepository<Location, String> {

    List<Location> findByType(String type);

    List<Location> findByCountry(String country);

    List<Location> findByCity(String city);

    List<Location> findByCountryAndCity(String country, String city);

    @Query("""
        {
          "bool": {
            "filter": {
              "geo_distance": {
                "distance": "?0",
                "coordinates": {
                  "lat": ?1,
                  "lon": ?2
                }
              }
            }
          }
        }
    """)
    List<Location> findByDistanceFrom(String distance, double lat, double lon);

    @Query("""
        {
          "bool": {
            "filter": {
              "geo_bounding_box": {
                "coordinates": {
                  "top_left": { "lat": ?0, "lon": ?1 },
                  "bottom_right": { "lat": ?2, "lon": ?3 }
                }
              }
            }
          }
        }
    """)
    List<Location> findByBoundingBox(double topLat, double topLon, double bottomLat, double bottomLon);
}
