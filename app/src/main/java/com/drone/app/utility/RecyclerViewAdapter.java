package com.drone.app.utility;

import androidx.recyclerview.widget.RecyclerView;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.drone.app.R;
import com.drone.app.models.FlightModel;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private final List<FlightModel> flights;

    public RecyclerViewAdapter(List<FlightModel> flights){
        this.flights=flights;
    }

    public FlightModel getFlight(int position){return flights.get(position);}


    @NonNull@Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
            holder.getDateView().setText(flights.get(position).getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }
    @Override
    public int getItemCount(){return flights.size();}
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            date = itemView.findViewById(R.id.date_textView);
        }
        public TextView getDateView(){return date;}
    }
}
