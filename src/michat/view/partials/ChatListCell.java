package michat.view.partials;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import michat.Main;
import michat.dataaccess.api.UserService;
import michat.dataaccess.api.UserServiceImpl;
import michat.dataaccess.model.Chat;
import michat.dataaccess.model.ChatParticipant;
import michat.view.controllers.SideMenuController;

import java.io.IOException;

public class ChatListCell extends ListCell<Chat> {

    @FXML
    private GridPane imageView;

    @FXML
    private Label label;

    @FXML
    private Circle unread;

    @FXML
    private HBox hBox;

    private FXMLLoader fxmlLoader;
    private UserService userService;
    private SideMenuController.OnActionListener listener;

    public ChatListCell(SideMenuController.OnActionListener listener) {
        userService = new UserServiceImpl();
        this.listener = listener;
    }

    @Override
    protected void updateItem(Chat item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || item.getName() == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/michat/view/fxml/partials/chat_list_cell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            unread.setVisible(item.isUnread());
            unread.setManaged(item.isUnread());
            label.setText(String.valueOf(item.getName()));
            imageView.getChildren().clear();
            imageView.setClip(new Circle(25, 25, 24));
            int participantCount = Math.min(item.getParticipants().size() - 1, 4);
            int index = 0;
            int gridIndex = 0;
            int rowSpan = participantCount == 1 ? 2 : 1;
            for (ChatParticipant participant : item.getParticipants()) {
                if (participant.getUser().getId().equals(Main.loggedInUser.getId())) {
                    index++;
                    continue;
                }
                int colSpan = (participantCount == 3 && gridIndex == 2) || participantCount == 1 ? 2 : 1;
                int row = gridIndex / 2;
                int column = gridIndex % 2;
                double width = 50.0 / (3 - colSpan);
                double height = 50.0 / (3 - rowSpan);
                ImageView img = new ImageView();
                img.setImage(new Image(userService.getProfileImageUrl(item.getParticipants().get(index).getUser().getProfileImage())));
                img.setFitHeight(height);
                img.setFitWidth(width);
                img.setPreserveRatio(true);
                img.setSmooth(false);
                img.setCache(true);
                GridPane.setValignment(img, rowSpan == 2 ? VPos.CENTER : row == 0 ? VPos.BOTTOM : VPos.TOP);
                GridPane.setHalignment(img, colSpan == 2 ? HPos.CENTER : column == 0 ? HPos.RIGHT : HPos.LEFT);
                imageView.add(img, column, row, colSpan, rowSpan);
                gridIndex++;
                index++;
            }
            ContextMenu contextMenu = new ContextMenu();
            MenuItem leaveItem = new MenuItem("Napusti razgovor");
            leaveItem.setOnAction(event -> listener.onChatLeave(item));
            MenuItem participantsItem = new MenuItem("Učensici razgovora");
            participantsItem.setOnAction(event -> listener.onShowChatParticipants(item));
            contextMenu.getItems().addAll(participantsItem, leaveItem);
            label.setContextMenu(contextMenu);
            setText(null);
            setGraphic(hBox);
        }
    }
}
