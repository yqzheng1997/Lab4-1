package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {
  @FXML
  private Button showgraphb;

  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage.setTitle("สตั้1");
    Pane myPane = (Pane) FXMLLoader.load(getClass().getResource("ffx.fxml"));
    Scene myScene = new Scene(myPane);
    primaryStage.setScene(myScene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
