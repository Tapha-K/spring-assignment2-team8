package com.wafflestudio.spring2025.timetable.enum

enum class Semester(
    val value: Int,
    val fullName: String,
) {
    SPRING(1, "1학기"),
    AUTUMN(2, "2학기"),
    SUMMER(3, "여름학기"),
    WINTER(4, "겨울학기"),
    ;
}