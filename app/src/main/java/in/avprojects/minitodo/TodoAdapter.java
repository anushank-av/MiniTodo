package in.avprojects.minitodo;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by anush on 15-02-2017.
 */

public class TodoAdapter extends ArrayAdapter<TodoDetails> {
    public TodoAdapter(Context context, List<TodoDetails> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item,parent,false);

        }
        TodoDetails singleData = getItem(position);
        TextView priorityColor = (TextView)listView.findViewById(R.id.todo_priority);
        TextView todoTitle = (TextView)listView.findViewById(R.id.todo_detail);
        todoTitle.setText(singleData.gettTitle());
        GradientDrawable priorityCircle = (GradientDrawable)priorityColor.getBackground();
        int colorValue = getPriorityColor(singleData.getId());
        priorityCircle.setColor(colorValue);
        return listView;
    }

    private int getPriorityColor(int id) {
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
        return ContextCompat.getColor(getContext(),returnValue);
    }
}