package com.knoxolotl.petpal.changehousehold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knoxolotl.petpal.Household;
import com.knoxolotl.petpal.R;

import java.util.ArrayList;

public class HouseholdListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Household> arrayList;

    TextView householdNameView;

    public HouseholdListAdapter(Context context, ArrayList<Household> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Household getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.household_list_item,
                parent, false );

        householdNameView = convertView.findViewById(R.id.household_list_item_name);

        householdNameView.setText(arrayList.get(position).getName());

        return convertView;
    }
}
