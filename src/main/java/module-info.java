module com.example.gym_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.gym_app to javafx.fxml;
    exports com.example.gym_app;
}