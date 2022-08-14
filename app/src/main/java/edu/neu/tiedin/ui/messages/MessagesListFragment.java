package edu.neu.tiedin.ui.messages;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.Conversation;
import edu.neu.tiedin.data.Message;
import edu.neu.tiedin.data.User;
import edu.neu.tiedin.databinding.FragmentMessagesListBinding;

public class MessagesListFragment extends Fragment {

    public String SHARED_PREFS;
    public String USER_KEY;

    private static final String CHANNEL_ID = "Messages-ID";
    private static final String CHANNEL_NAME = "Messages-Name";
    private static final String CHANNEL_DESCRIPTION = "Messages-Description";

    private static final String TAG = "MessagesFragment";
    private MessagesListViewModel conversationViewModel;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;
    private FragmentMessagesListBinding binding;

    private RecyclerView.LayoutManager messageViewLayoutManager;
    private ConversationAdapter conversationAdapter;

    private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SHARED_PREFS = getString(R.string.sessionLoginPrefsKey);
        USER_KEY = getString(R.string.sessionUserIdKey);
        sharedpreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString(USER_KEY, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMessagesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Connect with firebase
        firestoreDatabase = FirebaseFirestore.getInstance();

        conversationViewModel = new ViewModelProvider(this).get(MessagesListViewModel.class);

        // Configure recyclerview
        binding.conversationsView.setHasFixedSize(true);
        messageViewLayoutManager = new LinearLayoutManager(getContext());
        binding.conversationsView.setLayoutManager(messageViewLayoutManager);
        conversationAdapter = new ConversationAdapter(conversationViewModel.getConversations().getValue(), getContext(), userId, firestoreDatabase);
        binding.conversationsView.setAdapter(conversationAdapter);

        // Pull down the current user
        firestoreDatabase.collection("users").document(userId).get()
                .onSuccessTask(snapshot -> Tasks.forResult(validateTUserResultExists(snapshot, userId, User.class)))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "onCreateView: pulled down user");
                        conversationViewModel.getCurrentUser().setValue(task.getResult().toObject(User.class));
                    } else {
                        Log.e(TAG, "onCreateView: failed to pull down user");
                        Toast.makeText(getContext(), "Failed to pull down user information", Toast.LENGTH_SHORT);
                    }
                });

        // Configure notifications
        // TODO
//        notificationManager = NotificationManagerCompat.from(getContext());
//        createNotificationChannel();

        // Get current messages
        final Query colRef = firestoreDatabase.collection("conversations")
                .whereArrayContains("participantIds", userId);

        colRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments() != null) {
                        Log.i(TAG, "pulled down conversations: " + task.getResult().getDocuments().size());
                        conversationAdapter.addConversations(
                                task.getResult().getDocuments()
                                        .stream()
                                        .map(
                                                (Function<DocumentSnapshot, Conversation>) documentSnapshot ->
                                                        documentSnapshot.toObject(Conversation.class))
                                        .collect(Collectors.toList()));
                    } else {
                        Log.e(TAG, "failed to pull down conversations");
                    }
                });

        // Listen for ongoing changes in trips
        ListenerRegistration listenerRegistration = colRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed", e);
                return;
            }

            if (snapshot != null && !snapshot.getDocumentChanges().isEmpty()) {
                for (DocumentChange change : snapshot.getDocumentChanges()) {
                    Conversation changedConversation = change.getDocument().toObject(Conversation.class);

                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "Added data: " + change.getDocument().getData());
                        conversationAdapter.addConversation(changedConversation);
                    } else if (change.getType() == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "Removed data: " + changedConversation.toString());
                        conversationAdapter.removeConversation(changedConversation);
                    } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "Modified data");
                        conversationAdapter.modifyConversation(changedConversation);
                    } else {
                        Log.d(TAG, "Unknown data change: " + change.getDocument().getData());
                    }
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        return root;
    }

    public void notifyMessage(Message changedMessage) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(); // TODO
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setContentTitle("New message")
                .setContentText(changedMessage.getPayload())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        int notificationId = ThreadLocalRandom.current().nextInt();
        notificationManager.notify(notificationId, builder.build());
//        notificationIds.add(notificationId);
    }

    /**
     * https://developer.android.com/training/notify-user/build-notification
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

    private DocumentSnapshot validateTUserResultExists(DocumentSnapshot snapshot, String attemptedKey, Class<?> t) throws Exception {
        if (snapshot == null) {
            Log.e(TAG, "bind: " + "null result from DB (key: " + attemptedKey + ")");
            Toast.makeText(getContext(), "Null result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null result from DB (key: " + attemptedKey + ")");
        } else if (snapshot.toObject(t) == null) {
            Log.e(TAG, "bind: " + "null user from DB (key: " + attemptedKey + ")");
            Toast.makeText(getContext(), "Null object result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null object from DB (key: " + attemptedKey + ")");
        }
        return snapshot;
    }
}
