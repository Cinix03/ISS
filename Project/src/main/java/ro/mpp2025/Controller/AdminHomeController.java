package ro.mpp2025.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Service.BugService;
import ro.mpp2025.Service.UserService;

public class AdminHomeController {

    @FXML private Label greetingLabel;

    @FXML private ListView<User> pendingUsersList;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Button assignRoleButton;

    @FXML private ListView<User> allUsersList;
    @FXML private Button removeUserButton;

    @Setter
    private UserService userService;

    /**
     * Called by Main after FXML is loaded
     */
    public void setServices(UserService userService) {
        this.userService = userService;
        loadData();
    }

    @FXML
    private void initialize() {
        // populate the role dropdown
        roleCombo.setItems(FXCollections.observableArrayList(
                Role.Tester, Role.Programmer
        ));

        assignRoleButton.setOnAction(e -> assignSelectedRole());
        removeUserButton.setOnAction(e -> removeSelectedUser());

        // customize list cell display to show name and email
        pendingUsersList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getName() + " (" + user.getEmail() + ")");
                }
            }
        });

        allUsersList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getName() + " (" + user.getEmail() + ")");
                }
            }
        });
    }

    private void loadData() {
        // load pending users (role == null)
        pendingUsersList.setItems(
                FXCollections.observableArrayList(
                        userService.getAllUsers().stream()
                                .filter(u -> u.getRole() == null)
                                .toList()
                )
        );

        // load all users
        allUsersList.setItems(
                FXCollections.observableArrayList(
                        userService.getAllUsers()
                )
        );

        greetingLabel.setText("Hello, Admin");
    }

    private void assignSelectedRole() {
        User sel = pendingUsersList.getSelectionModel().getSelectedItem();
        Role r = roleCombo.getValue();
        if (sel == null || r == null) {
            showAlert("Select a user and a role before assigning.");
            return;
        }
        userService.assignRole(sel, r);
        loadData();
    }

    private void removeSelectedUser() {
        User sel = allUsersList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Select a user to remove.");
            return;
        }
        userService.deleteUser(sel.getEmail());
        loadData();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Admin Action");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}