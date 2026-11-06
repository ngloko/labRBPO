package org.example.lab1rbpo.controller;

import org.example.lab1rbpo.dto.RegisterRequest;
import org.example.lab1rbpo.entity.User;
import org.example.lab1rbpo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Публичная регистрация. Роль клиенту не доступна — всегда CURATOR.
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    /**
     * Триггерит генерацию CSRF-токена: Spring кладёт его в cookie XSRF-TOKEN.
     * Клиент должен вызвать этот эндпоинт перед первым изменяющим запросом
     * и далее слать значение токена в заголовке X-XSRF-TOKEN.
     */
    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return ResponseEntity.ok(Map.of(
                "headerName", token.getHeaderName(),
                "token", token.getToken()
        ));
    }
}