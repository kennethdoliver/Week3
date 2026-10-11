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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kennethdo.week3.ui.theme.Week3Theme
import kotlin.math.ceil

class ClickerGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ClickerGame(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ClickerGame(modifier: Modifier = Modifier) {
    var coin by rememberSaveable {mutableIntStateOf(0)}
    var value by rememberSaveable {mutableIntStateOf(1)}
    var nextValue = ceil(value * 1.5).toInt()
    var upgrade by rememberSaveable {mutableIntStateOf(10)}
    var coinUntilNextUpgrade = upgrade - coin
    var canUpgrade = coinUntilNextUpgrade <= 0
    var catState by rememberSaveable {mutableStateOf(false)}
    val catPic = if (catState) {
        R.drawable.cat_open_mouth
    } else {
        R.drawable.cat_closed_mouth
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.cat_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White.copy(0.25f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Coins",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "$coin",
                        color = Color.Green,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp
                    )
                    Text(
                        text = "$value coins per tap",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Tap the Cat!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Image(
                painter = painterResource(catPic),
                contentDescription = "Cat",
                modifier = Modifier.clickable {
                    catState = true
                    coin += value
                    coinUntilNextUpgrade = upgrade - coin
                    catState = false
                }
            )
            Text(
                text = "Purr~",
                color = Color.White
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
                        text = "Give Me Your Coin",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = "Next Upgrade: $nextValue coins per tap",
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    val buttonText = if (canUpgrade) {
                        "Pay for $upgrade coins"
                    } else {
                        "Find $coinUntilNextUpgrade more coins"
                    }
                    Button(
                        onClick = {
                            coin -= upgrade
                            upgrade *= 2
                            value = nextValue
                        },
                        enabled = canUpgrade,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Text(
                            text = buttonText
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClickerGamePreview() {
    Week3Theme {
        ClickerGame()
    }
}