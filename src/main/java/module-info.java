module Chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens InterfaceAdapter to javafx.fxml;
    exports InterfaceAdapter to javafx.graphics;
    opens FrameworkNDrivers to javafx.fxml;
    exports FrameworkNDrivers to javafx.graphics;
    opens Entities to javafx.fxml;
    exports Entities to javafx.graphics;
}