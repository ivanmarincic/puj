package michat.dataaccess.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Objects;

public class Message {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("chat_id")
    private Integer chatId;
    @JsonProperty("content")
    private String content;
    @JsonProperty("participant_id")
    private Integer participantId;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Berlin")
    private Timestamp createdAt;
    @JsonProperty("edited_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Berlin")
    private Timestamp editedAt;
    @JsonProperty("chat")
    private Chat chat;
    @JsonProperty("participant")
    private ChatParticipant participant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Timestamp editedAt) {
        this.editedAt = editedAt;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ChatParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(ChatParticipant participant) {
        this.participant = participant;
    }

    public Message() {
    }

    public Message(Integer id, Integer chatId, String content, Integer participantId, Timestamp createdAt, Timestamp editedAt, Chat chat, ChatParticipant participant) {
        this.id = id;
        this.chatId = chatId;
        this.content = content;
        this.participantId = participantId;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
        this.chat = chat;
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(chatId, message.chatId) &&
                Objects.equals(content, message.content) &&
                Objects.equals(participantId, message.participantId) &&
                Objects.equals(createdAt, message.createdAt) &&
                Objects.equals(editedAt, message.editedAt) &&
                Objects.equals(chat, message.chat) &&
                Objects.equals(participant, message.participant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
