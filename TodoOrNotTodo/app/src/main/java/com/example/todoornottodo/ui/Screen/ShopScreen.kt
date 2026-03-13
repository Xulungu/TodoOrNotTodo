package com.example.todoornottodo.ui.Screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todoornottodo.ViewModel.TaskViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

data class ShopItem(
    val id: Int,
    val name: String,
    val price: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavHostController,
    viewModel: TaskViewModel
) {

    var totalPoints by remember { mutableStateOf(0) }
    var faahPurchased by remember { mutableStateOf(false) }
    var themePurchased by remember { mutableStateOf(false) }
    var customSoundUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        totalPoints = viewModel.getTotalPoints()
        faahPurchased = viewModel.isFaahPurchased()
        themePurchased = viewModel.isRandomThemeUnlocked()
        customSoundUri = viewModel.getCustomSoundUri()
    }

    val pickAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                customSoundUri = it
                viewModel.setCustomSoundUri(it)
            }
        }
    )

    val items = listOf(
        ShopItem(1, "Son Faaaah", 150),
        ShopItem(2, "Thème aléatoire", 250),
        ShopItem(3, "Son custom", 500)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Boutique") },
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
                .padding(15.dp)
        ) {

            Text(
                "⭐ Points disponibles : $totalPoints",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {

                items(items) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(item.name)

                            when (item.id) {

                                // 🔊 SON FAAAAH
                                1 -> {

                                    if (faahPurchased) {

                                        Text("Acheté ✅")

                                    } else {

                                        Button(
                                            onClick = {
                                                if (totalPoints >= item.price) {
                                                    viewModel.spendPoints(item.price)
                                                    viewModel.buyFaah()
                                                    totalPoints -= item.price
                                                    faahPurchased = true
                                                }
                                            }
                                        ) {
                                            Text("Acheter (${item.price}⭐)")
                                        }

                                    }

                                }

                                // 🎨 THÈME ALÉATOIRE
                                2 -> {

                                    if (themePurchased) {

                                        Text("Acheté ✅")

                                    } else {

                                        Button(
                                            onClick = {
                                                if (totalPoints >= item.price) {
                                                    viewModel.spendPoints(item.price)
                                                    viewModel.unlockRandomTheme()
                                                    totalPoints -= item.price
                                                    themePurchased = true
                                                }
                                            }
                                        ) {
                                            Text("Acheter (${item.price}⭐)")
                                        }

                                    }

                                }

                                // 🔊 SON CUSTOM
                                3 -> {

                                    Button(
                                        onClick = {
                                            if (totalPoints >= item.price) {
                                                // Ouvrir le sélecteur de fichiers audio
                                                pickAudioLauncher.launch(arrayOf("audio/*"))
                                                // On retire les points après sélection
                                                totalPoints -= item.price
                                                viewModel.spendPoints(item.price)
                                            }
                                        }
                                    ) {
                                        Text(
                                            if (customSoundUri != null) "Modifier son custom 🎵"
                                            else "Acheter et choisir son son 🎵"
                                        )
                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

    }

}