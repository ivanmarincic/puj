package michat.dataaccess.api;

import michat.dataaccess.model.Chat;
import michat.dataaccess.model.ChatParticipant;
import michat.dataaccess.model.Message;
import michat.dataaccess.model.User;

import java.util.List;

public interface MessageService {
    void getMessagesByChat(Chat chat, Integer offset, BaseService.ResponseListener<List<Message>> responseListener);

    void save(String content, Chat chat, ChatParticipant participant, BaseService.ResponseListener<Message> responseListener);

    void delete(Message message, Chat chat, BaseService.ResponseListener<Message> responseListener);

    void update(Message message, String content, Chat chat, BaseService.ResponseListener<Message> responseListener);

    void subscribeOnMessageChannel(User user, BaseService.ResponseListener<Message> responseListener);
}
