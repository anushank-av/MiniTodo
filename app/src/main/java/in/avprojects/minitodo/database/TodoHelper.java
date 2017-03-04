package in.avprojects.minitodo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static in.avprojects.minitodo.database.TodoContract.*;

/**
 * Created by anush on 16-02-2017.
 */

public class TodoHelper extends SQLiteOpenHelper {

    // The database name

    public static final String DB_NAME = "tododatabase.db";

    //The database version

    public static final int DB_VERSION = 1;
    //The SQL Instruction to ceate a database
    public static final String CREATE_TABLE = "CREATE TABLE " + TodoTable.TABLE_NAME + "("
            +TodoTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            +TodoTable.COLUMN_TITLE + " TEXT NOT NULL,"
            +TodoTable.COLUMN_PRIORITY+" INTEGER DEFAULT 0,"
            +TodoTable.COLUMN_DESCRIPTION+" TEXT"+");";
    public TodoHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist"+TodoTable.TABLE_NAME);
        onCreate(db);
    }
}
