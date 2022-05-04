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
import com.example.backpackapp.interfaces.OnSelectedOnly;

import java.util.ArrayList;
import java.util.List;


public class AdapterBook_T extends RecyclerView.Adapter<AdapterBook_T.BookHolder> {

    private List<Book> bookList = new ArrayList<>();
    private OnSelectedOnly onSelectedOnly;

    public void setOnSelectedOnly(OnSelectedOnly onSelectedOnly) {
        this.onSelectedOnly = onSelectedOnly;
    }

    public void setData(List<Book> bookList) {
        this.bookList.clear();
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterBook_T.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_two,parent,false),onSelectedOnly);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBook_T.BookHolder holder, int position) {
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
        public BookHolder(@NonNull View itemView, OnSelectedOnly onSelectedOnly) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            subName = itemView.findViewById(R.id.subName);
            tvClass = itemView.findViewById(R.id.tvClass);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION && onSelectedOnly!=null){
                        onSelectedOnly.onSelectedClick(pos);
                    }
                }
            });

        }
    }

}
