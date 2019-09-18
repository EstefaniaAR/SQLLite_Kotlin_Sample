package com.example.estefaniaar.sqlkotlin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.appcompat.R.id.image
import android.view.View
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class Main2Activity : AppCompatActivity() {

    lateinit var selectedImage:Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val intent=intent
        val info = intent.getStringExtra("info")
        if(info.equals("new"))
        {
           // val background = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.select)
            //imageView2.setImageBitmap(background)
            save.visibility=View.VISIBLE
            name.setText("")
        }
        else
        {
            val title = intent.getStringExtra("name")
            name.setText(title)

            val chosen = Globals.Chosen
            val bitmap = chosen.returnImage()

            imageView2.setImageBitmap(bitmap)
            save.visibility= View.INVISIBLE
        }
    }

    fun select(view: View)
    {
        println("Selecting image....")
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),2)
        }
        else
        {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode ==1 && resultCode==Activity.RESULT_OK && data !=null)
        {
            try {
                val image = data.data
                selectedImage= MediaStore.Images.Media.getBitmap(this.contentResolver,image)
                imageView2.setImageBitmap(selectedImage)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun save (view:View)
    {
        val artName = name.text.toString()
        val outputStream= ByteArrayOutputStream()
        selectedImage?.compress(Bitmap.CompressFormat.PNG,50,outputStream)
        val byteArray = outputStream.toByteArray()
        try
        {
            val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, imageBLOB)")
            val sqlString= "INSERT INTO arts (name,image) VALUES(?,?)"
            val statement = database.compileStatement(sqlString)
            statement.bindString(1,artName)
            statement.bindBlob(2,byteArray)
            statement.execute()
        }
        catch(e: Exception)
        {
            e.printStackTrace()
        }
        val intent= Intent(applicationContext,MainActivity::class.java)
        startActivity(intent)
    }
}
