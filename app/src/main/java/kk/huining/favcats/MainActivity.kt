package kk.huining.favcats

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kk.huining.favcats.di.PresentationComponent
import kk.huining.favcats.di.PresentationModule
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sharedVM: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        sharedVM = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBar(navController)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        /*Set up Action Bar and navigation
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        val navController = navHost.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.title_screen, R.id.trips_fragment)) // topLevelDestinationIds
        setupActionBar(navController, appBarConfiguration)
        addOnDestinationChangedListener(navController, toolbar)*/
    }

    /**
     * Let NavigationUI handle what the ActionBar displays.
     * This allows NavigationUI to decide what label to show in the action bar
     * by using appBarConfig, it will also determine whether to show the up arrow or not.
     */
    private fun setupActionBar(
        navController: NavController
    ) {
        // Passing each menu ID as a set of Ids because each menu should be considered as
        // top level destinations.
        val appBarConfig = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_upload))
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun getPresentationComponent(): PresentationComponent {
        return (application as FavCatsApplication).getApplicationComponent()
            .newPresentationComponent(PresentationModule(this))
    }


}
