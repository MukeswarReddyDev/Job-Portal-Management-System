package com.mkr.service;

import com.mkr.dto.LoginRequest;
import com.mkr.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}