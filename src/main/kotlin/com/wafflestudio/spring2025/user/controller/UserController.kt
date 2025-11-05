package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.dto.GetMeResponse
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자 API", description = "사용자 정보 조회 API")
@RestController
@RequestMapping("/api/v1/users")
class UserController {
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보 조회 (토큰 필요)")
    @GetMapping("/me")
    fun me(
        @LoggedInUser user: User,
    ): ResponseEntity<GetMeResponse> = ResponseEntity.ok(UserDto(user))
}
