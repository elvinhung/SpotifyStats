package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FXMLLoginController implements Initializable {
    private double x,y = 0;
    @FXML private Button loginButton;
    @FXML private Text loginFailed;

    @FXML
    void dragged (MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed (MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }


    @FXML
    void close(MouseEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void maximize(MouseEvent event){
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        if (stage.isFullScreen()){
            stage.setFullScreen(false);
        }
        else {
            stage.setFullScreen(true);
        }
    }

    @FXML
    void minimize(MouseEvent event){
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        loginFailed.setVisible(false);
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                try {
                    loginFailed.setVisible(false);
                    Stage thisStage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    Stage stage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml_files/FXMLWebEngine.fxml"));
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/stylesheets/SpotifyWebEngineStyles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setY(thisStage.getY() + 35);
                    stage.setX(thisStage.getX() + 35);

                    stage.initStyle(StageStyle.UNDECORATED);
                    ResizeHelper.addResizeListener(stage, 500, 600, 4000, 2000);

                    stage.show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        loginButton.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                loginButton.fire();
                ev.consume();
            }
        });
        API_Data.loginFailure.textProperty().addListener((observable, oldValue, newValue) -> {
            Pattern pattern = Pattern.compile("login failed");
            Matcher matcher = pattern.matcher(newValue);
            if (matcher.find()) {
                loginFailed.setVisible(true);
            }
        });
        API_Data.mainScene.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Parent mainScene = FXMLLoader.load(getClass().getResource("/fxml_files/FXMLMain.fxml"));
                Scene scene = new Scene(mainScene);
                scene.getStylesheets().add(getClass().getResource("/stylesheets/SpotifyStyles.css").toExternalForm());
                stage.setScene(scene);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}

