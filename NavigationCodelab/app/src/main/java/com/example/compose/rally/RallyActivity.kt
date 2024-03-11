/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.overview.OverviewScreen
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

@Composable
fun RallyApp() {
    RallyTheme {
        val navController = rememberNavController()

        val currentBackStack by navController.currentBackStackEntryAsState()
        // Fetch your currentDestination:
        val currentDestination = currentBackStack?.destination

        // Change the variable to this and use Overview as a backup screen if this returns null
        val currentScreen = rallyTabRowScreens.find {
            it.route == currentDestination?.route   // LingJie's Mark: currentDestination?.route指②③④处代码
        } ?: Overview

        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    // Pass the callback like this,
                    // defining the navigation action when a tab is selected:
                    onTabSelected = { screen ->
                        navController.navigateSingleTopTo(screen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Overview.route,  // LingJie's Mark: ①
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = Overview.route  // LingJie's Mark: ②
                ) {
                    OverviewScreen(
                        onClickSeeAllAccounts = {
                            navController.navigateSingleTopTo(Accounts.route)
                        },
                        onClickSeeAllBills = {
                            navController.navigateSingleTopTo(Bills.route)
                        }
                    )
                }
                composable(
                    route = Accounts.route  // LingJie's Mark: ③
                ) {
                    AccountsScreen()
                }
                composable(
                    route = Bills.route     // LingJie's Mark: ④
                ) {
                    BillsScreen()
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(
        route = route
    ) {
        popUpTo(
            // 弹出到导航图的起始目的地，以免在您选择标签页时在返回堆栈上积累大量目的地。在本应用中，这意味着，在任何目的地按下返回箭头都会将整个返回堆栈弹出到“Overview”屏幕
            id = this@navigateSingleTopTo.graph.findStartDestination().id   // LingJie's Mark: 指①处代码
        ) {
            saveState = true
        }
        // 这可确保返回堆栈顶部最多只有给定目的地的一个副本。在本应用中，这意味着，多次重按同一标签页不会启动同一目的地的多个副本
        launchSingleTop = true
        // 确定此导航操作是否应恢复 PopUpToBuilder.saveState 或 popUpToSaveState 属性之前保存的任何状态。请注意，如果之前未使用要导航到的目的地 ID 保存任何状态，此项不会产生任何影响。在本应用中，这意味着，重按同一标签页会保留屏幕上之前的数据和用户状态，而无需重新加载。
        restoreState = true
    }
