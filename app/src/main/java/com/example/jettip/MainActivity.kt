package com.example.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.widgets.RoundIconButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        enableEdgeToEdge()
        setContent {
            MyApp {
                BillForm { billAmt ->
                    Log.d("AMT", "MainContent: $billAmt")
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp, 30.dp, 12.dp, 12.dp)
            .clip(CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFD9B54D3)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total Per Person", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "₹${"%.2f".format(totalPerPerson)}",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
    val totalBillState = remember { mutableStateOf("") }
    val validState = totalBillState.value.trim().isNotEmpty()
    val sliderPositionState = remember { mutableFloatStateOf(0f) }
    val splitByState = remember { mutableIntStateOf(1) }
    val range = 1..100
    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()
    val tipValue = (totalBillState.value.toDoubleOrNull() ?: 0.0) * tipPercentage / 100
    val keyboardController = LocalSoftwareKeyboardController.current
    val totalPerPersonState =
        (tipValue + (totalBillState.value.toDoubleOrNull() ?: 0.0)) / splitByState.intValue

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = CircleShape.copy(all = CornerSize(8.dp)),
        border = BorderStroke(1.dp, Color.LightGray),
        color = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TopHeader(totalPerPerson = totalPerPersonState)
                InputField(
                    modifier = Modifier.padding(12.dp),
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValueChange(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )
                if (validState) {
                    SplitRow(splitByState, range)
                }
                TipRow(tipValue)
                TipSlider(sliderPositionState)
            }
        }
    }
}

@Composable
fun SplitRow(splitByState: MutableIntState, range: IntRange) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Split")
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIconButton(
                imageVector = Icons.Default.Remove,
                onClick = {
                    splitByState.intValue = maxOf(1, splitByState.intValue - 1)
                },
                onLongClick = { longPressState ->
                    longPressState.value = true
                    coroutineScope.launch {
                        while (longPressState.value) {
                            splitByState.intValue = maxOf(1, splitByState.intValue - 1)
                            delay(80)
                        }
                    }
                }
            )
            Text(
                text = "${splitByState.intValue}",
                Modifier.padding(horizontal = 9.dp)
            )
            RoundIconButton(
                imageVector = Icons.Default.Add,
                onClick = {
                    if (splitByState.intValue < range.last) {
                        splitByState.intValue += 1
                    }
                },
                longPressState = remember { mutableStateOf(false) },
                onLongClick = { longPressState ->
                    longPressState.value = true
                    coroutineScope.launch {
                        while (longPressState.value) {
                            splitByState.intValue += 1
                            delay(80)
                        }
                    }
                }

            )
        }
    }
}

@Composable
fun TipRow(tipValue: Double) {
    Row(
        modifier = Modifier
            .padding(horizontal = 3.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Tip")
        Text(text = "₹ ${"%.2f".format(tipValue)}")
    }
}

@Composable
fun TipSlider(sliderPositionState: MutableFloatState) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        val sliderPositionValue = "%.1f".format(sliderPositionState.floatValue * 100)
        Text(text = "${sliderPositionValue}%")
        Spacer(modifier = Modifier.height(14.dp))
        Slider(
            value = sliderPositionState.floatValue,
            onValueChange = { sliderPositionState.floatValue = it },
            modifier = Modifier.padding(horizontal = 16.dp),
            steps = 5,
            valueRange = 0f..0.3f
        )
    }
}
