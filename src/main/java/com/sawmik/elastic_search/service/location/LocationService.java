package com.sawmik.elastic_search.service.location;

import com.sawmik.elastic_search.entity.Location;
import com.sawmik.elastic_search.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location save(Location location) {
        if (location.getCreatedAt() == null) {
            location.setCreatedAt(LocalDateTime.now());
        }
        return locationRepository.save(location);
    }

    public Optional<Location> findById(String id) {
        return locationRepository.findById(id);
    }

    public List<Location> findAll() {
        List<Location> locations = new ArrayList<>();
        locationRepository.findAll().forEach(locations::add);
        return locations;
    }

    public void deleteById(String id) {
        locationRepository.deleteById(id);
    }

    public List<Location> findByType(String type) {
        return locationRepository.findByType(type);
    }

    public List<Location> findByCountry(String country) {
        return locationRepository.findByCountry(country);
    }

    public List<Location> findByCity(String city) {
        return locationRepository.findByCity(city);
    }
}
