package com.knoxolotl.petpal.ui.auth_fragments;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentDecisionBinding;
import com.knoxolotl.petpal.databinding.FragmentHouseholdSelectionBinding;


public class DecisionFragment extends Fragment {

    private FragmentDecisionBinding binding;
    View root;

    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDecisionBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();
        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (MainActivity.getUsersHouseholdsRefs() != null) {  // if the user is in a household
            if (sp.getString("household", null) == null) {  // if a household is not selected
                navController.navigate(R.id.action_decisionFragment_to_householdSelection);
            } else { // in a household and a household is selected
                Log.d("DecisionFragment", "in a household and a household is selected");
                MainActivity.requestPetsAndHousehold(db.document("households/" + sp.getString("household", null)));
                navController.navigate(R.id.action_decisionFragment_to_navigation_home);
            }

        } else {  // not in a household
            navController.navigate(R.id.action_decisionFragment_to_createOrJoinHousehold);
        }
        // Inflate the layout for this fragment
        return root;
    }
}