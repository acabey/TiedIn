package edu.neu.tiedin.ui.messages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.Conversation;
import edu.neu.tiedin.data.User;


class ConversationAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ConversationAdapter";
    private final List<Conversation> conversations;
    private final Context context;
    private final String currentUserId;
    private FirebaseFirestore firebaseFirestore;

    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        public TextView txtParticipants;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtParticipants = (TextView) itemView.findViewById(R.id.txtParticipants);
        }

        public void bindThisData(Conversation conversationToBind) {
            // Initially set with UserIds, filtering out the current user
            List<String> filteredUserIds = conversationToBind
                    .getParticipantIds()
                    .stream()
                    .filter(s -> !s.equals(currentUserId))
                    .collect(Collectors.toList());

            // Comma separate
            txtParticipants.setText(filteredUserIds
                    .stream()
                    .collect(Collectors.joining(", ")));

            // Async pull actual usernames
            firebaseFirestore.collection("users")
                    .whereIn("_id", filteredUserIds)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments() != null && task.getResult().getDocuments().size() > 0) {
                            ArrayList<String> filteredNames = new ArrayList<>();
                            task.getResult().getDocuments().forEach(doc -> {
                                User retrievedUser = doc.toObject(User.class);
                                if (retrievedUser != null) {
                                    filteredNames.add(retrievedUser.getName());
                                } else {
                                    Log.e(TAG, "bindThisData: null user retrieved");
                                }
                            });

                            txtParticipants.setText(filteredNames
                                    .stream()
                                    .collect(Collectors.joining(", ")));
                        } else {
                            Log.e(TAG, "bindThisData: failed to replace participant IDs with names");
                        }
                    });
        }
    }

    public ConversationAdapter(List<Conversation> conversations, Context context, String currentUserId, FirebaseFirestore firebaseFirestore) {
        this.conversations = conversations;
        this.context = context;
        this.currentUserId = currentUserId;
        this.firebaseFirestore = firebaseFirestore;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(LayoutInflater.from(context).inflate(R.layout.conversation_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ConversationViewHolder) holder).bindThisData(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void addConversations(Collection<Conversation> newConversations) {
        int priorSize = this.conversations.size();
        int added = 0;
        for (Conversation newConversation : newConversations) {
            if (!conversations.contains(newConversation)) {
                conversations.add(newConversation);
                added++;
            }
        }
        notifyItemRangeInserted(priorSize, added);
    }

    public void addConversation(Conversation changedConversation) {
        if (!conversations.contains(changedConversation)) {
            conversations.add(changedConversation);
            notifyItemInserted(conversations.size());
        }
    }

    public void removeConversation(Conversation changedConversation) {
        int previousIndex = conversations.indexOf(changedConversation);
        if (previousIndex > -1) {
            conversations.remove(changedConversation);
            notifyItemRemoved(previousIndex);
        }
    }

    public void modifyConversation(Conversation changedConversation) {
        if (conversations.contains(changedConversation)) {
            int indexModified = conversations.indexOf(changedConversation);
            conversations.set(indexModified, changedConversation);
            notifyItemChanged(indexModified);
        }
    }

}
