package com.sawmik.elastic_search.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String username;
    private String email;
    private String role;
    private boolean enabled;
}
