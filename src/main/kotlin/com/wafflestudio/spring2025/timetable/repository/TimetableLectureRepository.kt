package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.TimetableLecture
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TimetableLectureRepository : CrudRepository<TimetableLecture, Long> {
    @Query("SELECT lecture_id FROM timetable_lecture WHERE timetable_id = :tid")
    fun findLectureIdsByTimetableId(
        @Param("tid") timetableId: Long,
    ): List<Long>

    @Query("DELETE FROM timetable_lecture WHERE timetable_id = :tid AND lecture_id = :lid")
    fun deleteByTimetableIdAndLectureId(
        @Param("tid") timetableId: Long,
        @Param("lid") lectureId: Long,
    )
}
