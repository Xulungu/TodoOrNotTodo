package com.example.todoornottodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.Navigation.AppNavigation
import com.example.todoornottodo.ViewModel.TaskViewModel
import com.example.todoornottodo.Worker.TaskSchedulerWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        val request = PeriodicWorkRequestBuilder<TaskSchedulerWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "task_scheduler",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

        setContent {
            MaterialTheme {
                val viewModel: TaskViewModel = viewModel()
                AppNavigation(viewModel)
            }
        }
    }
}