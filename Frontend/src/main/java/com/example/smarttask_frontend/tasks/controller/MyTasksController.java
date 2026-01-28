package com.example.smarttask_frontend.tasks.controller;

import com.example.smarttask_frontend.entity.Task;
import com.example.smarttask_frontend.session.UserSession;
import com.example.smarttask_frontend.subtasks.controller.SubtaskController;
import com.example.smarttask_frontend.tasks.service.TaskService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MyTasksController implements Initializable {

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> titleColumn;

    @FXML
    private TableColumn<Task, String> priorityColumn;

    @FXML
    private TableColumn<Task, String> dueDateColumn;

    @FXML
    private TableColumn<Task, String> statusColumn;

    @FXML
    private TableColumn<Task, Void> shareColumn;

    // 🔹 BUTTON COLUMN
    @FXML
    private TableColumn<Task, Void> subTasksColumn;

    private final TaskService taskService = new TaskService();

    private final ObservableList<String> statusOptions =
            FXCollections.observableArrayList(
                    "TODO",
                    "IN_PROGRESS",
                    "DONE"
            );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupColumns();
        setupSubtaskButtonColumn();
        setupShareButtonColumn();
        loadTasks();
        taskTable.setEditable(true);
    }

    // =========================
    // TABLE COLUMNS
    // =========================
    private void setupColumns() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus())
        );

        statusColumn.setCellFactory(
                ComboBoxTableCell.forTableColumn(statusOptions)
        );

        statusColumn.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            String newStatus = event.getNewValue();
            task.setStatus(newStatus);
            taskService.updateTaskStatus(task.getId(), newStatus);
        });
    }

    // =========================
    // SUBTASK BUTTON COLUMN
    // =========================
    private void setupSubtaskButtonColumn() {

        subTasksColumn.setCellFactory(col -> new TableCell<>() {

            private final Button button = new Button("Show Subtasks");

            {
                button.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openSubtasksWindow(task.getId(), task.getTitle());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Task task = getTableView().getItems().get(getIndex());

                // ✅ SHOW BUTTON ONLY IF SUBTASKS EXIST
                if (taskHasSubtasks(task)) {
                    setGraphic(button);
                } else {
                    setGraphic(null);
                }
            }
        });
    }
    private void setupShareButtonColumn() {

        shareColumn.setCellFactory(col -> new TableCell<>() {
    
            private final Button shareButton = new Button("Share");
    
            {
                shareButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    openShareTaskDialog(task);
                });
            }
    
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(shareButton);
                }
            }
        });
    }

    // =========================
    // SUBTASK CONDITION
    // =========================
    private boolean taskHasSubtasks(Task task) {
        return task.getSubTasks() != null && !task.getSubTasks().isEmpty();
    }

    // =========================
    // OPEN SUBTASK WINDOW
    // =========================
    private void openSubtasksWindow(Long taskId, String taskTitle) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/views/Subtasks.fxml"));

            Parent root = loader.load();

            SubtaskController controller = loader.getController();
            controller.setTaskId(taskId);

            Stage stage = new Stage();
            stage.setTitle("Subtasks - " + taskTitle);
            stage.setScene(new Scene(root, 800, 500));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to open Subtasks window");
        }
    }

    // =========================
    // LOAD TASKS
    // =========================
    private void loadTasks() {
        try {
            Long userId = UserSession.getUserId();
            List<Task> tasks = taskService.getTasksByUser(userId);

            ObservableList<Task> observableTasks =
                    FXCollections.observableArrayList(tasks);

            taskTable.setItems(observableTasks);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load tasks");
        }
    }

    // =========================
    // CREATE TASK
    // =========================
    @FXML
    private void createTask() throws IOException {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/views/CreateTaskView.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Create Task");
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    public void aiGenerate() {
        // Future AI logic
    }

    // =========================
    // ERROR HANDLING
    // =========================
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void openShareTaskDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ShareTaskView.fxml"));
            Parent root = loader.load();
    
            ShareTaskController controller = loader.getController();
            controller.setTaskId(task.getId(), task.getTitle());
    
            Stage stage = new Stage();
            stage.setTitle("Share Task - " + task.getTitle());
            stage.setScene(new Scene(root, 350, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to open Share Task window");
        }
    }

}
