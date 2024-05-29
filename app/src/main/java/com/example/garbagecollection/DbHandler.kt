package com.example.garbagecollection

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

const val DATABASE_NAME="MyDB.db"
const val USERS_TABLE_NAME="Users"
const val IMAGES_TABLE_NAME="Images"
const val id="id"
const val Name="Name"
const val Mobile="Mobile"
const val Password="Password"
const val Rewards="Rewards"
const val userid="userid"
const val bitmap="bitmap"

var currentuser:String = ""
var points:Int = 0
var cid:Int= 0

class DbHandler(var context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null,1) {
    override fun onCreate(Db: SQLiteDatabase?) {
        val createTable1="CREATE TABLE $USERS_TABLE_NAME(id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT , Mobile TEXT , Password TEXT,Rewards INT)"
        val createTable2="CREATE TABLE $IMAGES_TABLE_NAME(user_id INTEGER,bitmap TEXT , FOREIGN KEY (user_id) REFERENCES $USERS_TABLE_NAME(id))"
        Db?.execSQL(createTable1)
        Db?.execSQL(createTable2)
    }

    override fun onUpgrade(Db: SQLiteDatabase?, p1: Int, p2: Int) {
        Db?.execSQL("Drop Table if exists $USERS_TABLE_NAME")
        Db?.execSQL("Drop Table if exists $IMAGES_TABLE_NAME")
        onCreate(Db)
    }

    fun insertuser(user:User): Boolean {
        val db = this.writableDatabase
        val cv=ContentValues().apply{
            put("Name",user.nama)
            put("Mobile",user.mobile)
            put("Password",user.password)
            put("Rewards", 0)
        }
        val result = db.insert(USERS_TABLE_NAME, null, cv)

        if (result == -1L) {
            return false
        }
        return true
    }

    fun insertimage(id:Int,bitmap:String) : Boolean{
        val db=this.writableDatabase
        val cv=ContentValues()
        cv.put("user_id",id)
        cv.put("bitmap",bitmap)

        val result=db.insert("$IMAGES_TABLE_NAME",null,cv)

        if(result.toInt() ==-1){
            return false
        }
        return true
    }

    fun checkusernamepassword(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val selection="Name = ? AND Password=? "
        val selectionArgs= arrayOf(username,password)
        val cursor=db.query(USERS_TABLE_NAME,null,selection,selectionArgs,null,null,null)

        val userExists=cursor.count > 0
        if(userExists){
            currentuser=username
        }
        cursor.close()
        return userExists
    }

    fun getallusers(): ArrayList<User> {
        val notelist = ArrayList<User>()
        val db = readableDatabase
        val query = "SELECT * FROM $USERS_TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
            val mobile = cursor.getString(cursor.getColumnIndexOrThrow("Mobile"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("Password"))

            val user = User(id, name, mobile, password)
            notelist.add(user)
        }

        cursor.close()
        db.close()
        return notelist
    }

    fun addpoints(){
        val db=writableDatabase
        val cv=ContentValues()

        val query = "SELECT * FROM $USERS_TABLE_NAME WHERE Name='$currentuser'"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()

        cid=cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        points=cursor.getInt(cursor.getColumnIndexOrThrow("Rewards"))
        points += 5
        cv.put("Rewards",points)


        val selection = "id = ?"
        val selectionArgs = arrayOf(cid.toString())

        val success = db.update(USERS_TABLE_NAME, cv, selection, selectionArgs)
        if(success>0) {
            Log.d("Success", "POINTS UPDATED :$points  ")
        }
        cursor.close()
        db.close()

    }

    fun getusername():String{
        return currentuser
    }

    fun getrewards():Int{
        return points
    }

    fun getuserid():Int{
        return cid
    }

    fun displayallimages(): ArrayList<Image>{
        val imagelist=ArrayList<Image> ()
        val db=this.readableDatabase
        val query="SELECT ${IMAGES_TABLE_NAME}.bitmap from $IMAGES_TABLE_NAME , $USERS_TABLE_NAME where ${USERS_TABLE_NAME}.id=${IMAGES_TABLE_NAME}.userid"
        val cursor=db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val id=cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val bitmap=cursor.getString(cursor.getColumnIndexOrThrow("bitmap"))

            val image=Image(id,bitmap)
            imagelist.add(image)
        }

        cursor.close()
        db.close()
        return imagelist
    }

}