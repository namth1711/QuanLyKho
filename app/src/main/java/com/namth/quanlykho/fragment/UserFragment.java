package com.namth.quanlykho.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.Nullable;
import com.namth.quanlykho.R;
import com.namth.quanlykho.adapter.UserAdapter;
import com.namth.quanlykho.dao.UserDAO;
import com.namth.quanlykho.model.User;

import java.util.ArrayList;
import java.util.Locale;

public class UserFragment extends Fragment {
    private RecyclerView recycler;
    private Button btnAddMember;
    private ArrayList<User> list;
    private UserDAO dao;
    private UserAdapter addapter;
    private EditText edSearch;
    private Button btnSearch, btnHuy;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dao = new UserDAO(getContext());
        recycler = view.findViewById(R.id.recyclerMember);

        edSearch = view.findViewById(R.id.edSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnHuy = view.findViewById(R.id.btnCancel);

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMember();
                edSearch.setText("");
                Toast.makeText(getContext(), "Refresed", Toast.LENGTH_SHORT).show();
            }
        });
        loadMember();

        btnAddMember = view.findViewById(R.id.btnAddMember);
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddMember();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edSearch.getText().toString();
                ArrayList<User> search = new ArrayList<>();

                if (name.equals("")) {
                    Toast.makeText(getContext(), "Vui lòng nhập tên người dùng !", Toast.LENGTH_SHORT).show();
                    return;
                }

                int i = 0;

                for (User x: list) {
                    if (x.getFull_name().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))) {
                        search.add(x);
                        i = 1;
                    }
                }

                if (i == 0) {
                    Toast.makeText(getContext(), "Không có user nào trùng khớp !", Toast.LENGTH_SHORT).show();
                } else {
                    loadSearch(search);
                    Toast.makeText(getContext(), "Đã tìm thấy !", Toast.LENGTH_SHORT).show();
                }


            }
        });

        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (edSearch.getText().toString().equals(""))
                    loadMember();
                return false;
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    public void showDialogAddMember() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_member);
        EditText edName = dialog.findViewById(R.id.edFullName);
        EditText edUsername = dialog.findViewById(R.id.edUsername);
        EditText edPassword = dialog.findViewById(R.id.edPassword);
        EditText edEmail = dialog.findViewById(R.id.edEmail);
        Spinner edLever = dialog.findViewById(R.id.spinnerLever);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoten = edName.getText().toString().trim();
                String username = edUsername.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String lever = edLever.getSelectedItem().toString().trim();
                String matv = "";
                if (hoten.equals("")) {
                    Toast.makeText(getContext(), "Vui lòng không bỏ trống Họ Tên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.equals("")) {
                    Toast.makeText(getContext(), "Vui lòng không bỏ trống Tên đăng nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals("")) {
                    Toast.makeText(getContext(), "Vui lòng không bỏ trống Mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.equals("")) {
                    Toast.makeText(getContext(), "Vui lòng không bỏ trống Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lever.equals("")) {
                    Toast.makeText(getContext(), "Vui lòng không bỏ trống Quyền!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hoten.length() < 5) {
                    Toast.makeText(getContext(), "Họ tên phải có ít nhất 5 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hoten.length() > 15) {
                    Toast.makeText(getContext(), "Họ tên không được phép dài quá 15 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lever.equals("anmin")){
                    matv="AD00"+String.valueOf(dao.getList().size()+1);
                }else {
                    matv="TV00"+String.valueOf(dao.getList().size()+1);
                }
                Toast.makeText(getContext(), dao.addUser(matv,username,password,hoten,email,lever), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void loadSearch(ArrayList<User> lists) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        addapter = new UserAdapter(getContext(), lists);
        recycler.setAdapter(addapter);
    }
    public void loadMember() {
        list = dao.getList();
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        addapter = new UserAdapter(getContext(), list);
        recycler.setAdapter(addapter);
    }
}
