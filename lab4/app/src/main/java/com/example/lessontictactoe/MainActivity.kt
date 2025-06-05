package com.example.lessontictactoe

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lessontictactoe.ui.theme.LessonTicTacToeTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var gameStarted by remember { mutableStateOf(false) }
    var scoreX by remember { mutableStateOf(0) }
    var scoreO by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Dark theme", modifier = Modifier.padding(end = 8.dp))
            Switch(checked = isDarkTheme, onCheckedChange = { onToggleTheme() })
        }

        Text(
            text = "Хрестики Нолики",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        if (!gameStarted) {
            Spacer(modifier = Modifier.height(100.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    gameStarted = true
                }) {
                    Text("Start Game")
                }
            }
        } else {
            GameBoard(
                onGameStop = {
                    gameStarted = false
                    scoreX = 0
                    scoreO = 0
                },
                scoreX = scoreX,
                scoreO = scoreO,
                onScoreUpdate = { isX -> if (isX) scoreX++ else scoreO++ }
            )
        }
    }
}

@Composable
fun GameBoard(
    onGameStop: () -> Unit,
    scoreX: Int,
    scoreO: Int,
    onScoreUpdate: (Boolean) -> Unit
) {
    val dim = 3
    val field = remember { mutableStateListOf(*Array(dim * dim) { "" }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var timer by remember { mutableStateOf(10) }
    var timerActive by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var roundResult by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text("Score — X: $scoreX | O: $scoreO", style = MaterialTheme.typography.bodyLarge)
        Text("Time left: $timer s", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))

        LaunchedEffect(currentPlayer, isRunning) {
            if (!isRunning) return@LaunchedEffect
            timer = 10
            timerActive = true
            while (timer > 0 && timerActive) {
                delay(1000)
                timer--
            }
            if (timer == 0 && isRunning) {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
        }

        for (row in 0 until dim) {
            Row {
                for (col in 0 until dim) {
                    val index = row * dim + col
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            .clickable(enabled = field[index] == "" && isRunning) {
                                field[index] = currentPlayer
                                timerActive = false

                                if (checkWin(field, currentPlayer, dim)) {
                                    roundResult = "Player $currentPlayer wins!"
                                    onScoreUpdate(currentPlayer == "X")
                                    showDialog = true
                                } else if (field.none { it == "" }) {
                                    roundResult = "Draw!"
                                    showDialog = true
                                } else {
                                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = field[index],
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Round Result") },
                text = { Text(roundResult) },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                for (i in field.indices) field[i] = ""
                                currentPlayer = "X"
                                showDialog = false
                                isRunning = true
                                timerActive = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 8.dp)
                        ) {
                            Text("Restart Round")
                        }

                        Button(
                            onClick = {
                                isRunning = false
                                timerActive = false
                                showDialog = false
                                onGameStop()
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 8.dp)
                        ) {
                            Text("Stop Game")
                        }
                    }
                }
            )
        }
    }
}

fun checkWin(field: List<String>, player: String, dim: Int): Boolean {
    for (i in 0 until dim) {
        if ((0 until dim).all { j -> field[i * dim + j] == player }) return true
    }
    for (j in 0 until dim) {
        if ((0 until dim).all { i -> field[i * dim + j] == player }) return true
    }
    if ((0 until dim).all { i -> field[i * dim + i] == player }) return true
    if ((0 until dim).all { i -> field[i * dim + (dim - i - 1)] == player }) return true
    return false
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LessonTicTacToeTheme {
        MainScreen(
            isDarkTheme = false,
            onToggleTheme = {}
        )
    }
}