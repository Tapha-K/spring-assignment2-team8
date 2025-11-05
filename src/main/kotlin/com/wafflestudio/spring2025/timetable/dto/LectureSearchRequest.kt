package com.wafflestudio.spring2025.timetable.dto

data class LectureSearchRequest(
    val year: Int,
    val semester: Int,
    val keyword: String,
    val page: Int,
    val size: Int,
)
