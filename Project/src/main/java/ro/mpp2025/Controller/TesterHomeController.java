package ro.mpp2025.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Service.BugService;
import ro.mpp2025.Service.UserService;
import ro.mpp2025.Utils.Subject;

@Component
public class TesterHomeController implements Subject {
    @FXML private Label greetingLabel;
    @FXML private ListView<Bug> bugsList;

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<User> assignCombo;
    @FXML private Button addBugButton;

    private UserService userService;
    private BugService bugService;
    @Getter
    private User currentUser;

    /**
     * Inject services and load initial data
     */
    public void setServices(UserService us, BugService bs, User u) {
        this.userService = us;
        userService.addSubject(this);
        this.bugService = bs;
        bugService.addSubject(this);
        this.currentUser = u;
        loadData();
    }

    @FXML
    private void initialize() {
        greetingLabel.setText("Hello, Tester");

        // cell factory to show colored status circle + name
        bugsList.setCellFactory(list -> new ListCell<>() {
            private final HBox container = new HBox(10);
            private final Circle statusCircle = new Circle(8);
            private final Label nameLbl = new Label();
            {
                container.getChildren().addAll(statusCircle, nameLbl);
            }
            @Override
            protected void updateItem(Bug bug, boolean empty) {
                super.updateItem(bug, empty);
                if (empty || bug == null) {
                    setGraphic(null);
                } else {
                    switch (bug.getStatus()) {
                        case Pending   -> statusCircle.setFill(Color.RED);
                        case InProgress-> statusCircle.setFill(Color.ORANGE);
                        case Finished  -> statusCircle.setFill(Color.GREEN);
                        case Refused   -> statusCircle.setFill(Color.GRAY);
                    }
                    nameLbl.setText(bug.getName());
                    setGraphic(container);
                }
            }
        });

        // configure add button
        addBugButton.setOnAction(e -> addBug());
    }

    private void loadData() {
        // populate list of all bugs
        bugsList.setItems(FXCollections.observableArrayList(
                bugService.getAllBugs()
        ));
        // populate combo with programmers
        assignCombo.setItems(FXCollections.observableArrayList(
                userService.getAllUsers().stream()
                        .filter(u -> u.getRole() == Role.Programmer)
                        .toList()
        ));
    }

    private void addBug() {
        String name = nameField.getText().trim();
        String desc = descriptionField.getText().trim();
        User assignee = assignCombo.getValue();
        if (name.isBlank() || desc.isBlank()) {
            showAlert("Name and description are required.");
            return;
        }
        Bug newBug = new Bug();
        newBug.setName(name);
        newBug.setDescription(desc);
        newBug.setReportedBy(currentUser);
        newBug.setAssignedTo(assignee);
        bugService.createBug(newBug);
        clearForm();
        loadData();
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        assignCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Add Bug");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void update() {
        loadData();
    }
}