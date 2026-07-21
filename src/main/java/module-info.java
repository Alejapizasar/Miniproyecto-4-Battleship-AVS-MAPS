module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.miniproyecto4 to javafx.fxml;
    exports com.example.miniproyecto4;
}