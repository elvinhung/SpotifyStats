package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Stack;

public class FXMLMainController implements Initializable {
    private boolean controlsHidden = true;
    private boolean isPressed = false;
    private double x,y = 0;
    private Image playPng = new Image(getClass().getResourceAsStream("../images/play.png"));
    private Image pausePng = new Image(getClass().getResourceAsStream("../images/pause.png"));
    private Image nextPng = new Image(getClass().getResourceAsStream("../images/next.png"));
    private Image prevPng = new Image(getClass().getResourceAsStream("../images/prev.png"));
    private ImageView toggleControl = new ImageView();
    @FXML private ProgressBar progressBar;
    @FXML private Slider slider;
    @FXML private StackPane detector;
    @FXML private VBox controlBox;
    @FXML private AnchorPane anchor;
    @FXML private ToggleButton playBtn;
    @FXML private Button prevBtn;
    @FXML private Button nextBtn;
    @FXML private Button searchArtists;
    @FXML private GridPane artistScene;
    @FXML private GridPane artistsGrid;
    @FXML private TextField artistsText;
    @FXML private ScrollPane artistsScroll;
    @FXML private GridPane tracksScene;
    @FXML private GridPane tracksGrid;
    @FXML private ScrollPane tracksScroll;

    @FXML
    void mousePressed(MouseEvent event){
        isPressed = true;
    }

    @FXML
    void mouseReleased(MouseEvent event){
        isPressed = false;
        //double position = progressBar.getProgress() * controllers.API_Data.duration;
        //controllers.API_Data.seek((long)position);
    }

    @FXML
    void play(){
        if (playBtn.isSelected()){
            toggleControl.setImage(playPng);
           // controllers.API_Data.action("play");
        }
        else {
            toggleControl.setImage(pausePng);
            //controllers.API_Data.action("pause");
        }

    }

    @FXML
    void next(){
        //controllers.API_Data.action("next");
    }

    @FXML
    void prev(){
        //controllers.API_Data.action("previous");
    }

    @FXML
    void showArtistScene() {
        artistScene.setVisible(true);
        tracksScene.setVisible(false);
    }

    @FXML
    void showTracksScene() {
        tracksScene.setVisible(true);
        artistScene.setVisible(false);
    }

    @FXML
    void getArtists(){
        while (artistsGrid.getChildren().size() != 0){
            artistsGrid.getChildren().remove(0);
        }
        artistsGrid.setAlignment(Pos.TOP_LEFT);
        HashMap<String, String> artists = API_Data.searchArtists(artistsText.getText());
        int rowInd = 0;
        for (String id: artists.keySet()){
            rowInd++;
            Button btn = new Button(artists.get(id));
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    long followerCount = API_Data.getArtistData(id);
                    ArrayList<String> albumIds = API_Data.getIds(id, "album");
                    ArrayList<String> trackIds = new ArrayList<String>();
                    albumIds.forEach((albumId) -> {
                        trackIds.addAll(API_Data.getIds(albumId, "track"));
                    });
                    showArtistStats(API_Data.getAudioFeatures(trackIds), followerCount);
                    //API_Data.getTracks(trackIds);
                }
            });
            btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    btn.setStyle("-fx-text-fill: #2994ff; -fx-background-color: #000000; -fx-border-color: #ffffff; -fx-border-width: 1px;  -fx-font-size: 13px");
                }
            });
            btn.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    btn.setStyle("-fx-text-fill: white; -fx-background-color: #000000; -fx-border-color: #ffffff; -fx-border-width: 1px;  -fx-font-size: 13px");
                }
            });
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setPadding(new Insets(2,0,2,5));
            btn.setPrefWidth(200);
            btn.prefWidthProperty().bind(artistsGrid.widthProperty().divide(2));
            btn.setMinHeight(30);
            btn.setStyle("-fx-text-fill: #ffffff; -fx-background-color: #000000; -fx-border-color: #ffffff; -fx-border-width: 1px; -fx-font-size: 13px; ");
            Shape status = new Circle(btn.getMinHeight() / 6);
            status.setFill(Color.LIMEGREEN);
            btn.setGraphic(status);
            btn.setGraphicTextGap(5);
            artistsGrid.add(btn, 0 , rowInd);
        }
    }

    void showArtistStats(HashMap<String, Double> mapOfFeatures, Long followerCount) {
        // clear artists grid
        while (artistsGrid.getChildren().size() != 0){
            artistsGrid.getChildren().remove(0);
        }
        GridPane featurePane = new GridPane();
        featurePane.setAlignment(Pos.CENTER);
        HBox chart = new HBox();
        chart.setMinWidth(250);
        chart.setAlignment(Pos.TOP_LEFT);
        chart.setSpacing(5);
        for (String featureName: mapOfFeatures.keySet()) {
            if (!featureName.equals("tempo") && !featureName.equals("loudness")) {
                VBox featureVBox = new VBox();
                featureVBox.setAlignment(Pos.BOTTOM_CENTER);
                featureVBox.setSpacing(10);
                featureVBox.setMinHeight(220);
                GridPane barPane = new GridPane();
                barPane.setAlignment(Pos.CENTER);
                Rectangle fullBar = new Rectangle(40, 200);
                fullBar.setFill(Color.TRANSPARENT);
                Rectangle bar = new Rectangle(40, 200 * mapOfFeatures.get(featureName));
                fullBar.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fullBar.setStroke(Color.WHITE);
                        setFeatureName(featureName, featurePane);
                    }
                });
                fullBar.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fullBar.setStroke(Color.TRANSPARENT);
                    }
                });
                bar.setFill(Color.color(.427, .765,.557));
                barPane.add(bar, 0, 0);
                barPane.add(fullBar, 0, 0);
                GridPane.setValignment(bar, VPos.BOTTOM);
                featureVBox.getChildren().add(barPane);
                FlowPane iconPane = new FlowPane();
                iconPane.setMaxWidth(50);
                iconPane.setAlignment(Pos.CENTER);
                Label test = new Label("test");
                test.setTextFill(Color.WHITE);
                iconPane.getChildren().add(test);
                featureVBox.getChildren().add(iconPane);
                chart.getChildren().add(featureVBox);
            }
            if (featureName.equals("tempo")) {

            }
        }
        FlowPane namePane = new FlowPane();
        namePane.setAlignment(Pos.CENTER);
        Text name = new Text(" ");
        name.setFont(Font.font("Segoe UI", 15));
        name.setFill(Color.WHITE);
        namePane.getChildren().add(name);
        System.out.println(mapOfFeatures.get("loudness") + " dB");
        VBox otherFeatures = createOtherFeaturesVBox(mapOfFeatures.get("tempo"), mapOfFeatures.get("loudness"), followerCount);

        featurePane.add(chart, 0, 0);
        featurePane.add(namePane, 0, 1);
        artistsGrid.add(featurePane, 0, 0);
        artistsGrid.add(otherFeatures, 1, 0);
    }

    void setFeatureName(String featureName, GridPane gridPane) {
        FlowPane namePane = new FlowPane();
        namePane.setAlignment(Pos.CENTER);
        namePane.setMaxHeight(40);
        namePane.setMaxWidth(250);
        Text name = new Text(featureName);
        name.setFont(Font.font("Segoe UI", 30));
        name.setFill(Color.WHITE);
        namePane.getChildren().add(name);
        gridPane.getChildren().remove(1);
        gridPane.add(namePane, 0, 1);
    }

    VBox createOtherFeaturesVBox(double tempo, double loudness, long followerCount) {
        VBox otherFeatures = new VBox();
        otherFeatures.setPrefWidth(250);
        otherFeatures.setPrefHeight(500);

        String borderStyle = "-fx-border-color: #4d4d4d; -fx-border-width: 1; ";
        DecimalFormat df = new DecimalFormat("#.##");

        FlowPane followerPane = new FlowPane();
        followerPane.setPrefHeight(otherFeatures.getPrefHeight() / 3);
        followerPane.setStyle(borderStyle);
        followerPane.setAlignment(Pos.CENTER);
        Text followers = new Text(followerCount + " followers");
        followers.setFont(Font.font("Segoe UI", 20));
        followers.setFill(Color.WHITE);
        followerPane.getChildren().add(followers);

        FlowPane tempoPane = new FlowPane();
        tempoPane.setPrefHeight(otherFeatures.getPrefHeight() / 3);
        tempoPane.setStyle(borderStyle);
        tempoPane.setAlignment(Pos.CENTER);
        Text tempoText = new Text(Double.valueOf(df.format(tempo)) + " BPM");
        tempoText.setFont(Font.font("Segoe UI", FontWeight.BOLD,25));
        tempoText.setFill(Color.WHITE);
        tempoPane.getChildren().add(tempoText);

        FlowPane loudnessPane = new FlowPane();
        loudnessPane.setPrefHeight(otherFeatures.getPrefHeight() / 3);
        loudnessPane.setStyle(borderStyle);
        loudnessPane.setAlignment(Pos.CENTER);
        Text loudnessText = new Text(Double.valueOf(df.format(loudness)) + " dB");
        loudnessText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        loudnessText.setFill(Color.WHITE);
        loudnessPane.getChildren().add(loudnessText);

        otherFeatures.getChildren().addAll(tempoPane, loudnessPane, followerPane);

        return otherFeatures;
    }

    @FXML
    void getTracks() {

    }

    void controlShow(){
        double anchorY = anchor.getLayoutY();
        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(0.5), controlBox);
        moveUp.setFromY(anchorY + controlBox.getHeight());
        moveUp.setToY(anchorY);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.7), controlBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        ParallelTransition pShow = new ParallelTransition(moveUp, fadeIn);
        pShow.play();
        controlsHidden = false;
    }

    void controlHide(){
        double currControlY = controlBox.getLayoutY();
        double anchorY = anchor.getLayoutY();
        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(0.5), controlBox);
        moveDown.setFromY(currControlY);
        moveDown.setToY(anchorY + controlBox.getHeight());
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), controlBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        ParallelTransition pHide = new ParallelTransition(moveDown, fadeOut);
        pHide.play();
        controlsHidden = true;
    }

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
        API_Data.playbackTimerStarted = false;
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
    public void initialize(URL url, ResourceBundle rb) {
        ImageView prevView = new ImageView(prevPng);
        ImageView nextView = new ImageView(nextPng);
        prevView.setFitWidth(22.0);
        prevView.setFitHeight(22.0);
        nextView.setFitWidth(22.0);
        nextView.setFitHeight(22.0);
        toggleControl.setFitHeight(22.0);
        toggleControl.setFitWidth(22.0);
        toggleControl.setImage(playPng);
        progressBar.setMinWidth(slider.getPrefWidth());
        progressBar.setMaxWidth(slider.getPrefWidth());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setProgress(newValue.doubleValue() / 100.0);
        });
        /*controllers.API_Data.progressOfSong.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            if (!isPressed) {
                slider.setValue(Double.valueOf(controllers.API_Data.progressOfSong.getText()) * 100.0);
            }
        });
        */ //testing

        artistsText.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER){
                getArtists();
                ev.consume();
            }
        });
        playBtn.setGraphic(toggleControl);
        playBtn.setPrefSize(20,20);
        nextBtn.setGraphic(nextView);
        nextBtn.setPrefSize(15,15);
        prevBtn.setGraphic(prevView);
        prevBtn.setPrefSize(15,15);
        artistsScroll.setFitToWidth(true);
        artistsGrid.prefHeightProperty().bind(artistsScroll.heightProperty());
    }
}
