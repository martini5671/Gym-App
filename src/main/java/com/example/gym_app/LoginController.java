package com.example.gym_app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;

public class LoginController {
    @FXML
    private Label welcomeText;
    @FXML
    private Label errors_label;
    @FXML
    private Button login_button;
    @FXML
    private Button register_button;
    @FXML
    private Button reset_password;
    @FXML
    private TextField email_input;
    @FXML
    private PasswordField password_input;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToRegisterScene(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("register.fxml")));
        stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void clickLoginButton (ActionEvent actionEvent) throws SQLException, NoSuchAlgorithmException {
        if (email_input.getLength() == 0 && password_input.getLength() == 0) {
            errors_label.setTextFill(Color.RED);
            errors_label.setText("Nie podano danych logowania.");
        } else if (email_input.getLength() == 0) {
            errors_label.setTextFill(Color.RED);
            errors_label.setText("Nie podano maila.");
        } else if (password_input.getLength() == 0) {
            errors_label.setTextFill(Color.RED);
            errors_label.setText("Nie podano hasła.");
        }
        else
        {
            validateLogin();
        }
        // Fade away error labels after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            errors_label.setText("");
        }));
        timeline.play();

    }

    public void validateLogin() throws SQLException, NoSuchAlgorithmException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        String getCredentialsQuery ="SELECT imie_klienta, nazwisko_klienta" +
                " FROM tbl_klienci" +
                " WHERE haslo_sha256 = ? AND email = ?";

        PreparedStatement statement = connection.prepareStatement(getCredentialsQuery);
        statement.setString(1, Encryption.getSHA256fromString(password_input.getText()));
        statement.setString(2, email_input.getText());

        ResultSet result = statement.executeQuery();

        if (result.next()) {
            do {
                String name = result.getString(1);
                String surname = result.getString(2);
                errors_label.setTextFill(Color.GREEN);
                errors_label.setText("Witaj " + name + " " + surname+"!");
            } while(result.next());
        } else {
            errors_label.setTextFill(Color.RED);
            errors_label.setText("Wprowadzono niepoprawne dane logowania!");
        }
        statement.close();
        connection.close();
    }
}