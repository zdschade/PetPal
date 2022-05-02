package com.knoxolotl.petpal.ui.dashboard.pages.managehousehold;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentAddMemberBinding;
import com.knoxolotl.petpal.databinding.FragmentManageHouseholdPageBinding;


public class ManageHouseholdPage extends Fragment {
    // Nav fragment for choosing which household operation to perform

    private FragmentManageHouseholdPageBinding binding;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentManageHouseholdPageBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        // All da buttons
        Button navAddMemberButton = binding.navAddMemberButton;
        Button navRemoveMemberButton = binding.navRemoveMemberButton;
        Button navChangeNameButton = binding.navChangeNameButton;

        // Navigate to add member page
        navAddMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_manageHouseholdPage_to_addMember);
            }
        });

        // Navigate to remove member page
        navRemoveMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_manageHouseholdPage_to_removeMember);
            }
        });

        // Navigate to change name page
        navChangeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_manageHouseholdPage_to_changeHouseholdName);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}