package com.sreeginy.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sreeginy.todolist.Model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "todo_list.db";

    private static final String TABLE_NAME = "my_todo";
    private static final String COLUMN_ID = "ID";
    public static final String COLUMN_TASK = "TASK";
    public static final String COLUMN_STATUS = "STATUS";
    private Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        db = getWritableDatabase();
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
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, model.getTask());
        values.put(COLUMN_STATUS, 0);
        db.insert(TABLE_NAME, null, values);
    }

    public void updateTask(int id, String task) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id, int status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }

    public List<ToDo> getAllTasks() {
        List<ToDo> modelList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                ToDo task = new ToDo();
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                task.setTask(cursor.getString(cursor.getColumnIndex(COLUMN_TASK)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                modelList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return modelList;
    }

    public Cursor getDataByName(String task) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TASK + " LIKE '%" + task + "%'";
        return db.rawQuery(query, null);
    }

    public Cursor readAllData() {
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
