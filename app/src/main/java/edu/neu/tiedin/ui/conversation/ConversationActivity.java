package edu.neu.tiedin.ui.conversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
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
import edu.neu.tiedin.databinding.ActivityConversationBinding;
import edu.neu.tiedin.databinding.ActivityMainBinding;

public class ConversationActivity extends AppCompatActivity {

    public String SHARED_PREFS;
    public String USER_KEY;

    private static final String CHANNEL_ID = "Conversations-ID";
    private static final String CHANNEL_NAME = "Conversations-Name";
    private static final String CHANNEL_DESCRIPTION = "Conversations-Description";

    private static final String TAG = "ConversationFragment";
    private ConversationViewModel conversationViewModel;

    private FirebaseFirestore firestoreDatabase;
    private SharedPreferences sharedpreferences;
    private String userId;
    private String conversationId;
    private ActivityConversationBinding binding;

    private RecyclerView.LayoutManager messageViewLayoutManager;
    private MessageAdapter messageAdapter;

    private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SHARED_PREFS = getString(R.string.sessionLoginPrefsKey);
        USER_KEY = getString(R.string.sessionUserIdKey);
        sharedpreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString(USER_KEY, null);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            this.conversationId = b.getString("conversationId", null);
        }

        // Connect with firebase
        firestoreDatabase = FirebaseFirestore.getInstance();

        // View elements
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversationViewModel = new ViewModelProvider(this).get(ConversationViewModel.class);

        // Configure recyclerview
        binding.messagesView.setHasFixedSize(true);
        messageViewLayoutManager = new LinearLayoutManager(this);
        binding.messagesView.setLayoutManager(messageViewLayoutManager);
        messageAdapter = new MessageAdapter(conversationViewModel.getFilteredMessages().getValue(), this, userId, firestoreDatabase);
        binding.messagesView.setAdapter(messageAdapter);

        // Pull down current conversation
        firestoreDatabase.collection("conversations").document(conversationId).get()
                .onSuccessTask(snapshot -> Tasks.forResult(validateTUserResultExists(snapshot, conversationId, Conversation.class)))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "onCreateView: pulled down conversation");
                        conversationViewModel.getCurrentConversation().setValue(task.getResult().toObject(Conversation.class));
                    } else {
                        Log.e(TAG, "onCreateView: failed to pull down conversation");
                        Toast.makeText(this, "Failed to pull down conversation", Toast.LENGTH_SHORT);
                    }
                });

        // Pull down the current user
        firestoreDatabase.collection("users").document(userId).get()
                .onSuccessTask(snapshot -> Tasks.forResult(validateTUserResultExists(snapshot, userId, User.class)))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "onCreateView: pulled down user");
                        conversationViewModel.getCurrentUser().setValue(task.getResult().toObject(User.class));
                    } else {
                        Log.e(TAG, "onCreateView: failed to pull down user");
                        Toast.makeText(this, "Failed to pull down user information", Toast.LENGTH_SHORT);
                    }
                });

        // Configure notifications
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();

        // Get current messages
        final Query colRef = firestoreDatabase.collection("messages")
                .whereEqualTo("conversationId", conversationId);
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getDocuments() != null) {
                Log.i(TAG, "pulled down messages " + task.getResult().getDocuments().size());
                messageAdapter.addMessages(
                        task.getResult().getDocuments()
                                .stream()
                                .map(
                                        (Function<DocumentSnapshot, Message>) documentSnapshot ->
                                                documentSnapshot.toObject(Message.class))
                                .collect(Collectors.toList()));
            } else {
                Log.e(TAG, "failed to pull down messages");
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
                    Message changedMessage = change.getDocument().toObject(Message.class);

                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "Added data: " + change.getDocument().getData());
                        messageAdapter.addMessage(changedMessage);

                        // Create notification unless message was sent by this user
                        if (!changedMessage.getSender().equals(userId)) {
                            notifyMessage(changedMessage);
                        }
                    } else if (change.getType() == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "Removed data: " + changedMessage.toString());
                        messageAdapter.removeMessage(changedMessage);
                    } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "Modified data");
                        messageAdapter.modifyMessage(changedMessage);
                    } else {
                        Log.d(TAG, "Unknown data change: " + change.getDocument().getData());
                    }
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

        // Bind editText send action
        binding.txtMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.btnSendMessage.performClick();
                return true;
            }
            return false;
        });

        // Bind button handlers
        binding.btnSendMessage.setOnClickListener(v -> sendMessageHandler());
    }

    private void sendMessageHandler() {
        // Error handling: current user id and conversation must be populated, textbox bust be full
        String messagePayload = binding.txtMessage.getText().toString();
        if (userId == null || conversationId == null) {
            Toast.makeText(this,"Unknown sender or receiver",Toast.LENGTH_SHORT).show();
            return;
        } else if (messagePayload.isEmpty()) {
            Toast.makeText(this,"Message cannot be blank",Toast.LENGTH_SHORT).show();
            return;
        }

        // Create message object and push to DB
        Message newMessage = new Message(userId, conversationId, messagePayload, System.currentTimeMillis());
        firestoreDatabase.collection("messages").document(newMessage.get_id()).set(newMessage)
                .addOnCompleteListener((OnCompleteListener<Void>) completedPostMessage -> {
            if(completedPostMessage.isSuccessful()){
                Toast.makeText(this,"Posted new message",Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "sendMessageHandler: failed to post message to DB");
                Toast.makeText(this,"Unable to post message to DB",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void notifyMessage(Message changedMessage) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(); // TODO
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
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
            Toast.makeText(this, "Null result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null result from DB (key: " + attemptedKey + ")");
        } else if (snapshot.toObject(t) == null) {
            Log.e(TAG, "bind: " + "null user from DB (key: " + attemptedKey + ")");
            Toast.makeText(this, "Null object result from database", Toast.LENGTH_SHORT).show();
            throw new Exception("null object from DB (key: " + attemptedKey + ")");
        }
        return snapshot;
    }
}