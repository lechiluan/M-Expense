package com.example.m_expense.Expense;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_expense.Database.MyDatabaseHelper;
import com.example.m_expense.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<Expense> expenses;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    ExpenseAdapter(Activity activity, Context context, List<Expense> expenses) {
        this.activity = activity;
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_expense, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Expense expense = expenses.get(position);

        int id = expense.getId();
        String type = expense.getTypeExpense();
        String amount = String.valueOf(expense.getAmount());
        String date = expense.getDate();
        String note = expense.getNote();

        // set value to form
        holder.expenseType.setText(type);
        holder.expenseAmount.setText(amount);
        holder.expenseDate.setText(date);
        holder.expenseNote.setText(note);

        holder.editExpense.setOnClickListener(view -> {
            //passing parameter values
            Intent intent = new Intent(context, UpdateExpenseActivity.class);
            intent.putExtra("selectedExpense", expense);
            activity.startActivityForResult(intent, 1);
        });

        holder.deleteExpense.setOnClickListener(v -> deleteExpense(expense, expense.getId()));
    }

    private void deleteExpense(Expense expense, Integer id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + expense.getTypeExpense() + " ?");
        builder.setMessage("Are you sure you want to delete ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                long result = myDB.deleteExpense(String.valueOf(id));
                if (result == -1) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                    activity.finish();
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    context.startActivity(activity.getIntent());
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            }
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
            // do nothing
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView editExpense, deleteExpense;
        TextView expenseType, expenseAmount, expenseDate, expenseNote;
        LinearLayout expenseLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseType = itemView.findViewById(R.id.expenseType);
            expenseAmount = itemView.findViewById(R.id.expenseAmount);
            expenseDate = itemView.findViewById(R.id.expenseDate);
            expenseNote = itemView.findViewById(R.id.expenseNote);
            editExpense = itemView.findViewById(R.id.editExpense);
            deleteExpense = itemView.findViewById(R.id.deleteExpense);
            expenseLayout = itemView.findViewById(R.id.expenseLayout);

            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            expenseLayout.setAnimation(translate_anim);
        }
    }
}
