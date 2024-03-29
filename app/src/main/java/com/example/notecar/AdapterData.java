package com.example.notecar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterData extends BaseAdapter
{
    private Context context;
    private ArrayList<DataTable> arrayList;
    public ArrayList<Integer> itemsSelected = new ArrayList<Integer>();

    public AdapterData(Context context, ArrayList<DataTable> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Object getItem(int position)
    {
        return arrayList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view==null)
        {
            view=LayoutInflater.from(context).inflate(R.layout.list_layout,parent,false);
        }

        TextView person=view.findViewById(R.id.person_text);
        TextView time=view.findViewById(R.id.time_text);
        TextView place=view.findViewById(R.id.place_text);
        //TextView date=view.findViewById(R.id.date_text);
        //TextView id=view.findViewById(R.id.id_text);

        DataTable dataItem = (DataTable)getItem(position);

        person.setText(dataItem.getPerson());
        time.setText(dataItem.getTime());
        place.setText(dataItem.getPlace());
        //date.setText(dataItem.getDate());
        //id.setText(String.valueOf(dataItem.getId()));

        return view;
    }

    @Override
    public int getCount()
    {
        return this.arrayList.size();
    }

    public void swapItems(ArrayList<DataTable> items) {
        this.arrayList = items;
        notifyDataSetChanged();
    }

}