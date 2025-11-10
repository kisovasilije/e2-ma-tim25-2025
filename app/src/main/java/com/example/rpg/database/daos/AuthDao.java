package com.example.rpg.database.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthDao {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public Task<AuthResult> create(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
