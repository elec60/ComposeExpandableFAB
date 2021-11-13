package com.mousavi.expandablefab

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mousavi.expandablefab.ui.theme.ExpandableFabTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpandableFabTheme {

                var fabState by remember {
                    mutableStateOf(FabState.Collapsed)
                }

                Scaffold(
                    modifier = Modifier,
                    floatingActionButton = {
                        ExpandableFab(
                            fabState = fabState,
                            items = listOf(
                                FabData("Email", Icons.Default.Email, tint = Color.White),
                                FabData("Favorite", Icons.Default.Favorite, tint = Color.White),
                                FabData("Settings", Icons.Default.Settings, tint = Color.White),
                            ),
                            toggle = {
                                fabState = if (fabState == FabState.Collapsed) {
                                    FabState.Expanded
                                } else {
                                    FabState.Collapsed
                                }
                            },
                            itemClicked = {
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

enum class FabState {
    Expanded,
    Collapsed
}


@Composable
fun ExpandableFab(
    items: List<FabData>,
    fabState: FabState,
    toggle: () -> Unit = {},
    itemClicked: (String) -> Unit = {},
) {
    var firstTime by remember {
        mutableStateOf(true)
    }

    var rotate = animateFloatAsState(targetValue = if (fabState == FabState.Collapsed) 0f else 45f)

    val animateValues = items.mapIndexed { index, fabData ->
        var animatedValue by remember { mutableStateOf(0f) }

        LaunchedEffect(key1 = fabState) {
            if (firstTime) return@LaunchedEffect
            delay(200L * index)

            animate(
                initialValue = if (fabState == FabState.Collapsed) 1f else 0f,
                targetValue = if (fabState == FabState.Expanded) 1f else 0f,
                animationSpec = tween(durationMillis = 400)
            ) { value, _ ->
                animatedValue = value
            }
        }
        animatedValue
    }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        items.forEachIndexed { index, fabData ->
            FabItem(
                fabData = fabData,
                scale = if (fabState == FabState.Expanded) animateValues[items.size - index - 1] else animateValues[index]) {
                itemClicked(it)
            }
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
        }
        Spacer(modifier = Modifier.padding(vertical = 6.dp))


        FloatingActionButton(
            modifier = Modifier.size(56.dp).rotate(rotate.value),
            onClick = {
                firstTime = false
                toggle()
            },
            backgroundColor = Color.Blue
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                tint = Color.White
            )
        }
    }

}


@Composable
fun FabItem(
    fabData: FabData,
    scale: Float,
    onclick: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .scale(scale)
            .offset(x = (-4).dp, y = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fabData.text,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
        IconButton(modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color = Color.Blue),
            onClick = {
                onclick(fabData.text)
            }) {
            Icon(
                imageVector = fabData.icon,
                contentDescription = null,
                tint = fabData.tint
            )
        }
    }
}

@Preview
@Composable
fun FabItemPreview() {
    FabItem(
        fabData = FabData("Email", Icons.Default.Email, tint = Color.White),
        scale = 1f
    )
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
    ExpandableFabTheme {
        ExpandableFab(
            fabState = FabState.Expanded,
            items = listOf(
                FabData("Email", Icons.Default.Email, tint = Color.White),
                FabData("Favorite", Icons.Default.Favorite, tint = Color.White),
                FabData("Settings", Icons.Default.Settings, tint = Color.White),
            )
        )
    }
}