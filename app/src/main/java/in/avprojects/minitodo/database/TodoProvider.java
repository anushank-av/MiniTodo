package in.avprojects.minitodo.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static in.avprojects.minitodo.database.TodoContract.*;

/**
 * Created by anush on 16-02-2017.
 */

public class TodoProvider extends ContentProvider {
    //Log tag
    public static final String LOG_TAG = TodoProvider.class.getName();

    // Uri Matcher code for all todos
        public static final int TODO_ALL = 101;

    //Uri matcher code for a single todo.
        public static final int TODO_SINGLE = 102;

    //Creating the instance of the Uri matcher
    public static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //A static block, for adding the Uri's and their corresponding codes to the uri matcher
    static{
        //General uri.Implement : adduri(Base uri, Table name, code)
        mUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY,TodoContract.PATH_TO_TODO,TODO_ALL);

        //Specific Uri. Implement: same as above(but using the column id also in the uri)
        mUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY,TodoContract.PATH_TO_TODO+"/#",TODO_SINGLE);
    }

    //The helper object
    private TodoHelper mhelper;

    @Override
    public boolean onCreate() {
        mhelper = new TodoHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase myDB = mhelper.getReadableDatabase();
        Cursor c;
        int code = mUriMatcher.match(uri);
        //switch case to process each type of uri differently.
        switch (code){
            //if it is a general uri
            case TODO_ALL:
                c =  myDB.query(TodoTable.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case TODO_SINGLE:
                selection = TodoTable.ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c =  myDB.query(TodoTable.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:throw new IllegalArgumentException("Invalid URI");
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case TODO_ALL:
                return TodoTable.CONTENT_BASE_TYPE;
            case TODO_SINGLE:
                return TodoTable.CONTENT_DIR_TYPE;
            default:
                throw new IllegalStateException("Unknown uro" + uri + "with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {                  //Todo: For genral Uri's only
        int match = mUriMatcher.match(uri);
        switch (match){
            case TODO_ALL: return insertData(uri,values);

            default:throw new IllegalArgumentException("Not Correct Uri for insertion");

        }
    }

    private Uri insertData(Uri uri, ContentValues values) {
        SQLiteDatabase iDb = mhelper.getWritableDatabase();
        String title = values.getAsString(TodoTable.COLUMN_TITLE);
        if (title == null)
            throw new IllegalArgumentException("Specify a title");

        Integer prioity = values.getAsInteger(TodoTable.COLUMN_PRIORITY);
        if (prioity == null)
            throw new IllegalArgumentException("Specify a priority");

        long result = iDb.insert(TodoTable.TABLE_NAME, null, values);
        if (result <0)
            Log.e(LOG_TAG,"Error during Insertion");

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,result);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {   //Todo: For Specific Uri's only
        int match = mUriMatcher.match(uri);
        int delCode;
        SQLiteDatabase db = mhelper.getWritableDatabase();
        switch (match){
            case TODO_ALL : delCode = db.delete(TodoTable.TABLE_NAME,selection,selectionArgs);
                break;
            case TODO_SINGLE : selection = TodoTable.ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                delCode = db.delete(TodoTable.TABLE_NAME,selection,selectionArgs);
                break;
            default: throw new IllegalArgumentException("Deletion is not possible with this uri.");
        }
        if (delCode!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return delCode;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        switch(match){
            case TODO_ALL : return updateData(uri,values,selection,selectionArgs);
            case TODO_SINGLE : selection = TodoTable.ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateData(uri,values,selection,selectionArgs);
            default:throw new IllegalArgumentException("Unable to update Illegal query");
        }

    }

    private int updateData(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) return 0;
        String title = values.getAsString(TodoTable.COLUMN_TITLE);
        if (title == null)
            throw new IllegalArgumentException("Specify a title");

        Integer prioity = values.getAsInteger(TodoTable.COLUMN_PRIORITY);
        if (prioity == null)
            throw new IllegalArgumentException("Specify a priority");
        SQLiteDatabase uDb = mhelper.getWritableDatabase();
        int update = uDb.update(TodoTable.TABLE_NAME, values, selection, selectionArgs);
        if (update!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return update;
    }
}
