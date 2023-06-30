package com.sreeginy.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sreeginy.todolist.Model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "todo_list.db";

    private static final String TABLE_NAME = "my_todo";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_TASK = "TASK";
    private static final String COLUMN_STATUS = "STATUS";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , TASK TEXT , STATUS INTEGER )");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
    public void insertTask(ToDo model) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, model.getTask());
        values.put(COLUMN_STATUS, 0);
        db.insert(TABLE_NAME, null,values);
    }


    public void updateTask(int id, String task) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        db.update(TABLE_NAME, values , "ID=?" , new String[]{String.valueOf(id)});
    }


    public void updateStatus(int id, int status) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME , values , "ID=?" , new String[]{String.valueOf(id)});
    }


    public void deleteTask(int id) {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME , "ID=?" , new String[]{String.valueOf(id)});
    }


     public List<ToDo> getAllTasks() {
        db = this.getWritableDatabase();
         Cursor cursor = null;
         List<ToDo> modelList = new ArrayList<>();

         db.beginTransaction();
         try {
             cursor = db.query(TABLE_NAME, null, null , null, null, null, null);
             if (cursor !=null) {
                 if (cursor.moveToFirst()) {
                     do {
                         ToDo task = new ToDo();
                         task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                         task.setTask(cursor.getString(cursor.getColumnIndex(COLUMN_TASK)));
                         task.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                         modelList.add(task);

                     }while (cursor.moveToNext());
                 }
             }
         }finally {
             db.endTransaction();
             cursor.close();
         }
         return modelList;
     }
     
}
