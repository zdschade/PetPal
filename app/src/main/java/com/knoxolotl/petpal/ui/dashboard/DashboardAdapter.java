package com.knoxolotl.petpal.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knoxolotl.petpal.Household;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;

import java.util.ArrayList;
import java.util.Arrays;

public class DashboardAdapter extends BaseAdapter {

    private final ArrayList<String> arrayList;
    private Context context;
    private TextView itemTitle;
    private SharedPreferences sp;

    public DashboardAdapter(Context context) {
        Log.d("Adapter", "DashboardAdapter");
        this.context = context;

        Household currentHousehold = MainActivity.getCurrentHousehold();
        Log.d("Adapter1", MainActivity.getCurrentHousehold().getHead().toString());
        Log.d("Adapter2", MainActivity.getCurrentUser().toString());

        if (currentHousehold.getHead().toString().equals(MainActivity.getCurrentUser().getRef().toString())) {  // if the user is the head of the household
            //this.arrayList = new ArrayList<>(Arrays.asList("Switch Household", "Manage Household", "Manage Pets", "Manage Notifications", "Profile", "Settings", "Logout"));
            this.arrayList = new ArrayList<>(Arrays.asList("Switch Household", "Manage Household", "Manage Pets", "Settings", "Logout"));
        } else {  // if the user is not the head of the household
            //this.arrayList = new ArrayList<>(Arrays.asList("Switch Household", "Profile", "Settings", "Logout"));  // hide options for non-head of household
            this.arrayList = new ArrayList<>(Arrays.asList("Switch Household", "Settings", "Logout"));  // hide options for non-head of household
        }

        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public String getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.dashboard_item,
                parent, false );

        itemTitle = convertView.findViewById(R.id.dashboard_item_title);

        itemTitle.setText(arrayList.get(position));

        return convertView;
    }
}
