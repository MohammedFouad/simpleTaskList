package com.advancedtimecontrol.gymlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.advancedtimecontrol.gymlist.TaskContract.DB_NAME;
import static com.advancedtimecontrol.gymlist.TaskContract.DB_VERSION;
/**
 * Created by Mohamed on 19/07/2017.
 */

public class TaskDbHelper extends SQLiteOpenHelper {
    public TaskDbHelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + "( "+
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      //  db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
       // onCreate(db);

    }
}
