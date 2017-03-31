package uk.ac.northumbria.securephonebook;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import uk.ac.northumbria.securephonebook.models.Contact;

/**
 * This class is necessary for displaying records in the ListView.
 * Created by Siddhant on 30/03/2017.
 */

public class ContentAdapter extends BaseAdapter {
    private Context context;    // context of the application
    private ArrayList<Contact> contacts;    // ArrayList of Contact

    /**
     * Removing the default constructor because we need contacts to be passed to it.
     */
    private ContentAdapter() {}

    /**
     * Constructor for Content Adapter. Requires to be initialized with application context and
     * the ArrayList of Contact.
     * @param context - Application Context.
     * @param contacts - ArrayList of Contact.
     */
    public ContentAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    /**
     * Overriding the getCount method. Returns total number of contacts.
     * @return - Total number of contacts.
     */
    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Overriding the getView method for displaying data in the ListView.
     * @param position - position of the record which is passed by the parent class.
     * @param convertView - View passed by the parent class.
     * @param parent - ViewGroup passed by the parent class.
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setText(contacts.get(position).toString());
        textView.setTextSize(20);
        return textView;
    }
}
