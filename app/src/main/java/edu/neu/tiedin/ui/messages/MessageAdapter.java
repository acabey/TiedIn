package edu.neu.tiedin.ui.messages;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.List;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.Message;
import edu.neu.tiedin.data.User;

class MessageAdapter extends RecyclerView.Adapter {
    private final List<Message> messages;
    private final Context context;
    private final MutableLiveData<User> getCurrentUser;

    public class MessageViewHolder extends RecyclerView.ViewHolder {

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
            if (getCurrentUser.getValue() != null && messageToBind.getSender().equals(getCurrentUser.getValue().getEmail())) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
            }
            cardView.setLayoutParams(params);
        }
    }

    public MessageAdapter(List<Message> messages, Context context, MutableLiveData<User> getCurrentUser) {
        this.messages = messages;
        this.context = context;
        this.getCurrentUser = getCurrentUser;
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
