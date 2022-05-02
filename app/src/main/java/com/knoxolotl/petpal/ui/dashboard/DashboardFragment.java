package com.knoxolotl.petpal.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.knoxolotl.petpal.LoginActivity;
import com.knoxolotl.petpal.MainActivity;
import com.knoxolotl.petpal.R;
import com.knoxolotl.petpal.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    // Dashboard fragment. Has options for settings, pet management, household management, etc

    private FragmentDashboardBinding binding;
    SharedPreferences sp;
    GridView dashboardGrid;
    DashboardAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (isAdded()) {  // Show nav view if MainActivity is active
            requireActivity().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
        }

        dashboardGrid = root.findViewById(R.id.dashboard_gridview);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(root.getContext());

        adapter = new DashboardAdapter(root.getContext());
        dashboardGrid.setAdapter(adapter);

        // On dashboard item click, navigate to appropriate fragment
        dashboardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("On Item Listener", "Clicked");

                NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
                NavController navController = navHostFragment.getNavController();

                // Navigate to appropriate fragment
                switch (adapter.getItem(position)) {
                    case "Switch Household":
                        navController.navigate(R.id.action_navigation_dashboard_to_householdSelection);
                        break;
                    case "Manage Household":
                        navController.navigate(R.id.action_navigation_dashboard_to_manageHouseholdPage);
                        break;
                    case "Manage Pets":
                        navController.navigate(R.id.action_navigation_dashboard_to_managePetsPage);
                        break;
                    case "Manage Notifications":
                        navController.navigate(R.id.action_navigation_dashboard_to_manageNotificationsPage);
                        break;
                    case "Manage Profile":
                        navController.navigate(R.id.action_navigation_dashboard_to_manageProfilePage);
                        break;
                    case "Settings":
                        navController.navigate(R.id.action_navigation_dashboard_to_settingPage);
                        break;
                    case "Logout":  // Logout user
                        FirebaseAuth.getInstance().signOut();  // log out of firebase auth
                        sp.edit().putString("household", null).apply();  // clear current household
                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);  // login intent
                        startActivity(loginIntent);  // navigate to login activity
                        getActivity().finish(); // finish current activity
                        break;
                }

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