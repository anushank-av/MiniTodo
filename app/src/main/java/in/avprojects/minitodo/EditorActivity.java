package in.avprojects.minitodo;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class EditorActivity extends AppCompatActivity {
    private EditText titletext;

    private EditText descriptionEditText;

    private Spinner prioritySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        titletext.findViewById(R.id.edit_title);
        descriptionEditText.findViewById(R.id.edit_details);
        setupSpinner();


    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerItems = ArrayAdapter.createFromResource(this,R.array.priorities,android.R.layout.simple_spinner_item);
        spinnerItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner = (Spinner)findViewById(R.id.edit_priority);
        prioritySpinner.setAdapter(spinnerItems);
    }
}
