package com.example.todoornottodo.utils

import com.example.todoornottodo.Data.Task

enum class SortType {
    NAME_ASC,
    NAME_DESC,
    DONE_FIRST,
    TODO_FIRST
}

fun sortTasks(tasks: List<Task>, sortType: SortType): List<Task> {

    return when (sortType) {

        SortType.NAME_ASC ->
            tasks.sortedBy { it.title.lowercase() }

        SortType.NAME_DESC ->
            tasks.sortedByDescending { it.title.lowercase() }

        SortType.DONE_FIRST ->
            tasks.sortedByDescending { it.isDone }

        SortType.TODO_FIRST ->
            tasks.sortedBy { it.isDone }
    }
}