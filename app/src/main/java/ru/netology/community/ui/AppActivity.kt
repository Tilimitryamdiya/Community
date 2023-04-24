package ru.netology.community.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.community.R
import ru.netology.community.viewmodel.AuthViewModel

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment

        var previousMenuProvider: MenuProvider? = null

        authViewModel.data.observe(this) {
            previousMenuProvider?.let(::removeMenuProvider)
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)

                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
                    menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.login -> {
                            navHostFragment.navController
                                .navigate(R.id.loginFragment)
                            true
                        }
                        R.id.register -> {
                            navHostFragment.navController
                                .navigate(R.id.registerFragment)
                            true
                        }

                        R.id.logout -> {
                            authViewModel.confirmLogout(supportFragmentManager)
                            true
                        }
                        else -> false
                    }
            }.also { previousMenuProvider = it })
        }
    }
}