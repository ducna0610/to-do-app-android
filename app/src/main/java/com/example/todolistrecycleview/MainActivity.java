package com.example.todolistrecycleview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todolistrecycleview.adapter.TaskAdapter;
import com.example.todolistrecycleview.dao.TaskDAO;
import com.example.todolistrecycleview.model.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText edtId, edtTitle, edtContent, edtDate, edtType;
    Button btnAdd;
    RecyclerView rcvTask;
    TaskDAO dao;
    ArrayList<Task> list;
    TaskAdapter adapter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGUI();
        dao = new TaskDAO(this);
        list = dao.get();
        adapter = new TaskAdapter(MainActivity.this, list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvTask.setLayoutManager(linearLayoutManager);

        rcvTask.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
                String date = edtDate.getText().toString().trim();
                String type = edtType.getText().toString().trim();

                if(title.isEmpty() || content.isEmpty() || date.isEmpty() || type.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please input data", Toast.LENGTH_LONG).show();
                    if (title.isEmpty()) {
                        edtTitle.setError("Please enter title");
                    }
                    if (content.isEmpty()) {
                        edtContent.setError("Please enter content");
                    }
                    if (date.isEmpty()) {
                        edtDate.setError("Please enter date");
                    }
                    if (type.isEmpty()) {
                        edtType.setError("Please enter type");
                    }
                } else {
                    Task task = new Task(1, title, content, date, type, 0);
                    long check = dao.add(task);
                    if (check < 0) {
                        Toast.makeText(MainActivity.this, "ERROR: Not insert data", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Insert successfully", Toast.LENGTH_LONG).show();
                    }
                    list = dao.get();
                    adapter = new TaskAdapter(MainActivity.this, list);
                    rcvTask.setLayoutManager(linearLayoutManager);
                    rcvTask.setAdapter(adapter);
                    reset();
                }
            }
        });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog  StartTime = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        edtDate.setText(DateFormat.getDateInstance().format(newDate.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                StartTime.show();
            }
        });

        edtType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] arrType = {"Hard", "Medium", "Easy"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please choose level");
//                builder.setIcon();
                builder.setItems(arrType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtType.setText(arrType[i]);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void initGUI() {
        edtId = findViewById(R.id.edtId);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        edtDate = findViewById(R.id.edtDate);
        edtType = findViewById(R.id.edtType);
        btnAdd = findViewById(R.id.btnAdd);
        rcvTask = findViewById(R.id.rcvTask);
    }

    private void reset() {
        edtTitle.setText("");
        edtContent.setText("");
        edtDate.setText("");
        edtType.setText("");
        edtId.setText("");
    }
}