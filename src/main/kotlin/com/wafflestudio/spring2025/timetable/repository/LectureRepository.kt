package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Lecture
import org.springframework.data.repository.ListCrudRepository

interface LectureRepository : ListCrudRepository<Lecture, Long> {
    fun findAllByYearAndSemester(year: Int, semester: Int) : List<Lecture>
}