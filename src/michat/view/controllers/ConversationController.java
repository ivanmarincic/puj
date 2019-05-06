package michat.view.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import michat.Main;
import michat.dataaccess.api.*;
import michat.dataaccess.model.Chat;
import michat.dataaccess.model.ChatParticipant;
import michat.dataaccess.model.Message;
import michat.dataaccess.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversationController {

    @FXML
    private ScrollPane messagesListScroll;

    @FXML
    private VBox messagesList;

    @FXML
    private Button send;

    @FXML
    private Button cancel;

    @FXML
    private TextField messageInput;

    @FXML
    private ProgressIndicator progressIndicator;

    private UserService userService;
    private MessageService messageService;
    private Chat currentChat;
    private ChatParticipant currentParticipant;
    private List<Message> messages;
    private Message editMessage;
    private OnActionListener listener;
    private int lastMessageIndex;
    private boolean fetchInProgress = false;
    private boolean editMode = false;
    private boolean reachedEnd = false;

    public ConversationController() {
        messages = new ArrayList<>();
        messageService = new MessageServiceImpl();
        userService = new UserServiceImpl();
    }

    @FXML
    public void initialize() {
        send.setOnMouseClicked(event -> {
            send.setDisable(true);
            String content = messageInput.getText();
            if (content != null && !content.isEmpty() && !content.trim().isEmpty()) {
                BaseService.ResponseListener messageResponse = new BaseService.ResponseListener<Message>() {
                    @Override
                    public void onResponse(Message response) {
                        Platform.runLater(() -> {
                            messageInput.clear();
                            messageInput.requestFocus();
                            send.setDisable(false);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Platform.runLater(() -> send.setDisable(false));
                    }
                };
                if (editMode) {
                    messageService.update(editMessage, content, currentChat, messageResponse);
                } else {
                    messageService.save(content, currentChat, currentParticipant, messageResponse);
                }
            }
            setEditMode(false, null);
        });
        cancel.setOnMouseClicked(event -> setEditMode(false, null));
        messagesListScroll.setOnScroll(event -> {
            if (messagesListScroll.getVvalue() == 0.0 && !fetchInProgress && !reachedEnd) {
                getMessages(currentChat, lastMessageIndex + 15);
            }
        });
    }

    public void onChatSelected(Chat chat) {
        if ((currentChat != null && currentChat.getId().equals(chat.getId())) || fetchInProgress) {
            return;
        }
        setEditMode(false, null);
        reachedEnd = false;
        currentChat = chat;
        messages.clear();
        lastMessageIndex = 0;
        for (ChatParticipant participant : chat.getParticipants()) {
            if (participant.getUser().getId().equals(Main.loggedInUser.getId())) {
                currentParticipant = participant;
                break;
            }
        }
        messagesList.getChildren().clear();
        getMessages(chat, lastMessageIndex);
        messagesListScroll.setVvalue(messagesList.getHeight());
    }

    private void getMessages(Chat chat, Integer offset) {
        fetchInProgress = true;
        progressIndicator.setVisible(true);
        messageService.getMessagesByChat(chat, offset, new BaseService.ResponseListener<List<Message>>() {
            @Override
            public void onResponse(List<Message> response) {
                lastMessageIndex = offset;
                if (response.size() == 0) {
                    reachedEnd = true;
                }
                Platform.runLater(() -> {
                    for (Message message : response) {
                        addMessage(message, true);
                    }
                    progressIndicator.setVisible(false);
                    fetchInProgress = false;
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                });
            }
        });

    }

    public void addMessage(Message message, boolean prepend) {
        boolean isMe = false;
        if (message.getParticipant().getId().equals(currentParticipant.getId())) {
            isMe = true;
        }
        try {
            FXMLLoader chatMessageLoader = new FXMLLoader(getClass().getResource("/michat/view/fxml/partials/chat_message.fxml"));
            User user = message.getParticipant().getUser();
            HBox messageGrid = chatMessageLoader.load();
            if (isMe) {
                messageGrid.getStyleClass().add("me");
            } else {
                messageGrid.getStyleClass().add("other");
            }
            messageGrid.setId("message-" + message.getId());
            Label messageContent = (Label) messageGrid.lookup("#message-content");
            Label editLabel = (Label) messageGrid.lookup("#message-edit-label");
            messageContent.setText(message.getContent());
            if (message.getEditedAt() != null) {
                editLabel.setVisible(true);
                editLabel.setManaged(true);
            }
            if (isMe || currentParticipant.getAdmin()) {
                ContextMenu messageOptions = new ContextMenu();
                if (isMe || currentParticipant.getAdmin()) {
                    MenuItem deleteOption = new MenuItem("Izbriši");
                    deleteOption.setOnAction(event -> messageService.delete(message, currentChat, null));
                    messageOptions.getItems().add(deleteOption);
                }
                if (isMe) {
                    MenuItem editOption = new MenuItem("Uredi");
                    editOption.setOnAction(event -> setEditMode(true, message));
                    messageOptions.getItems().add(editOption);
                }
                messageContent.setContextMenu(messageOptions);
            }
            if (prepend) {
                if (messages.size() == 0) {
                    HBox messageHeader = getMessageHeader(user, isMe);
                    messagesList.getChildren().add(0, messageHeader);
                } else if (!messages.get(0).getParticipant().getId().equals(message.getParticipant().getId())) {
                    HBox messageHeader = getMessageHeader(user, isMe);
                    messagesList.getChildren().add(0, messageHeader);
                }
                messages.add(0, message);
                messagesList.getChildren().add(1, messageGrid);
            } else {
                messages.add(message);
                if (!messages.get(messages.size() - 1).getParticipant().getId().equals(message.getParticipant().getId())) {
                    HBox messageHeader = getMessageHeader(user, isMe);
                    messagesList.getChildren().add(messageHeader);
                }
                messagesList.getChildren().add(messageGrid);
                lastMessageIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HBox getMessageHeader(User user, boolean isMe) {
        try {
            FXMLLoader chatMessageHeaderLoader;
            if (isMe) {
                chatMessageHeaderLoader = new FXMLLoader(getClass().getResource("/michat/view/fxml/partials/chat_message_header_me.fxml"));
            } else {
                chatMessageHeaderLoader = new FXMLLoader(getClass().getResource("/michat/view/fxml/partials/chat_message_header_other.fxml"));
            }
            HBox headerContainer = chatMessageHeaderLoader.load();
            Label headerContent = (Label) headerContainer.lookup("#header-content");
            ImageView headerImage = (ImageView) headerContainer.lookup("#header-image");
            headerContent.setText(user.getUsername());
            headerImage.setImage(new Image(userService.getProfileImageUrl(user.getProfileImage())));
            headerImage.setClip(new Circle(15, 15, 14));
            headerContainer.setOnMouseClicked(event -> showUserInfoDialog(user));
            return headerContainer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setEditMode(boolean isEditMode, Message message) {
        if (isEditMode) {
            editMode = true;
            messageInput.setText(message.getContent());
            cancel.setVisible(true);
            cancel.setManaged(true);
            editMessage = message;
        } else {
            editMode = false;
            messageInput.setText("");
            cancel.setVisible(false);
            cancel.setManaged(false);
        }
    }

    public void removeMessage(Message message) {
        messagesList.getChildren().remove(messagesList.lookup("#message-" + message.getId()));
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.remove(i);
                break;
            }
        }
    }

    public void updateMessage(Message message) {
        Label label = (Label) messagesList.lookup("#message-" + message.getId()).lookup("#message-edit-label");
        label.setVisible(true);
        label.setManaged(true);
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.set(i, message);
                break;
            }
        }
    }

    private void showUserInfoDialog(User user) {
        listener.onShowUserInfo(user);
    }

    public void addListener(OnActionListener listener) {
        this.listener = listener;
    }

    public interface OnActionListener {
        void onShowUserInfo(User user);
    }

}
