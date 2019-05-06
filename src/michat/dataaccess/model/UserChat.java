package michat.dataaccess.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UserChat {
    @JsonProperty("user")
    private User user;
    @JsonProperty("chat")
    private Chat chat;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public UserChat() {
    }

    public UserChat(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }
}
