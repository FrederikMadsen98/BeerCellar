package screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.beercellar.model.Beer
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeerList(
    beers: List<Beer>,
    errorMessage: String,
    modifier: Modifier = Modifier,
    onBeerSelected: (Beer) -> Unit = {},
    onBeerDeleted: (Beer) -> Unit = {},
    onAdd: () -> Unit = {},
    sortByName: (up: Boolean) -> Unit = {},
    sortByAbv: (up: Boolean) -> Unit = {},
    filterByName: (String) -> Unit = {},
    user: FirebaseUser? = null,
    OnSignOut: () -> Unit = {}

    ) {
    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Beer List") },
                actions = {
                    IconButton(onClick = OnSignOut) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { onAdd() },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }) { innerPadding ->
        BeerListPanel(
            beers = beers,
            modifier = Modifier.padding(innerPadding),
            errorMessage = errorMessage,
            sortByName = sortByName,
            sortByAbv = sortByAbv,
            onBeerSelected = onBeerSelected,
            onBeerDeleted = onBeerDeleted,
            onFilterByName = filterByName,
            userEmail = user?.email ?: ""
        )
    }
}

@Composable
private fun BeerListPanel(
    beers: List<Beer>,
    modifier: Modifier = Modifier,
    errorMessage: String,
    sortByName: (up: Boolean) -> Unit,
    sortByAbv: (up: Boolean) -> Unit,
    onBeerSelected: (Beer) -> Unit,
    onBeerDeleted: (Beer) -> Unit,
    onFilterByName: (String) -> Unit,
    userEmail : String
) {
    Column(modifier = modifier.padding(8.dp)) {
        if (errorMessage.isNotEmpty()) {
            Text(text = "Problem: $errorMessage")
        }
        val nameUp = "Name \u2191"
        val nameDown = "Name \u2193"
        val abvUp = "ABV \u2191"
        val abvDown = "ABV \u2193"
        var sortNameAscending by remember { mutableStateOf(true) }
        var sortAbvAscending by remember { mutableStateOf(true) }
        var nameFragment by rememberSaveable { mutableStateOf("") }
        val userBeers = beers.filter { it.user == userEmail }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = nameFragment,
                onValueChange = { nameFragment = it },
                label = { Text("Filter by name") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { onFilterByName(nameFragment) },
                modifier = Modifier.padding(8.dp)) {
                Text("Filter")
            }
        }

        Row {
            OutlinedButton(onClick = {
                sortByName(sortNameAscending)
                sortNameAscending = !sortNameAscending
            }) {
                Text(text = if (sortNameAscending) nameDown else nameUp)
            }
            TextButton(onClick = {
                sortByAbv(sortAbvAscending)
                sortAbvAscending = !sortAbvAscending
            }) {
                Text(text = if (sortAbvAscending) abvDown else abvUp)
            }
        }
        val orientation = LocalConfiguration.current.orientation
        val columns = if (orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
        ) {
            items(userBeers) { beer ->
                BeerItem(
                    beer = beer,
                    onBeerSelected = onBeerSelected,
                    onBeerDeleted = onBeerDeleted
                )
            }
        }
    }
}

@Composable
private fun BeerItem(
    beer: Beer,
    modifier: Modifier = Modifier,
    onBeerSelected: (Beer) -> Unit = {},
    onBeerDeleted: (Beer) -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxSize(),
        onClick = { onBeerSelected(beer) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "${beer.name} (${beer.brewery})", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Style: ${beer.style}, ABV: ${beer.abv}%", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Volume: ${beer.volume}ml", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onBeerDeleted(beer) }
            )
        }
    }
}

@Preview
@Composable
fun BeerListPreview() {
    BeerList(
        beers = listOf(
            Beer(id = 1, user = "John", brewery = "Brew Co", name = "Lager", style = "Pilsner", abv = 5.0, volume = 500.0, pictureUrl = ""),
            Beer(id = 2, user = "Doe", brewery = "Ale House", name = "IPA", style = "IPA", abv = 6.5, volume = 330.0, pictureUrl = "")
        ),
        errorMessage = "Some error message"
    )
}
