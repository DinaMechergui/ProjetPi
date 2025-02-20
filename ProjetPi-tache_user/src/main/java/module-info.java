module tn.esprit.tacheuser {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires bcrypt;

    opens tn.esprit.tacheuser to javafx.fxml;
    exports tn.esprit.tacheuser;
    exports tn.esprit.tacheuser.contoller;
    opens tn.esprit.tacheuser.contoller to javafx.fxml;
}