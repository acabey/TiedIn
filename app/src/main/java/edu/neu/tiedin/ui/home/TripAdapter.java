package edu.neu.tiedin.ui.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.ClimbingTrip;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private ArrayList<ClimbingTrip> trips;
    private Context context;

    public TripAdapter(ArrayList<ClimbingTrip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    public void filterList(ArrayList<ClimbingTrip> filterList) {
        trips = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.ViewHolder holder, int position) {
        ClimbingTrip trip = trips.get(position);
        holder.text.setText(trip.getOrganizerUserId());
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
