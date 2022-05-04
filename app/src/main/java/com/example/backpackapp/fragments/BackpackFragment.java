package com.example.backpackapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backpackapp.R;
import com.example.backpackapp.adapters.AdapterBook_S;
import com.example.backpackapp.adapters.AdapterBook_T;
import com.example.backpackapp.adapters.AuthorsArrayAdapter;
import com.example.backpackapp.adapters.SubjectArrayAdapter;
import com.example.backpackapp.datasql.DataHandler;
import com.example.backpackapp.enteties.Author;
import com.example.backpackapp.enteties.Backpack;
import com.example.backpackapp.enteties.Book;
import com.example.backpackapp.enteties.Status;
import com.example.backpackapp.enteties.Subject;
import com.example.backpackapp.interfaces.OnBackpack;
import com.example.backpackapp.interfaces.OnResponse;
import com.example.backpackapp.interfaces.OnSelectedOnly;
import com.example.backpackapp.serverutils.JsonConv;
import com.example.backpackapp.serverutils.ServerReq;
import com.example.backpackapp.serverutils.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BackpackFragment extends Fragment {

    private List<Backpack> backpackList;
    private List<Book> bookList;
    private List<Book> myBooks;
    private List<Book> myNotBooks;
    private String [] classes;

    private List<Subject> subjectList;
    private List<Author> authorList;

    private DataHandler dataHandler;

    private SubjectArrayAdapter subjectArrayAdapter;
    private AuthorsArrayAdapter authorsArrayAdapter;

    private RecyclerView recyclerView;
    private FloatingActionButton btnAddBackpack;
    private TextView head_txt;

    private AdapterBook_S adapterBookS;
    private ArrayAdapter arrayAdapter;

    private File selectedFile;

    public static BackpackFragment newInstance() {
        BackpackFragment fragment = new BackpackFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backpack, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnAddBackpack = view.findViewById(R.id.btnAddBackpack);
        head_txt = view.findViewById(R.id.head_txt);

        bookList = new ArrayList<>();
        myBooks = new ArrayList<>();
        myNotBooks = new ArrayList<>();
        backpackList = new ArrayList<>();
        subjectList = new ArrayList<>();
        authorList = new ArrayList<>();

        dataHandler = new DataHandler(getContext());

        classes = new String[]{"1", "2", "3", "4", "5", "6"};

        adapterBookS = new AdapterBook_S(Util.getCurrentUser().getRole());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterBookS);

        getAllBooks_Backpacks();

        adapterBookS.setOnBackpack(new OnBackpack() {
            @Override
            public void onDeleteClick(int position) {
                Book book = myBooks.get(position);
                ServerReq.deleteRequest("backbooks/"+Util.getCurrentUser().getId()+"/"+book.getId(), getActivity(),textResponse -> {
                    Status status = JsonConv.getStatus(textResponse);
                    Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onEditClick(int position) {
                Book book = myBooks.get(position);
                showDialog(book);
            }
            @Override
            public void onDownloadClick(int position) {
                Book book = myBooks.get(position);
                String filename = book.getFilename();
                ServerReq.downloadRequest("download/"+filename,new File(getActivity().getExternalFilesDir(getActivity().DOWNLOAD_SERVICE),filename),
                        getActivity(), filename);
                dataHandler.addBook(book);
            }
        });
        btnAddBackpack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShowDialog();
            }
        });
    }

    private TextView tvStatus;

    private void showDialog(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_book,null);
        builder.setView(view);

        Spinner spinnerClass = view.findViewById(R.id.spinnerClass),
                spinnerSub = view.findViewById(R.id.spinnerSub),
                authorSpinner = view.findViewById(R.id.authorSpinner);
        EditText edName = view.findViewById(R.id.edName);
        ImageButton btnAddFile = view.findViewById(R.id.btnAddFile);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvStatus.setText("Статус: файл не выбран");

        tvTitle.setText("Редактирование учебника");

        arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,classes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(arrayAdapter);

        authorsArrayAdapter = new AuthorsArrayAdapter(getActivity().getBaseContext(),authorList);
        authorSpinner.setAdapter(authorsArrayAdapter);
        getAllAuthors();


        subjectArrayAdapter = new SubjectArrayAdapter(getActivity().getBaseContext(),subjectList);
        spinnerSub.setAdapter(subjectArrayAdapter);
        getAllSubjects();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spinnerSub.setSelection(getPosAtSubList(book));
                authorSpinner.setSelection(getPosAtAuthorList(book));
                spinnerClass.setSelection(book.getClassNum()-1);
                edName.setText(book.getName());
            }
        },400);

        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edName.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Name book is null!", Toast.LENGTH_SHORT).show();
                } else {
                    Author author = (Author) authorSpinner.getSelectedItem();
                    Subject subject = (Subject) spinnerSub.getSelectedItem();
                    String classS  = (String) spinnerClass.getSelectedItem();

                    Map<String,Object> params = new HashMap<>();
                    params.put("name",edName.getText().toString());
                    params.put("author_id", author.getId());
                    params.put("sub_id", subject.getId());
                    if (selectedFile!=null) params.put("file", selectedFile.getName());
                  else params.put("file", book.getFilename());
                    params.put("class", classS);
                    ServerReq.patchRequest("editbook/"+book.getId(), params, getActivity(),textResponse -> {
                        Status status = JsonConv.getStatus(textResponse);
                        Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                    if (selectedFile!=null){
                        ServerReq.uploadRequest("upload", selectedFile,getActivity(),textResponse -> {
                            Status status = JsonConv.getStatus(textResponse);
                            Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                            selectedFile.delete();
                            selectedFile = null;
                        });
                    }
                    myBooks.clear();
                    myNotBooks.clear();
                    bookList.clear();
                    backpackList.clear();
                    getAllBooks_Backpacks();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private Book selectedBook;

    private void addShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_book_backpack,null);
        builder.setView(view);

        TextView tvName = view.findViewById(R.id.tvName);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterBook_T adapterBook_t = new AdapterBook_T();
        recyclerView.setAdapter(adapterBook_t);
        tvName.setText("None");
        adapterBook_t.setData(myNotBooks);



        adapterBook_t.setOnSelectedOnly(new OnSelectedOnly() {
            @Override
            public void onSelectedClick(int position) {
                selectedBook = myNotBooks.get(position);
                tvName.setText(selectedBook.getName());
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedBook!=null){
                    Map<String,Object> params = new HashMap<>();
                    params.put("user_id", Util.getCurrentUser().getId());
                    params.put("book_id",selectedBook.getId());
                    ServerReq.postRequest("addback",params,getActivity(), textResponse -> {
                        Status status = JsonConv.getStatus(textResponse);
                        Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    myBooks.clear();
                    myNotBooks.clear();
                    bookList.clear();
                    backpackList.clear();
                    getAllBooks_Backpacks();
                } else {
                    Toast.makeText(getActivity(), "None selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private final int CHOOSE_FILE = 111;

    private void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent,"Select some file"),CHOOSE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CHOOSE_FILE&&resultCode==getActivity().RESULT_OK&&data!=null){
            Uri uri = data.getData();
            File file = new File(uri.getPath());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String now = sdf.format(new Date());
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                selectedFile = Util.getFile(inputStream,getActivity().getFilesDir()+"/"+now+file.getName());
                if (selectedFile!=null)tvStatus.setText("Статус: файл выбран!");
                else tvStatus.setText("Статус: файл не выбран");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private int getPosAtSubList(Book book) {
        int pos=0;
        for (Subject subject:subjectList)
        {
            if (subject.getFull_name().equals(book.getSubName())) break;
            else pos++;
        }
        return pos;
    }

    private int getPosAtAuthorList(Book book) {
        int pos=0;
        for (Author author:authorList)
        {
            if (author.getFull_name().equals(book.getAuthorName())) break;
            else pos++;
        }
        return pos;
    }

    private void splitBooks() {
        if (bookList.size()>0){
            for (Backpack backpack : backpackList){
                for (Book book : bookList) {
                    if (book.getId() == backpack.getBook_id()){
                        myBooks.add(book);
                    }
                }
            }

            for (Book book:bookList){
                if (!myBooks.contains(book)) myNotBooks.add(book);
            }

            //if (myBooks.size()==0) myNotBooks.addAll(bookList);

            /*Toast.makeText(getActivity(), "sizeMy:"+myBooks.size(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "sizeNotMy:"+myNotBooks.size(), Toast.LENGTH_SHORT).show();*/
            adapterBookS.setData(myBooks);
        }
            if (backpackList.size()>0)
            head_txt.setText("Мой портфель");
            else head_txt.setText("Мой портфель: пусто");

    }

    private void getAllBooks_Backpacks() {

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Backpack is loading....");
        progressDialog.show();

        ServerReq.getRequest("books", getActivity(),textResponse -> {
            bookList.addAll(JsonConv.getBooks(textResponse));
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ServerReq.getRequest("backbooks/" + Util.getCurrentUser().getId(), getActivity(), new OnResponse() {
                    @Override
                    public void onGetResponse(String textResponse) {
                        backpackList.addAll(JsonConv.getMyBackpack(textResponse));
                        splitBooks();
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                });
            }
        },2000);
    }

    private void getAllSubjects() {
        ServerReq.getRequest("subs",getActivity(),textResponse -> {
            subjectList.addAll(JsonConv.getSubjects(textResponse));
            subjectArrayAdapter.notifyDataSetChanged();
            //Toast.makeText(getActivity(), "size:"+subjectList.size(), Toast.LENGTH_SHORT).show();
        });
    }

    private void getAllAuthors(){
        ServerReq.getRequest("auths", getActivity(),textResponse -> {
            authorList.addAll(JsonConv.getAuthors(textResponse));
            authorsArrayAdapter.notifyDataSetChanged();
        });
    }

}