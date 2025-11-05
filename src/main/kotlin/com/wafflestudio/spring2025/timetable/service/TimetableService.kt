package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.timetable.TimetableBlankTitleException
import com.wafflestudio.spring2025.timetable.TimetableDuplicateTitleException
import com.wafflestudio.spring2025.timetable.TimetableNotFoundException
import com.wafflestudio.spring2025.timetable.TimetableUpdateForbiddenException
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.dto.core.TimetableWithLectures
import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.repository.LectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableLectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TimetableService(
    private val timetableRepository: TimetableRepository,
    private val lectureRepository: LectureRepository,
    private val timetableLectureRepository: TimetableLectureRepository,
) {
    fun list(user: User): List<TimetableDto> {
        val timetableList = timetableRepository.findAllByUserId(user.id!!)
        return timetableList.map { TimetableDto(it, user) }
    }

    fun get(
        timetableId: Long,
        user: User,
    ): TimetableWithLectures {
        // 일단 다른 사람의 시간표도 조회할 수 있게
        val timetable = timetableRepository.findByIdOrNull(timetableId) ?: throw TimetableNotFoundException()
        val lectureIds = timetableLectureRepository.findLectureIdsByTimetableId(timetableId)
        val lectures = lectureRepository.findAllById(lectureIds)

        return TimetableWithLectures(timetable, user, lectures)
    }

    fun create(
        user: User,
        year: Int,
        semester: Semester,
        title: String,
    ): TimetableDto {
        if (title.isBlank()) {
            throw TimetableBlankTitleException()
        }
        if (timetableRepository.existsByTitleAndUserId(title, user.id!!)) {
            throw TimetableDuplicateTitleException()
        }

        val timetable =
            timetableRepository.save(
                Timetable(
                    userId = user.id!!,
                    year = year,
                    semester = semester.value,
                    title = title,
                ),
            )

        return TimetableDto(timetable, user)
    }

    fun update(
        timetableId: Long,
        user: User,
        title: String?,
    ): TimetableDto {
        if (title?.isBlank() == true) {
            throw TimetableBlankTitleException()
        }

        val timetable = timetableRepository.findByIdOrNull(timetableId) ?: throw TimetableNotFoundException()

        if (timetable.userId != user.id) {
            throw TimetableUpdateForbiddenException()
        }

        title?.let { timetable.title = it }
        timetableRepository.save(timetable)

        return TimetableDto(timetable, user)
    }

    fun delete(
        timetableId: Long,
        user: User,
    ) {
        val timetable = timetableRepository.findByIdOrNull(timetableId) ?: throw TimetableNotFoundException()
        if (timetable.userId != user.id) {
            throw TimetableUpdateForbiddenException()
        }

        timetableRepository.delete(timetable)
    }
}
