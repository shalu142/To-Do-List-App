import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class Main extends Application {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final File taskFile = new File("tasks.txt");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do List");

        // UI Components 
        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter new task");

        Button addButton = new Button("Add");
        Button editButton = new Button("Edit Selected");
        Button removeButton = new Button("Remove Selected");
        Button saveButton = new Button("Save File"); // save button

        ListView<Task> taskList = new ListView<>(tasks);
        taskList.setCellFactory(CheckBoxListCell.forListView(Task::doneProperty));

        // Add Task 
        addButton.setOnAction(e -> {
            String text = taskInput.getText().trim();
            if (!text.isEmpty()) {
                tasks.add(new Task(text));
                taskInput.clear();
                saveTasks();
            }
        });

        //  Edit Task 
        editButton.setOnAction(e -> {
            Task selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(selected.getName());
                dialog.setTitle("Edit Task");
                dialog.setHeaderText("Edit your task:");
                dialog.setContentText("Task:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(name -> {
                    selected.setName(name);
                    taskList.refresh();
                    saveTasks();
                });
            }
        });

        // Remove Task 
        removeButton.setOnAction(e -> {
            Task selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Are you sure you want to delete this task?");
                alert.setContentText(selected.getName());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    tasks.remove(selected);
                    saveTasks();
                }
            }
        });

        //  Save File Button 
        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Task List");
            fileChooser.setInitialFileName("exported_tasks.txt");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile != null) {
                try (FileWriter writer = new FileWriter(selectedFile)) {
                    for (Task task : tasks) {
                        writer.write(task.getName() + " [" + (task.isDone() ? "Done" : "Not Done") + "]\n");
                    }
                } catch (IOException ex) {
                    showError("Failed to save file: " + ex.getMessage());
                }
            }
        });

        //  Layouts 
        HBox inputBox = new HBox(10, taskInput, addButton);
        HBox actionBox = new HBox(10, editButton, removeButton, saveButton); // save button included here
        VBox layout = new VBox(10, inputBox, taskList, actionBox);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadTasks(); // Load saved tasks
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskFile))) {
            for (Task task : tasks) {
                writer.write(task.getName() + ";" + task.isDone());
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Error saving tasks.");
        }
    }

    private void loadTasks() {
        if (!taskFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(taskFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    Task task = new Task(parts[0]);
                    task.setDone(Boolean.parseBoolean(parts[1]));
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            showError("Error loading tasks.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
