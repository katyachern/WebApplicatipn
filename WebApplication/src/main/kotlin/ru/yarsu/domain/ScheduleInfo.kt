package ru.yarsu.domain

import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.random.Random

data class ScheduleInfo(
    val courses: List<Course>,
    val scheduleItems: List<ScheduleItem>,
)

fun formCourseList() = listOf(
    Course(
        "physics-course",
        "Физика",
        "Иванов И. И.",
        "Курс про устройство мира, если на него смотреть с помощью современного аппарата"
    ),
    Course(
        "web-course",
        "Разработка веб-приложений",
        "Васильев А. М.",
        "Курс про разработку приложений для обслуживания людей с помощью браузера и интернета"
    ),
    Course(
        "physics-course",
        "Физика",
        "Иванов И. И.",
        "Курс про устройство мира, если на него смотреть с помощью современного аппарата"
    ),
)

fun formSchedule(): ScheduleInfo {
    val courses = formCourseList()
    val auditoryNumber = listOf(203, 220, 216, 219)
    val days = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    val scheduleItems = days.flatMap { day ->
        courses
            .shuffled()
            .take(Random.nextInt(2, 4))
            .mapIndexed { index, course ->
                ScheduleItem(
                    course.id,
                    auditoryNumber.random(),
                    day,
                    LocalTime.of(9, 0).plusMinutes( 90L * index),
                )
            }
    }
    return ScheduleInfo(
        courses,
        scheduleItems,
    )
}
