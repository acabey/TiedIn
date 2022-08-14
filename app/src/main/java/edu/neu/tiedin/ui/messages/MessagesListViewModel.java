package edu.neu.tiedin.ui.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.neu.tiedin.data.Conversation;
import edu.neu.tiedin.data.User;

public class MessagesListViewModel extends ViewModel {
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<List<Conversation>> conversations = new MutableLiveData(new ArrayList<Conversation>());

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(MutableLiveData<User> currentUser) {
        this.currentUser = currentUser;
    }

    public MutableLiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public void setConversations(MutableLiveData<List<Conversation>> conversations) {
        this.conversations = conversations;
    }
}
