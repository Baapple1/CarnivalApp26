package org.bridgwatercarnival.companion.pages.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun StoreTile(product: Product, onClick: () -> Unit) {
    val painter = rememberAsyncImagePainter(product.product_image_full);
    val showPrice = product.price != "0.00"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.surface)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Image(
                painter = painter,
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            )

            if (showPrice) {
                Text(
                    text = "£${product.price}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(4.dp),
                            clip = false
                        )
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.primaryVariant)
                        .padding(8.dp)
                )
            }
        }

        Text(
            text = product.title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.70f))
        )
    }
}
