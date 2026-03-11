package com.example.todoornottodo.utils

import com.example.todoornottodo.Data.Task

enum class SortType {
    PRIORITY,
    DONE_FIRST,
    TODO_FIRST
}

fun sortTasks(tasks: List<Task>, sortType: SortType): List<Task> {
    return when (sortType) {
        SortType.PRIORITY ->
            tasks.sortedWith(compareByDescending<Task> { it.priority }
                .thenBy { it.title.lowercase() }) // priorité décroissante, puis A→Z

        SortType.DONE_FIRST ->
            tasks.sortedWith(compareByDescending<Task> { it.isDone }
                .thenByDescending { it.priority })

        SortType.TODO_FIRST ->
            tasks.sortedWith(compareBy<Task> { it.isDone }
                .thenByDescending { it.priority })
    }
}

fun filterAndSortTasks(tasks: List<Task>, filterType: FilterType, sortType: SortType): List<Task> {
    val filtered = filterTasks(tasks, filterType)
    return sortTasks(filtered, sortType)
}