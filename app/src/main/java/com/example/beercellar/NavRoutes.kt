package com.example.beercellar

sealed class NavRoutes(val route: String) {
    data object BeerList : NavRoutes("list")
    data object BeerDetails : NavRoutes("details")
    data object BeerAdd : NavRoutes("add")
    data object Authentication : NavRoutes("authentication")
    data object Welcome : NavRoutes("welcome")
}
