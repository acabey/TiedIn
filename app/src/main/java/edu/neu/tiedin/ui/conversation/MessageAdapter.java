package edu.neu.tiedin.ui.conversation;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.Message;
import edu.neu.tiedin.data.User;

class MessageAdapter extends RecyclerView.Adapter {
    private final List<Message> messages;
    private final Context context;
    private final String currentUserId;
    private final FirebaseFirestore firebaseFirestore;

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "MessageAdapter";
        public CardView cardView;
        public TextView txtSender, txtPayload;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = (CardView) itemView.findViewById(R.id.messageCard);
            this.txtSender = (TextView) itemView.findViewById(R.id.txtMessageLayoutSender);
            this.txtPayload = (TextView) itemView.findViewById(R.id.txtPayload);
        }

        public void bindThisData(Message messageToBind) {
            txtSender.setText(messageToBind.getSender());
            txtPayload.setText(messageToBind.getPayload());

            // Right align message if sent by the current user
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (currentUserId != null && messageToBind.getSender().equals(currentUserId)) {
                // Do not show name if sent by this user
                txtSender.setVisibility(View.GONE);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
            } else {
                // Async name replace for the other user
                firebaseFirestore.collection("users").document(messageToBind.getSender())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && task.getResult().toObject(User.class) != null) {
                                User retrievedUser = task.getResult().toObject(User.class);
                                if (retrievedUser != null) {
                                    txtSender.setText((retrievedUser.getName()));
                                    Log.d(TAG, "bindThisData: replaced user ID with name");
                                } else {
                                    Log.e(TAG, "bindThisData: null user retrieved");
                                }
                            } else {
                                Log.e(TAG, "bindThisData: failed to replace participant IDs with names");
                            }
                        });
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
            }
            cardView.setLayoutParams(params);
        }
    }

    public MessageAdapter(List<Message> messages, Context context, String currentUserId, FirebaseFirestore firebaseFirestore) {
        this.messages = messages;
        this.context = context;
        this.currentUserId = currentUserId;
        this.firebaseFirestore = firebaseFirestore;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MessageViewHolder) holder).bindThisData(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessages(Collection<Message> newMessages) {
        int priorSize = this.messages.size();
        int added = 0;
        for (Message newMessage : newMessages) {
            if (!messages.contains(newMessage)) {
                messages.add(newMessage);
                added++;
            }
        }
        notifyItemRangeInserted(priorSize, added);
    }

    public void addMessage(Message changedMessage) {
        if (!messages.contains(changedMessage)) {
            messages.add(changedMessage);
            notifyItemInserted(messages.size());
        }
    }

    public void removeMessage(Message changedMessage) {
        int previousIndex = messages.indexOf(changedMessage);
        if (previousIndex > -1) {
            messages.remove(changedMessage);
            notifyItemRemoved(previousIndex);
        }
    }

    public void modifyMessage(Message changedMessage) {
        if (messages.contains(changedMessage)) {
            int indexModified = messages.indexOf(changedMessage);
            messages.set(indexModified, changedMessage);
            notifyItemChanged(indexModified);
        }
    }

}
