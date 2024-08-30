package com.example.demo.dto;

import jakarta.validation.constraints.Email;

public record RequestRegisterDTO(@Email String login, String password, String name) {

}
