package edu.neu.tiedin.ui.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.neu.tiedin.data.Conversation;
import edu.neu.tiedin.data.Message;
import edu.neu.tiedin.data.User;

public class ConversationViewModel extends ViewModel {
    private MutableLiveData<User> currentUser;
    private MutableLiveData<Conversation> currentConversation;
    private MutableLiveData<List<Message>> existingMessages = new MutableLiveData(new ArrayList<Message>());
    private MutableLiveData<List<Message>> filteredMessages = new MutableLiveData(new ArrayList<>());
    private MutableLiveData<List<Integer>> notificationIds = new MutableLiveData(new ArrayList<>());

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public MutableLiveData<Conversation> getCurrentConversation() {
        return currentConversation;
    }

    public MutableLiveData<List<Message>> getExistingMessages() {
        return existingMessages;
    }

    public MutableLiveData<List<Message>> getFilteredMessages() {
        return filteredMessages;
    }

    public MutableLiveData<List<Integer>> getNotificationIds() {
        return notificationIds;
    }
}