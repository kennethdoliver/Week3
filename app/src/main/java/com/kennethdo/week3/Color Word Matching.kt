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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

class ColorWordMatchingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColorWordMatching(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class ColorGameState {
    INITIAL, COUNTDOWN, RUNNING, GAME_OVER
}

enum class Mode {
    COLOR, TEXT
}

enum class Choice {
    INK, WORD
}

enum class ColorName(val color: Color) {
    RED(Color(0xFFE53935)),
    BLUE(Color(0xFF1E88E5)),
    GREEN(Color(0xFF43A047)),
    YELLOW(Color(0xFFFDD835))
}

data class ColorWordQuestion(
    val word: ColorName,
    val ink: ColorName,
    val mode: Mode,
    val inkOnLeft: Boolean
)

private fun createQuestion(): ColorWordQuestion {
    val word = ColorName.values().random()
    val ink = ColorName.values()
        .filter { it != word }
        .random()

    return ColorWordQuestion(
        word = word,
        ink = ink,
        mode = Mode.values().random(),
        inkOnLeft = Random.nextBoolean()
    )
}

@Composable
fun ColorWordMatching(modifier: Modifier = Modifier) {

    var gameState by rememberSaveable {
        mutableStateOf(ColorGameState.INITIAL)
    }

    var question by remember {
        mutableStateOf(createQuestion())
    }

    var score by rememberSaveable {
        mutableIntStateOf(0)
    }

    var mistakes by rememberSaveable {
        mutableIntStateOf(0)
    }

    var bestScore by rememberSaveable {
        mutableIntStateOf(0)
    }

    var remainingMillis by remember {
        mutableIntStateOf(5000)
    }

    var countdownText by remember {
        mutableStateOf("3")
    }

    var questionId by remember {
        mutableIntStateOf(0)
    }

    var answered by remember {
        mutableStateOf(false)
    }

    fun registerWrongAnswer() {
        if (gameState != ColorGameState.RUNNING || answered) return

        answered = true
        mistakes++

        if (mistakes >= 3) {
            gameState = ColorGameState.GAME_OVER
        } else {
            question = createQuestion()
            questionId++
        }
    }

    fun registerCorrectAnswer() {
        if (gameState != ColorGameState.RUNNING || answered) return

        answered = true
        score++

        if (score > bestScore) {
            bestScore = score
        }

        question = createQuestion()
        questionId++
    }

    LaunchedEffect(gameState) {
        if (gameState == ColorGameState.COUNTDOWN) {
            for (number in 3 downTo 1) {
                countdownText = number.toString()
                delay(700)
            }

            countdownText = "Start!"
            delay(700)

            gameState = ColorGameState.RUNNING
        }
    }

    LaunchedEffect(gameState, questionId) {
        if (gameState == ColorGameState.RUNNING) {
            remainingMillis = 5000
            answered = false

            while (remainingMillis > 0 && !answered) {
                delay(50)

                if (!answered) {
                    remainingMillis =
                        (remainingMillis - 50).coerceAtLeast(0)
                }
            }

            if (remainingMillis == 0 && !answered) {
                registerWrongAnswer()
            }
        }
    }

    fun selectAnswer(choice: Choice) {
        if (gameState != ColorGameState.RUNNING || answered) return

        val correctChoice = when (question.mode) {
            Mode.COLOR -> Choice.INK
            Mode.TEXT -> Choice.WORD
        }

        if (choice == correctChoice) {
            registerCorrectAnswer()
        } else {
            registerWrongAnswer()
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
                ColorGameState.INITIAL -> {
                    Text(
                        text = "COLOR WORD",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF303F9F)
                    )

                    Text(
                        text = "MATCHING",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF303F9F)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Test your focus and reaction!",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = "Best Score: $bestScore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            score = 0
                            mistakes = 0
                            question = createQuestion()
                            questionId++
                            gameState = ColorGameState.COUNTDOWN
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("START GAME", fontSize = 18.sp)
                    }
                }

                ColorGameState.COUNTDOWN -> {
                    Text(
                        text = "GET READY!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303F9F)
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = countdownText,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF7043)
                    )
                }

                ColorGameState.RUNNING -> {
                    Text(
                        text = "COLOR WORD MATCHING",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SCORE",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = "$score",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "MISTAKES",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Text(
                                text = "$mistakes / 3",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8EAF6)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "MODE: ${question.mode.name}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF303F9F)
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = when (question.mode) {
                                    Mode.COLOR -> "Pilih WARNA TINTA"
                                    Mode.TEXT -> "Pilih NAMA KATA"
                                },
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Time: ${
                            String.format(
                                "%.1f",
                                remainingMillis / 1000f
                            )
                        } s",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingMillis <= 1500) {
                            Color(0xFFE53935)
                        } else {
                            Color.DarkGray
                        }
                    )

                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = question.word.name,
                                color = question.ink.color,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = "Choose your answer:",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val leftChoice = if (question.inkOnLeft) {
                            Choice.INK
                        } else {
                            Choice.WORD
                        }

                        val rightChoice = if (question.inkOnLeft) {
                            Choice.WORD
                        } else {
                            Choice.INK
                        }

                        Button(
                            onClick = {
                                selectAnswer(leftChoice)
                            },
                            enabled = !answered,
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (leftChoice == Choice.INK) {
                                    question.ink.name
                                } else {
                                    question.word.name
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                selectAnswer(rightChoice)
                            },
                            enabled = !answered,
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (rightChoice == Choice.INK) {
                                    question.ink.name
                                } else {
                                    question.word.name
                                },
                                fontSize = 16.sp,
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

                ColorGameState.GAME_OVER -> {
                    Text(
                        text = "GAME OVER",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFE53935)
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Final Score",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "$score",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF303F9F)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Best Score: $bestScore",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Mistakes: $mistakes / 3",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            score = 0
                            mistakes = 0
                            question = createQuestion()
                            questionId++
                            gameState = ColorGameState.COUNTDOWN
                        },
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
                            gameState = ColorGameState.INITIAL
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
fun ColorWordMatchingPreview() {
    Week3Theme {
        ColorWordMatching()
    }
}