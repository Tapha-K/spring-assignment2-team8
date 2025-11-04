package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("lecture_time")
class LectureTime (
    @Id var id: Long? = null,
    var lectureId: Long,
    var dayOfWeek: String,
    var startTime: Int,
    var endTime: Int,
    var lectureType: String,
    var location: String,
)