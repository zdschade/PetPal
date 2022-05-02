package com.knoxolotl.petpal.ui.dashboard.pages.managepets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentDashboardBinding;
import com.knoxolotl.petpal.databinding.FragmentManagePetsPageBinding;


public class ManagePetsPage extends Fragment {
    // Fragment to navigate to the desired pet management page

    FragmentManagePetsPageBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentManagePetsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        // Add pet
        Button addPetButton = binding.addPetButton;
        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_managePetsPage_to_addPet);
            }
        });

        // Remove pet
        Button removePetButton = binding.removePetButton;
        removePetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_managePetsPage_to_removePet);
            }
        });

        // Modify pet
        Button modifyPetButton = binding.modifyPetButton;
        modifyPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_managePetsPage_to_chooseModifyPet);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}