package in.avprojects.minitodo;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import in.avprojects.minitodo.database.TodoContract;

import static in.avprojects.minitodo.database.TodoContract.*;

/**
 * Created by anush on 15-02-2017.
 */

public class TodoAdapter extends CursorAdapter{
    public final String LO_TAG = TodoAdapter.class.getName();

    public TodoAdapter(Context context, Cursor c) {
        super(context, c, 0 /*Flags*/);
    }

    private int getPriorityColor(Context context,int id) {
        int returnValue;
        switch (id){
            case 0:returnValue = R.color.colorLow;
                break;
            case 1:returnValue = R.color.colorMed;
                break;
            case 2:returnValue = R.color.colorHigh;
                break;
            default:returnValue = R.color.colorLow;
        }
        return ContextCompat.getColor(context,returnValue);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.todo_item,parent,false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        TextView todoDetails = (TextView)view.findViewById(R.id.todo_detail);
        TextView todoPriority = (TextView)view.findViewById(R.id.todo_priority);
        int detailsIndex = cursor.getColumnIndex(TodoTable.COLUMN_TITLE);
        int priorityIndex = cursor.getColumnIndex(TodoTable.COLUMN_PRIORITY);

        String det = cursor.getString(detailsIndex);
        int priority = cursor.getInt(priorityIndex);

        todoDetails.setText(det);
        GradientDrawable mDrawable = (GradientDrawable)todoPriority.getBackground();
        mDrawable.setColor(getPriorityColor(context,priority));

        CheckBox itemCheck = (CheckBox)view.findViewById(R.id.todo_checked);
        itemCheck.setChecked(false);
        itemCheck.setTag(new Integer(cursor.getPosition()));
        itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               Integer current = (Integer)buttonView.getTag();
                if (cursor.moveToPosition(current)) {
                    if (isChecked) {
                        int idIndex = cursor.getColumnIndex(TodoTable.ID);
                        final int id = cursor.getInt(idIndex);
                        final Uri uriTodDelete = ContentUris.withAppendedId(TodoTable.TABLE_URI, id);
                        Animation slideOut = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
                        slideOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                context.getContentResolver().delete(uriTodDelete, null, null);


                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(slideOut);


                    }
                }
            }
        });

    }
}