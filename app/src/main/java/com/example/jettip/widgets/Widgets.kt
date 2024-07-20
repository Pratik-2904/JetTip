package com.example.jettip.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val IconButtonSizeModifier = Modifier.size(40.dp)


@Preview
@Composable
fun RoundIconButton(
    imageVector: ImageVector = Icons.Default.Add,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    tint : Color = MaterialTheme.colorScheme.onBackground,
//    tint: Color = Color.Black.copy(alpha = 0.8f),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    elevation: Dp = 4.dp
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable { onClick.invoke() }
            .then(IconButtonSizeModifier),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Plus or Minus Icon",
            tint = tint,
            modifier = Modifier.padding(10.dp)
        )
    }
}