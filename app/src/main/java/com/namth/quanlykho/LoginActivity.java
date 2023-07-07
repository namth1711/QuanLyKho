package com.namth.quanlykho;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.namth.quanlykho.model.User;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText edUser, edPass;
    Button btnLoginUser, btnLoginEmail;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("User");
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUser = findViewById(R.id.edUser);
        edPass = findViewById(R.id.edPass);
        btnLoginEmail = findViewById(R.id.btnLoginEmail);
        btnLoginUser = findViewById(R.id.btnLoginUser);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnForgot = findViewById(R.id.btnlaylaimatkhau);
        getUser();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    listenForUserChanges(user.getUid());
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = edUser.getText().toString().trim();
                String password = edPass.getText().toString().trim();
                if (TextUtils.isEmpty(Username)) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập username", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
                } else {
                    signInUser(Username, password);
                }
            }
        });

        btnLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= edUser.getText().toString().trim();
                String password = edPass.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập email ", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
                } else {
                    signInWithEmail(email, password);
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogViewRegister = LayoutInflater.from(LoginActivity.this).inflate(R.layout.item_dangky, null);

                AlertDialog.Builder builderRegister = new AlertDialog.Builder(LoginActivity.this);
                builderRegister.setView(dialogViewRegister);

                builderRegister.setTitle("Đăng ký");
                builderRegister.setPositiveButton("Đăng ký", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edEmail = dialogViewRegister.findViewById(R.id.edEmailRegister);
                        EditText edPassword = dialogViewRegister.findViewById(R.id.edPassRegister);
                        EditText edRetypePassword = dialogViewRegister.findViewById(R.id.edRePassRegister);

                        String email = edEmail.getText().toString();
                        String password = edPassword.getText().toString();
                        String retypePassword = edRetypePassword.getText().toString();

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(LoginActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(password)) {
                            Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(retypePassword)) {
                            Toast.makeText(LoginActivity.this, "Vui lòng nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
                        } else if (!password.equals(retypePassword)) {
                            Toast.makeText(LoginActivity.this, "Mật khẩu nhập lại không đúng", Toast.LENGTH_SHORT).show();
                        } else {
                            registerWithEmail(email, password);
                        }
                    }
                });
                builderRegister.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builderRegister.create();
                dialog.show();
            }
        });
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.item_quen_mat_khau, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(dialogView);

                builder.setTitle("Quên Mật Khẩu");
                builder.setPositiveButton("Gửi Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edEmail = dialogView.findViewById(R.id.edEmailForgot);
                        String email = edEmail.getText().toString();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(LoginActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                        } else {
                            sendPasswordResetEmail(email);
                        }
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }


    public void getUser(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    String username = x.child("username").getValue(String.class);
                    String password = x.child("password").getValue(String.class);
                    String full_name = x.child("full_name").getValue(String.class);
                    String email = x.child("email").getValue(String.class);
                    String lever = x.child("lever").getValue(String.class);
                    String matv = x.child("matv").getValue(String.class);

                    listUser.add(new User(matv,username,password,full_name,email,lever));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Lỗi tài khoản", error.toException());
            }
        });
    }
    private void signInUser(String username, String password) {
        int check=0;
        for (User x : listUser) {
            if (username.equals(x.getUsername()) && password.equals(x.getPassword())) {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                i.putExtra("nguoidung", (Parcelable) x);
                startActivity(i);
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            }
            check=check+1;
        }
        if(check==listUser.size()) {
            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
        }
    }
    private void signInWithEmail(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            DatabaseReference userRef = ref.child(userId);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User currentUser = snapshot.getValue(User.class);
                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                    i.putExtra("nguoidung", (Parcelable) currentUser);
                                    startActivity(i);
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void registerWithEmail(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = "TK00"+listUser.size();
                            String username = email.split("@")[0];
                            String fullName = "";
                            String lever = "thukho";
                            User newUser = new User(userId, username, password, fullName, email, lever);
                            saveUserToDatabase(newUser);
                            sendVerificationEmail(user);
                            Toast.makeText(LoginActivity.this, "Đăng ký thành công. Vui lòng xác thực email để đăng nhập.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveUserToDatabase(User user) {
        DatabaseReference userRef = ref.child(user.getMatv());
        userRef.setValue(user);
    }
    private void sendVerificationEmail(FirebaseUser user) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://quanlykho.page.link/verify-email")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.namth.quanlykho", true, "19")
                .build();

        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email verification sent.");
                        }
                    }
                });
    }
    private void sendPasswordResetEmail(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Mật khẩu đã được gửi đến địa chỉ email của bạn", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Gửi email thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void listenForUserChanges(String userId) {
        DatabaseReference userRef = ref.child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi đồng bộ tài khoản thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}