package com.knoxolotl.petpal.ui.home.modulegrid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.knoxolotl.petpal.R;

import java.util.ArrayList;

public class CheckboxAdapter extends BaseAdapter {
    // UNUSED CLASS

    private final ArrayList<String> arrayList;
    private Context context;
    private CheckBox checkBox;

    public CheckboxAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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

    public void deleteItem(int position) {
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.simple_checkbox,
                parent, false );
        checkBox = convertView.findViewById(R.id.display_checkbox);
        checkBox.setChecked(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("checkbox", "CHECKBOXADAPTER");
            }
        });

        return convertView;
    }
}
