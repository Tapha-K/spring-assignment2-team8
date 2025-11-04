package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.LectureTime
import org.springframework.data.repository.ListCrudRepository

interface LectureTimeRepository : ListCrudRepository<LectureTime, Long>