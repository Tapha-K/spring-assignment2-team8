package com.wafflestudio.spring2025.timetable.dto.core

import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Lecture
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User

data class TimetableWithLectures(
    var id: Long? = null,
    val user: UserDto,
    var year: Int,
    var semester: Semester,
    var title: String,
    var lectures: List<LectureDto>,
    var totalCredits: Int,
) {
    constructor(timetable: Timetable, user: User, lectures: List<Lecture>) : this(
        id = timetable.id!!,
        user = UserDto(user),
        year = timetable.year,
        semester = Semester.fromValue(timetable.semester),
        title = timetable.title,
        lectures = lectures.map { LectureDto(it) },
        totalCredits = lectures.sumOf { it.credit },
    )
}
