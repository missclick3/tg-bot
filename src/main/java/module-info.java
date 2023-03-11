module com.example.finalbot {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires telegrambots;
    requires telegrambots.meta;

    opens com.example.finalbot to javafx.fxml;
    exports com.example.finalbot;
}