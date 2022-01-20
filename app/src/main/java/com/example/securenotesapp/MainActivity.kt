package com.example.securenotesapp
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.MenuItemCompat




const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
class MainActivity : AppCompatActivity(), onItemClickListner {
    private val dataList = arrayListOf<DataClass>()
    private var mydb: DbHelper? = null
    var fab: FloatingActionButton? = null
    lateinit var mRecyclerView: RecyclerView
    var customDialog: Dialog? = null
    lateinit var toolbar: Toolbar
    lateinit var adapter: AdaptarClass
    var positionn: Int? = null
    lateinit var mAdView : AdView
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false
    private var TAG = "MainActivity"
    var searchView:SearchView? = null
    private var doubleBackToExitPressedOnce = false
//   lateinit var textView_counter: TextView
    companion object {

        var is_in_action_mode: Boolean = false
        val listNew = arrayListOf<Int>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       toolbar = findViewById(R.id.toolbar)
       searchView = findViewById(R.id.new_search)
       setSupportActionBar(toolbar)
        mRecyclerView = findViewById(R.id.recylerView)
        fab = findViewById(R.id.mAddNewNote)
        customDialog = Dialog(this@MainActivity)
        MobileAds.initialize(this)
        loadAd()

//        textView_counter = findViewById(R.id.countertext)
//        textView_counter.visibility = View.GONE
        mShow()
        fab?.setOnClickListener { view ->
            showDialog(this@MainActivity)
        }
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

    }

    @SuppressLint("Range")
    fun mShow() {
        dataList.clear()
        val db = DbHelper(this)
        val cursor = db.ShowData()
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val desc = cursor.getString(cursor.getColumnIndex("DESCRIPTION"))
                val id = cursor.getString(cursor.getColumnIndex("id"))
                dataList.add(DataClass(id.toInt(), title, desc,false))
            } while (cursor.moveToNext())
        }
        cursor.close()
        val layoutManager: RecyclerView.LayoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        mRecyclerView.setLayoutManager(layoutManager)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.setBackgroundColor(Color.TRANSPARENT)
        adapter = AdaptarClass(this@MainActivity, dataList, this)
        mRecyclerView.adapter = adapter

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menues, menu)
        val searchViewitem:MenuItem = menu.findItem(R.id.new_search)
        val searchView:SearchView = (MenuItemCompat.getActionView(searchViewitem) as SearchView)
        searchView.setOnClickListener {
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })

        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.new_Delete -> {
                if (listNew.isEmpty()){
                    Toast.makeText(this, "Make Selection", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val db = DbHelper(this)
                    AlertDialog.Builder(this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this Note}") // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(R.string.yes) { dialog, which ->
                            // Continue with delete operation

                            for (i in 0 until listNew.size) {
                                val result = db.delete(listNew[i])
                                if (result != null) {

                                    if (result > 0) {
                                        Toast.makeText(this, "delete sucessfully", Toast.LENGTH_SHORT)
                                            .show()
                                        is_in_action_mode = false
                                        toolbar.menu.clear()
                                        toolbar.inflateMenu(R.menu.menues)
//                                    textView_counter.visibility = View.GONE
                                        adapter.notifyDataSetChanged()
                                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                                        mShow()
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            is_in_action_mode =false
                            toolbar.setTitle("Notes")
                            listNew.clear()
                        } // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filter(text: String) {
        var filteredList: MutableList<DataClass> = mutableListOf()
        for(item in dataList) {
            if (item.mTitle.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter!!.filterList(filteredList)

    }

    fun showDialog(activity: Activity?) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_box)
        val saveButton = dialog.findViewById<View>(R.id.dialogSaveBtn) as Button
        val cancel = dialog.findViewById<View>(R.id.dialogCancelBtn) as Button
        val title = dialog.findViewById<View>(R.id.dialogTitle) as EditText
        val desc = dialog.findViewById<View>(R.id.dialogDescription) as EditText
        saveButton.setOnClickListener {

            if (TextUtils.isEmpty(title.text.toString())) {
                title.setError("Enter title")
            } else if (TextUtils.isEmpty(desc.text.toString())) {
                desc.setError("please enter field")
            } else {
                val db = DbHelper(this)
                val bol = db.insertNote(title.text.toString(), desc.text.toString())
                if (bol) {

                    title.text.clear()
                    desc.text.clear()
                    mShow()
                }
                dialog.dismiss()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }




    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        is_in_action_mode = false
        toolbar.menu.clear()
        toolbar.setTitle("Notes")
        toolbar.inflateMenu(R.menu.menues)
        adapter.notifyDataSetChanged()
        listNew.clear()
        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        is_in_action_mode = false
        adapter.notifyDataSetChanged()
        toolbar.setTitle("Notes")
        listNew.clear()
    }

    override fun onClick(position: Int, title: String, desc: String) {

        val intentt = Intent(this, EditActivity::class.java).apply {
        }
        intentt.putExtra("Title", title)
        intentt.putExtra("Desc", desc)
        intentt.putExtra("id", EditActivity.mId)

        this.startActivity(intentt)
        (this as Activity).finish()
        showInterstitial()

//        Toast.makeText(this, "onClicl  ${position.toString()}", Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onLongClick(position: Int) {
        is_in_action_mode = true
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.example_action_mode)
//                 textView_counter.visibility = View.VISIBLE
        positionn = position
        toolbar.setTitle("${listNew.size} item selected")

        adapter.notifyDataSetChanged()

//        textView_counter.visibility = View.VISIBLE

//        Toast.makeText(this, "onLongClick Listner $position", Toast.LENGTH_SHORT).show()

    }

    override fun onSelectNote(list: List<Int>) {
        toolbar.setTitle("${list.size} item selected")
    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@MainActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    Toast.makeText(this@MainActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
//            startGame()
        }
    }


}

interface onItemClickListner {
    fun onClick(position: Int, title: String, desc: String)
    fun onLongClick(position: Int)
    fun onSelectNote(list: List<Int>)


}
