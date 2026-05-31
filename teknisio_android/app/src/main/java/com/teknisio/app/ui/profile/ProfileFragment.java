package com.teknisio.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.teknisio.app.R;
import com.teknisio.app.ui.auth.LoginActivity;
import com.teknisio.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        // Populate user data from session
        TextView tvProfileName = view.findViewById(R.id.tv_profile_name);
        TextView tvAccountName = view.findViewById(R.id.tv_account_name);
        TextView tvAccountEmail = view.findViewById(R.id.tv_account_email);
        TextView tvAccountPhone = view.findViewById(R.id.tv_account_phone);

        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userPhone = sessionManager.getUserPhone();

        if (userName != null && !userName.isEmpty()) {
            tvProfileName.setText(userName);
            tvAccountName.setText(userName);
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            tvAccountEmail.setText(userEmail);
        }

        if (userPhone != null && !userPhone.isEmpty()) {
            tvAccountPhone.setText(userPhone);
        }

        // Logout button
        Button btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log Out", (dialog, which) -> {
                        sessionManager.clearSession();
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}
