package com.example.m_expense;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> implements Filterable {

    private final Context context;
    private final Activity activity;
    private List<Trip> trips;
    private final List<Trip> tripsOld;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    TripAdapter(Activity activity, Context context, List<Trip> trips) {
        this.activity = activity;
        this.context = context;
        this.trips = trips;
        this.tripsOld = trips;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_trip, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Trip trip = trips.get(position);

        String name = trip.getName();
        String des = trip.getDes();
        String dateFrom = trip.getDateFrom();
        String dateTo = trip.getDateTo();

        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        Float totalExpenses = myDB.getTotalExpense(String.valueOf(trip.getId()));

        // set value to form
        holder.tripName.setText(name);
        holder.tripDestination.setText(des);
        holder.tripDate.setText(dateFrom.concat(" - " + dateTo));
        holder.totalExpense.setText(String.valueOf(totalExpenses));

        holder.editTrip.setOnClickListener(view -> {
            //passing parameter values
            Intent intent = new Intent(context, UpdateTripActivity.class);
            intent.putExtra("selectedTrip", trip);
            activity.startActivityForResult(intent, 1);
        });

        holder.deleteTrip.setOnClickListener(view -> deleteTrip(trip.getId(), trip.getName()));

        holder.mainLayout.setOnClickListener(view -> {
            //passing parameter values
            Intent intent = new Intent(context, ExpenseActivity.class);
            intent.putExtra("selectedTrip", trip);
            activity.startActivityForResult(intent, 1);
        });
    }

    private void deleteTrip(int id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + name + " ?");
        builder.setMessage("Are you sure you want to delete ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                long result = myDB.delete(String.valueOf(id));
                if (result == -1) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, TripActivity.class));
                    activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView editTrip, deleteTrip;
        TextView tripName, tripDestination, tripDate,totalExpense;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tripName = itemView.findViewById(R.id.tripName);
            tripDestination = itemView.findViewById(R.id.destination);
            tripDate = itemView.findViewById(R.id.date);
            editTrip = itemView.findViewById(R.id.editTrip);
            deleteTrip = itemView.findViewById(R.id.deleteTrip);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            totalExpense = itemView.findViewById(R.id.totalExpense);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    trips = tripsOld;
                } else {
                    List<Trip> list = new ArrayList<>();
                    for (Trip trip : tripsOld) {
                        if (trip.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(trip);
                        }
                        else if (trip.getDateFrom().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(trip);
                        }
                        else if (trip.getDes().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(trip);
                        }
                    }
                    trips = list;
                }
                FilterResults result = new FilterResults();
                result.values = trips;
                return result;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                trips = (List<Trip>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
