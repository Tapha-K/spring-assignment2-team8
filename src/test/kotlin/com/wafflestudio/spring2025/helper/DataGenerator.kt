package com.wafflestudio.spring2025.helper

import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.board.repository.BoardRepository
import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.repository.PostRepository
// 2주차 과제 import
import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Lecture
import com.wafflestudio.spring2025.timetable.model.LectureTime
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.model.TimetableLecture
import com.wafflestudio.spring2025.timetable.repository.LectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableLectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository

import com.wafflestudio.spring2025.user.JwtTokenProvider
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    // 2주차 과제 리포지토리 주입
    private val timetableRepository: TimetableRepository,
    private val lectureRepository: LectureRepository,
    private val timetableLectureRepository: TimetableLectureRepository,

) {
    fun generateUser(
        username: String? = null,
        password: String? = null,
    ): Pair<User, String> {
        val user =
            userRepository.save(
                User(
                    username = username ?: "user-${Random.Default.nextInt(1000000)}",
                    password = BCrypt.hashpw(password ?: "password-${Random.Default.nextInt(1000000)}", BCrypt.gensalt()),
                ),
            )
        return user to jwtTokenProvider.createToken(user.username)
    }

    fun generateBoard(name: String? = null): Board {
        val board =
            boardRepository.save(
                Board(
                    name = name ?: "board-${Random.Default.nextInt(1000000)}",
                ),
            )
        return board
    }

    fun generatePost(
        title: String? = null,
        content: String? = null,
        user: User? = null,
        board: Board? = null,
    ): Post {
        val post =
            postRepository.save(
                Post(
                    title = title ?: "title-${Random.Default.nextInt(1000000)}",
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    boardId = (board ?: generateBoard()).id!!,
                ),
            )
        return post
    }

    fun generateComment(
        content: String? = null,
        user: User? = null,
        post: Post? = null,
    ): Comment {
        val comment =
            commentRepository.save(
                Comment(
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    postId = (post ?: generatePost()).id!!,
                ),
            )
        return comment
    }

    // 시간표 생성
    fun generateTimetable(
        user: User? = null,
        year: Int = 2025,
        semester: String = "SPRING",
        title: String? = null
    ): Timetable {
        val owner = user ?: generateUser().first
        // 본인의 DTO는 String을 받지만, 모델은 Int(enum.value)를 사용하므로 변환
        val semEnum = Semester.valueOf(semester)
        return timetableRepository.save(
            Timetable(
                userId = owner.id!!,
                year = year,
                semester = semEnum.value, // Semester enum의 value 사용
                title = title ?: "timetable-${Random.Default.nextInt(1000000)}"
            )
        )
    }

    // 강의 생성
    fun generateLecture(
        year: Int = 2025,
        semester: String = "SPRING",
        title: String? = null,
        credit: Int = 3,
        dayOfWeek: String = "월",
        startTime: Int = 600, // 10:00 (10 * 60)
        endTime: Int = 660, // 11:00 (11 * 60)
        location: String = "301-101",
        instructor: String? = null
    ): Lecture {
        val semEnum = Semester.valueOf(semester)

        // LectureTime 객체 생성
        val lectureTime = LectureTime(
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
            lectureType = "이론", // 기본값
            location = location
        )

        // Lecture 객체 생성
        val lecture = Lecture(
            year = year,
            semester = semEnum.value,
            courseTitle = title ?: "lecture-${Random.Default.nextInt(1000000)}",
            credit = credit,
            classTimeText = "$dayOfWeek(${startTime/60}:${String.format("%02d", startTime%60)}-${endTime/60}:${String.format("%02d", endTime%60)})",
            location = location,
            instructor = instructor ?: "instructor-${Random.Default.nextInt(1000)}",
            classification = "전공",
            college = "공과대학",
            department = "컴퓨터공학부",
            academicCourse = "001",
            academicYear = "3",
            courseNumber = "001.001-${Random.Default.nextInt(1000)}",
            lectureNumber = "001",
            courseSubtitle = "",
            classTypeText = "이론",
            remark = ""
        )

        // Lecture 객체에 Set 할당
        lecture.lectureTimes = setOf(lectureTime)

        // Lecture 저장
        return lectureRepository.save(lecture)
    }

    // 시간표에 강의 연결
    fun addLectureToTimetable(
        timetable: Timetable,
        lecture: Lecture
    ): TimetableLecture {
        return timetableLectureRepository.save(
            TimetableLecture(
                timetableId = timetable.id!!,
                lectureId = lecture.id!!
            )
        )
    }

}
