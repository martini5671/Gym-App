package com.example.gym_app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class RegistrationController implements Initializable {
    // input boxes
    @FXML
    private TextField name_input;

    @FXML
    private TextField surname_input;

    @FXML
    private TextField address_input;

    @FXML
    private TextField email_input;

    @FXML
    private TextField age_input;

    @FXML
    private TextField phone_input;

    @FXML
    private PasswordField password1_input;

    @FXML
    private PasswordField password2_input;

    @FXML
    private ChoiceBox<String> gender_dropdown;

    private final String[] input_names_array = {"imię", "nazwisko", "adres", "mail", "numer telefonu","wiek" ,"płeć","hasło", "powtórz hasło"};


    // input boxes
    private Stage stage;
    private Scene scene;
    private Parent root;


    private final String[] gender_choices = {"kobieta", "mężczyzna","inna"};

    public void switchToLoginScene(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gender_dropdown.getItems().addAll(gender_choices);
    }

    public boolean accountAlreadyExists() throws SQLException, NoSuchAlgorithmException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        String getCredentialsQuery ="SELECT imie_klienta, nazwisko_klienta" +
                " FROM tbl_klienci" +
                " WHERE haslo_sha256 = ? AND email = ?";

        PreparedStatement statement = connection.prepareStatement(getCredentialsQuery);
        statement.setString(1, Encryption.getSHA256fromString(password1_input.getText()));
        statement.setString(2, email_input.getText());

        ResultSet result = statement.executeQuery();

        if (result.next()) {
            statement.close();
            connection.close();
            return true;
        } else {
            statement.close();
            connection.close();
            return false;
        }

    }
    public int executeInsertion()
    {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        try {

            // make some inserts
            String insertQuery = "INSERT INTO tbl_klienci" +
                    "(imie_klienta, nazwisko_klienta, adres, telefon," +
                    "email, wiek, plec, data_dolaczenia, " +
                    "haslo_sha256) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name_input.getText());
            preparedStatement.setString(2, surname_input.getText());
            preparedStatement.setString(3, address_input.getText());
            preparedStatement.setString(4, phone_input.getText());
            preparedStatement.setString(5, email_input.getText());
            preparedStatement.setInt(6, Integer.parseInt(age_input.getText()));
            preparedStatement.setInt(7, convertGenderToInt(gender_dropdown.getValue()));
            // current_date
            Date date = Date.valueOf(LocalDate.now());
            preparedStatement.setDate(8, date);
            // encrypt password:
            preparedStatement.setString(9, Encryption.getSHA256fromString(password1_input.getText()));
            System.out.println(password1_input.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            System.out.println(e.getErrorCode());
            System.out.println("SQL Exception: " + e.getMessage());
            return e.getErrorCode();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    private int convertGenderToInt(String gender)
    {
        if(gender.equals("mężczyzna")) {
            return 1;

        } else if (gender.equals("kobieta")) {
            return 2;
        }
        else
        {
            return 3;
        }
    }

    public void register() throws SQLException, NoSuchAlgorithmException, IOException {

        String name = name_input.getText();
        String surname = surname_input.getText();
        String address = address_input.getText();
        String email = email_input.getText();
        String age = age_input.getText();
        String phone = phone_input.getText();
        String password1 = password1_input.getText();
        String password2 = password2_input.getText();
        String gender = gender_dropdown.getValue();

        String[] input_data = {name, surname, address, email, age, phone,gender, password1, password2 };

        String[][] inputs_array = new String[9][2];
        for (int i = 0; i < inputs_array.length; i++) {
            inputs_array[i][0] = input_names_array[i];
            inputs_array[i][1] = input_data[i];
        }

        ArrayList<String> empty_fields_array = new ArrayList<>();

        for (int i = 0; i < inputs_array.length; i++) {
            if (inputs_array[i][1] == null || inputs_array[i][1].trim().isEmpty())
            {
                empty_fields_array.add(inputs_array[i][0]);
            }
        }
        if(empty_fields_array.isEmpty())
        {
            //here all the fields are non-empty
            //check if passwords are the same
            System.out.println("check passwords");
            if(!password1_input.getText().equals(password2_input.getText()))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText("Niezgodne hasła!");
                alert.setContentText("Wprowadź takie same hasła");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
            //then check if the mail and password already exists or not with select
            else if(accountAlreadyExists())
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText("Podane konto już istnieje!");
                alert.setContentText("Konto z podanymi danymi logowania już istnieje w systemie.");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
            // check length of phone number
            else if (phone_input.getText().length() != 9) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setContentText("Wprowadzono niepoprawny numer telefonu");
                alert.show();
            } else if (parseStringToInt(age_input.getText()) ==0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText("Wprowadzono nieprawidłowe dane");
                alert.setContentText("Wprowadź poprawną wartość swojego wieku");
                alert.showAndWait();
            } else
            {
                System.out.println("perform insert");
                int status_code = executeInsertion();
                if(status_code == 0)
                {
                    System.out.println("Show finish screen");
                    switchToSuccessfulRegistration();
                }
                else
                {
                    //show error
                    // alert
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Wprowadzono niepoprawne dane!");
                    alert.setContentText("Nastąpił błąd podczas procesu rejestracji."+
                            " Istnieje możliwość że email albo hasło jest już w użyciu." +
                            " Sprawdź również czy informacje odnośnie twojego wieku lub mailu są poprawne."+
                            " Osoby niepełnoletnie nie mogą się zarejestrować.");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                }
            }
        }
        else{
            String string1 = "Nie podano żadnych wartości w następujących polach: ";
            String list_empty_fields = String.join(", ",empty_fields_array);
            String string2 = ". Wprowadź dane we wszystkich polach!";
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nie wypełniono wszystkich pól!");
            alert.setContentText(string1 + list_empty_fields + string2);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }


    }
    public int parseStringToInt(String str) {
        int num = 0;
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // Display warning in JavaFX

        }
        return num;
    }

    public void switchToSuccessfulRegistration() throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("successful_registration.fxml")));
        stage = (Stage) phone_input.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

}
