package org.bridgwatercarnival.companion.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import org.bridgwatercarnival.companion.CarParkMap
import org.bridgwatercarnival.companion.components.DeviceDataConsentDialog
import org.bridgwatercarnival.companion.theme.bungeeFont
import org.bridgwatercarnival.companion.util.PlatformType
import org.bridgwatercarnival.companion.util.getCurrentPlatformType
import org.bridgwatercarnival.companion.util.Storage
import org.bridgwatercarnival.companion.util.StorageKeys
import org.bridgwatercarnival.companion.util.TranslatedText
import org.bridgwatercarnival.companion.components.DeviceIdConsentDialog
import org.bridgwatercarnival.companion.util.ConsentManager
import org.bridgwatercarnival.companion.util.getUriHandler

object PlannerColors {
    // Vibrant colors for both themes
    val primary = Color(0xFF00BCD4)        // Cyan
    val secondary = Color(0xFF4CAF50)      // Green
    val accent = Color(0xFFFF4081)         // Pink
    val success = Color(0xFF4CAF50)        // Green
    val warning = Color(0xFFFFC107)        // Amber

    // Theme dependent colors
    @Composable
    fun background() = if (isSystemInDarkTheme()) Color.Black else Color.White
    
    @Composable
    fun cardBackground() = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFE8F5E9)
    
    @Composable
    fun textPrimary() = if (isSystemInDarkTheme()) Color.White else Color.Black
    
    @Composable
    fun textSecondary() = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f)
}

data class PlannerItem(
    val id: Int,
    val emoji: String,
    val nameKey: String,
    val categoryKey: String
)

data class CarPark(
    val id: Int,
    val name: String,
    val address: String,
    val coordinates: String
)

private val plannerItems = listOf(
    // Essentials
    PlannerItem(1, "🎟️", "item_tickets", "category_essentials"),
    PlannerItem(2, "💳", "item_payment", "category_essentials"),
    PlannerItem(3, "📱", "item_phone", "category_essentials"),
    PlannerItem(4, "🔋", "item_charger", "category_essentials"),
    PlannerItem(5, "🪪", "item_id", "category_essentials"),
    PlannerItem(6, "🔑", "item_keys_wallet", "category_essentials"),

    // Comfort & Clothing
    PlannerItem(7, "🧥", "item_warm_clothes", "category_comfort_clothing"),
    PlannerItem(8, "🌂", "item_waterproof", "category_comfort_clothing"),
    PlannerItem(9, "👟", "item_comfortable_shoes", "category_comfort_clothing"),
    PlannerItem(10, "🧤", "item_winter_accessories", "category_comfort_clothing"),

    // Food & Drinks
    PlannerItem(11, "🍫", "item_snacks", "category_food_drinks"),
    PlannerItem(12, "🧴", "item_water_bottle", "category_food_drinks"),
    PlannerItem(13, "☕", "item_thermos", "category_food_drinks"),

    // Extras
    PlannerItem(14, "🪑", "item_folding_chair", "category_extras"),
    PlannerItem(15, "🛏️", "item_blanket", "category_extras"),
    PlannerItem(16, "☔", "item_umbrella", "category_extras"),
    PlannerItem(17, "🔭", "item_binoculars", "category_extras"),

    // For Families & Kids
    PlannerItem(18, "🎧", "item_ear_defenders", "category_family"),
    PlannerItem(19, "🌟", "item_glow_sticks", "category_family"),
    PlannerItem(20, "👕", "item_spare_clothes", "category_family"),
    PlannerItem(21, "🧼", "item_sanitizer", "category_family"),

    // For Capturing the Moment
    PlannerItem(22, "📸", "item_camera", "category_capturing"),
    PlannerItem(23, "🔋", "item_camera_extras", "category_capturing")
)

private val carParksList = listOf(
    CarPark(1, "Morrison's car park", "500 spaces - TA6 3LN", "51.12484026457824,-3.0029453985243886"),
    CarPark(2, "B&M car park", "500 spaces - TA6 3LN", "51.12511284766959,-3.005504556856558"),
    CarPark(3, "ST Matthews field", "1000 spaces - TA6 7EU", "51.12378474061676,-3.014021403522863"),
    CarPark(4, "Northgate", "161 spaces - TA6 3EU", "51.13113154634135,-3.0033088940047166"),
    CarPark(5, "Asda car park", "300 spaces - TA6 4QJ", "51.13057513122341,-2.999515744804497"),
    CarPark(6, "Wickes car park", "TA6 4DH", "51.136255904015414,-2.999384322992658"),
    CarPark(7, "Bridgwater Hospital", "Car park", "51.1409344134999,-2.9753077989478713"),
    CarPark(8, "Morganians Rugby Football Club", "TA7 8QW", "51.14358039547969,-2.9702075153416083")
)

@Composable
fun Planner(navController: NavHostController) {
    var showConsentDialog by remember { mutableStateOf(true) }
    var showItemDialog by remember { mutableStateOf(false) }
    var selectedItems by remember<MutableState<Set<Int>>> { 
        val savedItems = Storage.getString(StorageKeys.PLANNER_SELECTED_ITEMS, "").split(",").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
        mutableStateOf(savedItems)
    }
    var packedItems by remember<MutableState<Set<Int>>> {
        val savedPackedItems = Storage.getString(StorageKeys.PLANNER_PACKED_ITEMS, "").split(",").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
        mutableStateOf(savedPackedItems)
    }
    var selectedCarPark by remember<MutableState<CarPark?>> { 
        val savedCarParkId = Storage.getString(StorageKeys.PLANNER_SELECTED_CAR_PARK, "").toIntOrNull()
        mutableStateOf(carParksList.find { it.id == savedCarParkId })
    }
    val uriHandler = getUriHandler()



    // Save selected items whenever they change
    LaunchedEffect(selectedItems) {
        Storage.saveString(StorageKeys.PLANNER_SELECTED_ITEMS, selectedItems.joinToString(","))
    }

    // Save packed items whenever they change
    LaunchedEffect(packedItems) {
        Storage.saveString(StorageKeys.PLANNER_PACKED_ITEMS, packedItems.joinToString(","))
    }

    // Save selected car park whenever it changes
    LaunchedEffect(selectedCarPark) {
        Storage.saveString(StorageKeys.PLANNER_SELECTED_CAR_PARK, selectedCarPark?.id?.toString() ?: "")
    }

    if (showItemDialog) {
        Dialog(onDismissRequest = { showItemDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PlannerColors.cardBackground(),
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.95f)  // Make dialog wider
                    .fillMaxHeight(0.7f)  // Control max height
            ) {
                Box(
                    modifier = Modifier
                        .background(PlannerColors.primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        TranslatedText(
                            key = "planner_select_items",
                            style = MaterialTheme.typography.h6.copy(
                                fontFamily = bungeeFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = PlannerColors.primary
                            ),
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            plannerItems.groupBy { it.categoryKey }.forEach { (category, categoryItems) ->
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        TranslatedText(
                                            key = category,
                                            style = MaterialTheme.typography.subtitle1.copy(
                                                color = PlannerColors.accent,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                
                                items(categoryItems) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .background(
                                                color = if (selectedItems.contains(item.id)) 
                                                    PlannerColors.primary.copy(alpha = 0.2f)
                                                else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                selectedItems = if (selectedItems.contains(item.id)) {
                                                    selectedItems - item.id
                                                } else {
                                                    selectedItems + item.id
                                                }
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedItems.contains(item.id),
                                            onCheckedChange = { checked ->
                                                selectedItems = if (checked) {
                                                    selectedItems + item.id
                                                } else {
                                                    selectedItems - item.id
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = PlannerColors.primary,
                                                uncheckedColor = PlannerColors.textSecondary()
                                            )
                                        )
                                        Text(
                                            text = item.emoji,
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        TranslatedText(
                                            key = item.nameKey,
                                            style = MaterialTheme.typography.body1.copy(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 16.sp,
                                                color = PlannerColors.textPrimary()
                                            )
                                        )
                                    }
                                }
                                
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        TextButton(
                                            onClick = {
                                                val categoryItemIds = categoryItems.map { it.id }.toSet()
                                                selectedItems = if (categoryItems.all { selectedItems.contains(it.id) }) {
                                                    selectedItems - categoryItemIds
                                                } else {
                                                    selectedItems + categoryItemIds
                                                }
                                            }
                                        ) {
                                            TranslatedText(
                                                key = if (categoryItems.all { selectedItems.contains(it.id) }) 
                                                    "planner_deselect_all" else "planner_select_all",
                                                style = MaterialTheme.typography.button.copy(
                                                    color = PlannerColors.primary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { selectedItems = emptySet() },
                                modifier = Modifier.weight(1f)
                            ) {
                                TranslatedText(
                                    key = "planner_clear",
                                    style = MaterialTheme.typography.button.copy(
                                        color = PlannerColors.textSecondary(),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            Button(
                                onClick = { showItemDialog = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = PlannerColors.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.elevation(8.dp)
                            ) {
                                TranslatedText(
                                    key = "planner_done",
                                    style = MaterialTheme.typography.button.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        backgroundColor = PlannerColors.background()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TranslatedText(
                    key = "planner_title",
                    style = MaterialTheme.typography.h4.copy(
                        fontFamily = bungeeFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = PlannerColors.primary
                    ),
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Selected Items Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = PlannerColors.cardBackground()
                ) {
                    Box(
                        modifier = Modifier
                            .background(PlannerColors.primary.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TranslatedText(
                                    key = "planner_items_title",
                                    style = MaterialTheme.typography.h6.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        color = PlannerColors.textPrimary()
                                    )
                                )
                            }

                            Button(
                                onClick = { showItemDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = PlannerColors.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TranslatedText(
                                        key = "planner_select_items",
                                        style = MaterialTheme.typography.button.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (selectedItems.isEmpty()) {
                                TranslatedText(
                                    key = "planner_tap_to_start",
                                    style = MaterialTheme.typography.body1.copy(
                                        color = PlannerColors.textSecondary(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                )
                            } else {
                                val selectedItemsList = plannerItems.filter { selectedItems.contains(it.id) }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Left column
                                    Column(
                                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                                    ) {
                                        selectedItemsList.filterIndexed { index, _ -> index % 2 == 0 }.forEach { item ->
                                            val isPacked = packedItems.contains(item.id)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .background(
                                                        color = if (isPacked)
                                                            PlannerColors.success.copy(alpha = 0.1f)
                                                        else PlannerColors.primary.copy(alpha = 0.1f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isPacked,
                                                    onCheckedChange = { checked ->
                                                        packedItems = if (checked) {
                                                            packedItems + item.id
                                                        } else {
                                                            packedItems - item.id
                                                        }
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = PlannerColors.success,
                                                        uncheckedColor = PlannerColors.textSecondary()
                                                    )
                                                )
                                                Column {
                                                    Text(
                                                        text = item.emoji,
                                                        fontSize = 20.sp,
                                                        modifier = Modifier.padding(horizontal = 4.dp)
                                                    )
                                                    TranslatedText(
                                                        key = item.nameKey,
                                                        style = MaterialTheme.typography.body1.copy(
                                                            fontWeight = FontWeight.Medium,
                                                            fontSize = 14.sp,
                                                            color = PlannerColors.textPrimary(),
                                                            textDecoration = if (isPacked) TextDecoration.LineThrough else TextDecoration.None
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    
                                    // Right column
                                    Column(
                                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                                    ) {
                                        selectedItemsList.filterIndexed { index, _ -> index % 2 == 1 }.forEach { item ->
                                            val isPacked = packedItems.contains(item.id)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .background(
                                                        color = if (isPacked)
                                                            PlannerColors.success.copy(alpha = 0.1f)
                                                        else PlannerColors.primary.copy(alpha = 0.1f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isPacked,
                                                    onCheckedChange = { checked ->
                                                        packedItems = if (checked) {
                                                            packedItems + item.id
                                                        } else {
                                                            packedItems - item.id
                                                        }
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = PlannerColors.success,
                                                        uncheckedColor = PlannerColors.textSecondary()
                                                    )
                                                )
                                                Column {
                                                    Text(
                                                        text = item.emoji,
                                                        fontSize = 20.sp,
                                                        modifier = Modifier.padding(horizontal = 4.dp)
                                                    )
                                                    TranslatedText(
                                                        key = item.nameKey,
                                                        style = MaterialTheme.typography.body1.copy(
                                                            fontWeight = FontWeight.Medium,
                                                            fontSize = 14.sp,
                                                            color = PlannerColors.textPrimary(),
                                                            textDecoration = if (isPacked) TextDecoration.LineThrough else TextDecoration.None
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                OutlinedButton(
                                    onClick = { 
                                        selectedItems = emptySet()
                                        packedItems = emptySet()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        backgroundColor = Color.Transparent,
                                        contentColor = PlannerColors.warning
                                    ),
                                    border = ButtonDefaults.outlinedBorder.copy(
                                        brush = SolidColor(PlannerColors.warning)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null,
                                            tint = PlannerColors.warning
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        TranslatedText(
                                            key = "planner_clear_all",
                                            style = MaterialTheme.typography.button.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = PlannerColors.warning
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Car Park Selection Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = PlannerColors.cardBackground()
                ) {
                    Box(
                        modifier = Modifier
                            .background(PlannerColors.primary.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            TranslatedText(
                                key = "planner_car_park_title",
                                style = MaterialTheme.typography.h6.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = PlannerColors.textPrimary()
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            carParksList.forEach { carPark ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .background(
                                            color = if (selectedCarPark?.id == carPark.id)
                                                PlannerColors.primary.copy(alpha = 0.2f)
                                            else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedCarPark = carPark }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedCarPark?.id == carPark.id,
                                        onClick = { selectedCarPark = carPark },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = PlannerColors.primary,
                                            unselectedColor = PlannerColors.textSecondary()
                                        )
                                    )
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    ) {
                                        TranslatedText(
                                            key = carPark.name,
                                            style = MaterialTheme.typography.body1.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = PlannerColors.textPrimary()
                                            )
                                        )
                                        TranslatedText(
                                            key = carPark.address,
                                            style = MaterialTheme.typography.caption.copy(
                                                color = PlannerColors.textSecondary(),
                                                fontSize = 14.sp
                                            )
                                        )
                                    }
                                }
                            }

                            selectedCarPark?.let { carPark ->
                                val platformType = getCurrentPlatformType()
                                Button(
                                    onClick = {
                                        val uri = when (platformType) {
                                            PlatformType.IOS -> "maps://?q=${carPark.coordinates}"
                                            PlatformType.ANDROID -> "geo:${carPark.coordinates}?q=${carPark.coordinates}(${carPark.name})"
                                            else -> "geo:${carPark.coordinates}?q=${carPark.coordinates}(${carPark.name})"
                                        }
                                        uriHandler.openUri(uri)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = PlannerColors.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = ButtonDefaults.elevation(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        TranslatedText(
                                            key = "planner_get_directions",
                                            style = MaterialTheme.typography.button.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Car Park Map Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = PlannerColors.cardBackground()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PlannerColors.primary.copy(alpha = 0.1f))
                    ) {
                        CarParkMap(
                            carParks = carParksList.map { 
                                val (lat, long) = it.coordinates.split(",").map { coord -> coord.toDouble() }
                                lat to long
                            }
                        )
                    }
                }
            }
        }
    }
}
