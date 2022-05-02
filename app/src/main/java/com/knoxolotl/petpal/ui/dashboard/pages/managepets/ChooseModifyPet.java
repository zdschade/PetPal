package com.knoxolotl.petpal.ui.dashboard.pages.managepets;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.Pet;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentChooseModifyPetBinding;
import com.knoxolotl.petpal.databinding.FragmentModifyPetBinding;

import java.util.ArrayList;

public class ChooseModifyPet extends Fragment {
    // Fragment for choosing which pet to modify

    FragmentChooseModifyPetBinding binding;
    View root;
    ArrayList<Pet> petsArray;
    PetListViewAdapter adapter;
    static Pet petToModify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChooseModifyPetBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        petsArray = MainActivity.getPetsArray();

        ListView petListview = binding.chooseModifyPetsListview;

        adapter = new PetListViewAdapter(getContext(), petsArray);
        petListview.setAdapter(adapter);

        petListview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // When a pet is clicked, navigate to the modify pet fragment
                petToModify = adapter.getItem(position);
                navController.navigate(R.id.action_chooseModifyPet_to_modifyPet);
            }
        });


        return root;
    }

    // Accessor for the pet to modify
    public static Pet getPetToModify() {
        return petToModify;
    }
}