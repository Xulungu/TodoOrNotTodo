package com.example.todoornottodo.Effect

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.*
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.R

@Composable
fun TaskEffect(
    task: Task,
    context: Context,
    soundResId: Int = R.raw.task_done
) {

    var hasPlayed by remember(task.id) { mutableStateOf(task.isDone) }

    LaunchedEffect(task.isDone) {
        if (!hasPlayed && task.isDone) {
            val mediaPlayer = MediaPlayer.create(context, soundResId)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { it.release() }

            hasPlayed = true
        }
    }
}