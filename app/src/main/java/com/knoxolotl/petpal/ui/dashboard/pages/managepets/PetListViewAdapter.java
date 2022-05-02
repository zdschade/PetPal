package com.knoxolotl.petpal.ui.dashboard.pages.managepets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;

import java.util.ArrayList;

public class PetListViewAdapter extends BaseAdapter {

    Context context;
    ArrayList<Pet> arrayList;

    ImageView petIcon;
    TextView petName;
    TextView petSpecies;

    public PetListViewAdapter(Context context, ArrayList<Pet> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Pet getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void deleteItem(int position) {
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.pet_listview_item,
                parent, false );
        //checkBox = convertView.findViewById(R.id.display_checkbox);
        petIcon = convertView.findViewById(R.id.pet_list_icon);
        petName = convertView.findViewById(R.id.pet_list_name);
        petSpecies = convertView.findViewById(R.id.pet_list_species);

        if (arrayList.get(position).getSpecies().equals("Dog")) {
            petIcon.setImageResource(R.drawable.dog_logo);
        } else if (arrayList.get(position).getSpecies().equals("Cat")) {
            petIcon.setImageResource(R.drawable.cat_logo);
        } else {
            petIcon.setImageResource(R.drawable.paw_logo);
        }

        petName.setText(arrayList.get(position).getName());
        petSpecies.setText(arrayList.get(position).getSpecies());

        return convertView;
    }
}
