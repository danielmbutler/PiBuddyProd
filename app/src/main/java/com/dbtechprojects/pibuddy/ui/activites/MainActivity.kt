package com.dbtechprojects.pibuddy.ui.activites


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.dbtechprojects.pibuddy.dialogs.HelpDialog
import com.dbtechprojects.pibuddy.R
import com.dbtechprojects.pibuddy.databinding.ActivityMainBinding
import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.ui.viewmodels.MainViewModel
import com.dbtechprojects.pibuddy.utilities.Constants
import com.dbtechprojects.pibuddy.utilities.Resource
import com.dbtechprojects.pibuddy.utilities.SharedPref
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences
    private val viewModel: MainViewModel by viewModels()
    private  var _binding: ActivityMainBinding? = null
    val binding: ActivityMainBinding get() = _binding!!
    private lateinit var drawer : DrawerLayout
    private lateinit var navigationView: NavigationView
    private var port :Int? = null


    private val TAG = "MainActivity"


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set IP address if intent has IP (occurs from Scan Activity
        if (intent.getStringExtra("IPAddress") != null) {
            val IP = intent.getStringExtra("IPAddress")

            binding.IPAddressText.setText(IP)

        }

        pref = SharedPref.getSharedPref(applicationContext)
        port = pref.getInt("port", 22)
        // verify network connectivity
        internetCheck()
        //setupDraw
        setupDraw()
        //setupClickListeners
        setupClicks()

        if (savedInstanceState == null) {
            //initialise Observers from ViewModel
            initObservers()
        }


    }

    private fun initObservers() {
        viewModel.pingTest.observe(this, Observer { result ->

            when (result) {
                is Resource.Success -> {
                    result.data?.let { pingTest ->
                        Log.d(TAG, "initObservers: pingTest is $pingTest")
                        if (!pingTest.result) {
                            Toast.makeText(
                                this@MainActivity,
                                Constants.CONNECTION_ERROR,
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.MainActivityTextDotLoader.visibility = INVISIBLE
                        } else {
                            // Ping Test Was successful lets run commands

                            // check for stored command for that IP
                            try {

                                val strJson =
                                    pref.getString(binding.IPAddressText.text.toString(), null)

                                if (strJson != null) {
                                    val jresponse = JSONObject(strJson!!)
                                    val storedCommand = jresponse.getString("CustomCommand")


                                    //Log.d("storedCommand", storedCommand!!)
                                    if (storedCommand != null) {
                                        // setup waiting dialogue

                                        binding.MainCustomCommandMessage.visibility = VISIBLE

                                        // execute commands
                                        viewModel.runPiCommand(
                                            ipAddress = binding.IPAddressText.text.toString(),
                                            username = binding.UsernameText.text.toString(),
                                            password = binding.PasswordText.text.toString(),
                                            customCommand = storedCommand,
                                            port!!
                                        )


                                    }

                                } else {
                                    viewModel.runPiCommand(
                                        ipAddress = binding.IPAddressText.text.toString(),
                                        username = binding.UsernameText.text.toString(),
                                        password = binding.PasswordText.text.toString(),
                                        customCommand = null,
                                        port!!
                                    )

                                }

                            } catch (ce: JSONException) {
                                //Log.d("MainAcvitiy", "No Stored command found")
                                viewModel.runPiCommand(
                                    ipAddress = binding.IPAddressText.text.toString(),
                                    username = binding.UsernameText.text.toString(),
                                    password = binding.PasswordText.text.toString(),
                                    customCommand = null,
                                    port!!
                                )

                            }
                        }
                    }

                }
                is Resource.Initial -> {
                    Log.d(TAG, "Initial")
                }
            }

        })

        viewModel.commandResults.observe(this, Observer { results ->

            when (results) {
                is Resource.Loading -> Log.d(TAG, "LOADING")
                is Resource.Error -> {
                    Toast.makeText(this, Constants.USERNAME_PASSWORD_ERROR, Toast.LENGTH_SHORT)
                        .show()
                    binding.MainActivityTextDotLoader.visibility = View.INVISIBLE
                }
                is Resource.Success -> {
                    Log.d(TAG, "results main: ${results.data}")
                    val intent = Intent(this@MainActivity, Result_Activity::class.java)
                    intent.putExtra("results", results.data)


                    binding.MainActivityTextDotLoader.visibility = INVISIBLE
                    binding.MainCustomCommandMessage.visibility = INVISIBLE

                    val editor = pref.edit()

                    val adcount = pref.getString("adcount", "")

                    if (adcount.isNullOrEmpty()) {
                        editor.putString("adcount", "1")
                        editor.apply()
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("MainActivity", adcount)
                        val newvalue = adcount.toInt() + 1
                        editor.putString("adcount", newvalue.toString())
                        if (adcount.toInt() >= 3) {
                            // show ad
                            Log.d("MainActvity", "Showing Ad")

                            // in place logic till ads work
                            editor.remove("adcount")
                            editor.apply()
                            startActivity(intent)
                            finish()


                        } else {
                            editor.apply()
                            startActivity(intent)
                            finish()
                        }

                    }
                }
            }
        })
    }

    private fun setupClicks() {
        binding.ConnectButton.setOnClickListener {

            val validationTest = validateFields()

            if (validationTest == "success") {
                binding.MainActivityTextDotLoader.visibility = VISIBLE
                pref.getInt("port", 22).let {
                    viewModel.pingTest(binding.IPAddressText.text.toString(), it)
                }

            } else {
                Toast.makeText(
                    this@MainActivity,
                    "$validationTest...Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        binding.ScanButton.setOnClickListener {
            val intent = Intent(this, Scan_Activity::class.java)
            startActivity(intent)
            finish()


        }

    }



    private fun setupDraw() {


        setSupportActionBar(binding.toolbar)
        drawer = findViewById(R.id.drawer_layout)
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawer, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()


        navigationView = findViewById<View>(R.id.nav_viewer) as NavigationView
        val menu = navigationView.menu
        navigationView.bringToFront()

        setupDrawItems()

        val headerview = navigationView.getHeaderView(0)
        val button = headerview.findViewById<Button>(R.id.Nav_Header_Clear_Connection_Button)
        button.setOnClickListener {

            // Build Confirmation alert

            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    for (i in 0 until menu.size()) {

                        menu.removeGroup(0)
                    }
                    val editor: Editor = pref.edit()
                    editor.clear()
                    editor.apply()
                    drawer.closeDrawers()

                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }
    }

    private fun setupDrawItems(){

        //get all preferences

        val keys: MutableMap<String, *> = pref.all
        //Log.d("KEYS", keys.toString())

        val mNavigationView = findViewById<View>(R.id.nav_viewer) as NavigationView

        val menu = mNavigationView.menu

        // setup menu Items with IPs
        for ((key, value) in keys) {
            if (key == "adcount") {
                pref.edit().remove("adcount").apply()
            } else if (key != "port" && key != "buttons") {
                menu.add(0, 0, 0, key).apply {
                    setOnMenuItemClickListener {

                        //Log.d("onclick listner", key)
                        pref.getString(this.title.toString(), null)
                        val strJson = pref.getString(key, null)

                        val jresponse = JSONObject(strJson)
                        val UsernameFromJson = jresponse.getString("Username")
                        val PasswordFromJson = jresponse.getString("Password")

                        if (strJson != null) {
                            binding.IPAddressText.setText(key)
                            binding.UsernameText.setText(UsernameFromJson)
                            binding.PasswordText.setText(PasswordFromJson)
                        }

                        drawer.closeDrawer(GravityCompat.START);
                        true

                    }
                    icon = ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.ic_computer
                    )

                }
            }
        }
        if (keys.isNotEmpty()){
            // script deployment
            menu.add("Script Deployment").apply {
                icon = (ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_flash_on_24))
                setOnMenuItemClickListener {
                    val intent = Intent(this@MainActivity, Deployment_Activity::class.java)
                    startActivity(intent)
                    true
                }
            }
        }
    }

    private fun validateFields(): String {
        if (binding.IPAddressText.text.isEmpty()) {

            return "Missing IP"
        }
        if (binding.UsernameText.text.isEmpty()) {

            return "Missing Username"
        }
        if (binding.PasswordText.text.isEmpty()) {

            return "Missing Password"
        }

        return "success"

    }

    private fun internetCheck() {
        try {
            val connectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val addresses =
                connectivityManager.getLinkProperties(connectivityManager.activeNetwork)!!.linkAddresses
        } catch (ce: NullPointerException) {

            Toast.makeText(
                this@MainActivity,
                "Wifi Connection Not Found, Please check Wifi",
                Toast.LENGTH_LONG
            ).show()


        }
    }


// set up right help icon on toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.pi_buddy_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_menu_help -> {
               val intent = Intent(this, Settings_Activity::class.java)
               startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.pingTest.removeObservers(this)
        viewModel.commandResults.removeObservers(this)
    }

}








