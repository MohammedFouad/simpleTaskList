package com.advancedtimecontrol.gymlist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.advancedtimecontrol.gymlist.TaskContract.TaskEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    /**
     * TAG will help distinguish between multiple activities in logcat
     */
    private static final String TAG = "MainActivity";

    /**
     * mHelper is an instance of the TaskDbhelper class ehich extends SQlitedatabaseopenhelper
     * this class contains the table which will store the database
     */
    private TaskDbHelper mHelper;

    /**
     * task is the text entered in the alert dialogue
     * this value will be stored in the database under the write() method
     */
    String task;
    /**
     * progress bar will track finished tasks
     * the idea is to keep a productive visualization of what person do
     * so when you done a task the bar will increase with certain percent
     * when you create a new task your progress bar will decrease by a certain percent
     * <p>
     * if we have ten tasks to be done
     * we complete one task then progress bar will increase by 10%
     * if we complete another one it will increase by another 10 percent so total increase will
     * be 20 percent
     * if we add another task so the total number of tasks will be 11 ( 10 + new one )
     * which means that the progress bar must decrease by
     */

    ProgressBar progressBar;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    int totalTasks = 0;

    TextView numOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       numOfTasks = (TextView) findViewById(R.id.numOfTasksTextView);
        numOfTasks.setText("" + totalTasks);

        progressBar = (ProgressBar) findViewById(R.id.showProgress);
        mHelper = new TaskDbHelper(this);

        mTaskListView = (ListView) findViewById(R.id.taskList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open pop up dialoge to add the new task
                alert();
            }
        });
        read();
    }

    /**
     * the alert() method for the alert dialogue, aler dialogue appears after clicking on
     * the Floating Action Button ( FAB ) On the mainactivity
     * Two buttons
     * 1. ADD button: will add new task to list
     * 2. CANCEL button : will disappear the dialogue
     */
    public void alert() {
        final EditText taskEditText = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.addNewTask))
                .setMessage(getString(R.string.messageTask))
                .setView(taskEditText) // this the ID for the entered text in the Alert dialogue
                .setPositiveButton(getString(R.string.addTaskButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // task will store the value entered in the alert dialogue that
                        // that will be stored in the database
                        task = String.valueOf(taskEditText.getText());
                        // write data in database
                        write();
                        // update ui to reflect the new added data
                        read();
                    }

                })
                .setNegativeButton(getString(R.string.cancelTaskButton), null)
                .create();
        dialog.show();

    }

    /**
     * storing text from alert dialogue by variable @task
     */
    public void write() {

        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
        db.insertWithOnConflict(TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);

        if (totalTasks == 0) {
            totalTasks = 1;
        } else {
            totalTasks = totalTasks + 1;
        }


        long tasksCount = getTaskCount();

        int numberOfNotCompletedTask = (int) tasksCount;


        int x = totalTasks - numberOfNotCompletedTask;
        double per2 = ((double) x) / totalTasks;
        double per3 = per2 * 100;
        int per4 = (int) per3;
        int max = 100 * totalTasks;
        progressBar.setMax(100);
        // int per = (int) percentOfNotCompletedToCompleted;
        progressBar.setProgress(per4);

        numOfTasks.setText("" + totalTasks);

        /*
         * check if there is a tasks available or not
         * in start no tasks will be recorded so the progress bar is already blank
         * so no need to set negative progress to it
         */

        db.close();
    }

    /**
     * update the User interface after every interaction with main activity
     */
    public void read() {

        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);

            mTaskListView.setAdapter(mAdapter);


        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }


        // getTaskCount() will fetch the number of rows in the database
        // number of rows will indicate how many unfinished tasks
        // progress will be count by the number of finished tasks dividing by
        // remaining task so it will be continously changing

        cursor.close();
        db.close();


    }

    /**
     * delete the task by clicking a checkbox
     */

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TABLE_NAME,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});

        db.close();


        read();

        /*
         * update the progress bar
         *
         */

        long tasksCount = getTaskCount();

        int numberOfNotCompletedTask = (int) tasksCount;


        int diffTasks = totalTasks - numberOfNotCompletedTask;
        double diffTasksDivision = ((double) diffTasks) / totalTasks;
        double difftasksPercent = diffTasksDivision * 100;
        int difftasksPercentInteger = (int) difftasksPercent;
        // maximum progress bar value is 100
        progressBar.setMax(100);

        progressBar.setProgress(difftasksPercentInteger);
        numOfTasks.setText("" + totalTasks);
    }

    /**
     * count of rows in the mHelper data
     */
    public long getTaskCount() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            long tasksCount = getTaskCount();

            int numberOfNotCompletedTask = (int) tasksCount;
            if (totalTasks == 0) {
                Toast.makeText(this, " " + getString(R.string.noTasks), Toast.LENGTH_SHORT).show();
            }
            if (numberOfNotCompletedTask > 0) {
                Toast.makeText(this, " " + getString(R.string.completeTasks), Toast.LENGTH_SHORT).show();
            } else {
                // reset progress bar and total tasks
                progressBar.setProgress(0);
                totalTasks = 0;
                numOfTasks.setText("" + totalTasks);
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
