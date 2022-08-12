package edu.neu.tiedin.ui.home;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.ClimbingTrip;
import edu.neu.tiedin.data.User;
import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private static final String TAG = "TripAdapter";
    private ArrayList<ClimbingTrip> trips;
    private Context context;
    FirebaseFirestore firebaseFirestore;

    public TripAdapter(ArrayList<ClimbingTrip> trips, Context context, FirebaseFirestore firebaseFirestore) {
        this.trips = trips;
        this.context = context;
        this.firebaseFirestore = firebaseFirestore;
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
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void addTrips(Collection<ClimbingTrip> newTrips) {
        int priorSize = this.trips.size();
        int added = 0;
        for (ClimbingTrip newTrip: newTrips) {
            if (!trips.contains(newTrip)) {
                trips.add(newTrip);
                added++;
            }
        }
        notifyItemRangeInserted(priorSize, added);
    }

    public void addTrip(ClimbingTrip newTrip) {
        if (!trips.contains(newTrip)) {
            trips.add(newTrip);
            notifyItemInserted(trips.size());
        }
    }

    public void removeTrip(ClimbingTrip newTrip) {
        if (trips.contains(newTrip)) {
            trips.remove(newTrip);
            notifyItemRemoved(trips.size());
        }
    }

    public void modifyTrip(ClimbingTrip modifiedTrip) {
        if (trips.contains(modifiedTrip)) {
            int indexModified = trips.indexOf(modifiedTrip);
            trips.set(indexModified, modifiedTrip);
            notifyItemChanged(indexModified);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtHostName, txtDate, txtAreas, txtStyles, txtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtHostName = itemView.findViewById(R.id.txtHostName);
            this.txtDate = itemView.findViewById(R.id.txtDate);
            this.txtAreas = itemView.findViewById(R.id.txtAreas);
            this.txtStyles = itemView.findViewById(R.id.txtStyles);
            this.txtDescription = itemView.findViewById(R.id.txtDescription);
        }

        public void bind(ClimbingTrip trip) {
            // Temporarily set organizer ("host") username to their UUID, async change to actual name from profile
            txtHostName.setText(trip.getOrganizerUserId());
            firebaseFirestore.collection("users")
                    .document(trip.getOrganizerUserId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "bind: " + "Failed to pull user from DB (UUID: " + trip.getOrganizerUserId() + ")");
                        } else if (task.getResult() == null) {
                            Log.e(TAG, "bind: " + "null result from DB (UUID: " + trip.getOrganizerUserId() + ")");
                        } else if (task.getResult().toObject(User.class) == null) {
                            Log.e(TAG, "bind: " + "null user from DB (UUID: " + trip.getOrganizerUserId() + ")");
                        } else if (task.getResult().toObject(User.class).getName() == null) {
                            Log.e(TAG, "bind: " + "null username from DB (UUID: " + trip.getOrganizerUserId() + ")");
                        } else {
                            txtHostName.setText(task.getResult().toObject(User.class).getName());
                        }
                    });

            // Set date
            txtDate.setText(LocalDate.ofEpochDay(trip.getEpochDate()).format(DateTimeFormatter.ofPattern("E MMM d")));

            // Set Area names comma-separated
            String joinedAreas = trip.getAreas().stream().map(composedArea -> composedArea.areaName).collect(Collectors.joining(","));
            txtAreas.setText(joinedAreas);

            // Set styles as comma-separated
            String joinedStyles = trip.getStyles().stream().map(composedStyle -> composedStyle.toString()).collect(Collectors.joining(","));
            txtStyles.setText(joinedStyles);

            // Description is easy
            txtDescription.setText(trip.getDetails());
        }
    }
}
