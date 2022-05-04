package com.example.backpackapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.backpackapp.MainActivity;
import com.example.backpackapp.R;
import com.example.backpackapp.adapters.AdapterBook_F;
import com.example.backpackapp.adapters.SubjectArrayAdapter;
import com.example.backpackapp.datasql.DataHandler;
import com.example.backpackapp.enteties.Book;
import com.example.backpackapp.enteties.Subject;
import com.example.backpackapp.interfaces.OnDownloadOnly;
import com.example.backpackapp.serverutils.JsonConv;
import com.example.backpackapp.serverutils.ServerReq;
import com.example.backpackapp.serverutils.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private List<Book> bookList;
    private List<Subject> subjectList;
    private RecyclerView recyclerView;
    private AppCompatButton btnSearch;
    private Spinner spinnerSub,spinnerClass;
    private SearchView bookSearch;
    private SubjectArrayAdapter subjectArrayAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private AdapterBook_F adapterBookF;

    private Subject currSub;
    private String currClass;

    private DataHandler dataHandler;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookSearch = view.findViewById(R.id.bookSearch);
        spinnerSub = view.findViewById(R.id.spinnerSub);
        spinnerClass = view.findViewById(R.id.spinnerClass);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnSearch = view.findViewById(R.id.btnSearch);

        dataHandler = new DataHandler(getContext());

        String [] classes = {"Не выбрано","1", "2", "3", "4", "5", "6"};

        arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,classes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(arrayAdapter);

        subjectList = new ArrayList<>();
        bookList = new ArrayList<>();

        subjectArrayAdapter = new SubjectArrayAdapter(getActivity().getBaseContext(),subjectList);
        spinnerSub.setAdapter(subjectArrayAdapter);
        getAllSubjects();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterBookF = new AdapterBook_F();
        recyclerView.setAdapter(adapterBookF);
        getAllBooks();

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currClass = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerSub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              currSub = (Subject) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filteringBooks();
            }
        });

        adapterBookF.setOnDownloadOnly(new OnDownloadOnly() {
            @Override
            public void onDownloadClick(int position) {
                String filename = bookList.get(position).getFilename();
                /*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());*/
                ServerReq.downloadRequest("download/",
                        new File(getActivity().getExternalFilesDir(getActivity().DOWNLOAD_SERVICE),filename),
                        getActivity(),
                        filename
                );
                dataHandler.addBook(bookList.get(position));
            }
        });

    }

    private void filteringBooks() {
        List<Book> books = new ArrayList<>();
         books.addAll(bookList);

        if (!currSub.getFull_name().equals("Не выбрано")) {
            books = filterBooksBySubject(currSub.getFull_name(), books);
            //Toast.makeText(getActivity(), "by sub:"+currSub.getFull_name(), Toast.LENGTH_SHORT).show();
        }

        if (!currClass.equals("Не выбрано")) {
            books = filterBooksByClass(currClass, books);
            //Toast.makeText(getActivity(), "by class:"+currClass, Toast.LENGTH_SHORT).show();
        }

        if (!bookSearch.getQuery().toString().equals("")){
            books =  filterBooksByQuery(bookSearch.getQuery(),books);
            //Toast.makeText(getActivity(), "by query:"+bookSearch.getQuery(), Toast.LENGTH_SHORT).show();
        }

        if (bookSearch.getQuery().toString().equals("")&&currClass.equals("Не выбрано")&&currSub.getFull_name().equals("Не выбрано")){
            getAllBooks();
        }

        //Toast.makeText(getActivity(), "size:"+books.size(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(), "size:"+bookList.size(), Toast.LENGTH_SHORT).show();
    }

    private List<Book> filterBooksByQuery(CharSequence query, List<Book> books) {
        List<Book> bookL = new ArrayList<>(books);
        bookL.removeIf(book -> !book.getName().toLowerCase().contains(query.toString().toLowerCase()));
        adapterBookF.filteredList(bookL);
        return bookL;
    }

    private List<Book> filterBooksBySubject(String full_name, List<Book> books) {
        List<Book> bookL = new ArrayList<>(books);
        bookL.removeIf(book -> !book.getSubName().equals(full_name));
        adapterBookF.filteredList(bookL);
        return bookL;
    }

    private List<Book> filterBooksByClass(String currClass, List<Book> books) {
        List<Book> bookL = new ArrayList<>(books);
        bookL.removeIf(book -> !String.valueOf(book.getClassNum()).equals(currClass));
        adapterBookF.filteredList(bookL);
        return bookL;
    }

    private void getAllBooks() {
        ServerReq.getRequest("books",getActivity(),textResponse -> {
            adapterBookF.setData(JsonConv.getBooks(textResponse));
            bookList.addAll(JsonConv.getBooks(textResponse));
        });
    }

    private void getAllSubjects() {
        ServerReq.getRequest("subs",getActivity(),textResponse -> {
            subjectList.add(new Subject(0,"Не выбрано"));
            subjectList.addAll(JsonConv.getSubjects(textResponse));
            subjectArrayAdapter.notifyDataSetChanged();
            //Toast.makeText(getActivity(), "size:"+subjectList.size(), Toast.LENGTH_SHORT).show();
        });
    }


}