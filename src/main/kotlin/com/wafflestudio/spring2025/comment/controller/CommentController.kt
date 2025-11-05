package com.wafflestudio.spring2025.comment.controller

import com.wafflestudio.spring2025.comment.dto.CreateCommentRequest
import com.wafflestudio.spring2025.comment.dto.CreateCommentResponse
import com.wafflestudio.spring2025.comment.dto.UpdateCommentRequest
import com.wafflestudio.spring2025.comment.dto.UpdateCommentResponse
import com.wafflestudio.spring2025.comment.dto.core.CommentDto
import com.wafflestudio.spring2025.comment.service.CommentService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "댓글 API", description = "게시글의 댓글 생성, 조회, 수정, 삭제 API")
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 모든 댓글 조회")
    @GetMapping
    fun list(
        @PathVariable postId: Long,
    ): ResponseEntity<List<CommentDto>> {
        val comments = commentService.list(postId)
        return ResponseEntity.ok(comments)
    }

    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글 작성")
    @PostMapping
    fun create(
        @PathVariable postId: Long,
        @RequestBody createRequest: CreateCommentRequest,
        @LoggedInUser user: User,
    ): ResponseEntity<CreateCommentResponse> {
        val comment =
            commentService.create(
                postId = postId,
                content = createRequest.content,
                user = user,
            )
        return ResponseEntity.ok(comment)
    }

    @Operation(summary = "댓글 수정", description = "특정 댓글의 내용 수정 (작성자 본인만 가능)")
    @PutMapping("/{id}")
    fun update(
        @PathVariable postId: Long,
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdateCommentRequest,
    ): ResponseEntity<UpdateCommentResponse> {
        val comment =
            commentService.update(
                commentId = id,
                postId = postId,
                content = updateRequest.content,
                user = user,
            )
        return ResponseEntity.ok(comment)
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글 삭제 (작성자 본인만 가능)")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable postId: Long,
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        commentService.delete(
            commentId = id,
            postId = postId,
            user = user,
        )
        return ResponseEntity.noContent().build()
    }
}
