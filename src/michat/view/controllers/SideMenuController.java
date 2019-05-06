package michat.view.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import michat.Main;
import michat.dataaccess.api.*;
import michat.dataaccess.model.Chat;
import michat.dataaccess.model.User;
import michat.view.partials.ChatListCell;

import java.util.List;

public class SideMenuController {

    @FXML
    public ListView chatsList;

    @FXML
    public ImageView userProfileImage;

    @FXML
    public MenuButton userName;

    @FXML
    public TextField searchChats;

    @FXML
    private Button addButton;

    @FXML
    private ProgressIndicator progressIndicator;

    private ChatService chatService;
    private UserService userService;
    private List<Chat> chats;
    private OnActionListener listener;

    public SideMenuController() {
        chatService = new ChatServiceImpl();
        userService = new UserServiceImpl();
    }

    @FXML
    public void initialize() {
        chatsList.setCellFactory(param -> new ChatListCell(listener));
        MenuItem profile = new MenuItem("Moj profil");
        profile.setOnAction(event -> listener.onShowProfileDialog(Main.loggedInUser));
        MenuItem logout = new MenuItem("Odjavi se");
        logout.setOnAction(event -> listener.onLogOut());
        userName.getItems().clear();
        userName.getItems().add(profile);
        userName.getItems().add(logout);
        addButton.setOnMouseClicked(event -> listener.onAddNewChat());
        chatsList.getSelectionModel().selectedItemProperty().addListener(event -> {
            if (listener != null) {
                Chat chat = (Chat) chatsList.getSelectionModel().getSelectedItem();
                chat.setUnread(false);
                chatsList.getItems().set(chatsList.getSelectionModel().getSelectedIndex(), chat);
                listener.onChatSelected(chat);
            }
        });
        searchChats.setOnKeyReleased(event -> {
            if (event.getCode() != KeyCode.ENTER && event.getCode() != KeyCode.SHIFT && event.getCode() != KeyCode.CONTROL && event.getCode() != KeyCode.ALT) {
                chatService.search(searchChats.getText(), new BaseService.ResponseListener<List<Chat>>() {
                    @Override
                    public void onResponse(List<Chat> response) {
                        chats = response;
                        Platform.runLater(() -> chatsList.setItems(FXCollections.observableArrayList(response)));
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
            }
        });
    }

    public void onLogin(User user) {
        progressIndicator.setVisible(true);
        userProfileImage.setClip(new Circle(20, 20, 19));
        userProfileImage.setImage(new Image(userService.getProfileImageUrl(user.getProfileImage())));
        userName.setText(user.getFullname() != null ? user.getFullname() : user.getUsername());
        chatService.getAllByUser(new BaseService.ResponseListener<List<Chat>>() {
            @Override
            public void onResponse(List<Chat> response) {
                Platform.runLater(() -> {
                    chats = response;
                    chatsList.setItems(FXCollections.observableArrayList(response));
                    chatsList.getSelectionModel().select(0);
                    progressIndicator.setVisible(false);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }
        });
    }

    public void setUnread(Chat chat) {
        chat.setUnread(true);
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getId().equals(chat.getId())) {
                chatsList.getItems().remove(i);
                chats.remove(i);
                break;
            }
        }
        chatsList.getItems().add(0, chat);
        chats.add(0, chat);
    }

    public void addNewChat(Chat chat) {
        chats.add(chat);
        Platform.runLater(() -> chatsList.getItems().add(chat));
    }

    public void addListener(OnActionListener listener) {
        this.listener = listener;
    }

    public interface OnActionListener {
        void onChatSelected(Chat chat);

        void onShowProfileDialog(User user);

        void onAddNewChat();

        void onShowChatParticipants(Chat chat);

        void onChatLeave(Chat chat);

        void onLogOut();
    }
}