package com.namth.quanlykho.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import interface_packe.OnDeleteUserListener;
import interface_packe.OnUpdateUserListener;
import com.namth.quanlykho.R;
import com.namth.quanlykho.dao.UserDAO;
import com.namth.quanlykho.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private UserDAO dao;
    private ArrayList<User> list = new ArrayList<>();
    String vitri;
    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanh_vien, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        String statusColor = "";
         vitri = String.valueOf(position);

        holder.tvName.setText(user.getFull_name());
        holder.tvEmail.setText(user.getUsername());

        if (position %2 == 0)
            statusColor = "#F44336";
        else
            statusColor = "#4CAF50";

        holder.tvName.setTextColor(Color.parseColor(statusColor));
        holder.tvEmail.setTextColor(Color.parseColor(statusColor));

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(user);
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(user);
            }
        });
    }

    private void showDeleteDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn có chắc muốn xóa mục này không?");
        builder.setNegativeButton("không", null);
        builder.setPositiveButton("có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dao.deleteUser(user.getMatv(), new OnDeleteUserListener() {
                    @Override
                    public void onDeleteSuccess() {
                        int index = list.indexOf(user);
                        list.remove(index);
                        notifyItemRemoved(index);
                        Toast.makeText(context, "Xóa thành công.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDeleteFailed() {
                        Toast.makeText(context, "Không thể xóa thành viên này!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.show();
    }

    private void showEditDialog(User user) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_member);
        EditText edName = dialog.findViewById(R.id.edFullName);
        EditText edUsername = dialog.findViewById(R.id.edUsername);
        EditText edPassword = dialog.findViewById(R.id.edPassword);
        EditText edEmail = dialog.findViewById(R.id.edEmail);
        Spinner edLever = dialog.findViewById(R.id.spinnerLever);
        edName.setText(user.getFull_name());
        edUsername.setText(user.getUsername());
        edPassword.setText(user.getPassword());
        edEmail.setText(user.getEmail());
        if (user.getLever().equals("admin")){
            edLever.setSelection(0);
        }else {
            edLever.setSelection(1);
        }
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
                if (hoten.equals("")) {
                    Toast.makeText(context, "Vui lòng không bỏ trống Họ Tên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.equals("")) {
                    Toast.makeText(context, "Vui lòng không bỏ trống Tên đăng nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals("")) {
                    Toast.makeText(context, "Vui lòng không bỏ trống Mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.equals("")) {
                    Toast.makeText(context, "Vui lòng không bỏ trống Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lever.equals("")) {
                    Toast.makeText(context, "Vui lòng không bỏ trống Quyền!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hoten.length() < 5) {
                    Toast.makeText(context, "Họ tên phải có ít nhất 5 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hoten.length() > 15) {
                    Toast.makeText(context, "Họ tên không được phép dài quá 15 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lever.equals("anmin")){
                    user.setMatv("AD00"+vitri);
                }else {
                    user.setMatv("TK00"+vitri);
                }

                user.setFull_name(hoten);
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setLever(lever);

                dao.updateUser(user, new OnUpdateUserListener() {
                    @Override
                    public void onUpdateSuccess() {
                        int index = list.indexOf(user);
                        notifyItemChanged(index);
                        Toast.makeText(context, "Update thành công.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUpdateFailed() {
                        Toast.makeText(context, "Update thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(User user) {
        list.add(user);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvEmail;
        private ImageView btnRemove, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}