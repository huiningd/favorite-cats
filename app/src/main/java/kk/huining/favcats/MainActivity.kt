package kk.huining.favcats

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
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
import timber.log.Timber
import java.io.File
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

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        val navController = navHost.navController
        setupActionBar(navController)
        addOnDestinationChangedListener(navController, toolbar)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)
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

    private fun addOnDestinationChangedListener(navController: NavController, toolbar: Toolbar) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            /*if (destination.id == R.id.title_screen) {toolbar.visibility = View.GONE
            } else {toolbar.visibility = View.VISIBLE}*/
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                destination.id.toString()
            }
            Timber.d("Navigated to $dest")
            if (BuildConfig.DEBUG) Timber.e("##### Navigated to $dest")
        }
    }

    companion object {

        /** Use external media if it is available, otherwise use app's file directory */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

}
