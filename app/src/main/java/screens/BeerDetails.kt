package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beercellar.model.Beer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeerDetails(
    beer: Beer,
    modifier: Modifier = Modifier,
    onUpdate: (Int, Beer) -> Unit = { id: Int, data: Beer -> },
    onNavigateBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf(beer.name) }
    var brewery by remember { mutableStateOf(beer.brewery) }
    var style by remember { mutableStateOf(beer.style) }
    var abvStr by remember { mutableStateOf(beer.abv.toString()) }
    var volumeStr by remember { mutableStateOf(beer.volume.toString()) }

    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Beer Details") }
            )
        }) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Beer Name") }
            )
            OutlinedTextField(
                value = brewery,
                onValueChange = { brewery = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Brewery") }
            )
            OutlinedTextField(
                value = style,
                onValueChange = { style = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Style") }
            )
            OutlinedTextField(
                value = abvStr,
                onValueChange = { abvStr = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "ABV (%)") }
            )
            OutlinedTextField(
                value = volumeStr,
                onValueChange = { volumeStr = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Volume (ml)") }
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onNavigateBack() }) {
                    Text("Back")
                }
                Button(onClick = {
                    val abv = abvStr.toDoubleOrNull() ?: 0.0
                    val volume = volumeStr.toDoubleOrNull() ?: 0.0
                    val data = Beer(
                        id = beer.id,
                        user = beer.user,
                        brewery = brewery,
                        name = name,
                        style = style,
                        abv = abv,
                        volume = volume,
                        pictureUrl = beer.pictureUrl,
                        howMany = beer.howMany
                    )
                    onUpdate(beer.id, data)
                    onNavigateBack()
                }) {
                    Text("Update")
                }
            }
        }
    }
}

@Preview
@Composable
fun BeerDetailsPreview() {
    BeerDetails(
        beer = Beer(
            id = 1,
            user = "John Doe",
            brewery = "Brew Co",
            name = "Lager",
            style = "Pilsner",
            abv = 5.0,
            volume = 500.0,
            pictureUrl = "https://example.com/beer.jpg"
        )
    )
}