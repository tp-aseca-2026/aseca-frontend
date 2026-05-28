package com.aseca.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aseca.mobile.auth.TokenStore
import com.aseca.mobile.repository.EdgarRepository
import com.aseca.mobile.ui.theme.AsecaMobileTheme
import com.aseca.mobile.navigation.AuthScreen
import com.aseca.mobile.repository.AuthRepository
import com.aseca.mobile.repository.PortfolioRepository
import com.aseca.mobile.ui.screens.EdgarScreen
import com.aseca.mobile.ui.screens.HomeScreen
import com.aseca.mobile.ui.screens.LoginScreen
import com.aseca.mobile.ui.screens.PortfolioScreen
import com.aseca.mobile.ui.screens.RegisterScreen
import com.aseca.mobile.ui.screens.TransactionsScreen
import com.aseca.mobile.ui.screens.WatchlistScreen
import com.aseca.mobile.viewmodel.LoginViewModel
import com.aseca.mobile.viewmodel.PortfolioViewModel
import com.aseca.mobile.viewmodel.RegisterViewModel
import com.aseca.mobile.viewmodel.TransactionsViewModel
import com.aseca.mobile.viewmodel.WatchlistViewModel
import com.aseca.mobile.viewmodel.EdgarViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AuthRepository()
        val portfolioRepository = PortfolioRepository()
        val edgarRepository = EdgarRepository()
        val tokenStore = TokenStore(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return when {
                    modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                        LoginViewModel(repository) as T

                    modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                        RegisterViewModel(repository) as T

                    modelClass.isAssignableFrom(PortfolioViewModel::class.java) ->
                        PortfolioViewModel(portfolioRepository) as T

                    modelClass.isAssignableFrom(TransactionsViewModel::class.java) ->
                        TransactionsViewModel(portfolioRepository) as T

                    modelClass.isAssignableFrom(WatchlistViewModel::class.java) ->
                        WatchlistViewModel(portfolioRepository) as T

                    modelClass.isAssignableFrom(EdgarViewModel::class.java) ->
                        EdgarViewModel(edgarRepository) as T

                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
        val provider = ViewModelProvider(this, factory)
        val loginViewModel = provider[LoginViewModel::class.java]
        val registerViewModel = provider[RegisterViewModel::class.java]
        val portfolioViewModel = provider[PortfolioViewModel::class.java]
        val transactionsViewModel = provider[TransactionsViewModel::class.java]
        val watchlistViewModel = provider[WatchlistViewModel::class.java]
        val edgarViewModel = provider[EdgarViewModel::class.java]

        setContent {
            AsecaMobileTheme {
                AuthFlow(
                    loginViewModel = loginViewModel,
                    registerViewModel = registerViewModel,
                    portfolioViewModel = portfolioViewModel,
                    transactionsViewModel = transactionsViewModel,
                    watchlistViewModel = watchlistViewModel,
                    edgarViewModel = edgarViewModel,
                    tokenStore = tokenStore,
                )
            }
        }
    }
}

@Composable
fun AuthFlow(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    portfolioViewModel: PortfolioViewModel,
    transactionsViewModel: TransactionsViewModel,
    watchlistViewModel: WatchlistViewModel,
    edgarViewModel: EdgarViewModel,
    tokenStore: TokenStore,
) {
    var accessToken by rememberSaveable { mutableStateOf(tokenStore.getAccessToken()) }
    var currentScreen by rememberSaveable {
        mutableStateOf(if (accessToken.isBlank()) AuthScreen.Login else AuthScreen.Home)
    }

    fun authenticate(token: String) {
        tokenStore.saveAccessToken(token)
        accessToken = token
        currentScreen = AuthScreen.Home
    }

    when (currentScreen) {
        AuthScreen.Login -> LoginScreen(
            viewModel = loginViewModel,
            onGoToRegister = { currentScreen = AuthScreen.Register },
            onAuthenticated = ::authenticate,
        )

        AuthScreen.Register -> RegisterScreen(
            viewModel = registerViewModel,
            onGoToLogin = { currentScreen = AuthScreen.Login },
            onAuthenticated = ::authenticate,
        )

        AuthScreen.Home -> HomeScreen(
            accessToken = accessToken,
            viewModel = portfolioViewModel,
            onGoToPortfolio = { currentScreen = AuthScreen.Portfolio },
            onGoToTransactions = { currentScreen = AuthScreen.Transactions },
            onGoToWatchlist = { currentScreen = AuthScreen.Watchlist },
            onGoToEdgar = { currentScreen = AuthScreen.Edgar },
            onLogout = {
                tokenStore.clear()
                accessToken = ""
                currentScreen = AuthScreen.Login
            },
        )

        AuthScreen.Portfolio -> PortfolioScreen(
            viewModel = portfolioViewModel,
            accessToken = accessToken,
            onBack = { currentScreen = AuthScreen.Home },
        )

        AuthScreen.Transactions -> TransactionsScreen(
            viewModel = transactionsViewModel,
            accessToken = accessToken,
            onBack = { currentScreen = AuthScreen.Home },
        )

        AuthScreen.Watchlist -> WatchlistScreen(
            viewModel = watchlistViewModel,
            accessToken = accessToken,
            onBack = { currentScreen = AuthScreen.Home },
        )

        AuthScreen.Edgar -> EdgarScreen(
            viewModel = edgarViewModel,
            onBack = { currentScreen = AuthScreen.Home },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthFlowPreview() {
    AsecaMobileTheme {
        // Preview simple sin ViewModels reales.
    }
}
