package com.vikrant.echo.echoDatabases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.vikrant.echo.Songs
import com.vikrant.echo.echoDatabases.Echodata.staticted.DB_name
import com.vikrant.echo.echoDatabases.Echodata.staticted.DB_table
import com.vikrant.echo.echoDatabases.Echodata.staticted.SongID
import com.vikrant.echo.echoDatabases.Echodata.staticted.favsonglist
import com.vikrant.echo.echoDatabases.Echodata.staticted.songArtist
import com.vikrant.echo.echoDatabases.Echodata.staticted.songPath
import com.vikrant.echo.echoDatabases.Echodata.staticted.songTitle
import com.vikrant.echo.echoDatabases.Echodata.staticted.version
import com.vikrant.echo.fragments.SongPlay
import java.lang.Exception

class Echodata : SQLiteOpenHelper {


    object staticted {
        var favsonglist: ArrayList<Songs>? = null
        val DB_table = "favTable"
        var SongID = "SongID"
        var songTitle = "SongTitle"
        var songArtist = "SongArtist"
        var songPath = "songPath"
        var version = 1
        val DB_name = "favDatabase"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var sqlquery = "CREATE TABLE IF NOT EXISTS favTable(SongID INTEGER,SongTitle STRING,SongArtist  STRING,songPath STRING);"
        db?.execSQL(sqlquery)

       // Toast.makeText(SongPlay.statfavUse.mysongActivity,"Table is created",Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    constructor(context: Context?) : super(
        context,
        DB_name,
        null,
        version

    )

    fun storeAsFav(ID: Int?, title: String?, Artist: String?, path: String?) {
        val db = this.writableDatabase
        var contentvalues = ContentValues()
        contentvalues?.put(SongID, ID)
        contentvalues?.put(songTitle, title)
        contentvalues?.put(songPath, path)
        contentvalues?.put(songArtist, Artist)
        db.insert(DB_table, null, contentvalues)
        db.close()
       // Toast.makeText(SongPlay.statfavUse.mysongActivity,"Data is saved in data base",Toast.LENGTH_SHORT).show()
    }

    fun quarydbList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            val quary = "SELECT * FROM $DB_table;"

            var cersor = db.rawQuery(quary, null)
            if (cersor.moveToFirst()) {
                do {
                    var idcu = cersor.getInt(cersor.getColumnIndexOrThrow(SongID)).toLong()
                    var artistcu = cersor.getString(cersor.getColumnIndexOrThrow(songArtist))
                    var titlecu = cersor.getString(cersor.getColumnIndexOrThrow(songTitle))
                    var path = cersor.getString((cersor.getColumnIndexOrThrow(songPath)))
                  //  Toast.makeText(SongPlay.statfavUse.mysongActivity,"$titlecu,$idcu",Toast.LENGTH_SHORT).show()
                    favsonglist?.add(Songs(idcu, titlecu, artistcu, path, 0))

                } while (cersor.moveToNext())
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return favsonglist


    }

    fun checkIdExist(id: Int): Boolean {
        var storeid = -1092
        val db = this.readableDatabase
       // Toast.makeText(SongPlay.statfavUse.mysongActivity,"$id",Toast.LENGTH_SHORT).show()
        val quary = "SELECT * FROM $DB_table WHERE $SongID = '$id' ;"

        var cersor = db.rawQuery(quary, null)
        if (cersor.moveToFirst()) {
            do {
                 storeid = cersor.getInt(cersor.getColumnIndexOrThrow(SongID))

            } while (cersor.moveToNext())
        } else {
            return false
        }



        return storeid!=-1092
    }

    fun delete(id: Int) {
        val db = this.writableDatabase
        db.delete(DB_table, SongID + "=" + id, null)
       // Toast.makeText(SongPlay.statfavUse.mysongActivity,"Delete from Faviroute echodatabase",Toast.LENGTH_SHORT).show()
        db.close()
    }

    fun checkSize(): Int {
        var count = 1
        var db = this.readableDatabase
        var query = "SELECT * FROM $DB_table;"
        val curser = db.rawQuery(query, null)
        if (curser != null && curser.moveToFirst()) {
            do {
                count=count+ 1
            } while (curser.moveToNext())
        } else {
            return 0
        }
        return count
    }

}