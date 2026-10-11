package com.kennethdo.week3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kennethdo.week3.ui.theme.Week3Theme
import kotlinx.coroutines.delay

class ReactionTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReactionTest(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class GameState {
    AWAL,
    MENUNGGU,
    DITEKAN,
    TRIAL,
    FINAL,
    GAGAL,
    RESTART
}

var counter by mutableIntStateOf(0)
val reactionTimes = mutableListOf("-", "-", "-")

@Composable
fun ReactionTest(modifier: Modifier = Modifier) {
    var state by rememberSaveable {mutableStateOf(GameState.AWAL)}
    when (state) {
        GameState.AWAL -> TampilanAwal(
            clickStart = {state = GameState.MENUNGGU}
        )
        GameState.MENUNGGU -> TampilanMenunggu(
            waktuDitekan = {state = GameState.DITEKAN},
            gagal = {
                android.util.Log.d("ReactionTest", "Fail callback called!")
                state = GameState.GAGAL
            }
        )
        GameState.DITEKAN -> TampilanDitekan(
            trial = {state = GameState.TRIAL},
            final = {state = GameState.FINAL},
            gagal = {state = GameState.GAGAL}
        )
        GameState.TRIAL -> TampilanTrial(
            continueTrial = {state = GameState.MENUNGGU}
        )
        GameState.FINAL -> TampilanFinal(
            keStart = {state = GameState.AWAL}
        )
        GameState.GAGAL -> TampilanGagal(
            tryAgain = {state = GameState.RESTART}
        )
        GameState.RESTART -> TampilanRestart(
            clickStart = {state = GameState.MENUNGGU}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReactionTestPreview() {
    Week3Theme {
        ReactionTest()
    }
}

@Composable
fun TampilanAwal(clickStart: () -> Unit) {
    counter = 0
    reactionTimes[0] = "-"
    reactionTimes[1] = "-"
    reactionTimes[2] = "-"
    Surface(
        color = Color(0xFF72CEDD),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = clickStart)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reaction",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.reaction),
                contentDescription = "Lightning Bolt",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Test",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Click to Start",
            )
        }
    }
}

@Composable
fun TampilanMenunggu(waktuDitekan: () -> Unit, gagal: () -> Unit) {
    var sudahHijau by rememberSaveable {mutableStateOf(false)}
    LaunchedEffect(Unit) {
        val randomDelay = (500..4500).random().toLong()
        delay(randomDelay)
        sudahHijau = true
        waktuDitekan()
    }
    Surface(
        color = Color(0xFFDBDBDB),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable (onClick = gagal)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Get Ready",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.get_ready),
                contentDescription = "Warning Sign",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Wait for green light...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "DON'T CLICK YET!"
            )
        }
    }
}

@Composable
fun TampilanDitekan(trial: () -> Unit, final: () -> Unit, gagal: () -> Unit) {
    val startTime = rememberSaveable {System.currentTimeMillis()}
    Surface(
        color = Color(0xFF4CAF50),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                val reactionTime = (System.currentTimeMillis() - startTime).toString() + "ms"
                reactionTimes[counter] = reactionTime
                counter++
                if (counter == 1) {
                    trial()
                } else if (counter == 2) {
                    trial()
                } else if (counter == 3) {
                    final()
                }
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "GO!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.go),
                contentDescription = "Person Running",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "CLICK NOW!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text("TAP AS FAST AS YOU CAN!")
        }
    }
}

@Composable
fun TampilanTrial(continueTrial: () -> Unit) {
    Surface(
        color = Color(0xFF4CAF50),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = continueTrial)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Trial $counter Complete!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.trial_complete),
                contentDescription = "Check",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            val time = reactionTimes.getOrElse(counter - 1) {"-"}
            Text(
                text = "Time: $time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Continue to Next")
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TampilanFinal(keStart: () -> Unit) {
    var sum = 0
    for (reactionTime in reactionTimes) {
        val reactionTimeNumber = reactionTime.filter {it.isDigit()}
        val reactionTimeInt = reactionTimeNumber.toIntOrNull()
        if (reactionTimeInt != null) {
            sum += reactionTimeInt
        }
    }
    val average = sum / counter
    if (average < 180) {
        Tampilan180(keStart, average)
    } else if (average < 280) {
        Tampilan280(keStart, average)
    } else if (average < 450) {
        Tampilan450(keStart, average)
    } else {
        TampilanSelebihnya(keStart, average)
    }
}

@Composable
fun Tampilan180(keStart: () -> Unit, average: Int) {
    Surface(
        color = Color(0xFF00E676),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = keStart)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DANG YOU ARE SO FAST BRO!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.dang_you_are_so_fast_bro),
                contentDescription = "Salute",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Average: ${average}ms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Click to Start New Test")
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                    Text(
                        text = "Average Score",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${average}ms",
                        color = Color(0xFFF3822D),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row() { }
                }
            }
        }
    }
}

@Composable
fun Tampilan280(keStart: () -> Unit, average: Int) {
    Surface(
        color = Color(0xFF2196F3),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = keStart)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "YOUR REFLEX IS GOOD",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.your_reflex_is_good),
                contentDescription = "Thumbs Up",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Average: ${average}ms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Click to Start New Test")
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                    Text(
                        text = "Average Score",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${average}ms",
                        color = Color(0xFFF3822D),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row() { }
                }
            }
        }
    }
}

@Composable
fun Tampilan450(keStart: () -> Unit, average: Int) {
    Surface(
        color = Color(0xFFFF9700),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = keStart)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MEH LIKE OTHER PERSON",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.meh_like_other_person),
                contentDescription = "Standard",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Average: ${average}ms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Click to Start New Test")
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                    Text(
                        text = "Average Score",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${average}ms",
                        color = Color(0xFFF3822D),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row() { }
                }
            }
        }
    }
}

@Composable
fun TampilanSelebihnya(keStart: () -> Unit, average: Int) {
    Surface(
        color = Color(0xFFFE5722),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = keStart)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "YOU LIKE A SNAIL BRO",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.you_like_a_snail_bro),
                contentDescription = "Thumbs Down",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Average: ${average}ms",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Click to Start New Test")
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                    Text(
                        text = "Average Score",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${average}ms",
                        color = Color(0xFFF3822D),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row() { }
                }
            }
        }
    }
}

@Composable
fun TampilanGagal(tryAgain: () -> Unit) {
    Surface(
        color = Color(0xFFFE4445),
        contentColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FAIL!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.fail),
                contentDescription = "Thumb Down",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "You clicked too early, TRY TO READ THE RULE BRO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "TRY AGAIN",
                modifier = Modifier.clickable(onClick = tryAgain)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TampilanRestart(clickStart: () -> Unit) {
    Surface(
        color = Color(0xFF72CEDD),
        contentColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reaction",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Icon(
                painter = painterResource(id = R.drawable.reaction),
                contentDescription = "Lightning Bolt",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Test",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Click to Start",
                modifier = Modifier.clickable(onClick = clickStart)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Results",
                        color = Color(0xFF3F8AD0),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "1",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[0],
                                color = if (reactionTimes[0] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "2",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[1],
                                color = if (reactionTimes[1] == "-") Color.Gray else Color.Black
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "3",
                                color = Color.Gray
                            )
                            Text(
                                text = reactionTimes[2],
                                color = if (reactionTimes[2] == "-") Color.Gray else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}