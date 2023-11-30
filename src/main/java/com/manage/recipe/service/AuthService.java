package com.manage.recipe.service;

import com.manage.recipe.model.dto.AuthRequestDto;
import java.util.Map;

public interface AuthService {
    Map<String, String> authRequest(AuthRequestDto authRequestDto);
}
