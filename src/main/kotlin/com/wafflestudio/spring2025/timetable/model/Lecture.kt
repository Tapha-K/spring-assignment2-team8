package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("lectures")
class Lecture (
    @Id var id: Long? = null,
    var year: Int,
    var semester: Int,
    var classification: String,
    var college: String,
    var department: String,
    var academicCourse: String,
    var academicYear: String,
    var courseNumber: String,
    var lectureNumber: String,
    var courseTitle: String,
    var courseSubtitle: String,
    var credit: Int,
    var classTimeText: String,
    var classTypeText: String,
    var location: String,
    var instructor: String,
    var remark: String,
)