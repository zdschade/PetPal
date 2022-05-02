package com.knoxolotl.petpal.ui.dashboard.pages.managehousehold;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.User;

import java.util.ArrayList;

public class RemoveMemberAdapter extends BaseAdapter {

    ArrayList<User> arrayList;
    Context context;

    TextView householdNameView;

    public RemoveMemberAdapter(Context context, ArrayList<User> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
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

        householdNameView.setText(arrayList.get(position).getEmail());


        return convertView;
    }
}
