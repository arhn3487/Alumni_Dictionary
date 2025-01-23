module com.company.alumniloginpage {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.company.alumniloginpage to javafx.fxml;
    exports com.company.alumniloginpage;
}