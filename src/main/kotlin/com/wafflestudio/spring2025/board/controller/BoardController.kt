package com.wafflestudio.spring2025.board.controller

import com.wafflestudio.spring2025.board.dto.CreateBoardRequest
import com.wafflestudio.spring2025.board.dto.CreateBoardResponse
import com.wafflestudio.spring2025.board.dto.ListBoardResponse
import com.wafflestudio.spring2025.board.service.BoardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "게시판 API", description = "게시판 생성 및 조회 API")
@RestController
@RequestMapping("/api/v1/boards")
class BoardController(
    private val boardService: BoardService,
) {
    @Operation(summary = "게시판 생성", description = "새로운 게시판 생성")
    @PostMapping
    fun create(
        @RequestBody createRequest: CreateBoardRequest,
    ): ResponseEntity<CreateBoardResponse> {
        val board = boardService.create(createRequest.name)
        return ResponseEntity.ok(board)
    }

    @Operation(summary = "게시판 목록 조회", description = "모든 게시판 목록 조회")
    @GetMapping
    fun list(): ResponseEntity<ListBoardResponse> {
        val boards = boardService.list()
        return ResponseEntity.ok(boards)
    }
}
