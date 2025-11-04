package com.wafflestudio.spring2025.timetable.dto

import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User

data class TimetableDto(
    var id: Long? = null,
    val user: UserDto,
    var year: Int,
    var semester: Semester,
    var title: String,
) {
    constructor(timetable: Timetable, user: User) : this(
        id = timetable.id!!,
        user = UserDto(user),
        year = timetable.year,
        semester = Semester.fromCode(timetable.semester),
        title = timetable.title,
    )
}