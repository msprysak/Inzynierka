package com.msprysak.rentersapp.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.msprysak.rentersapp.R
import com.msprysak.rentersapp.data.UserRepositoryInstance
import com.msprysak.rentersapp.data.repositories.JoinRequestRepository
import com.msprysak.rentersapp.data.repositories.PremisesRepository
import com.msprysak.rentersapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val repository = UserRepositoryInstance.getInstance()
    private lateinit var premisesRepository: PremisesRepository
    private lateinit var joinRequestRepository: JoinRequestRepository
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locale = Locale("pl", "PL")
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)

        setupNavigation()
        setupObservers()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.calendarFragment,
                R.id.paymentsFragment,
                R.id.chatFragment,
                R.id.notificationsFragment,
                R.id.menuFragment
            )
        )

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupObservers() {
        repository.fetchUserData()
        repository.getUserData().observe(this) { user ->
            if (user != null && !::premisesRepository.isInitialized) {
                // Inicjalizuj premisesRepository tylko jeśli nie zostało jeszcze zainicjowane
                premisesRepository = PremisesRepository.getInstance(repository.getUserData())
                premisesRepository.getPremisesData().observe(this) { premises ->
                    binding.premisesName.text = premises.name
                    if (premises != null) {
                        // Inicjalizuj joinRequestRepository tylko jeśli nie zostało jeszcze zainicjowane
                        joinRequestRepository = JoinRequestRepository(repository.getUserData())
                        joinRequestRepository.fetchJoinRequests()
                        val badgeDrawable = BadgeDrawable.create(this)
                        joinRequestRepository.joinRequestListener().observe(this) { joinRequests ->
                            if (joinRequests.isNotEmpty()) {
//                                badgeDrawable.isVisible = true
//                                binding.bottomNavigationView.getOrCreateBadge(R.id.notificationsFragment)
//                                    .apply {
//                                        badgeDrawable.isVisible = true
//                                        badgeDrawable.number = joinRequests.size
//                                    }
                            } else {
//                                badgeDrawable.isVisible = false
//                                binding.bottomNavigationView.removeBadge(R.id.notificationsFragment)
                            }
                        }
                    }
                }
            }
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }
}
