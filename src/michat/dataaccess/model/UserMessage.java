package michat.dataaccess.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UserMessage {
    @JsonProperty("user")
    private User user;
    @JsonProperty("message")
    private Message message;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public UserMessage() {
    }

    public UserMessage(User user, Message message) {
        this.user = user;
        this.message = message;
    }
}
