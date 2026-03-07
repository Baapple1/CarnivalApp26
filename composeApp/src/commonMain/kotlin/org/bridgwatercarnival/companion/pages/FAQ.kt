package org.bridgwatercarnival.companion.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.bridgwatercarnival.companion.theme.bungeeFont
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bridgwatercarnival.companion.util.TranslationManager

@Composable
fun FAQ(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val isLight = MaterialTheme.colors.isLight

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = TranslationManager.translate("faq_title"),
                style = MaterialTheme.typography.h4.copy(
                    color = ThemeColors.additionalColor,
                    fontFamily = bungeeFont
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            Divider(color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            FAQItem(
                question = TranslationManager.translate("faq_q1"),
                answer = TranslationManager.translate("faq_a1"),
                color = ThemeColors.additionalColor,
                icon = "⏰"
            )
            FAQItem(
                question = TranslationManager.translate("faq_q2"),
                answer = TranslationManager.translate("faq_a2"),
                color = ThemeColors.additionalColor,
                icon = "🚗"
            )
            FAQItem(
                question = TranslationManager.translate("faq_q3"),
                answer = TranslationManager.translate("faq_a3"),
                color = ThemeColors.additionalColor,
                icon = "💷"
            )
            FAQItem(
                question = TranslationManager.translate("faq_q4"),
                answer = TranslationManager.translate("faq_a4"),
                color = ThemeColors.additionalColor,
                icon = "🎒"
            )
            FAQItem(
                question = TranslationManager.translate("faq_q5"),
                answer = TranslationManager.translate("faq_a5"),
                color = ThemeColors.additionalColor,
                icon = "♿"
            )
            FAQItem(
                question = TranslationManager.translate("faq_q6"),
                answer = TranslationManager.translate("faq_a6"),
                color = ThemeColors.additionalColor,
                icon = "🐶"
            )
            FAQItem(
                question = TranslationManager.translate("faq_q7"),
                answer = TranslationManager.translate("faq_a7"),
                color = ThemeColors.additionalColor,
                icon = "🌧️"
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FAQItem(
    question: String,
    answer: String,
    color: Color,
    icon: String = "❓"
) {
    var expanded by remember { mutableStateOf(false) }
    val isLight = MaterialTheme.colors.isLight
    val textColor = if (isLight) Color.Black else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(18.dp),
        backgroundColor = ThemeColors.additionalColor
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fun icon
                Text(
                    text = icon,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
                // Question
                Text(
                    text = question.uppercase(),
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        fontFamily = bungeeFont,
                        fontSize = 17.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                // Arrow
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = textColor
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.body1.copy(
                            color = textColor,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
} 