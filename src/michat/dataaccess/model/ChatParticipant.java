package michat.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

import java.util.Objects;

public class ChatParticipant {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty(value = "chat_id")
    private Integer chatId;
    @JsonProperty("is_admin")
    private Boolean isAdmin;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("chat")
    private Chat chat;
    @JsonProperty("user")
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    @JsonIgnore
    public Boolean getAdmin() {
        return isAdmin;
    }

    @JsonIgnore
    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @JsonIgnore
    public Boolean getDeleted() {
        return isDeleted;
    }

    @JsonIgnore
    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public ChatParticipant() {
    }

    public ChatParticipant(Integer id, Integer userId, Integer chatId, Boolean isAdmin, Boolean isDeleted, Chat chat, User user) {
        this.id = id;
        this.userId = userId;
        this.chatId = chatId;
        this.isAdmin = isAdmin;
        this.isDeleted = isDeleted;
        this.chat = chat;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatParticipant that = (ChatParticipant) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(chatId, that.chatId) &&
                Objects.equals(isAdmin, that.isAdmin) &&
                Objects.equals(isDeleted, that.isDeleted) &&
                Objects.equals(chat, that.chat) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
