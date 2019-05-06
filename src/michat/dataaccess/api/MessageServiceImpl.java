package michat.dataaccess.api;

import michat.Main;
import michat.dataaccess.model.Chat;
import michat.dataaccess.model.ChatParticipant;
import michat.dataaccess.model.Message;
import michat.dataaccess.model.User;
import okhttp3.RequestBody;

import java.util.List;

public class MessageServiceImpl extends BaseService implements MessageService {

    @Override
    public void getMessagesByChat(Chat chat, Integer offset, ResponseListener<List<Message>> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonList("/api/messages/getByChat/", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"chat\": \"%s\", \"offset\": \"%d\"}", chat.getId(), offset)), Message.class, responseListener);
    }

    @Override
    public void save(String content, Chat chat, ChatParticipant participant, ResponseListener<Message> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonObject("/api/messages/save/", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"content\": \"%s\", \"chat\": \"%d\", \"user\": \"%d\"}", content, chat.getId(), participant.getId())), Message.class, responseListener);
    }

    @Override
    public void delete(Message message, Chat chat, ResponseListener<Message> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonObject("/api/messages/delete/", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"message\": \"%d\", \"chat\": \"%d\"}", message.getId(), chat.getId())), Message.class, responseListener);
    }

    @Override
    public void update(Message message, String content, Chat chat, ResponseListener<Message> responseListener) {
        setLoggedInUser(Main.loggedInUser);
        postJsonObject("/api/messages/update/", RequestBody.create(REQUEST_TYPE_JSON, String.format("{ \"message\": \"%d\", \"chat\": \"%d\", \"content\": \"%s\"}", message.getId(), chat.getId(), content)), Message.class, responseListener);
    }

    @Override
    public void subscribeOnMessageChannel(User user, ResponseListener<Message> responseListener) {

    }
}
