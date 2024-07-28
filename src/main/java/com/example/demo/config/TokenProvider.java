package com.example.demo.config;

import java.util.Map;

public interface TokenProvider {
	Map<String, String> decode(String decode);
}
