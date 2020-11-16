package kevlich.fit.bstu.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ListVIewItemAdapter extends ArrayAdapter<Contacts> {
    private final Context context;
    private final ArrayList<Contacts> values;

    public ListVIewItemAdapter(Context context, ArrayList<Contacts> values){
        super(context, R.layout.listview_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_item, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView phone = (TextView) rowView.findViewById(R.id.phone);
        CheckBox checkitem = (CheckBox) rowView.findViewById(R.id.checkItem);
        name.setText(values.get(position).name);
        phone.setText(values.get(position).phone);
        checkitem.setVisibility(rowView.INVISIBLE);
        return rowView;
    }
}
