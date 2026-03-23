module com.example.com.example.medicaloffice {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.medicaloffice to javafx.fxml;
    exports com.example.medicaloffice;
}