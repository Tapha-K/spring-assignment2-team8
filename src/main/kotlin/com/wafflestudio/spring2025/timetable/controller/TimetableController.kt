package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.dto.core.TimetableWithLectures
import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.service.TimetableService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.Long

@Tag(name = "시간표 API", description = "시간표 생성, 조회, 수정, 삭제 및 강의 추가/삭제 API")
@RestController
class TimetableController(
    private val timetableService: TimetableService,
) {
    @Operation(summary = "시간표 생성", description = "새로운 시간표 생성")
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

    @Operation(summary = "시간표 목록 조회", description = "현재 로그인한 유저의 모든 시간표 목록 조회")
    @GetMapping("/api/v1/timetable/list")
    fun list(
        @LoggedInUser user: User,
    ): ResponseEntity<List<TimetableDto>> {
        val timetableDtoList = timetableService.list(user)
        return ResponseEntity.ok(timetableDtoList)
    }

    @Operation(summary = "시간표 상세 조회", description = "특정 시간표의 상세 정보(강의 포함) 조회")
    @GetMapping("/api/v1/timetable/{id}")
    fun get(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<TimetableWithLectures> {
        val timetable = timetableService.get(id, user)
        return ResponseEntity.ok(timetable)
    }

    @Operation(summary = "시간표 이름 수정", description = "특정 시간표의 이름 수정 (작성자 본인만 가능)")
    @PatchMapping("/api/v1/timetable/{id}")
    fun update(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdateTimetableRequest,
    ): ResponseEntity<TimetableDto> {
        val timetableDto =
            timetableService.update(
                timetableId = id,
                user = user,
                title = updateRequest.title,
            )

        return ResponseEntity.ok(timetableDto)
    }

    @Operation(summary = "시간표 삭제", description = "특정 시간표를 삭제 (작성자 본인만 가능)")
    @DeleteMapping("/api/v1/timetable/{id}")
    fun delete(
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Void> {
        timetableService.delete(id, user)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "시간표에 강의 추가", description = "특정 시간표에 강의 추가 (시간 중복, 학기 일치 검사 포함)")
    @PostMapping("/api/v1/timetable/{id}/lectures")
    fun addLecture(
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody request: AddLectureRequest,
    ): ResponseEntity<TimetableDto> {
        val updatedTimetable =
            timetableService.addLecture(
                timetableId = id,
                lectureId = request.lectureId,
                user = user,
            )
        return ResponseEntity.ok(updatedTimetable)
    }

    @Operation(summary = "시간표에서 강의 삭제", description = "특정 시간표에서 특정 강의 삭제 (작성자 본인만 가능)")
    @DeleteMapping("/api/v1/timetable/{timetableId}/lectures/{lectureId}")
    fun deleteLecture(
        @PathVariable timetableId: Long,
        @LoggedInUser user: User,
        @PathVariable lectureId: Long,
    ): ResponseEntity<Void> {
        timetableService.deleteLecture(timetableId, user, lectureId)
        return ResponseEntity.noContent().build()
    }
}
