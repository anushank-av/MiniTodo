package in.avprojects.minitodo.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by anush on 16-02-2017.
 */

public final class TodoContract {
    // Declaration of the content authority

    public TodoContract(){}

    public static final String CONTENT_AUTHORITY = "in.avprojects.minitodo";


    //Base Uri for all Uri'
    public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    //The name of the table ( for external purposes only )

    public static final String PATH_TO_TODO = "todos";

    public static final class TodoTable implements BaseColumns {
        // Some normal declarations
        //Table name

        public static final String TABLE_NAME = "todos";

        //BAse Uri for this table
        public static Uri TABLE_URI = Uri.withAppendedPath(BASE_URI,PATH_TO_TODO);

        //Table attributes;

        //Mimetype for list of todos
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_TO_TODO;
        //Mime type for single todotask
        public static final String CONTENT_BASE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_TO_TODO;

        //ID Column
        public static final String ID = BaseColumns._ID;

        //Title column

        public static final String COLUMN_TITLE = "title";

        //Priority Column

        public static final String COLUMN_PRIORITY = "priority";

        //Description column

        public static final String COLUMN_DESCRIPTION = "description";

        //Possible values for the priority column

        public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_MED = 1;
        public static final int PRIORITY_HIGH = 2;

        public boolean isValidPriority(int pr){
            if (pr == PRIORITY_HIGH||pr == PRIORITY_LOW||pr == PRIORITY_MED) return true;
            else return false;
        }


    }

}
