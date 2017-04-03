package uk.ac.northumbria.securephonebook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.northumbria.securephonebook.models.Group;



public class GroupAdapter<T> extends ArrayAdapter<T> {

    /**
     * Constructor. Same as parent.
     * @param context
     * @param resource
     * @param objects
     */
    public GroupAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    /**
     * Overriding for changing the text color and size.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        // Set the color here
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        return textView;
    }

    /**
     * Overriding for changing the text color and size.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView text = (TextView)view.findViewById(android.R.id.text1);
        text.setTextSize(18);
        text.setTextColor(Color.WHITE);

        return view;
    }
}
