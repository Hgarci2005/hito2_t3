package com.empresa.h2_t3_hugo_garcia_p;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.Document;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistroController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registrarButton;

    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;

    @FXML
    private void initialize() {
        // Conexión a la base de datos MongoDB
        database = MongoClients.create("mongodb+srv://root:123@ejemplo.qyokj76.mongodb.net/").getDatabase("test");
        usersCollection = database.getCollection("login");

        registrarButton.setOnAction(this::registrarUsuario);
    }

    private void registrarUsuario(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals(confirmPassword)) {
            // Cifrado de contraseña
            String encryptedPassword = cifrarContraseña(password);

            // Insertar usuario en la base de datos
            Document userDocument = new Document("username", username)
                    .append("password", encryptedPassword);
            usersCollection.insertOne(userDocument);

            System.out.println("Usuario registrado correctamente: " + username);
            // Redirigir a otra página después del registro
            redirigirASitioEspecificado();
        } else {
            System.out.println("Las contraseñas no coinciden. Por favor, inténtalo de nuevo.");
        }
    }

    private String cifrarContraseña(String password) {
        try {
            // Crear un objeto MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Obtener el hash de la contraseña como arreglo de bytes
            byte[] hashBytes = digest.digest(password.getBytes());

            // Convertir el arreglo de bytes a una cadena hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Manejar el caso en que el algoritmo no esté disponible
            e.printStackTrace();
            return null;
        }
    }

    private void redirigirASitioEspecificado() {
        // Aquí deberías colocar el código para redirigir a la página deseada después del registro
        System.out.println("Redirigiendo a la página deseada después del registro...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Cerrar la ventana actual de registro
            Stage registroStage = (Stage) registrarButton.getScene().getWindow();
            registroStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
