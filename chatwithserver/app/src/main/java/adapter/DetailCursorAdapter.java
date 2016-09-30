package adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.example.rq.chatwithserver.R;


/**
 * Created by rq on 16/2/21.
 */
public class DetailCursorAdapter extends ResourceCursorAdapter{
    protected final static int ROW_LAYOUT = R.layout.main_list_text;
    public DetailCursorAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cur, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView titleLine = (TextView) view.findViewById(R.id.user_name);
        titleLine.setText(cursor.getString(cursor.getColumnIndex("name")));
        TextView authorLine = (TextView) view.findViewById(R.id.user_text);
       authorLine.setText(cursor.getString(cursor.getColumnIndex("text")));

    }
}
