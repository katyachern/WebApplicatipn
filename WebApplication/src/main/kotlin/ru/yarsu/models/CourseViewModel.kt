package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.Course
import ru.yarsu.domain.ScheduleItem

data class CourseViewModel(
        val description: String,
        val course: Course,
        val scheduleItemList: List<ScheduleItem>
): ViewModel
