package com.example.todoornottodo.utils

import com.example.todoornottodo.Data.Task
import java.util.*

enum class FilterType {
    ALL,
    LATE,
    DONE,
    TODAY
}

fun isToday(timestamp: Long): Boolean {
    val calTask = Calendar.getInstance().apply { timeInMillis = timestamp }
    val calToday = Calendar.getInstance()
    return calTask.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
            calTask.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)
}

fun filterAndSortTasks(
    tasks: List<Task>,
    filterType: FilterType,
    sortType: SortType
): List<Task> {

    val filteredTasks = when (filterType) {
        FilterType.LATE -> tasks.filter { !it.isDone && it.date < System.currentTimeMillis() && !isToday(it.date) }
        FilterType.DONE -> tasks.filter { it.isDone }
        FilterType.TODAY -> tasks.filter { !it.isDone && isToday(it.date) }
        FilterType.ALL -> tasks
    }

    return sortTasks(filteredTasks, sortType)
}