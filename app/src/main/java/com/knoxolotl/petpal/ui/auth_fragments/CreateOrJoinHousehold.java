package com.knoxolotl.petpal.ui.auth_fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.LoginActivity;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentCreateOrJoinHouseholdBinding;

public class CreateOrJoinHousehold extends Fragment {

    private FragmentCreateOrJoinHouseholdBinding binding;
    private View root;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreateOrJoinHouseholdBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("CreateOrJoinHousehold", "User not in household");

        Button requestJoinHousehold = binding.requestJoinHousehold;
        Button requestCreateHousehold = binding.requestCreateHousehold;
        Button logout = binding.logoutButton;

        requestJoinHousehold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_createOrJoinHousehold_to_joinHousehold);
            }
        });

        requestCreateHousehold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_createOrJoinHousehold_to_createHousehold);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                sp.edit().putString("household", null).apply();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        });

        return root;
    }
}