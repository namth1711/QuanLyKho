package com.namth.quanlykho.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import interface_packe.OnDeleteUserListener;
import interface_packe.OnUpdateUserListener;
import com.namth.quanlykho.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserDAO {
    DatabaseReference mDatabase;

    public UserDAO(Context c) {
        mDatabase = FirebaseDatabase.getInstance().getReference("User");
    }

    public ArrayList<User> getList() {
        final ArrayList<User> list = new ArrayList<>();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot x : snapshot.getChildren()) {
                    String username = x.child("username").getValue(String.class);
                    String password = x.child("password").getValue(String.class);
                    String full_name = x.child("full_name").getValue(String.class);
                    String email = x.child("email").getValue(String.class);
                    String lever = x.child("lever").getValue(String.class);
                    String matv = x.child("matv").getValue(String.class);

                    list.add(new User(matv,username,password,full_name,email,lever));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "loadPost:onCancelled", error.toException());
            }
        });

        return list;
    }

    public boolean isHaveData(String matv) {
        return false;
    }

    public boolean deleteUser(String key, OnDeleteUserListener onDeleteUserListener) {
        mDatabase.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "deleteUser: success");
                } else {
                    Log.d("TAG", "deleteUser: failed");
                }
            }
        });

        return true;
    }

    public String addUser(String matv, String username, String password, String full_name, String email, String lever) {
        User user = new User(matv, username, password, full_name, email, lever);

        mDatabase.child(matv).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "addUser: success");
                } else {
                    Log.d("TAG", "addUser: failed");
                }
            }
        });

        return "Thêm thành viên thành công.";
    }

    public void updateUser(User tv, OnUpdateUserListener listener) {
        mDatabase.child(tv.getMatv()).setValue(tv).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onUpdateSuccess();
                } else {
                    listener.onUpdateFailed();
                }
            }
        });
    }
}