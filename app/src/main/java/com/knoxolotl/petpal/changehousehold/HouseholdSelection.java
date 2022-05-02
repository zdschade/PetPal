package com.knoxolotl.petpal.changehousehold;

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
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentHouseholdSelectionBinding;

public class HouseholdSelection extends Fragment {
    // Fragment for selecting a household

    private FragmentHouseholdSelectionBinding binding;
    private View root;
    private HouseholdListAdapter adapter;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHouseholdSelectionBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        View navView = requireActivity().findViewById(R.id.nav_view);

        if (navView != null) {
            navView.setVisibility(View.GONE);
        }

        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        // List of households the user is a member of
        ListView listView = binding.listOfHouseholds;
        Log.d("HouseholdSelection", MainActivity.getUsersHouseholds().toString());
        adapter = new HouseholdListAdapter(getContext(), MainActivity.getUsersHouseholds());
        listView.setAdapter(adapter);

        // On click, set that household as the current household
        listView.setOnItemClickListener((parent, view, position, id) -> {
            sp.edit().putString("household", adapter.getItem(position).getReference().getId().toString()).apply();  // log in SharedPreferences
            Log.d("HouseholdSelection", "household/" + sp.getString("household", ""));
            MainActivity.requestPetsAndHousehold(db.document("households/" + sp.getString("household", null)));  // Update pets and household
            navController.navigate(R.id.action_householdSelection_to_navigation_home);  // Navigate to home
        });

        // Button to navigate to household creation fragment
        FloatingActionButton fab = binding.createJoinHouseholdFab;
        fab.setOnClickListener(view -> navController.navigate(R.id.action_householdSelection_to_createOrJoinHousehold));

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //root.findViewById(R.id.nav_view).setVisibility(View.GONE);
    }
}