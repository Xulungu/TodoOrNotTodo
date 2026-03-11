package com.example.todoornottodo.Navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.todoornottodo.ViewModel.TaskViewModel
import com.example.todoornottodo.ui.Screen.AddTaskScreen
import com.example.todoornottodo.ui.Screen.EditTaskScreen
import com.example.todoornottodo.ui.Screen.HomeScreen
import com.example.todoornottodo.ui.Screen.TaskDetailScreen

@Composable
fun AppNavigation(viewModel: TaskViewModel) {

    val navController = rememberNavController()
    val tasks by viewModel.tasks.collectAsState()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController = navController, tasks = tasks, viewModel = viewModel)
        }

        composable("add") {
            AddTaskScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = "detail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->

            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val task = tasks.find { it.id == taskId }

            task?.let {
                TaskDetailScreen(navController = navController, task = it)
            }
        }

        composable(
            route = "edit/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->

            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val task = tasks.find { it.id == taskId }

            task?.let {
                EditTaskScreen(
                    navController = navController,
                    viewModel = viewModel,
                    task = it,
                )
            }
        }
    }
}