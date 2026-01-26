package com.example.smarttask_frontend.tasks.controller;

import com.example.smarttask_frontend.entity.SubTask;
import com.example.smarttask_frontend.entity.Task;
import com.example.smarttask_frontend.session.UserSession;
import com.example.smarttask_frontend.tasks.service.TaskService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.cell.ComboBoxTableCell;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private TableColumn<Task, String> subTasksColumn; // ✅ SIMPLE

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
        loadTasks();
        taskTable.setEditable(true);
        taskTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                TablePosition<?, ?> pos =
                        taskTable.getSelectionModel().getSelectedCells().isEmpty()
                                ? null
                                : taskTable.getSelectionModel().getSelectedCells().get(0);

                if (pos != null && pos.getTableColumn() == subTasksColumn) {
                    Task task = taskTable.getSelectionModel().getSelectedItem();
                    showSubTasks(task);
                }
            }
        });

    }

    private void setupColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatus())
        );
        
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(statusOptions));
        
        statusColumn.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            String newStatus = event.getNewValue();
        
            task.setStatus(newStatus); // UI update
        
            // OPTIONAL: persist to backend
            taskService.updateTaskStatus(task.getId(), newStatus);
        });

        // ✅ SIMPLE SUBTASK COUNT
        subTasksColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            int count = task.getSubTasks() == null ? 0 : task.getSubTasks().size();
            return new SimpleStringProperty(
                    count > 0 ? "View (" + count + ")" : "0"
            );
        });
    }

    private void loadTasks() {
        try {
            Long userId = UserSession.getUserId();
            List<Task> tasks = taskService.getTasksByUser(userId);
            List<List<SubTask>> list = new ArrayList<>();
            tasks.get(0).getSubTasks();

            ObservableList<Task> observableTasks =
                    FXCollections.observableArrayList(tasks);
            taskTable.setItems(observableTasks);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load tasks");
        }
    }

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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    private void showSubTasks(Task task) {
        if (task.getSubTasks() == null || task.getSubTasks().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("No Subtasks");
            alert.setContentText("This task has no subtasks.");
            alert.show();
            return;
        }

        StringBuilder content = new StringBuilder();
        for (SubTask subTask : task.getSubTasks()) {
            content.append("• ").append(subTask.getTitle()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Subtasks for: " + task.getTitle());
        alert.setContentText(content.toString());
        alert.show();
    }

    public void aiGenerate(ActionEvent actionEvent) {
    }
}
