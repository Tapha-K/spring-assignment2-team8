package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.Timetable
import org.springframework.data.repository.ListCrudRepository

interface TimetableRepository : ListCrudRepository<Timetable, Long> {
    fun existsByTitleAndUserId(title: String, userId: Long): Boolean
    fun findAllByUserId(userId: Long): List<Timetable>
}