package com.example.backpackapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.backpackapp.adapters.AdapterBook_F;
import com.example.backpackapp.adapters.AuthorsArrayAdapter;
import com.example.backpackapp.adapters.SubjectArrayAdapter;
import com.example.backpackapp.datasql.DataHandler;
import com.example.backpackapp.enteties.Author;
import com.example.backpackapp.enteties.Book;
import com.example.backpackapp.enteties.Status;
import com.example.backpackapp.enteties.Subject;
import com.example.backpackapp.enteties.User;
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

public class PersonFragment extends Fragment {

    private TextView tvName, tvSurname, tvNumber, tvRole;
    private ImageButton btnEditProfile;
    private RecyclerView recyclerView;
    private DataHandler dataHandler;
    private AdapterBook_F adapterBookF;
    private List<Book> bookList;

    private String [] classes;

    private List<Subject> subjectList;
    private List<Author> authorList;

    private SubjectArrayAdapter subjectArrayAdapter;
    private AuthorsArrayAdapter authorsArrayAdapter;
    private ArrayAdapter arrayAdapter;

    private FloatingActionButton btnAddBook;

    private File selectedFile;
    private TextView head_txt;


    public static PersonFragment newInstance() {
        PersonFragment fragment = new PersonFragment();
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
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tvName);
        tvSurname = view.findViewById(R.id.tvSurname);
        tvNumber = view.findViewById(R.id.tvNumber);
        tvRole = view.findViewById(R.id.tvRole);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnAddBook = view.findViewById(R.id.btnAddBook);
        recyclerView = view.findViewById(R.id.recyclerView);
        head_txt = view.findViewById(R.id.head_txt);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        tvName.setText(Util.getCurrentUser().getName());
        tvSurname.setText(Util.getCurrentUser().getSurname());
        tvRole.setText(Util.getCurrentUser().getRole());
        tvNumber.setText(Util.getCurrentUser().getTelephone());

        subjectList = new ArrayList<>();
        authorList = new ArrayList<>();
        classes = new String[]{"1", "2", "3", "4", "5", "6"};

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileDialog();
            }
        });

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddBook();
            }
        });

        bookList = new ArrayList<>();
        adapterBookF = new AdapterBook_F();
        dataHandler = new DataHandler(getContext());
        bookList.addAll(dataHandler.getAllBooks());
        recyclerView.setAdapter(adapterBookF);

        if (bookList.size()>0){
            head_txt.setText("Недавно скачанные");
            adapterBookF.setData(bookList);
        } else {
            head_txt.setText("Недавно скачанные: здесь еще ничего нет");
        }

    }

    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_profile,null);
        builder.setView(view);

        EditText edTelephone = view.findViewById(R.id.edTelephone),
                edName = view.findViewById(R.id.edName),
                edSurname = view.findViewById(R.id.edSurname);

        edSurname.setText(Util.getCurrentUser().getSurname());
        edName.setText(Util.getCurrentUser().getName());
        edTelephone.setText(Util.getCurrentUser().getTelephone());

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edName.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Name is null!", Toast.LENGTH_SHORT).show();
                } else if (edSurname.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Surname is null!", Toast.LENGTH_SHORT).show();
                } else if (edTelephone.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Telephone is null!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String,Object> params = new HashMap<>();
                    params.put("telephone",edTelephone.getText().toString());
                    params.put("surname",edSurname.getText().toString());
                    params.put("name",edName.getText().toString());
                    params.put("password", Util.getCurrentUser().getPassword());
                    params.put("login", Util.getCurrentUser().getLogin());
                    params.put("role", Util.getCurrentUser().getRole());

                    Util.getCurrentUser().setName(edName.getText().toString());
                    Util.getCurrentUser().setSurname(edSurname.getText().toString());
                    Util.getCurrentUser().setTelephone(edTelephone.getText().toString());

                    saveToShared(Util.getCurrentUser());

                    ServerReq.patchRequest("edituser/"+Util.getCurrentUser().getId(), params,getActivity(),textResponse -> {
                        Status status = JsonConv.getStatus(textResponse);
                        Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                    });
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

    private TextView tvStatus;
    private void showDialogAddBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_book,null);
        builder.setView(view);

        Spinner spinnerClass = view.findViewById(R.id.spinnerClass),
                spinnerSub = view.findViewById(R.id.spinnerSub),
                authorSpinner = view.findViewById(R.id.authorSpinner);
        EditText edName = view.findViewById(R.id.edName);
        ImageButton btnAddFile = view.findViewById(R.id.btnAddFile);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvStatus.setText("Статус: файл не выбран");

        arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,classes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(arrayAdapter);

        subjectArrayAdapter = new SubjectArrayAdapter(getActivity().getBaseContext(),subjectList);
        spinnerSub.setAdapter(subjectArrayAdapter);
        getAllSubjects();

        authorsArrayAdapter = new AuthorsArrayAdapter(getActivity().getBaseContext(),authorList);
        authorSpinner.setAdapter(authorsArrayAdapter);
        getAllAuthors();

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
                    Toast.makeText(getActivity(), "Name is null!", Toast.LENGTH_SHORT).show();
                } else if (selectedFile==null){
                    Toast.makeText(getActivity(), "File is null!", Toast.LENGTH_SHORT).show();
                } else {
                    Author author = (Author) authorSpinner.getSelectedItem();
                    Subject subject = (Subject) spinnerSub.getSelectedItem();
                    String classS  = (String) spinnerClass.getSelectedItem();

                    Map<String,Object> params = new HashMap<>();
                    params.put("name",edName.getText().toString());
                    params.put("author_id", author.getId());
                    params.put("sub_id", subject.getId());
                    params.put("file", selectedFile.getName());
                    params.put("class", classS);
                    ServerReq.postRequest("addbook", params, getActivity(),textResponse -> {
                        Status status = JsonConv.getStatus(textResponse);
                        Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ServerReq.uploadRequest("upload", selectedFile,getActivity(),textResponse -> {
                                Status status = JsonConv.getStatus(textResponse);
                                Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
                                selectedFile.delete();
                                selectedFile = null;
                            });
                        }
                    },1000);
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

    private void getAllSubjects() {
        ServerReq.getRequest("subs",getActivity(), textResponse -> {
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

    private void saveToShared(User user) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("curr_user", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id",user.getId());
        editor.putString("login",user.getLogin());
        editor.putString("password",user.getPassword());
        editor.putString("name",user.getName());
        editor.putString("surname",user.getSurname());
        editor.putString("telephone",user.getTelephone());
        editor.putString("role",user.getRole());
        editor.apply();
    }

}