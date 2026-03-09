package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.ViewModel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavHostController,
    viewModel: TaskViewModel,
    task: Task
) {
    var title by remember { mutableStateOf(task.title) }
    var date by remember { mutableStateOf(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(task.date))) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Modifier la tâche", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre de la tâche") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DatePickerButton(
            selectedDate = date,
            onDateSelected = { date = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val timestamp = formatter.parse(date)?.time ?: task.date
                viewModel.updateTask(task.copy(title = title, date = timestamp), task.isDone)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Annuler")
        }
    }
}