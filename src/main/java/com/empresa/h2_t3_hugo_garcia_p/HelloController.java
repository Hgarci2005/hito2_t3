package com.empresa.h2_t3_hugo_garcia_p;

import com.mongodb.Block;
import com.mongodb.client.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import org.bson.Document;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.function.Consumer;

public class HelloController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField correoField;
    @FXML
    private TextField edadField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> searchField;
    @FXML
    private TableView<Usuario> tableView;
    @FXML
    private TableColumn<Usuario, String> nombreColumn;
    @FXML
    private TableColumn<Usuario, String> apellidosColumn;
    @FXML
    private TableColumn<Usuario, String> correoColumn;
    @FXML
    private TableColumn<Usuario, Integer> edadColumn;

    private MongoCollection<Document> collection;

    public static class Usuario {
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty apellidos;
        private final SimpleStringProperty correo;
        private final SimpleIntegerProperty edad;

        public Usuario(String nombre, String apellidos, String correo, int edad) {
            this.nombre = new SimpleStringProperty(nombre);
            this.apellidos = new SimpleStringProperty(apellidos);
            this.correo = new SimpleStringProperty(correo);
            this.edad = new SimpleIntegerProperty(edad);
        }

        public String getNombre() {
            return nombre.get();
        }

        public void setNombre(String nombre) {
            this.nombre.set(nombre);
        }

        public String getApellidos() {
            return apellidos.get();
        }

        public void setApellidos(String apellidos) {
            this.apellidos.set(apellidos);
        }

        public String getCorreo() {
            return correo.get();
        }

        public void setCorreo(String correo) {
            this.correo.set(correo);
        }

        public int getEdad() {
            return edad.get();
        }

        public void setEdad(int edad) {
            this.edad.set(edad);
        }
    }

    @FXML
    public void initialize() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://root:123@ejemplo.qyokj76.mongodb.net/");
            MongoDatabase database = mongoClient.getDatabase("test");

            if (!database.listCollectionNames().into(new ArrayList<>()).contains("usuarios")) {
                database.createCollection("usuarios");
                resetButton.setOnAction(event -> handleReset());

            }

            collection = database.getCollection("usuarios");

            // Configurar las columnas de la tabla
            nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            apellidosColumn.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
            correoColumn.setCellValueFactory(new PropertyValueFactory<>("correo"));
            edadColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));

            // Cargar los datos en la tabla
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdd() {
        try {
            String nombre = nombreField.getText();
            String apellidos = apellidosField.getText();
            String correo = correoField.getText();
            int edad = Integer.parseInt(edadField.getText());

            // Crear un documento con los datos del nuevo usuario
            Document nuevoUsuario = new Document("nombre", nombre)
                    .append("apellidos", apellidos)
                    .append("correo", correo)
                    .append("edad", edad);

            // Insertar el nuevo usuario en la colección
            collection.insertOne(nuevoUsuario);

            // Actualizar la tabla
            loadData();

            showAlert("Usuario agregado correctamente.");
        } catch (NumberFormatException e) {
            showAlert("Por favor, introduce una edad válida.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al agregar usuario.");
        }
    }

    @FXML
    private void handleUpdate() {
        // Obtener el usuario seleccionado en la tabla
        Usuario usuarioSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            showAlert("Por favor, selecciona un usuario para actualizar.");
            return;
        }

        try {
            // Obtener los nuevos valores de los campos de texto
            String nuevoNombre = nombreField.getText();
            String nuevosApellidos = apellidosField.getText();
            String nuevoCorreo = correoField.getText();
            int nuevaEdad = Integer.parseInt(edadField.getText());

            // Actualizar el documento en la base de datos con los nuevos valores
            Document filtro = new Document("nombre", usuarioSeleccionado.getNombre())
                    .append("apellidos", usuarioSeleccionado.getApellidos());
            Document nuevosValores = new Document("$set", new Document("nombre", nuevoNombre)
                    .append("apellidos", nuevosApellidos)
                    .append("correo", nuevoCorreo)
                    .append("edad", nuevaEdad));
            collection.updateOne(filtro, nuevosValores);

            // Actualizar la tabla
            loadData();

            showAlert("Usuario actualizado correctamente.");
        } catch (NumberFormatException e) {
            showAlert("Por favor, introduce una edad válida.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al actualizar usuario.");
        }
    }

    @FXML
    private void handleDelete() {
        // Obtener el usuario seleccionado en la tabla
        Usuario usuarioSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            showAlert("Por favor, selecciona un usuario para eliminar.");
            return;
        }

        try {
            // Eliminar el documento de la base de datos
            Document filtro = new Document("nombre", usuarioSeleccionado.getNombre())
                    .append("apellidos", usuarioSeleccionado.getApellidos());
            collection.deleteOne(filtro);

            // Actualizar la tabla
            loadData();

            showAlert("Usuario eliminado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error al eliminar usuario.");
        }
    }



    @FXML
    private Button resetButton;

    @FXML
    private void handleReset() {
        // Llamar al método loadData() para cargar todos los datos nuevamente en la tabla
        loadData();
    }


    private void loadData() {
        try {
            ObservableList<Usuario> data = FXCollections.observableArrayList();

            collection.find().forEach((Consumer<? super Document>) document -> {
                String nombre = document.getString("nombre");
                String apellidos = document.getString("apellidos");
                String correo = document.getString("correo");
                int edad = document.getInteger("edad");

                data.add(new Usuario(nombre, apellidos, correo, edad));
            });

            tableView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSearch() {
        try {
            String searchTerm = searchField.getValue();
            ObservableList<Usuario> searchData = FXCollections.observableArrayList();

            if (searchTerm != null && !searchTerm.isEmpty()) {
                if ("Nombre".equals(searchTerm)) {
                    String searchValue = nombreField.getText().trim(); // Obtener el valor de búsqueda del campo de nombre

                    // Verificar si el campo de búsqueda por nombre está vacío
                    if (!searchValue.isEmpty()) {
                        // Construir un filtro basado en el nombre
                        Document filtro = new Document("nombre", searchValue);

                        // Buscar documentos que coincidan con el filtro en la colección
                        FindIterable<Document> result = collection.find(filtro);
                        result.forEach((Consumer<? super Document>) document -> {
                            String nombre = document.getString("nombre");
                            String apellidos = document.getString("apellidos");
                            String correo = document.getString("correo");
                            int edad = document.getInteger("edad");

                            searchData.add(new Usuario(nombre, apellidos, correo, edad));
                        });
                    }
                } else if ("Edad".equals(searchTerm)) {
                    String searchValue = edadField.getText().trim(); // Obtener el valor de búsqueda del campo de edad

                    // Verificar si el campo de búsqueda por edad está vacío y es un número válido
                    if (!searchValue.isEmpty() && searchValue.matches("\\d+")) {
                        int edad = Integer.parseInt(searchValue);

                        // Construir un filtro basado en la edad
                        Document filtro = new Document("edad", edad);

                        // Buscar documentos que coincidan con el filtro en la colección
                        FindIterable<Document> result = collection.find(filtro);
                        result.forEach((Consumer<? super Document>) document -> {
                            String nombre = document.getString("nombre");
                            String apellidos = document.getString("apellidos");
                            String correo = document.getString("correo");
                            int edadUsuario = document.getInteger("edad");

                            searchData.add(new Usuario(nombre, apellidos, correo, edadUsuario));
                        });
                    }
                }

                // Establecer los datos de búsqueda en la tabla
                tableView.setItems(searchData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
