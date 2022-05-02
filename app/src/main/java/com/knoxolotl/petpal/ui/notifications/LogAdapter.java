package com.knoxolotl.petpal.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knoxolotl.petpal.DataHistory;
import com.knoxolotl.petpal.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LogAdapter extends BaseAdapter {
    // Adapter for history log

    Context context;
    ArrayList<HashMap<String, Object>> arrayList;
    TextView description, details;

    public LogAdapter(Context context, ArrayList<HashMap<String, Object>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public HashMap<String, Object> getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.log_listview_item,
                parent, false );

        description = convertView.findViewById(R.id.log_item_description);
        details = convertView.findViewById(R.id.log_item_details);

        // String setup

        String action = "";
        switch (arrayList.get(position).get("type").toString()) {            case "food_history":
                action = " fed ";
                break;
            case "med_history":
                action = " gave medicine to ";
                break;
            case "water_history":
                action = " gave water to ";
                break;
            case "walk_history":
                action = " walked ";
        }

        String itemTime = "";
        if (arrayList.get(position).get("item_time") != null) {
            itemTime = String.format(" (%s)", arrayList.get(position).get("item_time").toString());
        }

        description.setText(arrayList.get(position).get("username") + action + arrayList.get(position).get("pet_name")
                + itemTime);

        details.setText("@" + arrayList.get(position).get("log_time").toString());

        return convertView;
    }
}
