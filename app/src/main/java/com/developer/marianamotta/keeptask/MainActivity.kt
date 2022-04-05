package com.developer.marianamotta.keeptask

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developer.marianamotta.keeptask.adapter.NotesAdapter
import com.developer.marianamotta.keeptask.model.Criptografia
import com.developer.marianamotta.keeptask.model.Notes
import com.developer.marianamotta.keeptask.ui.create.CreateActivity
import com.developer.marianamotta.keeptask.ui.login.LoginActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private var userId = ""
    private var drawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewMain)
        layoutManager = LinearLayoutManager(this)
        drawer = findViewById(R.id.drawerLayout)
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        for (profile in user?.providerData!!) {
            userId = profile.uid
        }

        navigationView()
        adapterNotes()


        floating.setOnClickListener {
            val intent = Intent(applicationContext, CreateActivity::class.java)
            startActivity(intent)
        }

        showAdMob()
    }

    private fun showList(lista: String): Notes {
        var removeSuffix = lista.removeSuffix(".fig")
        removeSuffix = removeSuffix.removeSuffix(".txt")
        val imagem = Criptografia.cryptoReadImage("$removeSuffix.fig", this)
        val description = Criptografia.cryptoReadText("$removeSuffix.txt", this)[0]
        val title = lista.split("*")[0]
        val date = lista.split("*")[1].removeSuffix("*")
        val bmp = BitmapFactory.decodeByteArray(imagem, 0, imagem.size)

        return Notes(title, description, date, bmp)
    }

    private fun adapterNotes() {
        val data = ArrayList<Notes>()
        val folder = applicationContext.filesDir
        val nFolder = File(folder, "archives")
        val path = File(nFolder.toURI())
        var prefix = ""
        val files = path.listFiles()
        try {
            files?.forEach {
                prefix = it.name.removeSuffix(".txt")
                prefix = it.name.removeSuffix(".fig")
                if (it.nameWithoutExtension != prefix) {
                    data.add(showList(prefix))
                }
                recyclerView.adapter = NotesAdapter(data)
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)

            }
        } catch (e: IOException) {
            showSnack(this@MainActivity,"ERRO:retorno dos dados.")
        }

    }

    private fun showAdMob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer?.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun navigationView() {
        val user = auth.currentUser

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)

        val navigationView = findViewById<NavigationView>(R.id.navigation)

        val toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer?.addDrawerListener(toggle)
        drawer?.isClickable = true
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        navigationView.setCheckedItem(R.id.home)

        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            drawer?.closeDrawers()

            when (it.itemId) {
                R.id.home -> {
                    return@setNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    logout()
                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun showSnack(activity: Activity?, text:String, duration:Int = Snackbar.LENGTH_SHORT) {
        if (activity != null) {
            Snackbar.make(activity.findViewById(android.R.id.content),text,duration).show()
        }
    }
}