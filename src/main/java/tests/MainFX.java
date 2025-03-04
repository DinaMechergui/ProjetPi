package tests;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
       //FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/tacheuser/User/login.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));

        Parent root = loader.load();

        // Configurer la scène
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
