package com.example.demo.dto;

import jakarta.validation.constraints.Email;

public record RequestAuthDTO(@Email String login, String password) {

}
