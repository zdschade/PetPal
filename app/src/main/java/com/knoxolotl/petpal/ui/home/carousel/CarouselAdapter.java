package com.knoxolotl.petpal.ui.home.carousel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;

import java.util.ArrayList;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    // adapter for the pet selector carousel

    ArrayList<Pet> arrayList;
    Context context;
    View view;
    ViewGroup parentContext;

    CarouselViewHolder extViewHolder;
    private int lastPosition = -1;
    int selectedPosition = 0;
    Pet currentPet;

    private final OnItemClickListener listener;  // allows for selecting a pet

    // allows for selecting a pet
    public interface OnItemClickListener {
        void onItemClick(Pet pet);
    }

    public CarouselAdapter(ArrayList<Pet> arrayList, Context context, OnItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;

        // set the first pet as the current pet
        currentPet = arrayList.get(0);
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContext = parent;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);

        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(arrayList.get(position), listener);  //bind the pet, for use with click listener

        // first pet operations
        if (position == 0 && selectedPosition == 0) {
            selectPetBG(holder, 0);  // change background color
            extViewHolder = holder;  // set the current view holder

            // Add extra margin to the first pet to allow for centering (not perfect)
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);

            params.setMargins(250, 35, 15, 35);
            holder.carouselItemLayout.setLayoutParams(params);
        }

        // last pet operations
        if (position == arrayList.size() - 1) {
            // Add extra margin to the last pet to allow for centering (not perfect)
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(15, 35, 300, 35);
            holder.carouselItemLayout.setLayoutParams(params);
        }

        // set the correct icon for the pet species (paw for misc)
        if (arrayList.get(position).getSpecies().toLowerCase().equals("dog")) {
            holder.petLogo.setImageResource(R.drawable.dog_logo_black);
        } else if (arrayList.get(position).getSpecies().toLowerCase().equals("cat")) {
            holder.petLogo.setImageResource(R.drawable.cat_logo_black);
        } else {
            holder.petLogo.setImageResource(R.drawable.paw_logo);
        }

        holder.petName.setText(arrayList.get(position).getName());  // set the pet name

        // the actual click listener
        holder.carouselItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(arrayList.get(position));

                if (extViewHolder == holder) return;  // if you click on the already selected pet, do nothing

                // scroll to the selected pet and change its background color, also make it slightly bigger
                RecyclerView rv = (RecyclerView) holder.carouselItemLayout.getParent();
                rv.smoothScrollToPosition(position);
                v.setBackgroundResource(R.drawable.carousel_item_bg_selected);
                v.setPadding(150, 30, 150, 30);
                currentPet = arrayList.get(position);

                // reset the previously selected pet
                extViewHolder.carouselItemLayout.setBackgroundResource(R.drawable.carousel_item_bg);
                extViewHolder.carouselItemLayout.setPadding(100, 20, 100, 20);
                extViewHolder = holder;
            }
        });
    }

    public void scrollToPet(RecyclerView rv, int position) {
        rv.scrollToPosition(position); //TODO: make this smoother
        // Default pet??? how to do???

        //TODO: Fix pet highlighting
    }

    public Pet getCurrentPet() {
        return currentPet;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Pet getItem(int position) {
        return arrayList.get(position);
    }

    public int getItemPosition(Pet pet) {
        return arrayList.indexOf(pet);
    }

    public void selectPetBG(CarouselViewHolder holder, int position) {
        holder.carouselItemLayout.setBackgroundResource(R.drawable.carousel_item_bg_selected);
        holder.carouselItemLayout.setPadding(150, 30, 150, 30);
    }

    public CarouselViewHolder getViewHolder() {
        return extViewHolder;
    }

    public View getView() {
        return view;
    }

    public class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView petLogo;
        TextView petName;
        LinearLayout carouselItemLayout;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            petLogo = itemView.findViewById(R.id.carousel_pet_logo);
            petName = itemView.findViewById(R.id.carousel_pet_name);
            carouselItemLayout = itemView.findViewById(R.id.carousel_item_layout);
        }

        // I have no idea why I need this but it doesn't work without it
        public void bind(final Pet item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // maybe set up animations later???
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void swapData(ArrayList<Pet> newArray) {
        arrayList = newArray;
        notifyDataSetChanged();
    }

    public void clearData() {
        arrayList.clear();
        notifyDataSetChanged();
    }

}
