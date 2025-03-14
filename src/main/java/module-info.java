module com.company.alumniloginpage {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires java.mail;
    requires activation;
    requires org.apache.pdfbox;

    opens com.company.alumniloginpage to javafx.fxml;
    exports com.company.alumniloginpage;
}