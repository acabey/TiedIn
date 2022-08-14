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
import java.util.stream.Collectors;

import edu.neu.tiedin.R;
import edu.neu.tiedin.data.Conversation;
import edu.neu.tiedin.data.User;


class ConversationAdapter extends RecyclerView.Adapter {
    private final List<Conversation> conversations;
    private final Context context;
    private final MutableLiveData<User> getCurrentUser;

    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        public TextView txtParticipants;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.txtParticipants = (TextView) itemView.findViewById(R.id.txtParticipants);
        }

        public void bindThisData(Conversation conversationToBind) {
            txtParticipants.setText(conversationToBind.getParticipants().stream().map(user -> user.getName()).collect(Collectors.joining(", ")));
        }
    }

    public ConversationAdapter(List<Conversation> conversations, Context context, MutableLiveData<User> getCurrentUser) {
        this.conversations = conversations;
        this.context = context;
        this.getCurrentUser = getCurrentUser;
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
