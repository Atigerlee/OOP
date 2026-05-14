module com.example.oop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires org.apache.commons.text;

    opens com.example.oop to javafx.fxml;
    exports com.example.oop;
}
