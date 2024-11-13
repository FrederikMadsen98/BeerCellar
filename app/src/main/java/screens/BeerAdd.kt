package screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.beercellar.model.Beer
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeerAdd(
    modifier: Modifier = Modifier,
    addBeer: (Beer) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var abvStr by remember { mutableStateOf("") }
    var volumeStr by remember { mutableStateOf("") }
    var brewery by remember { mutableStateOf("") }
    var nameIsError by remember { mutableStateOf(false) }
    var abvIsError by remember { mutableStateOf(false) }
    var volumeIsError by remember { mutableStateOf(false) }

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser?.email ?: "unknown user"

    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Add a Beer") })
        }) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            val orientation = LocalConfiguration.current.orientation
            val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT

            if (isPortrait) {
                OutlinedTextField(
                    onValueChange = { name = it },
                    value = name,
                    isError = nameIsError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Beer Name") }
                )
                OutlinedTextField(
                    onValueChange = { abvStr = it },
                    value = abvStr,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = abvIsError,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "ABV (%)") }
                )
                OutlinedTextField(
                    onValueChange = { volumeStr = it },
                    value = volumeStr,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = volumeIsError,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Volume (ml)") }
                )
                OutlinedTextField(
                    onValueChange = { brewery = it },
                    value = brewery,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Brewery") }
                )
            } else {
                // Landscape layout for TextFields
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        onValueChange = { name = it },
                        value = name,
                        isError = nameIsError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Beer Name") }
                    )
                    OutlinedTextField(
                        onValueChange = { abvStr = it },
                        value = abvStr,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = abvIsError,
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "ABV (%)") }
                    )
                }
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        onValueChange = { volumeStr = it },
                        value = volumeStr,
                        isError = volumeIsError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Volume (ml)") }
                    )
                    OutlinedTextField(
                        onValueChange = { brewery = it },
                        value = brewery,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Brewery") }
                    )
                }
            }

            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { navigateBack() }) {
                    Text("Back")
                }
                Button(onClick = {
                    nameIsError = name.isEmpty()
                    abvIsError = abvStr.isEmpty() || abvStr.toDoubleOrNull() == null
                    volumeIsError = volumeStr.isEmpty() || volumeStr.toDoubleOrNull() == null

                    if (nameIsError || abvIsError || volumeIsError) return@Button

                    val abv = abvStr.toDoubleOrNull() ?: 0.0
                    val volume = volumeStr.toDoubleOrNull() ?: 0.0
                    val beer = Beer(
                        id = 0,
                        user = currentUser,  // Use the authenticated user's email here
                        brewery = brewery,
                        name = name,
                        style = "Pilsner",
                        abv = abv,
                        volume = volume,
                        pictureUrl = "defaultUrl"
                    )

                    addBeer(beer)
                    navigateBack()
                }) {
                    Text("Add")
                }
            }
        }
    }
}

@Preview
@Composable
fun BeerAddPreview() {
    BeerAdd()
}
