package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.utils.Periodicity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    task: Task
) {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val dateString = formatter.format(Date(task.date))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Détail de la tâche",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(task.title, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Date : $dateString", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        if (task.isDone) {
            Text("La tâche est faîte", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("La tâche n'est pas faîte", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))
        val repeatText = when (task.repeatType) {
            Periodicity.NONE -> "Pas de répétition"
            Periodicity.DAILY -> "Tous les jours"
            Periodicity.WEEKLY -> "Toutes les semaines"
            Periodicity.MONTHLY -> "Tous les mois"
            else -> {}
        }
        Text(
            text = "Répétition : $repeatText",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Priorité : ${task.priority}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Retour")
        }
    }
}