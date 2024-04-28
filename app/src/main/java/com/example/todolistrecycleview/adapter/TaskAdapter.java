package com.example.todolistrecycleview.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistrecycleview.MainActivity;
import com.example.todolistrecycleview.R;
import com.example.todolistrecycleview.dao.TaskDAO;
import com.example.todolistrecycleview.model.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolderInfo> {
    Context context;
    ArrayList<Task> list;
    TaskDAO taskDAO;

    public TaskAdapter(Context context, ArrayList<Task> list) {
        this.context = context;
        this.list = list;
        taskDAO = new TaskDAO(context);
    }

    @NonNull
    @Override
    public ViewHolderInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolderInfo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInfo holder, @SuppressLint("RecyclerView") int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvDate.setText(list.get(position).getDate());

        if(list.get(position).getStatus() == 1) {
            holder.chkTask.setChecked(true);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.chkTask.setChecked(false);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        holder.chkTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int id = list.get(holder.getAdapterPosition()).getId();
                boolean checkRS = taskDAO.updateType(id, holder.chkTask.isChecked());
                if (checkRS) {
                    Toast.makeText(context, "Update successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Update fail", Toast.LENGTH_LONG).show();
                }
                list.clear();
                list = taskDAO.get();
                notifyDataSetChanged();
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = list.get(holder.getAdapterPosition()).getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete information");
//                builder.setIcon(R.drawable)
                builder.setMessage("Are you sure");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
                        boolean check = taskDAO.remove(id);
                        if (check) {
                            Toast.makeText(context.getApplicationContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                            list.clear();
                            list = taskDAO.get();
                            notifyItemRemoved(holder.getAdapterPosition());
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Delete fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.imgUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View view1 = inflater.inflate(R.layout.custom_dialog_task, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view1);
                EditText edtId, edtTitle, edtContent, edtDate, edtType;
                edtId = view1.findViewById(R.id.edtId);
                edtTitle = view1.findViewById(R.id.edtTitle);
                edtContent = view1.findViewById(R.id.edtContent);
                edtDate = view1.findViewById(R.id.edtDate);
                edtType = view1.findViewById(R.id.edtType);

                edtId.setText(String.valueOf(list.get(position).getId()));
                edtTitle.setText(String.valueOf(list.get(position).getTitle()));
                edtContent.setText(String.valueOf(list.get(position).getContent()));
                edtDate.setText(String.valueOf(list.get(position).getDate()));
                edtDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar newCalendar = Calendar.getInstance();
                        DatePickerDialog StartTime = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                edtDate.setText(DateFormat.getDateInstance().format(newDate.getTime()));
                            }

                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                        StartTime.show();
                    }
                });
                edtType.setText(String.valueOf(list.get(position).getType()));
                edtType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] arrType = {"Hard", "Medium", "Easy"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

                builder.setTitle("Update information");
//                builder.setIcon()

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Task task = new Task();
                        task.setId(Integer.parseInt(edtId.getText().toString().trim()));
                        task.setTitle(edtTitle.getText().toString().trim());
                        task.setContent(edtContent.getText().toString().trim());
                        task.setDate(edtDate.getText().toString().trim());
                        task.setType(edtType.getText().toString().trim());

                        long check = taskDAO.update(task);
                        if(check < 0) {
                            Toast.makeText(context.getApplicationContext(), "Update fail", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Update successfully", Toast.LENGTH_LONG).show();
                        }
                        list.set(position, task);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderInfo extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        CheckBox chkTask;
        ImageView imgUpdate, imgDelete;

        public ViewHolderInfo(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            chkTask = itemView.findViewById(R.id.chkTask);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
