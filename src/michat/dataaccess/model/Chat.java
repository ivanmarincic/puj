package michat.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class Chat {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Berlin")
    private Timestamp createdAt;
    @JsonProperty("last_message")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Berlin")
    private Timestamp lastMessage;
    @JsonProperty("participants")
    private List<ChatParticipant> participants;
    @JsonIgnore
    private boolean unread ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Timestamp lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<ChatParticipant> getParticipants() {
        return participants;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public void setParticipants(List<ChatParticipant> participants) {
        this.participants = participants;
    }

    public Chat() {
    }

    public Chat(Integer id, String name, Timestamp createdAt, Timestamp lastMessage, List<ChatParticipant> participants) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastMessage = lastMessage;
        this.participants = participants;
    }

    public Chat(Chat chat) {
        this.id = chat.id;
        this.name = chat.name;
        this.createdAt = chat.createdAt;
        this.lastMessage = chat.lastMessage;
        this.participants = chat.participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id) &&
                Objects.equals(name, chat.name) &&
                Objects.equals(createdAt, chat.createdAt) &&
                Objects.equals(lastMessage, chat.lastMessage) &&
                Objects.equals(participants, chat.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
