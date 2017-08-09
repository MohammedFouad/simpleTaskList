package com.advancedtimecontrol.gymlist;

import android.provider.BaseColumns;

/**
 * Created by Mohamed on 19/07/2017.
 */

public class TaskContract {

    public static final String DB_NAME = "taskProgress.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns{
        public static final String TABLE_NAME = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }
}
