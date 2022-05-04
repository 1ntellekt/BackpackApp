package com.example.backpackapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backpackapp.R;
import com.example.backpackapp.enteties.Book;
import com.example.backpackapp.interfaces.OnBackpack;

import java.util.ArrayList;
import java.util.List;

public class AdapterBook_S extends RecyclerView.Adapter<AdapterBook_S.BookHolder> {

    private List<Book> bookList = new ArrayList<>();

    private OnBackpack onBackpack;
    private String role;

    public AdapterBook_S(String role) {
        this.role = role;
    }

    public void setData(List<Book> bookList) {
        this.bookList.clear();
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }

    public void setOnBackpack(OnBackpack onBackpack) {
        this.onBackpack = onBackpack;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.backpack_item,parent,false),onBackpack,role);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        Book book = bookList.get(position);
        holder.subName.setText(book.getSubName());
        holder.tvName.setText(book.getName());
        holder.tvClass.setText("Класс:"+book.getClassNum());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookHolder extends RecyclerView.ViewHolder{
        private TextView tvName, tvClass, subName;
        private ImageButton btnEdit, btnDelete, btnDownload;
        public BookHolder(@NonNull View itemView, OnBackpack onBackpack, String role) {
            super(itemView);
            tvClass = itemView.findViewById(R.id.tvClass);
            tvName = itemView.findViewById(R.id.tvName);
            subName = itemView.findViewById(R.id.subName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            btnEdit = itemView.findViewById(R.id.btnEdit);

            if (role.equals("учитель")) btnEdit.setVisibility(View.VISIBLE);
            else btnEdit.setVisibility(View.GONE);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION&&onBackpack!=null){
                       onBackpack.onEditClick(pos);
                    }
                }
            });

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION&&onBackpack!=null){
                        onBackpack.onDownloadClick(pos);
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION&&onBackpack!=null){
                        onBackpack.onDeleteClick(pos);
                    }
                }
            });

        }
    }

}
