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
import com.example.backpackapp.interfaces.OnDownloadOnly;

import java.util.ArrayList;
import java.util.List;

public class AdapterBook_F extends RecyclerView.Adapter<AdapterBook_F.BookHolder> {

    private List<Book> bookList = new ArrayList<>();
    private OnDownloadOnly onDownloadOnly;

    public void setOnDownloadOnly(OnDownloadOnly onDownloadOnly) {
        this.onDownloadOnly = onDownloadOnly;
    }

    public void setData(List<Book> bookList) {
        this.bookList.clear();
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }

    public void filteredList(List<Book> filteredBookList){
        bookList = filteredBookList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book,parent,false),onDownloadOnly);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
         Book book = bookList.get(position);
         holder.tvClass.setText("Класс:"+book.getClassNum());
         holder.subName.setText(book.getSubName());
         holder.tvName.setText(book.getName());
         holder.tvAuthor.setText(book.getAuthorName());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }


    public static class BookHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvClass, tvAuthor, subName;
        private ImageButton btnDownload;
        public BookHolder(@NonNull View itemView, OnDownloadOnly onDownloadOnly) {
            super(itemView);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            subName = itemView.findViewById(R.id.subName);
            tvClass = itemView.findViewById(R.id.tvClass);

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION && onDownloadOnly!=null){
                        onDownloadOnly.onDownloadClick(pos);
                    }
                }
            });
        }
    }

}
