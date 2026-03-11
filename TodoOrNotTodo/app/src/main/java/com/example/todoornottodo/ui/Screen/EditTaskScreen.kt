package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.ViewModel.TaskViewModel
import com.example.todoornottodo.utils.Periodicity
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
    var repeatType by remember { mutableStateOf(task.repeatType) }
    var repeatMenuExpanded by remember { mutableStateOf(false) }
    var taskPriority by remember { mutableStateOf(task.priority.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Modifier la tâche", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Titre
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre de la tâche") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date
        DatePickerButton(
            selectedDate = date,
            onDateSelected = { date = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Périodicité
        Box {
            Button(onClick = { repeatMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    when (repeatType) {
                        Periodicity.NONE -> "Pas de répétition"
                        Periodicity.DAILY -> "Tous les jours"
                        Periodicity.WEEKLY -> "Toutes les semaines"
                        Periodicity.MONTHLY -> "Tous les mois"
                    }
                )
            }

            DropdownMenu(
                expanded = repeatMenuExpanded,
                onDismissRequest = { repeatMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Pas de répétition") },
                    onClick = {
                        repeatType = Periodicity.NONE
                        repeatMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Tous les jours") },
                    onClick = {
                        repeatType = Periodicity.DAILY
                        repeatMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Toutes les semaines") },
                    onClick = {
                        repeatType = Periodicity.WEEKLY
                        repeatMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Tous les mois") },
                    onClick = {
                        repeatType = Periodicity.MONTHLY
                        repeatMenuExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Priorité
        TextField(
            value = taskPriority,
            onValueChange = { taskPriority = it },
            label = { Text("Priorité de la tâche") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Enregistrer
        Button(
            onClick = {
                val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val timestamp = formatter.parse(date)?.time ?: task.date
                val priority = taskPriority.toIntOrNull() ?: 0
                val updatedTask = task.copy(title = title, date = timestamp, priority = taskPriority.toInt())
                viewModel.updateTask(updatedTask, task.isDone)

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bouton Annuler
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Annuler")
        }
    }
}