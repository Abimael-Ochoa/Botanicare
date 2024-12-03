package com.example.projectintegration.Papelera;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminRole {
    private FirebaseAuth mAuth;

    public boolean isAdmin(FirebaseUser user) {
        mAuth = FirebaseAuth.getInstance();
        boolean isAdmin = false;
        if (user != null) {
            if ("admin@admin.com".equalsIgnoreCase(user.getEmail())) {
                isAdmin = true;
            }
        }
        return isAdmin;  // Devolver el valor calculado, no llamar al método recursivamente
    }
}
