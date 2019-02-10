package abc.kintegratedtest

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBAdapter(context : Context, flags : Int) {
    companion object {
        const val DB_NAME = "list.db"
        const val DB_VERSION = 1

        const val READABLE = 0
        const val WRITABLE = 1
    }

    private val helper by lazy { println("helper initialized"); MySQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) }
    private val db by lazy { println("db initialized"); if (flags == 0) helper.readableDatabase else helper.writableDatabase }

    fun insert(table : String, nullColumnHack : String?, values: ContentValues) { db.insert(table, nullColumnHack, values) }
    fun update(table : String, values : ContentValues, whereClause : String, whereArgs : Array<String>) { db.update(table, values, whereClause, whereArgs) }
    fun delete(table : String, whereClause : String, whereArgs : Array<String>) { db.delete(table, whereClause, whereArgs) }
    fun rawQuery(sql : String, selectionArgs : Array<String?>? = null) : Cursor = db.rawQuery(sql, selectionArgs)

    class MySQLiteOpenHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
            sqLiteDatabase.execSQL("create table memo(" +
                    "_id integer primary key autoincrement, " +
                    "content text, " +
                    "feel_id integer, " +
                    "weather_id integer, " +
                    "date_year integer, " +
                    "date_month integer, " +
                    "date_day integer);")
        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
            sqLiteDatabase.execSQL("drop table if exists memo")

            onCreate(sqLiteDatabase)
        }

        override fun onDowngrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            sqLiteDatabase.execSQL("drop table if exists memo")

            onCreate(sqLiteDatabase)
        }
    }
}