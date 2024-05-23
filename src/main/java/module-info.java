module com.empresa.h2_t3_hugo_garcia_p {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires java.desktop;

    opens com.empresa.h2_t3_hugo_garcia_p to javafx.fxml;


    exports com.empresa.h2_t3_hugo_garcia_p;
}
