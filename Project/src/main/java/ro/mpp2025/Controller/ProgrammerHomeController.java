package ro.mpp2025.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ro.mpp2025.Domain.Bug;
import ro.mpp2025.Domain.Status;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Service.BugService;
import ro.mpp2025.Service.UserService;
import ro.mpp2025.Utils.Subject;

public class ProgrammerHomeController implements Subject {

    @FXML private Label greetingLabel;

    @FXML private ComboBox<Status> filterCombo;
    @FXML private ListView<Bug> allBugsList;
    @FXML private Button assignButton;

    @FXML private ListView<Bug> myBugsList;
    @FXML private ComboBox<Status> statusCombo;
    @FXML private Button updateStatusButton;

    private UserService userService;
    private BugService bugService;
    private User currentUser;

    /**
     * Inject services and current user, then load data
     */
    public void setServices(UserService us, BugService bs, User currentUser) {
        this.userService = us;
        userService.addSubject(this);
        this.bugService = bs;
        bugService.addSubject(this);
        this.currentUser = currentUser;
        greetingLabel.setText("Hello, " + currentUser.getRole());
        loadData();
    }

    @FXML
    private void initialize() {
        // set up filter ComboBox (for allBugsList)
        filterCombo.setItems(FXCollections.observableArrayList(Status.values()));
        filterCombo.setOnAction(e -> applyFilter());

        // set up status ComboBox (for updating status)
        statusCombo.setItems(FXCollections.observableArrayList(Status.values()));

        // cellFactory for coloring status circles in both lists
        allBugsList.setCellFactory(list -> new BugCell());
        myBugsList .setCellFactory(list -> new BugCell());

        assignButton.setOnAction(e -> assignToMe());
        updateStatusButton.setOnAction(e -> updateStatus());
    }

    private void loadData() {
        applyFilter();
        refreshMyBugs();
    }

    private void applyFilter() {
        Status selected = filterCombo.getValue();
        if (selected == null) {
            allBugsList.setItems(
                    FXCollections.observableArrayList(bugService.getAllBugs())
            );
        } else {
            allBugsList.setItems(
                    FXCollections.observableArrayList(
                            bugService.getAllBugs().stream()
                                    .filter(b -> b.getStatus() == selected)
                                    .toList()
                    )
            );
        }
    }

    private void refreshMyBugs() {
        myBugsList.setItems(
                FXCollections.observableArrayList(
                        bugService.getAllBugs().stream()
                                .filter(b -> b.getAssignedTo() != null &&
                                        b.getAssignedTo().equals(currentUser))
                                .toList()
                )
        );
    }

    private void assignToMe() {
        Bug sel = allBugsList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Select a bug to take.");
            return;
        }
        bugService.assignBug(sel.getId(), currentUser, currentUser);
        loadData();
    }

    private void updateStatus() {
        Bug sel = myBugsList.getSelectionModel().getSelectedItem();
        Status newStatus = statusCombo.getValue();
        if (sel == null || newStatus == null) {
            showAlert("Select a bug and a new status.");
            return;
        }
        bugService.changeStatus(sel.getId(), currentUser, newStatus);
        loadData();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Programmer Action");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void update() {
        loadData();
    }

    // Cell implementation to show colored circle + bug name
    private static class BugCell extends ListCell<Bug> {
        private final HBox container = new HBox(10);
        private final Circle circle = new Circle(6);
        private final Label label = new Label();
        {
            container.getChildren().addAll(circle, label);
        }
        @Override
        protected void updateItem(Bug bug, boolean empty) {
            super.updateItem(bug, empty);
            if (empty || bug == null) {
                setGraphic(null);
            } else {
                switch (bug.getStatus()) {
                    case Pending    -> circle.setFill(Color.RED);
                    case InProgress -> circle.setFill(Color.ORANGE);
                    case Finished   -> circle.setFill(Color.GREEN);
                    case Refused    -> circle.setFill(Color.GRAY);
                }
                label.setText(bug.getName());
                setGraphic(container);
            }
        }
    }
}