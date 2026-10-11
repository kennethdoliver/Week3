package com.kennethdo.week3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kennethdo.week3.ui.theme.Week3Theme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class RockPaperScissorsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RockPaperScissors(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class Rps(
    val label: String,
    val emoji: String
) {
    ROCK("Rock", "✊"),
    PAPER("Paper", "✋"),
    SCISSOR("Scissor", "✌️")
}

enum class State {
    INITIAL, PICK, REVEAL, FINISHED
}

enum class Result {
    WIN, LOSE, DRAW
}

fun determineResult(player: Rps, cpu: Rps): Result {
    if (player == cpu) return Result.DRAW

    return when (player) {
        Rps.ROCK ->
            if (cpu == Rps.SCISSOR) Result.WIN else Result.LOSE

        Rps.PAPER ->
            if (cpu == Rps.ROCK) Result.WIN else Result.LOSE

        Rps.SCISSOR ->
            if (cpu == Rps.PAPER) Result.WIN else Result.LOSE
    }
}

@Composable
fun RockPaperScissors(modifier: Modifier = Modifier) {

    val bestOf = 3
    val targetScore = bestOf / 2 + 1

    var gameState by rememberSaveable {
        mutableStateOf(State.INITIAL)
    }

    var playerScore by rememberSaveable {
        mutableIntStateOf(0)
    }

    var cpuScore by rememberSaveable {
        mutableIntStateOf(0)
    }

    var bestScore by rememberSaveable {
        mutableIntStateOf(0)
    }

    var playerChoice by rememberSaveable {
        mutableStateOf<Rps?>(null)
    }

    var cpuChoice by rememberSaveable {
        mutableStateOf<Rps?>(null)
    }

    var roundResult by rememberSaveable {
        mutableStateOf<Result?>(null)
    }

    var buttonOrder by remember {
        mutableStateOf(Rps.values().toList().shuffled())
    }

    fun startNewMatch() {
        playerScore = 0
        cpuScore = 0

        playerChoice = null
        cpuChoice = null
        roundResult = null

        buttonOrder = Rps.values().toList().shuffled()
        gameState = State.PICK
    }

    fun play(move: Rps) {
        if (gameState != State.PICK) return

        val computerMove = Rps.values().random()
        val result = determineResult(move, computerMove)

        playerChoice = move
        cpuChoice = computerMove
        roundResult = result

        when (result) {
            Result.WIN -> {
                playerScore++
                bestScore = maxOf(
                    bestScore,
                    playerScore - cpuScore
                )
            }

            Result.LOSE -> {
                cpuScore++
                bestScore = maxOf(
                    bestScore,
                    playerScore - cpuScore
                )
            }

            Result.DRAW -> {

            }
        }

        gameState = State.REVEAL
    }

    LaunchedEffect(gameState) {
        if (gameState == State.REVEAL) {
            delay(700)

            if (
                playerScore >= targetScore ||
                cpuScore >= targetScore
            ) {
                gameState = State.FINISHED
            } else {
                playerChoice = null
                cpuChoice = null
                roundResult = null

                buttonOrder = Rps.values().toList().shuffled()
                gameState = State.PICK
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF4F5FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            when (gameState) {
                State.INITIAL -> {
                    Text(
                        text = "ROCK • PAPER • SCISSORS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF303F9F)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "✊     ✋     ✌️",
                        fontSize = 48.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Challenge the computer!",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Mode: Best of $bestOf",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "First to $targetScore points wins.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick = { startNewMatch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("START GAME", fontSize = 18.sp)
                    }
                }

                State.PICK -> {
                    Text(
                        text = "ROCK • PAPER • SCISSORS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Best of $bestOf",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Text("🧑", fontSize = 32.sp)
                            Text(
                                "YOU",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$playerScore",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF303F9F)
                            )
                        }

                        Text(
                            text = "—",
                            fontSize = 28.sp,
                            color = Color.Gray
                        )

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Text("🤖", fontSize = 32.sp)
                            Text(
                                "CPU",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$cpuScore",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE53935)
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = "Pick your move!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❔", fontSize = 44.sp)
                        Text(
                            text = "VS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text("❔", fontSize = 44.sp)
                    }

                    Spacer(Modifier.height(28.dp))

                    buttonOrder.forEach { move ->
                        Button(
                            onClick = { play(move) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .height(58.dp)
                                .semantics {
                                    contentDescription =
                                        "Choose ${move.label}"
                                },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "${move.emoji}   ${move.label}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Best Score: $bestScore",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                State.REVEAL -> {
                    Text(
                        text = "ROUND RESULT",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF303F9F)
                    )

                    Spacer(Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Text("🧑", fontSize = 28.sp)
                            Text("YOU", fontWeight = FontWeight.Bold)

                            Text(
                                text = playerChoice?.emoji ?: "❔",
                                fontSize = 56.sp
                            )

                            Text(
                                text = playerChoice?.label ?: "",
                                fontSize = 16.sp
                            )
                        }

                        Text(
                            text = "VS",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Text("🤖", fontSize = 28.sp)
                            Text("CPU", fontWeight = FontWeight.Bold)

                            Text(
                                text = cpuChoice?.emoji ?: "❔",
                                fontSize = 56.sp
                            )

                            Text(
                                text = cpuChoice?.label ?: "",
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = when (roundResult) {
                            Result.WIN -> "You Win!"
                            Result.LOSE -> "You Lose!"
                            Result.DRAW -> "Draw!"
                            null -> ""
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (roundResult) {
                            Result.WIN -> Color(0xFF2E7D32)
                            Result.LOSE -> Color(0xFFE53935)
                            Result.DRAW -> Color(0xFFEF6C00)
                            null -> Color.DarkGray
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "$playerScore — $cpuScore",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Next round...",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                State.FINISHED -> {
                    val playerWon = playerScore >= targetScore

                    Text(
                        text = if (playerWon) {
                            "You Win the Match!"
                        } else {
                            "You Lose the Match!"
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = if (playerWon) {
                            Color(0xFF2E7D32)
                        } else {
                            Color(0xFFE53935)
                        }
                    )

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = "FINAL SCORE",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "$playerScore — $cpuScore",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF303F9F)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Best Score: $bestScore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { startNewMatch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("RESTART", fontSize = 18.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            playerChoice = null
                            cpuChoice = null
                            roundResult = null
                            gameState = State.INITIAL
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("EXIT", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RockPaperScissorsPreview() {
    Week3Theme {
        RockPaperScissors()
    }
}