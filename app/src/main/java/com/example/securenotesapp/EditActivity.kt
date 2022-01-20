package com.example.securenotesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.interstitial.InterstitialAd

class EditActivity : AppCompatActivity() {
    lateinit var eTitleEditText: EditText
    lateinit var edescEditText: EditText
    private lateinit var mInterstitialAd: InterstitialAd

    companion object{

        var mId :Int? = null
        var mydb: DbHelper? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit2)
        val mUpdateBtn:ImageButton
        mUpdateBtn = findViewById(R.id.update)
        eTitleEditText=findViewById(R.id.titleAfterClick)
        edescEditText=findViewById(R.id.descriptionAfterClick)

//        mInterstitialAd = InterstitialAd
        val intent = intent
        val title = intent.getStringExtra("Title")
        val desc =  intent.getStringExtra("Desc")

        eTitleEditText.setText(title)
        edescEditText.setText(desc)


//        Toast.makeText(this, "$mId", Toast.LENGTH_SHORT).show()


        mUpdateBtn.setOnClickListener {
            val intentt = Intent(this@EditActivity,MainActivity::class.java).apply {
                update()


            }
            startActivity(intentt)
            finish()

        }


    }
    fun update(){
        val title=eTitleEditText.text.toString()
        val desc=edescEditText.text.toString()
        val db = DbHelper(this)
        val bol = db.update(mId,title,desc)

        if (bol){

        }

//        Toast.makeText(this, "will b updated $mId ", Toast.LENGTH_SHORT).show()
    }



    override fun onBackPressed() {

        val intentt = Intent(this@EditActivity,MainActivity::class.java).apply {
            update()

        }
        startActivity(intentt)
        finish()
    }
}