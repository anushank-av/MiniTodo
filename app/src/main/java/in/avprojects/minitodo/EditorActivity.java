package in.avprojects.minitodo;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import in.avprojects.minitodo.database.TodoContract;

import static in.avprojects.minitodo.database.TodoContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText titletext;

    private EditText descriptionEditText;

    private Spinner prioritySpinner;

    public int priorityValue;

    private Uri selectionUri;

    private boolean isModified = false;

    private String LOG_TAG = EditorActivity.class.getName();




    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isModified = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent isEdit = getIntent();
        selectionUri = isEdit.getData();
        if (selectionUri == null){
            actionBar.setTitle(R.string.header_add);
        }
        else{
            actionBar.setTitle(R.string.header_edit);

            getLoaderManager().initLoader(0,null,this);
        }


        titletext = (EditText)findViewById(R.id.edit_title);
        descriptionEditText = (EditText)findViewById(R.id.edit_details);
        setupSpinner();

        titletext.setOnTouchListener(mTouchListener);
        descriptionEditText.setOnTouchListener(mTouchListener);
        prioritySpinner.setOnTouchListener(mTouchListener);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addedit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveFormInfo();
                finish();
                return true;
            case android.R.id.home:
                if (!isModified){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener backListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesOptions(backListener);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (!isModified){
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
        }
        DialogInterface.OnClickListener returnPressed = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        finish();
            }

        };
        showUnsavedChangesOptions(returnPressed);
    }

    private void showUnsavedChangesOptions(DialogInterface.OnClickListener backListener) {
        AlertDialog.Builder backDialog = new AlertDialog.Builder(this);
        backDialog.setTitle("Saving Changes")
                .setMessage("Save changes?")
                .setPositiveButton("Yes",backListener)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog!=null)dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void saveFormInfo() {
        String title = titletext.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        if (selectionUri == null /* if it is a new pet*/ && TextUtils.isEmpty(title) && TextUtils.isEmpty(description) && priorityValue == TodoTable.PRIORITY_LOW) return;
        ContentValues cValues = new ContentValues();
        cValues.put(TodoTable.COLUMN_TITLE,title);
        cValues.put(TodoTable.COLUMN_DESCRIPTION,description);
        cValues.put(TodoTable.COLUMN_PRIORITY,priorityValue);
        if(selectionUri == null){
             Uri result = getContentResolver().insert(TodoTable.TABLE_URI,cValues);
            Log.i(LOG_TAG,"Insert Performed");
            if (result!=null){
                Toast.makeText(getApplicationContext(),"Added Successfully :)",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Unable To insert :(",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int updates = getContentResolver().update(selectionUri,cValues,null,null);
            Log.i(LOG_TAG,"Update Performed");
            if (updates!=0){
                Toast.makeText(getApplicationContext(),"Successfully Updated :)",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Unable To Update :(",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerItems = ArrayAdapter.createFromResource(this,R.array.priorities,android.R.layout.simple_spinner_item);
        spinnerItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner = (Spinner)findViewById(R.id.edit_priority);
        prioritySpinner.setAdapter(spinnerItems);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:priorityValue = TodoTable.PRIORITY_LOW;
                        break;
                    case 1:priorityValue = TodoTable.PRIORITY_MED;
                        break;
                    case 2:priorityValue = TodoTable.PRIORITY_HIGH;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priorityValue = TodoTable.PRIORITY_LOW;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodoTable.ID,TodoTable.COLUMN_TITLE, TodoTable.COLUMN_PRIORITY, TodoTable.COLUMN_DESCRIPTION};
        return new CursorLoader(this,selectionUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() <1||data == null)return;
        if (data.moveToFirst()){
            int titleColumnIndex = data.getColumnIndex(TodoTable.COLUMN_TITLE);
            int priorityColumnIndex = data.getColumnIndex(TodoTable.COLUMN_PRIORITY);
            int descriptionColumnIndex = data.getColumnIndex(TodoTable.COLUMN_DESCRIPTION);

            String title = data.getString(titleColumnIndex);
            String description = data.getString(descriptionColumnIndex);
            int priority = data.getInt(priorityColumnIndex);

            titletext.setText(title);
            descriptionEditText.setText(description);
            switch (priority){
                case TodoTable.PRIORITY_LOW:prioritySpinner.setSelection(0);
                    break;
                case TodoTable.PRIORITY_MED:prioritySpinner.setSelection(1);
                    break;
                case TodoTable.PRIORITY_HIGH:prioritySpinner.setSelection(2);
                    break;
                default:prioritySpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        titletext.setText("");
        descriptionEditText.setText("");
        prioritySpinner.setSelection(0);

    }
}
