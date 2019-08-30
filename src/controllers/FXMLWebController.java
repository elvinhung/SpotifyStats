package controllers;

import controllers.API_Data;
import javafx.animation.FadeTransition;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FXMLWebController implements Initializable {

    private double x,y = 0;
    private Label currLink = new Label();
    @FXML private WebView webView;
    @FXML private Text loading;

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

    private void closeStage(){
        Stage stage = (Stage) webView.getScene().getWindow();
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
        webView.setVisible(false);
        webView.getEngine().load(API_Data.accessURL);
        webView.setZoom(0.8);
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED.equals(newValue)) {
                currLink.setText(webView.getEngine().getLocation());
                if (!webView.isVisible()){
                    loading.setVisible(false);
                    webView.setVisible(true);
                    FadeTransition webFade = new FadeTransition(Duration.seconds(1.0), webView);
                    webFade.setFromValue(0.0);
                    webFade.setToValue(1.0);
                    webFade.play();
                }
            }
        });
        currLink.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            Pattern pattern = Pattern.compile("code=(.*?)&");
            Matcher matcher = pattern.matcher(newValue);
            //user accepted access
            if (matcher.find()){
                API_Data.codeToken = matcher.group(1);
                System.out.println(API_Data.codeToken);
                if (API_Data.requestAccessToken() != 200){
                    API_Data.loginFailure.setText("login failed");
                }
                else {
                    API_Data.loginFailure.setText("login succeeded");
                }
                API_Data.mainScene.setText("main scene");
                closeStage();
            }
            //user denied access

        });
    }
}
