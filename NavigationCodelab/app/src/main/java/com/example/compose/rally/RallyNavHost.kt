package com.example.compose.rally

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.overview.OverviewScreen

/**
 * Created by ChenJun on 2024/3/11.
 */
@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Overview.route,  // LingJie's Mark: ①
        modifier = modifier
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
                },
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(
                        accountType = accountType
                    )
                }
            )
        }
        composable(
            route = Accounts.route  // LingJie's Mark: ③
        ) {
            AccountsScreen(
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(
                        accountType = accountType
                    )
                }
            )
        }
        composable(
            route = Bills.route     // LingJie's Mark: ④
        ) {
            BillsScreen()
        }
        composable(
            route = SingleAccount.routeWithArgs,
            arguments = SingleAccount.arguments,
            deepLinks = SingleAccount.deepLinks
        ) { navBackStackEntry ->
            // Retrieve the passed argument
            val accountType =
                navBackStackEntry.arguments?.getString(SingleAccount.accountTypeArg)

            // Pass accountType to SingleAccountScreen
            SingleAccountScreen(
                accountType = accountType
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(
    route: String,
    restoreState: Boolean = true
) =
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
        this.restoreState = restoreState
    }

private fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo(
        route = "${SingleAccount.route}/$accountType",
        restoreState = false
    )
}