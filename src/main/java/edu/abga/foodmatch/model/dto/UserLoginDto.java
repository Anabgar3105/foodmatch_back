package edu.abga.foodmatch.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDto {
    private String username;
    private String password;
}
