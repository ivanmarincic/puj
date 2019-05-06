package michat.dataaccess.api;

import michat.dataaccess.model.Chat;
import michat.dataaccess.model.ChatParticipant;
import michat.dataaccess.model.User;

import java.util.List;

public interface ChatService {
    void getAllByUser(BaseService.ResponseListener<List<Chat>> responseListener);
    void search(String searchQuery, BaseService.ResponseListener<List<Chat>> responseListener);
    void save(String name, List<User> users, BaseService.ResponseListener<Chat> responseListener);
    void updatePermissions(Chat chat, List<ChatParticipant> participants, BaseService.ResponseListener<Boolean> responseListener);
    void getParticipants(Chat chat, BaseService.ResponseListener<List<ChatParticipant>> responseListener);
    void leave(Chat chat);
}
