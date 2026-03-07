package org.bridgwatercarnival.companion.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bridgwatercarnival.companion.PageIndex
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavButton(
    pageIndex: PageIndex,
    text: String,
    icon: DrawableResource,
    onClick: () -> Unit = {},
    color: Color = MaterialTheme.colors.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .width(80.dp)
    ) {
        Card(
            modifier = Modifier.size(64.dp),
            backgroundColor = if (MaterialTheme.colors.isLight) {
                color.copy(alpha = 0.08f)  // More subtle in light mode
            } else {
                color.copy(alpha = 0.15f)  // Slightly more visible in dark mode
            },
            elevation = 0.dp,  // Removed elevation
            shape = RoundedCornerShape(16.dp)
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = text,
                    tint = color,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
} 