package com.example.todoornottodo.ui.Screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.Data.Task
import com.example.todoornottodo.utils.Periodicity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.AsyncImage
@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    task: Task
) {

    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val dateString = formatter.format(Date(task.date))

    val repeatText = when (task.repeatType) {
        Periodicity.NONE -> "Pas de répétition"
        Periodicity.DAILY -> "Tous les jours"
        Periodicity.WEEKLY -> "Toutes les semaines"
        Periodicity.MONTHLY -> "Tous les mois"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = "Détail de la tâche",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Date : $dateString",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (task.isDone)
                "La tâche est faite"
            else
                "La tâche n'est pas faite",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Répétition : $repeatText",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Priorité : ${task.priority}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Description :",
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (task.taskDescription.isBlank()) "Aucune description" else task.taskDescription,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        task.imageUri?.let {

            Text(
                text = "Image :",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = Uri.parse(it),
                contentDescription = "Image de la tâche",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retour")
        }
    }
}