package com.example.securenotesapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context): SQLiteOpenHelper(context,DATA_BASE_NAME_NOTES,null,1) {

    companion object {
        const val DATA_BASE_NAME_NOTES = "notes.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db!!.execSQL(
            "create table NOTES " +
                    "(id integer primary key autoincrement , Title varchar(255),DESCRIPTION varchar (255))"
        )
    }
    fun insertNote(title: String?, desc: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("Title", title)
        contentValues.put("DESCRIPTION", desc)
        db.insert("NOTES", null, contentValues)
        return true
    }
    fun ShowData(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("select * from NOTES ORDER by id DESC", null)
    }

    open fun update(id: Int?,  title: String?, desc: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Title", title)
        contentValues.put("DESCRIPTION", desc)
        db.update(
            "NOTES", contentValues, "id = ? ", arrayOf((id!!).toString())
        )
        return true
    }
    open fun delete(id: Int): Int? {
        val db = this.writableDatabase
        return db.delete(
            "NOTES",
            "id = ? ", arrayOf((id).toString())

        )

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db!!.execSQL("DROP TABLE IF EXISTS NOTES")
        onCreate(db)
    }
}