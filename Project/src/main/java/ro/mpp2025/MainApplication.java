package ro.mpp2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApplication extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        // Pornim Spring Boot (fără web server), cu headless=false pentru JavaFX
        springContext = new SpringApplicationBuilder(MainApplication.class)
                .headless(false)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Încarc FXML-ul şi spun loader-ului să ia controller-ul din Spring
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_signup.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.setTitle("BugTracker – Login / Sign Up");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
    }

    public static void main(String[] args) {
        // JavaFX launch va apela init(), start() şi, la final, stop()
        launch(args);
    }
}
