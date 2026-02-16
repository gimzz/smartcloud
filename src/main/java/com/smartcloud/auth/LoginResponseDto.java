package com.smartcloud.auth;

public class LoginResponseDto {

    private String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }
}
