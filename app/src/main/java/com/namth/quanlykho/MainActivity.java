package com.namth.quanlykho;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.namth.quanlykho.fragment.UserFragment;
import com.namth.quanlykho.model.User;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    SharedPreferences sharedPreferences;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            user = intent.getParcelableExtra("nguoidung");
        }
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        Toolbar toolbar = findViewById(R.id.toolbar);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerlayout);

        View header = navigationView.getHeaderView(0);
        TextView userFullname = header.findViewById(R.id.full_name);
        TextView userlever = header.findViewById(R.id.lever);
        TextView useremail = header.findViewById(R.id.email);
        TextView userLogin = header.findViewById(R.id.username);
        ImageView imgUser = header.findViewById(R.id.avata);
        if (user.getLever().equals("admin")) {
            imgUser.setImageResource(R.drawable.user);
        }else {
            imgUser.setImageResource(R.drawable.warehouse);
        }
        userFullname.setText(user.getFull_name());
        userlever.setText("Lever: "+user.getLever());
        useremail.setText("Email: "+user.getEmail());
        userLogin.setText("Tên đăng nhập: "+user.getUsername());

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        FragmentManager fragmentManager = getSupportFragmentManager();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if (item.getItemId() == R.id.qlPhieuXuatKho) {
                    // Xử lý khi người dùng chọn "Quản lý phiếu xuất kho"
                } else if (item.getItemId() == R.id.qlLoaiSanPham) {
                    // Xử lý khi người dùng chọn "Quản lý loại sản phẩm"
                } else if (item.getItemId() == R.id.tkXuatKho) {
                    // Xử lý khi người dùng chọn "Thống kê xuất kho"
                } else if (item.getItemId() == R.id.tkTonKho) {
                    // Xử lý khi người dùng chọn "Thống kê tồn kho"
                } else if (item.getItemId() == R.id.qlthanhvien) {
                    if (user.getLever().equals("admin")){
                        fragment= new UserFragment();
                    }else {
                        Toast.makeText(MainActivity.this, "Không đủ quyền hạn", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.qltaikhoan) {
                    // Xử lý khi người dùng chọn "Quản lý tài khoản"
                } else if (item.getItemId() == R.id.vehome) {
                    toolbar.setTitle("Quản Lý Kho");
                }else if (item.getItemId() == R.id.thoat) {
                    // Xử lý khi người dùng chọn "Thoát"
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    toolbar.setTitle(item.getTitle());
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home);
        drawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }
}