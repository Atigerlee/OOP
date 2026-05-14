module com.example.oop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;      // 允許連接資料庫
    requires javafx.media;  // 允許播放音樂

    opens com.example.oop to javafx.fxml;
    exports com.example.oop;
}