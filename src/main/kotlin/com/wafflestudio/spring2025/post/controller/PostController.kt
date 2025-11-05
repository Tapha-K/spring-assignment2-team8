package com.wafflestudio.spring2025.post.controller

import com.wafflestudio.spring2025.post.dto.CreatePostRequest
import com.wafflestudio.spring2025.post.dto.CreatePostResponse
import com.wafflestudio.spring2025.post.dto.PostPagingResponse
import com.wafflestudio.spring2025.post.dto.UpdatePostRequest
import com.wafflestudio.spring2025.post.dto.UpdatePostResponse
import com.wafflestudio.spring2025.post.dto.core.PostDto
import com.wafflestudio.spring2025.post.service.PostService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "게시글 API", description = "게시글 생성, 조회, 수정, 삭제 API")
@RestController
class PostController(
    private val postService: PostService,
) {
    @Operation(summary = "게시글 생성", description = "특정 게시판에 게시글 작성")
    @PostMapping("/api/v1/boards/{boardId}/posts")
    fun create(
        @LoggedInUser user: User,
        @PathVariable boardId: Long,
        @RequestBody createRequest: CreatePostRequest,
    ): ResponseEntity<CreatePostResponse> {
        val postDto =
            postService.create(
                title = createRequest.title,
                content = createRequest.content,
                user = user,
                boardId = boardId,
            )
        return ResponseEntity.ok(postDto)
    }

    @Operation(summary = "게시글 목록 조회 (페이지네이션)", description = "특정 게시판의 게시글 목록을 켠 후 기반 페이지네이션으로 조회")
    @GetMapping("/api/v1/boards/{boardId}/posts")
    fun page(
        @PathVariable boardId: Long,
        @RequestParam(value = "nextCreatedAt", required = false) nextCreatedAt: Long?,
        @RequestParam(value = "nextId", required = false) nextId: Long?,
        @RequestParam(value = "limit", defaultValue = "10") limit: Int,
    ): ResponseEntity<PostPagingResponse> {
        val postPagingResponse =
            postService.pageByBoardId(
                boardId,
                nextCreatedAt?.let { Instant.ofEpochMilli(it) },
                nextId,
                limit,
            )
        return ResponseEntity.ok(postPagingResponse)
    }

    @Operation(summary = "게시글 단건 조회", description = "ID로 특정 게시글을 조회")
    @GetMapping("/api/v1/posts/{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<PostDto> {
        val postDto = postService.get(id)
        return ResponseEntity.ok(postDto)
    }

    @Operation(summary = "게시글 수정", description = "특정 게시글의 제목 또는 내용 수정 (작성자 본인만 가능)")
    @PatchMapping("/api/v1/posts/{id}")
    fun update(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdatePostRequest,
    ): ResponseEntity<UpdatePostResponse> {
        val postDto =
            postService.update(
                postId = id,
                title = updateRequest.title,
                content = updateRequest.content,
                user = user,
            )
        return ResponseEntity.ok(postDto)
    }

    @Operation(summary = "게시글 삭제", description = "특정 게시글 삭제 (작성자 본인만 가능)")
    @DeleteMapping("/api/v1/posts/{id}")
    fun delete(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        postService.delete(id, user)
        return ResponseEntity.noContent().build()
    }
}
