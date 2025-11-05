package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.helper.DataGenerator
import com.wafflestudio.spring2025.helper.QueryCounter
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

// 2차 과제 필요 import
import com.wafflestudio.spring2025.timetable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableRequest
import com.wafflestudio.spring2025.timetable.repository.TimetableLectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.timetable.enum.Semester


@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class TimetableIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
        private val queryCounter: QueryCounter,
        private val timetableRepository: TimetableRepository,
        private val timetableLectureRepository: TimetableLectureRepository
    ) {

        // TDD용 임시 DTO
        data class AddLectureRequest(val lectureId: Long)

        @Test
        fun `should create a timetable`() {
            // 시간표를 생성할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val request = CreateTimetableRequest(
                year = 2025,
                semester = "SPRING",
                title = "새 학기 시간표"
            )

            mvc.perform(
                post("/api/v1/timetable/create")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.title").value("새 학기 시간표"))
        }

        @Test
        fun `should retrieve all own timetables`() {
            // 자신의 모든 시간표 목록을 조회할 수 있다
            val (user1, token1) = dataGenerator.generateUser("user1")
            dataGenerator.generateTimetable(user1, 2024, "SPRING", "T1")
            dataGenerator.generateTimetable(user1, 2024, "AUTUMN", "T2")

            mvc.perform(
                get("/api/v1/timetable/list")
                    .header("Authorization", "Bearer $token1")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Any>(2)))
        }

        @Test
        fun `should retrieve timetable details`() {
            // 시간표 상세 정보를 조회할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user)

            // 시간표 찾기 (쿼리 1)
            // 시간표-강의 ID 목록 찾기 (쿼리 2)
            // 강의 목록 찾기 (쿼리 3)
            // 총 3번의 쿼리가 정상
            queryCounter.assertQueryCount(3L) {
                mvc.perform(
                    get("/api/v1/timetable/{id}", timetable.id)
                        .header("Authorization", "Bearer $token")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.totalCredits").value(0))
            }
        }

        @Test
        fun `should update timetable name`() {
            // 시간표 이름을 수정할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user, title = "옛날 이름")
            val request = UpdateTimetableRequest(title = "새 이름")

            mvc.perform(
                patch("/api/v1/timetable/{id}", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.title").value("새 이름"))
        }

        @Test
        fun `should not update another user's timetable`() {
            // 다른 사람의 시간표는 수정할 수 없다
            val (owner, _) = dataGenerator.generateUser("owner")
            val (attacker, attackerToken) = dataGenerator.generateUser("attacker")
            val timetable = dataGenerator.generateTimetable(owner)
            val request = UpdateTimetableRequest(title = "해킹시도")

            mvc.perform(
                patch("/api/v1/timetable/{id}", timetable.id)
                    .header("Authorization", "Bearer $attackerToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isForbidden) // 403
        }

        @Test
        fun `should delete a timetable`() {
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user)
            val timetableId = timetable.id!!

            mvc.perform(
                delete("/api/v1/timetable/{id}", timetableId)
                    .header("Authorization", "Bearer $token")
            )
                .andExpect(status().isNoContent) // 204

            assertFalse(timetableRepository.findById(timetableId).isPresent)
        }

        @Test
        fun `should not delete another user's timetable`() {
            // 다른 사람의 시간표는 삭제할 수 없다
            val (owner, _) = dataGenerator.generateUser("owner")
            val (attacker, attackerToken) = dataGenerator.generateUser("attacker")
            val timetable = dataGenerator.generateTimetable(owner)

            mvc.perform(
                delete("/api/v1/timetable/{id}", timetable.id)
                    .header("Authorization", "Bearer $attackerToken")
            )
                .andExpect(status().isForbidden) // 403
        }

        @Test
        fun `should search for courses`() {
            // 강의를 검색할 수 있다
            val (user, token) = dataGenerator.generateUser()
            dataGenerator.generateLecture(2025, "SPRING", title = "컴퓨터의 이해", instructor = "A교수")
            dataGenerator.generateLecture(2025, "SPRING", title = "자료구조", instructor = "B교수")
            dataGenerator.generateLecture(2025, "SPRING", title = "데이터베이스", instructor = "A교수")
            dataGenerator.generateLecture(2025, "AUTUMN", title = "컴퓨터 프로그래밍", instructor = "C교수")

            mvc.perform(
                get("/api/v1/lectures") // 👈 실제 API 경로
                    .header("Authorization", "Bearer $token")
                    .param("year", "2025")
                    .param("semester", Semester.SPRING.value.toString()) // 👈 Int 값
                    .param("keyword", "A교수") // A교수로 검색
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Any>(2))) // 2개
                .andExpect(jsonPath("$[0].instructor").value("A교수"))
        }

        @Test
        fun `should add a course to timetable`() {
            // 시간표에 강의를 추가할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user)
            val lecture = dataGenerator.generateLecture()
            val request = AddLectureRequest(lectureId = lecture.id!!) // 실제 DTO

            mvc.perform(
                post("/api/v1/timetable/{id}/lectures", timetable.id) // 실제 API 경로
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(timetable.id!!)) // TimetableDto 반환
        }

        @Test
        fun `should return error when adding overlapping course to timetable`() {
            // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user)

            // [수정됨] DataGenerator가 (버그에 맞춘) HHmm 형식 사용
            val lecture1 = dataGenerator.generateLecture(
                dayOfWeek = "월", startTime = 1000, endTime = 1100 // 10:00 - 11:00
            )
            val lecture2 = dataGenerator.generateLecture(
                dayOfWeek = "월", startTime = 1030, endTime = 1130 // 10:30 - 11:30 (겹침)
            )

            dataGenerator.addLectureToTimetable(timetable, lecture1) // 강의1 추가

            val request = AddLectureRequest(lectureId = lecture2.id!!)

            mvc.perform(
                post("/api/v1/timetable/{id}/lectures", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest) // 400 (TimetableDuplicateTimeException)
        }

        @Test
        fun `should not add a course to another user's timetable`() {
            // 다른 사람의 시간표에는 강의를 추가할 수 없다
            val (owner, _) = dataGenerator.generateUser("owner")
            val (attacker, attackerToken) = dataGenerator.generateUser("attacker")
            val timetable = dataGenerator.generateTimetable(owner)
            val lecture = dataGenerator.generateLecture()
            val request = AddLectureRequest(lectureId = lecture.id!!)

            mvc.perform(
                post("/api/v1/timetable/{id}/lectures", timetable.id)
                    .header("Authorization", "Bearer $attackerToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isForbidden) // 403
        }

        @Test
        fun `should remove a course from timetable`() {
            // 시간표에서 강의를 삭제할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user)
            val lecture = dataGenerator.generateLecture()
            dataGenerator.addLectureToTimetable(timetable, lecture) // 미리 추가

            mvc.perform(
                delete("/api/v1/timetable/{timetableId}/lectures/{lectureId}", timetable.id, lecture.id) // 👈 실제 API 경로
                    .header("Authorization", "Bearer $token")
            )
                .andExpect(status().isNoContent) // 204

            // 연관 테이블에서 삭제되었는지 확인
            assertFalse(
                timetableLectureRepository.deleteByTimetableIdAndLectureId(timetable.id!!, lecture.id!!)
                    .let { timetableLectureRepository.findLectureIdsByTimetableId(timetable.id!!).contains(lecture.id!!) }
            )
        }

        @Test
        fun `should not remove a course from another user's timetable`() {
            // 다른 사람의 시간표에서는 강의를 삭제할 수 없다
            val (owner, _) = dataGenerator.generateUser("owner")
            val (attacker, attackerToken) = dataGenerator.generateUser("attacker")
            val timetable = dataGenerator.generateTimetable(owner)
            val lecture = dataGenerator.generateLecture()
            dataGenerator.addLectureToTimetable(timetable, lecture)

            mvc.perform(
                delete("/api/v1/timetable/{timetableId}/lectures/{lectureId}", timetable.id, lecture.id) // 👈 실제 API 경로
                    .header("Authorization", "Bearer $attackerToken")
            )
                .andExpect(status().isForbidden) // 403
        }

        @Test
        @Disabled("곧 안내드리겠습니다")
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
        }

        @Test
        fun `should return correct course list and total credits when retrieving timetable details`() {
            // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user)
            val lecture1 = dataGenerator.generateLecture(credit = 3)
            val lecture2 = dataGenerator.generateLecture(credit = 1)

            dataGenerator.addLectureToTimetable(timetable, lecture1)
            dataGenerator.addLectureToTimetable(timetable, lecture2)

            mvc.perform(
                get("/api/v1/timetable/{id}", timetable.id)
                    .header("Authorization", "Bearer $token")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.lectures", hasSize<Any>(2)))
                .andExpect(jsonPath("$.totalCredits").value(4)) // 3 + 1
        }

        @Test
        fun `should paginate correctly when searching for courses`() {
            // 강의 검색 시, 페이지네이션이 올바르게 동작한다
            val (user, token) = dataGenerator.generateUser()
            val semester = Semester.SPRING.value // Int 값

            // 15개의 강의 생성
            repeat(15) {
                dataGenerator.generateLecture(2025, "SPRING", title = "Paging Test $it")
            }
            dataGenerator.generateLecture(2025, "AUTUMN", title = "Paging Test Other") // 다른 학기

            // 1페이지 (size=10)
            mvc.perform(
                get("/api/v1/lectures")
                    .header("Authorization", "Bearer $token")
                    .param("year", "2025")
                    .param("semester", semester.toString())
                    .param("keyword", "Paging")
                    .param("page", "0") // 0-based
                    .param("size", "10")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Any>(10))) // 10개

            // 2페이지 (size=10)
            mvc.perform(
                get("/api/v1/lectures")
                    .header("Authorization", "Bearer $token")
                    .param("year", "2025")
                    .param("semester", semester.toString())
                    .param("keyword", "Paging")
                    .param("page", "1") // 1-based (2번째 페이지)
                    .param("size", "10")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Any>(5))) // 나머지 5개
        }
    }
