package com.sawmik.elastic_search.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

    private String name;
    private String address;
    private String type;
    private Coordinates coordinates;
    private String country;
    private String city;
}
