package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.dto.core.TimetableWithLectures
import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.service.TimetableService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TimetableController (
    private val timetableService: TimetableService,
) {
    @PostMapping("/api/v1/timetable/create")
    fun create(
        @LoggedInUser user: User,
        @RequestBody createRequest: CreateTimetableRequest,
    ): ResponseEntity<TimetableDto> {
        val timetableDto =
            timetableService.create(
                user = user,
                year = createRequest.year,
                semester = enumValueOf<Semester>(createRequest.semester),
                title = createRequest.title,
            )
        return ResponseEntity.ok(timetableDto)
    }

    @GetMapping("/api/v1/timetable/list")
    fun list(
        @LoggedInUser user: User,
    ): ResponseEntity<List<TimetableDto>> {
        val timetableDtoList = timetableService.list(user)
        return ResponseEntity.ok(timetableDtoList)
    }

    @GetMapping("/api/v1/timetable/{id}")
    fun get(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<TimetableWithLectures> {
        val timetable = timetableService.get(id, user)
        return ResponseEntity.ok(timetable)
    }

    @PatchMapping("/api/v1/timetable/{id}")
    fun update(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdateTimetableRequest,
    ): ResponseEntity<TimetableDto> {
        val timetableDto = timetableService.update(
            timetableId = id,
            user = user,
            title = updateRequest.title,
        )

        return ResponseEntity.ok(timetableDto)
    }

    @DeleteMapping("/api/v1/timetable/{id}")
    fun delete(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Void> {
        timetableService.delete(id, user)
        return ResponseEntity.noContent().build()
    }
}