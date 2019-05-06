package michat;

import borderless.BorderlessScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import michat.dataaccess.model.User;
import michat.view.controllers.MainController;
import okhttp3.OkHttpClient;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends javafx.application.Application {

    public static Application application;
    public static User loggedInUser;
    public static Stage mainStage;

    public static void main(String[] args) {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        application = this;
        mainStage = stage;
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("view/fxml/main.fxml"));
        Parent root = rootLoader.load();
        BorderlessScene scene = new BorderlessScene(stage, root);
        scene.getStylesheets().add(getClass().getResource("view/fxml/style.css").toExternalForm());
        MainController mainController = rootLoader.getController();
        mainController.setupTitleBar(scene);
        stage.setTitle("MI Chat");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
}
