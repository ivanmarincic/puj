package michat.view.controllers;

import borderless.BorderlessScene;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.util.HttpAuthorizer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import michat.Main;
import michat.dataaccess.api.*;
import michat.dataaccess.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private HBox windowTitleBar;

    @FXML
    private Button minimizeWindow;

    @FXML
    private Button maximizeWindow;

    @FXML
    private Button closeWindow;

    @FXML
    private VBox root;

    @FXML
    private Pane dialogContainer;

    @FXML
    private GridPane login;

    @FXML
    private SideMenuController sideMenuController;

    @FXML
    private ConversationController conversationController;

    @FXML
    private LoginController loginController;

    private UserService userService;
    private ChatService chatService;
    private ObjectMapper objectMapper;
    private Pusher pusher;
    private Chat selectedChat;

    public MainController() {
        userService = new UserServiceImpl();
        chatService = new ChatServiceImpl();
        objectMapper = new ObjectMapper();
    }

    @FXML
    public void initialize() {
        login.setVisible(true);
        loginController.addListener(user -> {
            sideMenuController.onLogin(user);
            setupPusher(user);
        });
        sideMenuController.addListener(new SideMenuController.OnActionListener() {
            @Override
            public void onChatSelected(Chat chat) {
                conversationController.onChatSelected(chat);
                selectedChat = chat;
            }

            @Override
            public void onShowProfileDialog(User user) {
                showProfileDialog(user);
            }

            @Override
            public void onAddNewChat() {
                showAddNewChatDialog();
            }

            @Override
            public void onShowChatParticipants(Chat chat) {
                showChatParticipantsDialog(chat);
            }

            @Override
            public void onChatLeave(Chat chat) {
                chatService.leave(chat);
            }

            @Override
            public void onLogOut() {
                pusher.disconnect();
                login.setVisible(true);
                login.setManaged(true);
            }
        });
        conversationController.addListener(user -> {
            showUserInfoDialog(user);
        });
    }

    private void setupPusher(User user) {
        PusherOptions pusherOptions = new PusherOptions();
        pusherOptions.setCluster("eu");
        pusherOptions.setEncrypted(true);
        pusherOptions.setAuthorizer(new HttpAuthorizer("https://ivanmarincic.com/mi-chat/api/broadcasting/auth?api_token=" + user.getApiToken()));
        pusher = new Pusher("cecb830d34ef8979a632", pusherOptions);
        pusher.connect();
        pusher.subscribePrivate("private-user." + user.getId(), new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {

            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {

            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Platform.runLater(() -> {
                    try {
                        switch (eventName) {
                            case "App\\Events\\MessageSent":
                                Message sentMessage = objectMapper.readValue(data, UserMessage.class).getMessage();
                                if (selectedChat != null && sentMessage.getChatId().equals(selectedChat.getId())) {
                                    conversationController.addMessage(sentMessage, false);
                                } else {
                                    sideMenuController.setUnread(sentMessage.getChat());
                                }
                                break;
                            case "App\\Events\\MessageUpdated":
                                Message updatedMessage = objectMapper.readValue(data, UserMessage.class).getMessage();
                                if (selectedChat != null && updatedMessage.getChat().getId().equals(selectedChat.getId())) {
                                    conversationController.updateMessage(updatedMessage);
                                } else {
                                    sideMenuController.setUnread(updatedMessage.getChat());
                                }
                                break;
                            case "App\\Events\\MessageDeleted":
                                Message deletedMessage = objectMapper.readValue(data, UserMessage.class).getMessage();
                                if (selectedChat != null && deletedMessage.getChat().getId().equals(selectedChat.getId())) {
                                    conversationController.updateMessage(deletedMessage);
                                } else {
                                    sideMenuController.setUnread(deletedMessage.getChat());
                                }
                                break;
                            case "App\\Events\\ChatCreated":
                                sideMenuController.addNewChat(objectMapper.readValue(data, UserChat.class).getChat());
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        }, "App\\Events\\MessageSent", "App\\Events\\MessageUpdated", "App\\Events\\MessageDeleted");
    }

    public void setupTitleBar(BorderlessScene scene) {
        scene.setMoveControl(windowTitleBar);
        scene.setResizable(true);
        closeWindow.setOnMouseClicked(event -> Platform.exit());
        maximizeWindow.setOnMouseClicked(event -> {
            scene.maximise();
            if (scene.isMaximised()) {
                maximizeWindow.getStyleClass().add("restore");
            } else {
                maximizeWindow.getStyleClass().remove("restore");
            }
        });
        minimizeWindow.setOnMouseClicked(event -> scene.minimise());
    }

    private void showUserInfoDialog(User user) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/michat/view/fxml/dialogs/user_info.fxml"));
        try {
            VBox dialog = loader.load();
            ImageView image = (ImageView) dialog.lookup("#image");
            Label usernameLabel = (Label) dialog.lookup("#username");
            Label fullnameLabel = (Label) dialog.lookup("#fullname");
            Label emailLabel = (Label) dialog.lookup("#email");
            image.setImage(new Image(userService.getProfileImageUrl(user.getProfileImage())));
            usernameLabel.setText(user.getUsername());
            fullnameLabel.setText(user.getFullname());
            emailLabel.setText(user.getEmail());
            dialogContainer.getChildren().clear();
            dialogContainer.getChildren().add(getDialog(dialog, "Informacije korisnika", 300, -1));
            dialogContainer.setVisible(true);
            dialogContainer.setManaged(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showChatParticipantsDialog(Chat chat) {
        ChatParticipant currentParticipant = null;
        for (ChatParticipant participant : chat.getParticipants()) {
            if (participant.getUser().getId().equals(Main.loggedInUser.getId())) {
                currentParticipant = participant;
                break;
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/michat/view/fxml/dialogs/chat_participants.fxml"));
        try {
            VBox dialog = loader.load();
            TableView table = (TableView) dialog.lookup("#table");
            TableColumn columnUsername = (TableColumn) table.getColumns().get(0);
            TableColumn columnAdmin = (TableColumn) table.getColumns().get(1);
            ProgressIndicator progress = (ProgressIndicator) dialog.lookup("#progress");
            Button save = (Button) dialog.lookup("#save");
            columnUsername.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChatParticipant, String>,
                    ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ChatParticipant, String> data) {
                    return new SimpleStringProperty(data.getValue().getUser().getUsername());
                }
            });
            columnAdmin.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChatParticipant, Boolean>,
                    ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ChatParticipant, Boolean> data) {
                    ObservableValue<Boolean> property = new SimpleBooleanProperty(data.getValue().getAdmin());
                    property.addListener((observable, oldValue, newValue) -> {
                        data.getValue().setAdmin(newValue);
                    });
                    return property;
                }
            });
            columnAdmin.setCellFactory(param -> {
                CheckBoxTableCell cell = new CheckBoxTableCell();
                cell.getStyleClass().add("center");
                return cell;
            });
            save.setOnMouseClicked(event -> {
                chatService.updatePermissions(chat, new ArrayList<>(table.getItems()), new BaseService.ResponseListener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        Platform.runLater(() -> {
                            dialogContainer.setVisible(false);
                            dialogContainer.setManaged(false);
                            dialogContainer.getChildren().clear();
                            chat.setParticipants(table.getItems());
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
            });
            if (currentParticipant != null && currentParticipant.getAdmin()) {
                columnAdmin.setEditable(true);
            }
            progress.setManaged(true);
            progress.setVisible(true);
            chatService.getParticipants(chat, new BaseService.ResponseListener<List<ChatParticipant>>() {
                @Override
                public void onResponse(List<ChatParticipant> response) {
                    Platform.runLater(() -> {
                        progress.setManaged(false);
                        progress.setVisible(false);
                        table.setItems(FXCollections.observableArrayList(response));
                    });
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
            dialogContainer.getChildren().clear();
            dialogContainer.getChildren().add(getDialog(dialog, "Učesnici razgovora", 300, -1));
            dialogContainer.setVisible(true);
            dialogContainer.setManaged(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAddNewChatDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/michat/view/fxml/dialogs/add_new_chat.fxml"));
        try {
            VBox dialog = loader.load();
            TextField name = (TextField) dialog.lookup("#name");
            TextField search = (TextField) dialog.lookup("#search");
            ListView users = (ListView) dialog.lookup("#users");
            Button save = (Button) dialog.lookup("#save");
            ProgressIndicator progress = (ProgressIndicator) dialog.lookup("#progress");
            users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            users.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getUsername() == null) {
                        setText(null);
                    } else {
                        setText(item.getUsername());
                    }
                }
            });
            search.setOnKeyReleased(event ->
            {
                progress.setVisible(true);
                progress.setManaged(true);
                userService.search(search.getText(), new BaseService.ResponseListener<List<User>>() {
                    @Override
                    public void onResponse(List<User> response) {
                        Platform.runLater(() -> {
                            List<User> selectedUsers = new ArrayList<>(users.getSelectionModel().getSelectedItems());
                            for (User selected : selectedUsers) {
                                if (!response.contains(selected)) {
                                    response.add(0, selected);
                                }
                            }
                            users.setItems(FXCollections.observableArrayList(response));
                            users.getSelectionModel().clearSelection();
                            for (User selected : selectedUsers) {
                                users.getSelectionModel().select(selected);
                            }
                            progress.setVisible(false);
                            progress.setManaged(false);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
            });
            Event.fireEvent(search, new KeyEvent(KeyEvent.KEY_RELEASED, "", "", KeyCode.ESCAPE, false, false, false, false));
            save.setOnMouseClicked(event -> chatService.save(name.getText(), users.getSelectionModel().getSelectedItems(), new BaseService.ResponseListener<Chat>() {
                @Override
                public void onResponse(Chat response) {

                }

                @Override
                public void onError(Throwable throwable) {

                }
            }));
            dialogContainer.getChildren().clear();
            dialogContainer.getChildren().add(getDialog(dialog, "Dodaj novi razgovor", 300, -1));
            dialogContainer.setVisible(true);
            dialogContainer.setManaged(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showProfileDialog(User user) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/michat/view/fxml/dialogs/profile.fxml"));
        try {
            GridPane dialog = loader.load();
            ImageView image = (ImageView) dialog.lookup("#image");
            TextField username = (TextField) dialog.lookup("#username");
            TextField fullname = (TextField) dialog.lookup("#fullname");
            TextField email = (TextField) dialog.lookup("#email");
            TextField password = (TextField) dialog.lookup("#password");
            Label passwordLabel = (Label) dialog.lookup("#password-label");
            Label passwordLabelError = (Label) dialog.lookup("#password-label-error");
            TextField passwordConfirm = (TextField) dialog.lookup("#password-confirm");
            Label passwordConfirmLabel = (Label) dialog.lookup("#password-confirm-label");
            Label passwordConfirmLabelError = (Label) dialog.lookup("#password-confirm-label-error");
            Button chanePassword = (Button) dialog.lookup("#change-password");
            Button changeImage = (Button) dialog.lookup("#change-image");
            Button save = (Button) dialog.lookup("#save");
            username.setText(user.getUsername());
            fullname.setText(user.getFullname());
            email.setText(user.getEmail());
            chanePassword.setOnMouseClicked(event -> {
                passwordLabel.setManaged(!passwordLabel.isVisible());
                passwordLabel.setVisible(!passwordLabel.isVisible());
                passwordLabelError.setManaged(false);
                passwordLabelError.setVisible(false);
                password.setManaged(!password.isVisible());
                password.setVisible(!password.isVisible());
                password.setText("");
                passwordConfirmLabel.setManaged(!passwordConfirmLabel.isVisible());
                passwordConfirmLabel.setVisible(!passwordConfirmLabel.isVisible());
                passwordConfirmLabelError.setManaged(false);
                passwordConfirmLabelError.setVisible(false);
                passwordConfirm.setManaged(!passwordConfirm.isVisible());
                passwordConfirm.setVisible(!passwordConfirm.isVisible());
                passwordConfirm.setText("");
            });
            save.setOnMouseClicked(event -> {
                User userToUpdate = new User();
                userToUpdate.setUsername(username.getText());
                userToUpdate.setFullname(fullname.getText());
                userToUpdate.setEmail(email.getText());
                boolean valid = true;
                if (password.isVisible()) {
                    if (password.getText().equals(passwordConfirm.getText())) {
                        userToUpdate.setPassword(password.getText());
                        passwordConfirmLabelError.setVisible(false);
                        passwordConfirmLabelError.setManaged(false);
                    } else {
                        passwordConfirmLabelError.setVisible(true);
                        passwordConfirmLabelError.setManaged(true);
                        passwordLabelError.setVisible(false);
                        passwordLabelError.setManaged(false);
                        valid = false;
                    }
                }
                if (valid) {
                    userService.updateProfile(userToUpdate, password.isVisible(), new BaseService.ResponseListener<User>() {
                        @Override
                        public void onResponse(User response) {
                            dialogContainer.setVisible(false);
                            dialogContainer.setManaged(false);
                            dialogContainer.getChildren().clear();
                            Main.loggedInUser = user;
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            passwordConfirmLabelError.setVisible(false);
                            passwordConfirmLabelError.setManaged(false);
                            passwordLabelError.setVisible(true);
                            passwordLabelError.setManaged(true);
                        }
                    });
                }
            });
            changeImage.setOnMouseClicked(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Odaberite sliku");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                File file = fileChooser.showOpenDialog(Main.mainStage);
                userService.setProfileImage(file);
                image.setImage(new Image(file.toURI().toString()));
            });
            image.setImage(new Image(userService.getProfileImageUrl(user.getProfileImage())));
            dialogContainer.getChildren().clear();
            dialogContainer.getChildren().add(getDialog(dialog, "Moj profil", -1, -1));
            dialogContainer.setVisible(true);
            dialogContainer.setManaged(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node getDialog(Node content, String title, double width, double height) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/michat/view/fxml/dialogs/dialog_base.fxml"));
        try {
            GridPane dialogContainer = loader.load();
            ScrollPane dialogContent = (ScrollPane) dialogContainer.lookup("#dialog-content");
            Label titleLabel = (Label) dialogContainer.lookup("#title");
            Button close = (Button) dialogContainer.lookup("#close");
            close.setOnMouseClicked(event -> {
                this.dialogContainer.setVisible(false);
                this.dialogContainer.setManaged(false);
                this.dialogContainer.getChildren().clear();
            });
            titleLabel.setText(title);
            VBox dialogContentContainer = (VBox) dialogContent.getContent();
            dialogContentContainer.getChildren().clear();
            dialogContentContainer.getChildren().add(content);
            GridPane.setColumnIndex(dialogContainer, 1);
            GridPane.setRowIndex(dialogContainer, 1);
            GridPane.setColumnSpan(dialogContainer, 1);
            GridPane.setRowSpan(dialogContainer, 1);
            GridPane.setHalignment(dialogContainer, HPos.CENTER);
            GridPane.setValignment(dialogContainer, VPos.CENTER);
            if (width != -1) {
                dialogContainer.setMaxWidth(width);
            }
            if (height != -1) {
                dialogContainer.setMaxHeight(height);
            }
            return dialogContainer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
