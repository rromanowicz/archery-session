package ex.rr.archerysession

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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
import ex.rr.archerysession.db.DBHelper
import ex.rr.archerysession.file.SessionFileProcessor


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                syncFileWithDb()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                syncFileWithDb()
            }
        } else {
            syncFileWithDb()
        }


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


        binding.fab0.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_MainFragment)
        }

        binding.fab1.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_NewSessionFragment)
        }

        binding.fab2.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_HistoryFragment)
        }

        binding.fab3.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_StatisticsFragment)
        }

        binding.fab4.setOnClickListener {
            closeFABMenu()
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_SettingsFragment)
        }

    }

    private fun syncFileWithDb() {
        val sessionFileProcessor = SessionFileProcessor()
        val sessionFile = sessionFileProcessor.getAllFromFile()
        val db = DBHelper(this, null)
        db.init()
        val allSessions = db.getAllSessions()

        if (allSessions.isNotEmpty() && sessionFile.isNullOrEmpty()) {
            sessionFileProcessor.writeAll(allSessions)
        } else if (allSessions.isEmpty() && sessionFile.isNotEmpty()) {
            db.addAll(sessionFile)
        } else if (sessionFile.size != allSessions.size) {
            val notInDb = sessionFile.minus(allSessions.toSet())
            val notInFile = allSessions.minus(sessionFile.toSet())
            db.addAll(notInDb.toMutableList())
            sessionFileProcessor.writeAll(notInFile.toMutableList())
        }

        db.close()
    }

    private fun showFABMenu() {
        binding.fabLayout0.visibility = View.VISIBLE
        binding.fabLayout1.visibility = View.VISIBLE
        binding.fabLayout2.visibility = View.VISIBLE
        binding.fabLayout3.visibility = View.VISIBLE
        binding.fabLayout4.visibility = View.VISIBLE
        fabBGLayout.visibility = View.VISIBLE
        binding.fab.animate().rotationBy(180F)
        binding.fabLayout0.animate().translationY(-resources.getDimension(R.dimen.standard_255))
        binding.fabLayout1.animate().translationY(-resources.getDimension(R.dimen.standard_210))
        binding.fabLayout2.animate().translationY(-resources.getDimension(R.dimen.standard_165))
        binding.fabLayout3.animate().translationY(-resources.getDimension(R.dimen.standard_120))
        binding.fabLayout4.animate().translationY(-resources.getDimension(R.dimen.standard_75))
    }

    private fun closeFABMenu() {
        fabBGLayout.visibility = View.GONE
        binding.fab.animate().rotation(0F)
        binding.fabLayout0.animate().translationY(0f)
        binding.fabLayout1.animate().translationY(0f)
        binding.fabLayout2.animate().translationY(0f)
        binding.fabLayout3.animate().translationY(0f)
        binding.fabLayout4.animate().translationY(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (View.GONE == fabBGLayout.visibility) {
                        binding.fabLayout0.visibility = View.GONE
                        binding.fabLayout1.visibility = View.GONE
                        binding.fabLayout2.visibility = View.GONE
                        binding.fabLayout3.visibility = View.GONE
                        binding.fabLayout4.visibility = View.GONE
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_MainFragment)
                return true
            }
            R.id.action_new_session -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_NewSessionFragment)
                return true
            }
            R.id.action_history -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_HistoryFragment)
                return true
            }
            R.id.action_statistics -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_StatisticsFragment)
                return true
            }
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.to_SettingsFragment)
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