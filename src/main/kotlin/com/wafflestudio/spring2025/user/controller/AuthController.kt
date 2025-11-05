package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.dto.LoginRequest
import com.wafflestudio.spring2025.user.dto.LoginResponse
import com.wafflestudio.spring2025.user.dto.RegisterRequest
import com.wafflestudio.spring2025.user.dto.RegisterResponse
import com.wafflestudio.spring2025.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "인증 API", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userService: UserService,
) {
    @Operation(summary = "회원가입", description = "아이디와 비밀번호로 회원가입")
    @PostMapping("/register")
    fun register(
        @RequestBody registerRequest: RegisterRequest,
    ): ResponseEntity<RegisterResponse> {
        val userDto =
            userService.register(
                username = registerRequest.username,
                password = registerRequest.password,
            )
        return ResponseEntity.ok(userDto)
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하고 JWT 토큰 발급")
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val token =
            userService.login(
                username = loginRequest.username,
                password = loginRequest.password,
            )
        return ResponseEntity.ok(LoginResponse(token))
    }
}
