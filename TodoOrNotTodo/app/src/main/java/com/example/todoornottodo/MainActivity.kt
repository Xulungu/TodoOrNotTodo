package com.example.todoornottodo


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var tasks by remember { mutableStateOf(listOf<String>()) }

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                navController = navController,
                tasks = tasks
            )
        }

        composable("add") {
            AddTaskScreen(
                navController = navController,
                onAddTask = { newTask ->
                    tasks = tasks + newTask
                }
            )
        }

        composable(
            route = "detail/{task}",
            arguments = listOf(navArgument("task") { defaultValue = "" })
        ) { backStackEntry ->
            val task = backStackEntry.arguments?.getString("task") ?: ""
            TaskDetailScreen(navController = navController, task = task)
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController, tasks: List<String>) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Todo Or Not Todo", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(task = task) {
                        navController.navigate("detail/$task")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(task)
    }
}
@Composable
fun AddTaskScreen(navController: NavHostController, onAddTask: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ajouter une tâche", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Nom de la tâche") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onAddTask(text)
                    navController.popBackStack()
                }
            }
        ) {
            Text("Ajouter")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Annuler")
        }
    }
}

@Composable
fun TaskDetailScreen(navController: NavHostController, task: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Détail de la tâche", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(task, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Retour")
        }
    }
}
