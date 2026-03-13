package com.example.todoornottodo.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.ViewModel.TaskViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: TaskViewModel
) {

    var soundEnabled by remember { mutableStateOf(false) }
    var selectedSound by remember { mutableStateOf("default") }
    var themeRandomUnlocked by remember { mutableStateOf(false) }

    var soundMenuExpanded by remember { mutableStateOf(false) }
    var customSoundUri by remember { mutableStateOf<String?>(null) } // URI du son custom

    // Liste de base des sons
    val baseSounds = listOf(
        "default",
        "faah"
    )

    // Liste finale des sons jouables : base + custom si disponible
    val sounds = remember(baseSounds, customSoundUri) {
        if (customSoundUri != null) baseSounds + listOf(customSoundUri!!) else baseSounds
    }

    LaunchedEffect(Unit) {
        soundEnabled = viewModel.isSoundEnabled()
        selectedSound = viewModel.getSelectedSound()
        themeRandomUnlocked = viewModel.isRandomThemeUnlocked()
        customSoundUri = viewModel.getSelectedSound().takeIf { it !in baseSounds }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "Préférences",
                style = MaterialTheme.typography.titleLarge
            )

            // 🔊 Activer les sons
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Activer les sons")

                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.setSoundEnabled(it)
                        }
                    )
                }
            }

            // 🔊 Choix du son
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text("Son de validation")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { soundMenuExpanded = true }
                    ) {
                        Text(selectedSound)
                    }

                    DropdownMenu(
                        expanded = soundMenuExpanded,
                        onDismissRequest = { soundMenuExpanded = false }
                    ) {

                        // Affiche tous les sons
                        sounds.forEach { sound ->

                            DropdownMenuItem(
                                text = { Text(sound) },
                                onClick = {
                                    selectedSound = sound
                                    soundMenuExpanded = false
                                    viewModel.setSelectedSound(sound)
                                }
                            )
                        }

                        // Option pour ajouter un son custom depuis le téléphone
                        DropdownMenuItem(
                            text = { Text("Choisir un son custom…") },
                            onClick = {
                                soundMenuExpanded = false
                                // Ici, tu peux lancer un intent pour choisir un son depuis le téléphone
                                // Par exemple via ActivityResultLauncher<Intent>
                                // Après sélection, tu mets le URI dans `customSoundUri` et `selectedSound`
                            }
                        )
                    }
                }
            }

            // 🎨 Thème aléatoire
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Text("Thème de l'application")

                    Spacer(modifier = Modifier.height(10.dp))

                    if (themeRandomUnlocked) {

                        Button(
                            onClick = {
                                viewModel.applyRandomTheme()
                            }
                        ) {
                            Text("Appliquer un thème aléatoire 🎨")
                        }

                    } else {

                        Text(
                            "Thème aléatoire verrouillé.\nAchetez-le dans la boutique."
                        )
                    }
                }
            }
        }
    }
}