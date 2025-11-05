package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.LectureSearchRequest
import com.wafflestudio.spring2025.timetable.dto.core.LectureDto
import com.wafflestudio.spring2025.timetable.service.LectureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "강의 API", description = "강의 검색 및 조회 API")
@RestController
@RequestMapping("/api/v1/lectures")
class LectureController(
    private val lectureService: LectureService,
) {
    @Operation(summary = "강의 검색", description = "연도, 학기, 키워드(강의명/교수명)로 강의 검색 (페이지네이션)")
    @GetMapping
    fun searchLectures(
        @RequestParam year: Int,
        @RequestParam semester: Int,
        @RequestParam(required = false, defaultValue = "") keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): List<LectureDto> {
        val request = LectureSearchRequest(year, semester, keyword, page, size)
        return lectureService.searchLectures(request)
    }
}
