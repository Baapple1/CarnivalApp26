package org.bridgwatercarnival.companion.pages.store

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.bridgwatercarnival.companion.theme.bungeeFont

@Serializable
data class Product(
	val id: Int,
	val title: String,
	val permalink: String,
	val product_image_full: String,
	var price: String,
	val categories: List<String>
)

val httpClient = createHttpClient()
var jsonCache: List<Product>? = null

expect fun createHttpClient(): HttpClient

@Composable
fun CatalogueHeader(
	filtersVisible: Boolean,
	toggleFilters: () -> Unit,
	categories: Collection<String>,
	selectedFilters: Collection<String>,
	filterSelected: (String, Boolean) -> Unit,
) {
	Text(
		text = "Store Catalogue",
		style = MaterialTheme.typography.h4,
		fontFamily = bungeeFont,
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		textAlign = TextAlign.Center,
	)

	// Toggle Filters Button
	Button(
		onClick = toggleFilters,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp)
	) {
		Text(
			text = if (filtersVisible) "Hide Filters" else "Show Filters",
			color = Color.White
		)
	}

	if (filtersVisible) {
		// Filters Header
		Text(
			text = "Filters",
			style = MaterialTheme.typography.h5,
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.padding(vertical = 8.dp)
		)

		FilterSelector(
			categories = categories,
			selectedFilters = selectedFilters,
			filterSelected = filterSelected,
		)
	}
}

@Composable
fun FilterSelector(
	categories: Collection<String>,
	selectedFilters: Collection<String>,
	filterSelected: (String, Boolean) -> Unit,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.fillMaxWidth().padding(4.dp)
	) {
		categories.forEach { category ->
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth()
			) {
				Checkbox(
					checked = selectedFilters.contains(category),
					onCheckedChange = { filterSelected(category, it); },
					colors = CheckboxDefaults.colors(
						checkedColor = Color.Blue, // Color for the checked state
						uncheckedColor = Color.LightGray, // Color for the unchecked state
						checkmarkColor = Color.White // Color for the checkmark
					)
				)

				Spacer(modifier = Modifier.width(8.dp))

				Text(
					text = category,
					modifier = Modifier.clickable {
						filterSelected(category, !selectedFilters.contains(category));
					}
				)
			}
		}
	}
}

@Composable
fun Store() {
	var products by remember { mutableStateOf<List<Product>?>(null) }
	var isLoading by remember { mutableStateOf(true) }
	val scope = rememberCoroutineScope()

	var selectedFilters by remember { mutableStateOf(listOf<String>()) } // Define state for the selected filters
	var filtersVisible by remember { mutableStateOf(false) } // Define state for filter visibility

	val uriHandler = LocalUriHandler.current // Get the UriHandler to handle the URL opening

	if (products == null) {
		if (jsonCache != null) {
			products = jsonCache
			isLoading = false
		} else {
			LaunchedEffect(Unit) {
				scope.launch {
					try {
						products =
							httpClient.get("https://www.bridgwatercarnival.org.uk/wp-json/bwc/v1/products/")
								.body<List<Product>?>()?.map {
									var price = (it.price.toFloatOrNull() ?: 0f).toString()

									if (price.split(".")[1].length == 1) price += "0"

									it.price = price
									return@map it
								}
						jsonCache = products
						isLoading = false
					} catch (e: Exception) {
						println(e.message)
						isLoading = false
					}
				}
			}
		}
	}

	if (isLoading) {
		Column(
			modifier = Modifier
				.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			Text("Please wait...")
			Spacer(Modifier.height(16.dp))
			CircularProgressIndicator()
		}
		return;
	}

	if (products == null) {
		Text("Unable to connect to server.", color = MaterialTheme.colors.onBackground)
		return;
	}

	// Generate a list of all the categories that exist inside the json data.
	val categories = hashSetOf<String>()
	for (product in products!!) {
		for (category in product.categories) {
			categories.add(category);
		}
	}

	// Filter items based on the selected filters
	val filteredProducts = products!!.filter { product ->
		if (selectedFilters.isEmpty()) return@filter true
		for (filter in selectedFilters) {
			if (product.categories.contains(filter)) return@filter true
		}
		return@filter false
	}

	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp)
	) {
		// Fixed Header
		item {
			CatalogueHeader(
				filtersVisible = filtersVisible,
				toggleFilters = { filtersVisible = !filtersVisible },
				categories = categories,
				selectedFilters = selectedFilters,
				filterSelected = { category, selected ->
					run {
						if (selected) {
							selectedFilters += category;
						} else {
							selectedFilters -= category;
						}
					}
				}
			)
		}

		items(filteredProducts) { product ->
			StoreTile(product) {
				uriHandler.openUri(product.permalink)
			}
			Spacer(modifier = Modifier.height(32.dp))
		}

		// Purchase and Browse More Message
		item {
			Text(
				text = "To purchase items and browse more visit: https://www.bridgwatercarnival.org.uk/shop",
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						uriHandler.openUri("https://www.bridgwatercarnival.org.uk/shop")
					}
					.padding(all = 16.dp)
			)
			Spacer(Modifier.height(16.dp))
		}
	}
}
