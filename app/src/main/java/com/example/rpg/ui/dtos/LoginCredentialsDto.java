package com.example.rpg.ui.dtos;

public class LoginCredentialsDto {
    public String email;

    public String password;

    public LoginCredentialsDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
