package com.example.todolistrecycleview.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.todolistrecycleview.database.DbHelper;
import com.example.todolistrecycleview.model.Task;

import java.util.ArrayList;

public class TaskDAO {
    private DbHelper dbHelper;
    private SQLiteDatabase database;

    public TaskDAO(Context context) {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long add(Task task) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("content", task.getContent());
        values.put("date", task.getDate());
        values.put("type", task.getType());

        long check = database.insert("tasks", null, values);
        if(check <= 0) {
            return -1;
        }
        return 1;
    }

    public long update(Task task) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("content", task.getContent());
        values.put("date", task.getDate());
        values.put("type", task.getType());

        long check = database.update("tasks", values, "id = ?", new String[]{String.valueOf(task.getId())});
        if(check <= 0) {
            return -1;
        }
        return 1;
    }

    public ArrayList<Task> get() {
        ArrayList<Task> list = new ArrayList<>();
        database = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM tasks", null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    list.add(new Task(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return list;
    }

    public boolean remove(int id) {
        int row = database.delete("tasks", "id = ?", new String[]{String.valueOf(id)});
        return row != -1;
    }

    public boolean updateType(int id, boolean check) {
        int status = check ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put("status", status);
        long row = database.update("tasks", values, "id = ?", new String[]{String.valueOf(id)});
        return row != -1;
    }
}
