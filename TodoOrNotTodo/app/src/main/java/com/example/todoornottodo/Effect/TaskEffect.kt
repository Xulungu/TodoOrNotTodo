package com.example.todoornottodo.Effect

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.*
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.R
import com.example.todoornottodo.ViewModel.TaskViewModel

@Composable
fun TaskEffect(
    task: Task,
    context: Context,
    viewModel: TaskViewModel
) {

    var lastState by remember(task.id) { mutableStateOf(task.isDone) }

    LaunchedEffect(task.isDone) {

        if (!lastState && task.isDone) {

            if (viewModel.isSoundEnabled()) {

                val soundRes = when (viewModel.getSelectedSound()) {

                    "faah" -> R.raw.faah_sound_effect
                    "happywheels" -> R.raw.task_done
                    else -> R.raw.task_done

                }

                val mediaPlayer = MediaPlayer.create(context, soundRes)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { it.release() }
            }

        }

        lastState = task.isDone
    }
}
