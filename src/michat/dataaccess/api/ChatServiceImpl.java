package michat.dataaccess.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import michat.Main;
import michat.dataaccess.model.Chat;
import michat.dataaccess.model.ChatParticipant;
import michat.dataaccess.model.User;
import michat.helpers.Utils;
import okhttp3.RequestBody;

import java.util.List;

public class ChatServiceImpl extends BaseService implements ChatService {

    @Override
    public void getAllByUser(ResponseListener<List<Chat>> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        getJsonList("/api/chats/getAllByUser", Chat.class, responseListener);
    }

    @Override
    public void search(String searchQuery, ResponseListener<List<Chat>> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonList("/api/chats/search", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"searchQuery\": \"%s\"}", searchQuery)), Chat.class, responseListener);
    }

    @Override
    public void save(String name, List<User> users, ResponseListener<Chat> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonObject("/api/chats/search", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"name\": \"%s\", \"participants\": \"%s\"}", name, Utils.convertToParticipantsList(users))), Chat.class, responseListener);
    }

    @Override
    public void updatePermissions(Chat chat, List<ChatParticipant> participants, ResponseListener<Boolean> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        try {
            postJsonObject("/api/chats/updatePermissions", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"chat\": \"%d\", \"participants\": %s}", chat.getId(), new ObjectMapper().writeValueAsString(participants))), Boolean.class, responseListener);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getParticipants(Chat chat, ResponseListener<List<ChatParticipant>> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonList("/api/chats/getParticipants", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"chat\": \"%d\"}", chat.getId())), ChatParticipant.class, responseListener);
    }

    @Override
    public void leave(Chat chat) {
        setLoggedInUser(Main.loggedInUser);
        try {
            postJsonObject("/api/chats/leave", RequestBody.create(REQUEST_TYPE_JSON, new ObjectMapper().writeValueAsString(chat)), Chat.class, null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
