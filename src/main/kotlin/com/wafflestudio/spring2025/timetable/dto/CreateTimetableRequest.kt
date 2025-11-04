package com.wafflestudio.spring2025.timetable.dto

data class CreateTimetableRequest (
    var year: Int,
    var semester: String,
    var title: String,
)