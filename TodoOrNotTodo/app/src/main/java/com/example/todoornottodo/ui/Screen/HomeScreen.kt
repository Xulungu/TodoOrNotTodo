package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.ViewModel.TaskViewModel
import com.example.todoornottodo.utils.SortType
import com.example.todoornottodo.utils.filterAndSortTasks
import com.example.todoornottodo.utils.FilterType
import com.example.todoornottodo.Effect.TaskEffect
import com.example.todoornottodo.Effect.TaskEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    tasks: List<Task>,
    viewModel: TaskViewModel
) {
    var sortType by remember { mutableStateOf(SortType.NAME_ASC) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }

    val context = LocalContext.current

    // Filtrage + tri délégué
    val sortedTasks = filterAndSortTasks(tasks, selectedFilter, sortType)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedFilter == FilterType.ALL,
                    onClick = { selectedFilter = FilterType.ALL },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("Tâches") }
                )
                NavigationBarItem(
                    selected = selectedFilter == FilterType.LATE,
                    onClick = { selectedFilter = FilterType.LATE },
                    icon = { Icon(Icons.Default.Warning, null) },
                    label = { Text("En retard") }
                )
                NavigationBarItem(
                    selected = selectedFilter == FilterType.DONE,
                    onClick = { selectedFilter = FilterType.DONE },
                    icon = { Icon(Icons.Default.Check, null) },
                    label = { Text("Fait") }
                )
                NavigationBarItem(
                    selected = selectedFilter == FilterType.TODAY,
                    onClick = { selectedFilter = FilterType.TODAY },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    label = { Text("Aujourd'hui") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(15.dp)
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

            // Ligne tri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trier :",
                    style = MaterialTheme.typography.titleMedium
                )

                Box {
                    Button(onClick = { sortMenuExpanded = true }) {
                        Text(
                            when (sortType) {
                                SortType.NAME_ASC -> "Nom A → Z"
                                SortType.NAME_DESC -> "Nom Z → A"
                                SortType.DONE_FIRST -> "Faites d'abord"
                                SortType.TODO_FIRST -> "À faire d'abord"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nom A → Z") },
                            onClick = {
                                sortType = SortType.NAME_ASC
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nom Z → A") },
                            onClick = {
                                sortType = SortType.NAME_DESC
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Faites d'abord") },
                            onClick = {
                                sortType = SortType.DONE_FIRST
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("À faire d'abord") },
                            onClick = {
                                sortType = SortType.TODO_FIRST
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(sortedTasks) { task ->
                    TaskRow(
                        task = task,
                        viewModel = viewModel,
                        onViewDetail = { navController.navigate("detail/${task.id}") },
                        onEditTask = { navController.navigate("edit/${task.id}") },
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    viewModel: TaskViewModel,
    onViewDetail: () -> Unit,
    onEditTask: (Int) -> Unit,
    context: android.content.Context
) {
    var expanded by remember { mutableStateOf(false) }

    TaskEffect(task, context)

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
                enabled = !task.isDone,
                onCheckedChange = { checked ->
                    if (checked) {
                        viewModel.updateTask(task.copy(isDone = true), task.isDone)
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(task.title)
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
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
                DropdownMenuItem(
                    text = { Text("Supprimer") },
                    onClick = {
                        expanded = false
                        viewModel.deleteTask(task)
                    }
                )
            }
        }
    }
}