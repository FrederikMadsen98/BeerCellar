package com.example.beercellar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beercellar.model.BeersViewModel
import com.example.beercellar.ui.theme.BeerCellarTheme
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.beercellar.model.Beer
import com.google.firebase.auth.FirebaseUser
import model.AuthenticationViewModel
import screens.Authentication
import screens.BeerAdd
import screens.BeerDetails
import screens.BeerList
import screens.Welcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeerCellarTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authenticationViewModel: AuthenticationViewModel = viewModel()
    val beersViewModel: BeersViewModel = viewModel()
    val beers = beersViewModel.beers.value
    val errorMessage = beersViewModel.errorMessage.value
    val user =authenticationViewModel.user

    NavHost(navController = navController, startDestination = NavRoutes.Authentication.route) {

        // Authentication Screen
        composable(NavRoutes.Authentication.route) {
            Authentication(
                user = authenticationViewModel.user,
                message = authenticationViewModel.message,
                signIn = { email, password ->
                    authenticationViewModel.signIn(email, password)
                },
                register = { email, password ->
                    authenticationViewModel.register(email, password)
                },
                navigateToNextScreen = {
                    navController.navigate(NavRoutes.Welcome.route)
                }
            )
        }

        // Welcome Screen after successful login
        composable(NavRoutes.Welcome.route) {
            Welcome(
                user = authenticationViewModel.user,
                signOut = {
                    authenticationViewModel.signOut()
                    navController.navigate(NavRoutes.Authentication.route) {
                        popUpTo(NavRoutes.Authentication.route) { inclusive = true }
                    }
                },
                navigateToBeerList = {
                    navController.navigate(NavRoutes.BeerList.route)
                }
            )
        }

        // Beer List Screen
        composable(NavRoutes.BeerList.route) {
            LaunchedEffect(user) {
                user?.email?.let { beersViewModel.getBeersByUsername(it) } ?: run { beersViewModel.clearBeers() }
            }
            BeerList(
                modifier = modifier,
                    beers = beers,
                    errorMessage = errorMessage,
                    onBeerSelected = { beer -> navController.navigate(NavRoutes.BeerDetails.route + "/${beer.id}") },
                    onBeerDeleted = { beer -> beersViewModel.remove(beer) },
                    onAdd = { navController.navigate(NavRoutes.BeerAdd.route) },
                    sortByName = { beersViewModel.sortBeersByName(ascending = it) },
                    sortByAbv = { beersViewModel.sortBeersByAbv(ascending = it) },
                    filterByName = { beersViewModel.filterByName(it) },
                    user = user,
                    OnSignOut = {
                        authenticationViewModel.signOut()
                        navController.navigate(NavRoutes.Authentication.route) {
                            popUpTo(NavRoutes.Authentication.route) { inclusive = true }
                        }
                    }
                )
        }

        // Beer Details Screen
        composable(
            NavRoutes.BeerDetails.route + "/{beerId}",
            arguments = listOf(navArgument("beerId") { type = NavType.IntType })
        ) { backstackEntry ->
            val beerId = backstackEntry.arguments?.getInt("beerId")
            val beer = beers.find { it.id == beerId } ?: Beer(
                id = 0,
                user = "unknown user",
                brewery = "unknown brewery",
                name = "No beer",
                style = "unknown style",
                abv = 0.0,
                volume = 0.0,
                pictureUrl = "no_image_url"
            )
            BeerDetails(
                modifier = modifier,
                beer = beer,
                onUpdate = { id: Int, updatedBeer: Beer -> beersViewModel.update(id, updatedBeer) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Beer Add Screen
        composable(NavRoutes.BeerAdd.route) {
            BeerAdd(
                modifier = modifier,
                addBeer = { beer -> beersViewModel.add(beer) },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Welcome(
    user: FirebaseUser?,
    signOut: () -> Unit,
    navigateToBeerList: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome") },
                actions = {
                    IconButton(onClick = signOut) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign out")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Welcome, ${user?.email ?: "unknown"}")

            Button(
                onClick = navigateToBeerList,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Go to Beer List")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BeerCellarTheme {
        MainScreen()
    }
}

