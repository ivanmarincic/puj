package michat.view.controllers;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import michat.Main;
import michat.dataaccess.api.AuthService;
import michat.dataaccess.api.AuthServiceImpl;
import michat.dataaccess.api.BaseService;
import michat.dataaccess.model.User;

public class LoginController {

    @FXML
    Hyperlink registerLink;

    @FXML
    Button loginButton;

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    @FXML
    GridPane loginScreen;

    private AuthService authService;
    private OnActionListener listener;

    public LoginController() {
        authService = new AuthServiceImpl();
    }

    @FXML
    public void initialize() {
        registerLink.setOnAction(event -> {
            HostServicesDelegate hostServices = HostServicesFactory.getInstance(Main.application);
            hostServices.showDocument("https://ivanmarincic.com/mi-chat/register");
        });
        passwordField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        loginButton.setOnMouseClicked(event -> login());
    }

    private void login() {
        authService.login(usernameField.getText(), passwordField.getText(), new BaseService.ResponseListener<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    Main.loggedInUser = user;
                    Platform.runLater(() -> {
                        loginScreen.setVisible(false);
                        loginScreen.setManaged(false);
                        usernameField.setText("");
                        passwordField.setText("");
                        if (listener != null) {
                            listener.onLogin(user);
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    public void addListener(OnActionListener listener) {
        this.listener = listener;
    }

    public interface OnActionListener {
        void onLogin(User user);
    }
}
