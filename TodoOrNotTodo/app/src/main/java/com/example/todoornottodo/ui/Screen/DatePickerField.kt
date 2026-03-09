package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text("Date") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        readOnly = true
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {

                    datePickerState.selectedDateMillis?.let {
                        val date = formatter.format(Date(it))
                        onDateSelected(date)
                    }

                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}