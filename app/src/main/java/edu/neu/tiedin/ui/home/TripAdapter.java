package edu.neu.tiedin.ui.home;


import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.ClimbingTrip;
import edu.neu.tiedin.data.Conversation;
import edu.neu.tiedin.data.User;
import edu.neu.tiedin.types.openbeta.composedschema.ComposedArea;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private static final String TAG = "TripAdapter";
    private ArrayList<ClimbingTrip> trips;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId;
    private MutableLiveData<Location> locationData;

    private Fragment parentFragment;

    public TripAdapter(ArrayList<ClimbingTrip> trips, Context context, FirebaseFirestore firebaseFirestore, String currentUserId, MutableLiveData<Location> locationData, Fragment parentFragment) {
        this.trips = trips;
        this.context = context;
        this.firebaseFirestore = firebaseFirestore;
        this.currentUserId = currentUserId;
        this.locationData = locationData;
        this.parentFragment = parentFragment;
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

    public void removeTrip(ClimbingTrip tripToRemove) {
        int previousIndex = trips.indexOf(tripToRemove);
        if (previousIndex > -1) {
            trips.remove(tripToRemove);
            notifyItemRemoved(previousIndex);
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

        ClimbingTrip viewTrip;
        CardView tripCard;
        TextView txtHostName, txtDate, txtAreas, txtStyles, txtDescription;
        ImageButton btnPopupMenu;

        boolean expandedVisibility = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tripCard = itemView.findViewById(R.id.tripCard);
            this.txtHostName = itemView.findViewById(R.id.txtHostName);
            this.txtDate = itemView.findViewById(R.id.txtDate);
            this.txtAreas = itemView.findViewById(R.id.txtAreas);
            this.txtStyles = itemView.findViewById(R.id.txtStyles);
            this.txtDescription = itemView.findViewById(R.id.txtDescription);
            this.btnPopupMenu = itemView.findViewById(R.id.btnPopupmenu);
        }

        public void bind(ClimbingTrip trip) {
            this.viewTrip = trip;

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
            String joinedAreas = trip.getAreas().stream().map(composedArea -> composedArea.areaName).collect(Collectors.joining(", "));
            txtAreas.setText(joinedAreas);

            // Set styles as comma-separated
            String joinedStyles = trip
                    .getStyles()
                    .stream()
                    .map(composedStyle -> WordUtils.capitalizeFully(composedStyle.toString()))
                    .collect(Collectors.joining(", "));
            txtStyles.setText(joinedStyles);

            // Description is easy
            txtDescription.setText(trip.getDetails());

            // Clicking on the card toggles "expanded view" to include the full areas list, description, etc.
            tripCard.setOnClickListener(v -> toggleExpanded());

            // Popup menu allows for deletion
            btnPopupMenu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.trip_item_manage_menu, popup.getMenu());

                // Do not show contact if trip is owned by current user
                popup.getMenu().findItem(R.id.tripItemContact).setVisible(!currentUserId.equals(trip.getOrganizerUserId()));

                // Show delete option on popup menu if trip is owned by current user
                popup.getMenu().findItem(R.id.tripItemDelete).setVisible(currentUserId.equals(trip.getOrganizerUserId()));

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case (R.id.tripItemContact):
                            contactOrganizer(trip);
                            return true;
                        case (R.id.tripItemDelete):
                            deleteTrip(trip);
                            return true;
                        default:
                            Log.d(TAG, "bind: Could not find menu item clicked");
                            return false;
                    }
                });
                popup.show();
            });
        }

        void toggleExpanded() {
            if (expandedVisibility) {
                // Toggle to off
                txtDescription.setVisibility(View.GONE);
                txtAreas.setMaxLines(1);
                txtStyles.setMaxLines(1);
            } else {
                // Toggle to on
                txtDescription.setVisibility(View.VISIBLE);
                txtAreas.setMaxLines(10);
                txtStyles.setMaxLines(10);

                // Show distance to each crag, if location is available
                if (locationData.getValue() != null) {
                    // Set Area names comma-separated
                    Function<ComposedArea, String> addAreaDistance = (ComposedArea composedArea) -> {
                        if (composedArea.metadata != null && composedArea.metadata.lng != null && composedArea.metadata.lat != null) {
                            Location areaLocation = new Location("");
                            areaLocation.setLatitude(composedArea.metadata.lat);
                            areaLocation.setLongitude(composedArea.metadata.lng);
                            float distanceMeters = locationData.getValue().distanceTo(areaLocation);
                            String distanceMiles = String.valueOf(Math.round(distanceMeters / 1600));

                            return composedArea.areaName + " (" + distanceMiles + "mi away)";
                        } else {
                            return composedArea.areaName;
                        }
                    };
                    String joinedAreas = viewTrip
                            .getAreas()
                            .stream()
                            .map(composedArea -> addAreaDistance.apply(composedArea))
                            .collect(Collectors.joining(", "));
                    txtAreas.setText(joinedAreas);
                } else {
                    Log.d(TAG, "toggleExpanded: location value is null");
                }
            }
            expandedVisibility = !expandedVisibility;
        }

        void deleteTrip(ClimbingTrip trip) {
            DocumentReference refToDelete = firebaseFirestore.collection("trips").document(trip.get_id());
            if (refToDelete != null) {
                refToDelete.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        removeTrip(trip);
                        Log.i(TAG, "deleteTrip: successfully deleted trip UUID " + trip.get_id());
                    } else {
                        Log.e(TAG, "deleteTrip: found, but could not delete trip UUID " + trip.get_id());
                    }
                });
            } else {
                Log.d(TAG, "deleteTrip: tried to delete trip UUID " + trip.get_id() + ", but could not find in DB");
            }
        }

        void contactOrganizer(ClimbingTrip trip) {
            // Create new conversation
            Conversation newConvo = new Conversation(Arrays.asList(currentUserId, trip.getOrganizerUserId()));

            // Post it to the DB
            firebaseFirestore.collection("conversations").document(newConvo.get_id()).set(newConvo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "contactOrganizer: created conversation, navigating to messages");
                    NavHostFragment.findNavController(parentFragment).navigate(R.id.nav_messages);
                } else {
                    Log.e(TAG, "contactOrganizer: failed to post message");
                    Toast.makeText(context, "Failed to create conversation with " + trip.getOrganizerUserId(), Toast.LENGTH_SHORT);
                }
            });
        }
    }
}
