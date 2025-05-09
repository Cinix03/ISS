package ro.mpp2025.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Domain.Role;
import ro.mpp2025.Service.BugService;
import ro.mpp2025.Service.UserService;

import java.io.IOException;
import java.net.URL;

public class LoginSignUpController {

    @FXML private Label titleLabel;
    @FXML private Hyperlink switchLink;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button actionButton;

    private UserService userService;
    private BugService bugService;
    private boolean isLoginMode = false;

    public LoginSignUpController() {
        // You might inject UserService via manual DI
    }

    public void setServices(UserService us, BugService bs){
        userService = us;
        bugService = bs;
    }

    @FXML
    public void initialize() {
        updateUI();
        switchLink.setOnAction(e -> toggleMode());
        actionButton.setOnAction(e -> handleAction());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        updateUI();
    }

    private void updateUI() {
        if (isLoginMode) {
            titleLabel.setText("Login");
            nameField.setVisible(false);
            switchLink.setText("New user? Sign up");
            actionButton.setText("Login");
        } else {
            titleLabel.setText("Create new Account");
            nameField.setVisible(true);
            switchLink.setText("Already Registered? Login");
            actionButton.setText("Sign up");
        }
    }

    private void handleAction() {
        String email = emailField.getText().trim();
        String pass = passwordField.getText().trim();

        try {
            if (isLoginMode) {
                User user = userService.login(email, pass);
                if(!user.isActivated()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Cont neactivat");
                    alert.setHeaderText(null);
                    alert.setContentText("Contul nu a fost încă activat de administrator.");
                    alert.showAndWait();
                    return;  // oprește flow-ul de login
                }
                else if (user.getRole() == Role.Admin) {
                    URL fxmlUrl = getClass().getResource("/admin_home.fxml");
                    System.out.println("admin_home.fxml URL = " + fxmlUrl);
                    if (fxmlUrl == null) {
                        throw new IllegalStateException("Cannot find /admin_home.fxml on classpath");
                    }


                    FXMLLoader loader = new FXMLLoader(fxmlUrl);
                    Parent root = loader.load();


                    AdminHomeController ctrl = loader.getController();
                    ctrl.setServices(userService);

                    Stage newStage = new Stage();
                    newStage.setScene(new Scene(root, 650, 450));
                    newStage.setTitle(user.getRole() + " Home - " + user.getName());
                    newStage.show();

                }
                else if(user.getRole() == Role.Tester)
                {
                    URL fxmlUrl = getClass().getResource("/tester_home.fxml");
                    System.out.println("tester_home.fxml URL = " + fxmlUrl);
                    if (fxmlUrl == null) {
                        throw new IllegalStateException("Cannot find /admin_home.fxml on classpath");
                    }


                    FXMLLoader loader = new FXMLLoader(fxmlUrl);
                    Parent root = loader.load();


                    TesterHomeController ctrl = loader.getController();
                    ctrl.setServices(userService, bugService, user);

                    Stage newStage = new Stage();
                    newStage.setScene(new Scene(root, 650, 450));
                    newStage.setTitle(user.getRole() + " Home - " + user.getName());
                    newStage.show();

                }
                else if(user.getRole() == Role.Programmer)
                {
                    URL fxmlUrl = getClass().getResource("/programmer_home.fxml");
                    System.out.println("programmer_home.fxml URL = " + fxmlUrl);
                    if (fxmlUrl == null) {
                        throw new IllegalStateException("Cannot find /programmer_home.fxml on classpath");
                    }


                    FXMLLoader loader = new FXMLLoader(fxmlUrl);
                    Parent root = loader.load();


                    ProgrammerHomeController ctrl = loader.getController();
                    ctrl.setServices(userService, bugService, user);

                    Stage newStage = new Stage();
                    newStage.setScene(new Scene(root, 650, 450));
                    newStage.setTitle(user.getRole() + " Home - " + user.getName());
                    newStage.show();
                }

            } else {
                String name = nameField.getText().trim();
                User newUser = new User();
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setPassword(pass);
                newUser.setActivated(false);
                userService.createUser(newUser);
                toggleMode();
            }
        } catch (RuntimeException ex) {
            // show error to user
            System.err.println(ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Credentiale gresite.");
            alert.setHeaderText(null);
            alert.setContentText("Credentiale gresite.");
            alert.showAndWait();
            return;  // oprește flow-ul de login
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}