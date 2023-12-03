package com.manage.recipe.service.auth;

import com.manage.recipe.model.dto.AuthRequestDTO;
import java.util.Map;

public interface AuthService {
    Map<String, String> authRequest(AuthRequestDTO authRequestDTO);
}
