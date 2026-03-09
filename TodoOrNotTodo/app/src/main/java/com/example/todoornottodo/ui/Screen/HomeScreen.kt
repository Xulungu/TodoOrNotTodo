package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.ViewModel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    tasks: List<Task>,
    viewModel: TaskViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .width(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Todo Or Not Todo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("add") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter une tâche")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                TaskRow(
                    task = task,
                    viewModel = viewModel,
                    onViewDetail = { navController.navigate("detail/${task.id}") },
                    onEditTask = { navController.navigate("edit/${task.id}") }
                )
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    viewModel: TaskViewModel,
    onViewDetail: () -> Unit,
    onEditTask: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = {
                    // Utilisation de ta fonction updateTask
                    viewModel.updateTask(task.copy(isDone = it), task.isDone)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(task.title)
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Plus d'options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Voir détails") },
                    onClick = {
                        expanded = false
                        onViewDetail()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Modifier") },
                    onClick = {
                        expanded = false
                        onEditTask(task.id)
                    }
                )
            }
        }
    }
}