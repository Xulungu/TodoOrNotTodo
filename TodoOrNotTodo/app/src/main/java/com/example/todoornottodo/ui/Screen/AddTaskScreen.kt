package com.example.todoornottodo.ui.Screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.todoornottodo.ViewModel.TaskViewModel
import com.example.todoornottodo.utils.Periodicity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavHostController,
    viewModel: TaskViewModel
) {

    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var repeatType by remember { mutableStateOf(Periodicity.NONE) }
    var repeatMenuExpanded by remember { mutableStateOf(false) }
    var taskPriority by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher robuste avec persistance
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Ajouter une tâche",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        DatePickerButton(
            selectedDate = date,
            onDateSelected = { date = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Nom de la tâche") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // MENU PERIODICITE
        Box {
            Button(
                onClick = { repeatMenuExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = taskPriority,
            onValueChange = { taskPriority = it },
            label = { Text("Priorité (1-10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton choisir image
        Button(
            onClick = { launcher.launch(arrayOf("image/*")) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter une image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aperçu image
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Image sélectionnée",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (text.isNotBlank() && date.isNotBlank()) {
                    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val timestamp = formatter.parse(date)?.time ?: System.currentTimeMillis()
                    val priority = taskPriority.toIntOrNull()?.coerceIn(1, 10) ?: 1

                    viewModel.addTask(
                        text,
                        timestamp,
                        repeatType,
                        priority,
                        taskDescription,
                        imageUri?.toString()
                    )

                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val formatter =
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            Icons.Default.DateRange,
            contentDescription = "Choisir date"
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            if (selectedDate.isEmpty())
                "Choisir une date"
            else
                selectedDate
        )
    }

    if (showDialog) {

        DatePickerDialog(
            onDismissRequest = { showDialog = false },

            confirmButton = {

                Button(
                    onClick = {

                        datePickerState.selectedDateMillis?.let {

                            onDateSelected(
                                formatter.format(Date(it))
                            )
                        }

                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {

                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }
}