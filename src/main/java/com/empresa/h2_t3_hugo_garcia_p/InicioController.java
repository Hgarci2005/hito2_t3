package com.empresa.h2_t3_hugo_garcia_p;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InicioController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button inicioButton;

    @FXML
    private Button registroButton;

    private MongoClient mongoClient;
    private MongoCollection<Document> usersCollection;

    @FXML
    private void initialize() {
        // Inicializar la conexión a la base de datos MongoDB
        mongoClient = MongoClients.create("mongodb+srv://root:123@ejemplo.qyokj76.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("test");
        usersCollection = database.getCollection("login");

        // Configurar los manejadores de eventos para los botones
        inicioButton.setOnAction(event -> handleInicio());
        registroButton.setOnAction(event -> handleRegistro());
    }

    @FXML
    private void handleInicio() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Verificar las credenciales en la base de datos
        Document query = new Document("username", username);
        Document user = usersCollection.find(query).first();

        if (user != null) {
            String hashedPasswordFromDatabase = user.getString("password");
            if (verifyPassword(password, hashedPasswordFromDatabase)) {
                // Inicio de sesión exitoso
                System.out.println("Inicio de sesión exitoso para: " + username);
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                    Stage registroStage = new Stage();
                    registroStage.setScene(new Scene(root));
                    registroStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                // Credenciales incorrectas
                System.out.println("Error de inicio de sesión. Verifica tus credenciales.");
            }
        } else {
            // Usuario no encontrado
            System.out.println("Error de inicio de sesión. Usuario no encontrado.");
        }
    }

    // Método para verificar si la contraseña proporcionada coincide con la almacenada en la base de datos
    private boolean verifyPassword(String password, String hashedPasswordFromDatabase) {
        try {
            // Calcular el hash de la contraseña proporcionada por el usuario
            String hashedPassword = cifrarContraseña(password);

            // Comparar el hash calculado con el hash almacenado en la base de datos
            return hashedPassword.equals(hashedPasswordFromDatabase);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para cifrar la contraseña
    private String cifrarContraseña(String password) throws NoSuchAlgorithmException {
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
    }

    @FXML
    private void handleRegistro() {
        // Aquí deberías colocar el código para abrir la ventana de registro
        System.out.println("Redirigiendo a la página de registro...");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("registro-view.fxml"));
            Stage registroStage = new Stage();
            registroStage.setScene(new Scene(root));
            registroStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        // Cerrar la conexión a la base de datos MongoDB al cerrar la aplicación
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
