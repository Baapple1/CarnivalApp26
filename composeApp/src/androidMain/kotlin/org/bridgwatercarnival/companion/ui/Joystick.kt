package org.bridgwatercarnival.companion.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onDirectionChange: (Float, Float) -> Unit
) {
    var stickPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Animation for the outer ring
    val infiniteTransition = rememberInfiniteTransition()
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Pulse animation for the inner circle when not dragging
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .size(150.dp)  // Increased size
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            stickPosition = offset
                        },
                        onDragEnd = {
                            isDragging = false
                            stickPosition = Offset.Zero
                            onDirectionChange(0f, 0f)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newPosition = stickPosition + dragAmount
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val maxDistance = min(size.width, size.height) / 3f

                            val distance = hypot(newPosition.x - center.x, newPosition.y - center.y)
                            stickPosition = if (distance > maxDistance) {
                                val angle = atan2(newPosition.y - center.y, newPosition.x - center.x)
                                Offset(
                                    center.x + maxDistance * cos(angle),
                                    center.y + maxDistance * sin(angle)
                                )
                            } else {
                                newPosition
                            }

                            val normalizedX = (stickPosition.x - center.x) / maxDistance
                            val normalizedY = (stickPosition.y - center.y) / maxDistance
                            onDirectionChange(normalizedX, normalizedY)
                        }
                    )
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            
            // Draw rotating outer ring
            rotate(ringRotation, center) {
                for (i in 0..7) {
                    rotate(i * 45f, center) {
                        drawCircle(
                            color = Color(0x40FFFFFF),
                            radius = size.minDimension / 2.2f,
                            center = center,
                            style = Stroke(
                                width = 4f,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(15f, 15f),
                                    0f
                                )
                            )
                        )
                    }
                }
            }

            // Draw outer guide circle
            drawCircle(
                color = Color(0x60FFFFFF),
                radius = size.minDimension / 2.5f,
                center = center
            )

            // Draw direction guides
            val guideRadius = size.minDimension / 2.5f
            for (i in 0..3) {
                rotate(i * 90f, center) {
                    drawLine(
                        color = Color(0x40FFFFFF),
                        start = Offset(center.x, center.y - guideRadius),
                        end = Offset(center.x, center.y + guideRadius),
                        strokeWidth = 2f
                    )
                }
            }

            // Draw stick with pulse animation when not dragging
            val stickCenter = if (isDragging) stickPosition else center
            val stickRadius = if (isDragging) 
                size.minDimension / 6f
            else
                (size.minDimension / 6f) * pulseAnim

            // Stick shadow
            drawCircle(
                color = Color(0x40000000),
                radius = stickRadius + 2f,
                center = Offset(stickCenter.x + 2f, stickCenter.y + 2f)
            )

            // Main stick
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFE0E0E0)
                    ),
                    center = stickCenter,
                    radius = stickRadius
                ),
                radius = stickRadius,
                center = stickCenter
            )
        }

        // Optional: Add hint text when not dragging
        if (!isDragging) {
            Text(
                "Drag to Move",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
} 