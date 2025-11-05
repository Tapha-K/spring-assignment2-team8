package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.LectureSearchRequest
import com.wafflestudio.spring2025.timetable.dto.core.LectureDto
import com.wafflestudio.spring2025.timetable.service.LectureService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/lectures")
class LectureController(
    private val lectureService: LectureService,
) {
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