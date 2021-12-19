package ex.rr.archerysession

import android.animation.Animator
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.rr.archerysession.R
import com.rr.archerysession.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var fabBGLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCenter.start(
            application, "b81c7307-434c-4fa1-bdb8-4ad3e0402838",
            Analytics::class.java, Crashes::class.java
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        fabBGLayout = findViewById(R.id.fabBGLayout)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            if (View.GONE == fabBGLayout.visibility) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        fabBGLayout.setOnClickListener { closeFABMenu() }


        binding.fab1.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_NewSessionFragment)
        }

        binding.fab2.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_HistoryFragment)
        }


    }

    private fun showFABMenu() {
        binding.fabLayout1.visibility = View.VISIBLE
        binding.fabLayout2.visibility = View.VISIBLE
        binding.fabLayout3.visibility = View.VISIBLE
        fabBGLayout.visibility = View.VISIBLE
        binding.fab.animate().rotationBy(180F)
        binding.fabLayout1.animate().translationY(-resources.getDimension(R.dimen.standard_165))
        binding.fabLayout2.animate().translationY(-resources.getDimension(R.dimen.standard_120))
        binding.fabLayout3.animate().translationY(-resources.getDimension(R.dimen.standard_75))
    }

    private fun closeFABMenu() {
        fabBGLayout.visibility = View.GONE
        binding.fab.animate().rotation(0F)
        binding.fabLayout1.animate().translationY(0f)
        binding.fabLayout2.animate().translationY(0f)
        binding.fabLayout3.animate().translationY(0f)
        binding.fabLayout3.animate().translationY(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (View.GONE == fabBGLayout.visibility) {
                        binding.fabLayout1.visibility = View.GONE
                        binding.fabLayout2.visibility = View.GONE
                        binding.fabLayout3.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_NewSessionFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}