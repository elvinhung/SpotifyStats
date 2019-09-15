package controllers;

import helperClasses.MusicObject;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.*;
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
    private boolean isMeSceneFilled = false;
    private boolean controlsHidden = true;
    private boolean isPressed = false;
    private double x,y = 0;
    private String[] timeList = {"4 weeks", "6 months", "All time"};
    private Image playPng = new Image(getClass().getResourceAsStream("/images/play.png"));
    private Image pausePng = new Image(getClass().getResourceAsStream("/images/pause.png"));
    private Image happyPng = new Image(getClass().getResourceAsStream("/images/happy.png"));
    private Image energyPng = new Image(getClass().getResourceAsStream("/images/energy.png"));
    private Image instrumentPng = new Image(getClass().getResourceAsStream("/images/instrument.png"));
    private Image dancePng = new Image(getClass().getResourceAsStream("/images/dance.png"));
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
    @FXML private TextField tracksText;
    @FXML private GridPane playlistsScene;
    @FXML private GridPane playlistsGrid;
    @FXML private ScrollPane playlistsScroll;
    @FXML private TextField playlistsText;
    @FXML private Text profileName;
    @FXML private GridPane meScene;
    @FXML private GridPane meGrid;
    @FXML private Circle profilePicture;
    @FXML private Button meBtn;
    @FXML private Button artistsBtn;
    @FXML private Button tracksBtn;
    @FXML private Button playlistsBtn;
    @FXML private Text userFollowers;
    @FXML private ImageView playImg;
    @FXML private ChoiceBox timePicker;

    @FXML
    void mousePressed(MouseEvent event){
        isPressed = true;
    }

    @FXML
    void mouseReleased(MouseEvent event){
        isPressed = false;
        double position = progressBar.getProgress() * controllers.API_Data.duration;
        controllers.API_Data.seek((long)position);
    }

    @FXML
    void play(){
        if (API_Data.isPlaying.getText().equals("false")) {
            playImg.setImage(pausePng);
            playBtn.setAlignment(Pos.CENTER);
            controllers.API_Data.action("play");
            API_Data.isPlaying.setText("true");
        }
        else {
            playBtn.setAlignment(Pos.CENTER_RIGHT);
            playImg.setImage(playPng);
            controllers.API_Data.action("pause");
            API_Data.isPlaying.setText("false");
        }
    }

    public void setPlayImg(boolean isPlaying) {
        if (isPlaying) {
            playBtn.setAlignment(Pos.CENTER);
            playImg.setImage(pausePng);
        } else {
            playBtn.setAlignment(Pos.CENTER_RIGHT);
            playImg.setImage(playPng);
        }
    }

    @FXML
    void next(){
        controllers.API_Data.action("next");
    }

    @FXML
    void prev(){
        controllers.API_Data.action("previous");
    }

    @FXML
    void showMeScene() {
        meScene.setVisible(true);
        playlistsScene.setVisible(false);
        tracksScene.setVisible(false);
        artistScene.setVisible(false);
        clearBtnStyles(meBtn);
        if (!isMeSceneFilled) {
            fillMeScene();
            isMeSceneFilled = true;
        }
    }

    @FXML
    void showPlaylistsScene() {
        playlistsScene.setVisible(true);
        tracksScene.setVisible(false);
        artistScene.setVisible(false);
        meScene.setVisible(false);
        clearBtnStyles(playlistsBtn);
    }

    @FXML
    void showArtistScene() {
        artistScene.setVisible(true);
        tracksScene.setVisible(false);
        playlistsScene.setVisible(false);
        meScene.setVisible(false);
        clearBtnStyles(artistsBtn);
    }

    @FXML
    void showTracksScene() {
        tracksScene.setVisible(true);
        artistScene.setVisible(false);
        playlistsScene.setVisible(false);
        meScene.setVisible(false);
        clearBtnStyles(tracksBtn);
    }

    void fillMeScene() {
        while (meGrid.getChildren().size() != 0) {
            meGrid.getChildren().remove(0);
        }
        API_Data.timeFrame = (String) timePicker.getValue();
        HashMap<String, String> userMap = API_Data.getUser();
        ArrayList<MusicObject> userTopRankedArtists = API_Data.getUserTopRanked("artists");
        ArrayList<MusicObject> userTopRankedTracks = API_Data.getUserTopRanked("tracks");
        ArrayList<MusicObject> userPlaylists = API_Data.getUserPlaylists();
        String name = userMap.get("name");
        String url = userMap.get("url");
        String followerCount = userMap.get("followers");
        profileName.setText(name);
        userFollowers.setText(followerCount + " followers");
        if (!url.equals("")) {
            ImagePattern imagePattern = new ImagePattern(new Image(url));
            profilePicture.setFill(imagePattern);
        }
        HBox topArtistsHBox = createLabel("Top Artists", meGrid);
        meGrid.add(topArtistsHBox, 0, 2);
        int rowInd = 3;
        for (MusicObject musicObj: userTopRankedArtists) {
            Button artistItem = createSearchItem(musicObj.name, meGrid);
            artistItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    showArtistScene();
                    ArrayList<String> albumIds = API_Data.getIds(musicObj.id, "album");
                    ArrayList<String> trackIds = new ArrayList<String>();
                    albumIds.forEach((albumId) -> {
                        trackIds.addAll(API_Data.getIds(albumId, "track"));
                    });
                    displayStats(API_Data.getAudioFeatures(trackIds), artistsGrid, API_Data.getData(musicObj.id, "artist"));
                }
            });
            meGrid.add(artistItem, 0, rowInd);
            rowInd++;
        }
        HBox topTracksHBox = createLabel("Top Tracks", meGrid);
        meGrid.add(topTracksHBox, 0, rowInd);
        rowInd++;
        for (MusicObject musicObj: userTopRankedTracks) {
            Button trackItem = createSearchItem(musicObj.name, meGrid);
            trackItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    showTracksScene();
                    ArrayList<String> trackIds = new ArrayList<String>();
                    trackIds.add(musicObj.id);
                    displayStats(API_Data.getAudioFeatures(trackIds), tracksGrid, API_Data.getData(musicObj.id, "track"));
                }
            });
            meGrid.add(trackItem, 0, rowInd);
            rowInd++;
        }
        HBox playlistsHBox = createLabel("Playlists", meGrid);
        meGrid.add(playlistsHBox, 0, rowInd);
        rowInd++;
        for (MusicObject musicObj: userPlaylists) {
            Button playlistItem = createSearchItem(musicObj.name, meGrid);
            playlistItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    showPlaylistsScene();
                    HashMap<String, String> dataMap = API_Data.getData(musicObj.id, "playlist");
                    ArrayList<String> ids = API_Data.getIds(musicObj.id, "playlist");
                    displayStats(API_Data.getAudioFeatures(ids), playlistsGrid, dataMap);
                }
            });
            meGrid.add(playlistItem, 0, rowInd);
            rowInd++;
        }
    }

    HBox createLabel(String title, GridPane gridPane) {
        HBox labelHBox = new HBox();
        labelHBox.setAlignment(Pos.CENTER_LEFT);
        labelHBox.setPadding(new Insets(10, 10, 15, 5));
        TextFlow textFlow = new TextFlow();
        textFlow.prefWidthProperty().bind(gridPane.widthProperty().divide(1.2));
        textFlow.setStyle("-fx-border-color: white; -fx-border-width: 0 0 1 0; -fx-padding: 10px 0px 10px 5px; ");
        Text text = new Text(title);
        text.setFont(Font.font("Segoe UI", 22.0));
        text.setFill(Color.WHITE);
        textFlow.getChildren().add(text);
        labelHBox.getChildren().add(textFlow);
        return labelHBox;
    }

    Button createSearchItem(String text, GridPane grid) {
        Button btn = new Button(text);
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
        btn.prefWidthProperty().bind(grid.widthProperty().divide(1.5));
        btn.setMinHeight(32);
        btn.setStyle("-fx-text-fill: #ffffff; -fx-background-color: #000000; -fx-border-color: #ffffff; -fx-border-width: 1px; -fx-font-size: 13px; ");
        Shape status = new Circle(btn.getMinHeight() / 7);
        status.setFill(Color.LIMEGREEN);
        btn.setGraphic(status);
        btn.setGraphicTextGap(5);

        return btn;
    }

    HBox createHeader(HashMap<String, String> dataMap, GridPane grid) {

        String name = dataMap.get("name");
        String followers = dataMap.get("followers");
        String imageUrl = dataMap.get("imageUrl");
        String artists = dataMap.get("artists");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15.0);
        header.setStyle("-fx-border-color: #b8b8b8; -fx-border-width: 0 0 1 0; ");
        header.setPadding(new Insets(50, 0, 10, 0));
        header.setMinWidth(grid.getWidth());
//        header.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Circle profilePicture = new Circle(35.0, Color.DODGERBLUE);

        if (!imageUrl.equals("")) {
            ImagePattern profilePattern = new ImagePattern(new Image(imageUrl));
            profilePicture.setFill(profilePattern);
        }

        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setSpacing(5);

        Text nameText = new Text(name);
        nameText.setFont(Font.font("Segoe UI", 28.0));
        nameText.setFill(Color.color(.325, .969, .549));
        textBox.getChildren().add(nameText);

        if (!followers.equals("")) {
            Text followerText = new Text(followers + " followers");
            followerText.setFill(Color.WHITE);
            followerText.setFont(Font.font("Segoe UI", 14.0));
            textBox.getChildren().add(followerText);
        }
        if (!artists.equals("")) {
            Text artistsText = new Text(artists);
            artistsText.setFill(Color.WHITE);
            artistsText.setFont(Font.font("Segoe UI", 14.0));
            textBox.getChildren().add(artistsText);
        }

        header.getChildren().add(profilePicture);
        header.getChildren().add(textBox);

        return header;
    }

    @FXML
    void getPlaylists() {
        while (playlistsGrid.getChildren().size() != 0) {
            playlistsGrid.getChildren().remove(0);
        }
        ArrayList<MusicObject> playlists = API_Data.search(playlistsText.getText(), "playlists");
        int rowInd = 0;
        for (MusicObject musicObj: playlists) {
            rowInd++;
            String id = musicObj.id;
            String name = musicObj.name;
            Button btn = createSearchItem(name, playlistsGrid);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    HashMap<String, String> dataMap = API_Data.getData(id, "playlist");
                    ArrayList<String> ids = API_Data.getIds(id, "playlist");
                    displayStats(API_Data.getAudioFeatures(ids), playlistsGrid, dataMap);
                }
            });
            playlistsGrid.add(btn, 0, rowInd);
        }
    }

    @FXML
    void getTracks() {
        while (tracksGrid.getChildren().size() != 0) {
            tracksGrid.getChildren().remove(0);
        }
        ArrayList<MusicObject> tracks = API_Data.search(tracksText.getText(), "tracks");
        int rowInd = 0;
        for (MusicObject musicObj: tracks){
            rowInd++;
            Button btn = createSearchItem(musicObj.name, tracksGrid);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ArrayList<String> ids = new ArrayList<String>();
                    ids.add(musicObj.id);
                    HashMap<String, Double> map = API_Data.getAudioFeatures(ids);
                    displayStats(map, tracksGrid, API_Data.getData(musicObj.id, "track"));
                }
            });
            tracksGrid.add(btn, 0 , rowInd);
        }
    }

    @FXML
    void getArtists(){
        while (artistsGrid.getChildren().size() != 0){
            artistsGrid.getChildren().remove(0);
        }
        artistsGrid.setAlignment(Pos.TOP_LEFT);
        ArrayList<MusicObject> artists = API_Data.search(artistsText.getText(), "artists");
        int rowInd = 0;
        for (MusicObject musicObj: artists){
            rowInd++;
            String id = musicObj.id;
            String name = musicObj.name;
            Button btn = createSearchItem(name, artistsGrid);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    HashMap<String, String> dataMap = API_Data.getData(id, "artist");
                    ArrayList<String> albumIds = API_Data.getIds(id, "album");
                    ArrayList<String> trackIds = new ArrayList<String>();
                    albumIds.forEach((albumId) -> {
                        trackIds.addAll(API_Data.getIds(albumId, "track"));
                    });
                    displayStats(API_Data.getAudioFeatures(trackIds), artistsGrid, dataMap);
                    //API_Data.getTracks(trackIds);
                }
            });
            artistsGrid.add(btn, 0 , rowInd);
        }
    }

    void displayStats(HashMap<String, Double> mapOfFeatures, GridPane grid, HashMap<String, String> dataMap) {
        while (grid.getChildren().size() != 0) {
            grid.getChildren().remove(0);
        }

        HBox header = createHeader(dataMap, grid);

        GridPane featurePane = new GridPane();
        featurePane.setAlignment(Pos.CENTER);
        HBox chart = new HBox();
        chart.setMinWidth(250);
        chart.setAlignment(Pos.CENTER);
        chart.setSpacing(10);
        for (String featureName: mapOfFeatures.keySet()) {
            if (!featureName.equals("tempo") && !featureName.equals("loudness")) {
                VBox featureVBox = new VBox();
                featureVBox.setAlignment(Pos.BOTTOM_CENTER);
                featureVBox.setSpacing(10);
                featureVBox.setMinHeight(220);
                GridPane barPane = new GridPane();
                barPane.setAlignment(Pos.CENTER);
                Rectangle fullBar = new Rectangle(40, 190);
                fullBar.setFill(Color.TRANSPARENT);
                Rectangle bar = new Rectangle(40, 190 * mapOfFeatures.get(featureName));
                fullBar.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fullBar.setStroke(Color.WHITE);
                        setFeatureName(featureName, featurePane, mapOfFeatures.get(featureName));
                    }
                });
                fullBar.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        fullBar.setStroke(Color.TRANSPARENT);
                    }
                });
                //bar.setFill(Color.color(.325, .969, .549));
                bar.setFill(Color.color(1.0, .6, .251));
                barPane.add(bar, 0, 0);
                barPane.add(fullBar, 0, 0);
                GridPane.setValignment(bar, VPos.BOTTOM);
                featureVBox.getChildren().add(barPane);
                FlowPane iconPane = new FlowPane();
                iconPane.setMaxWidth(20);
                iconPane.setMaxHeight(20);
                iconPane.setMinWidth(20);
                iconPane.setMinHeight(20);
                iconPane.setAlignment(Pos.CENTER);
                ImageView icon = getIcon(featureName);
                iconPane.getChildren().add(icon);
                featureVBox.getChildren().add(iconPane);
                chart.getChildren().add(featureVBox);
            }
        }
        FlowPane namePane = new FlowPane();
        namePane.setAlignment(Pos.CENTER);
        namePane.setMaxHeight(40);
        namePane.setMaxWidth(250);
        Text name = new Text(" ");
        name.setFont(Font.font("Segoe UI", 20));
        name.setFill(Color.WHITE);
        namePane.getChildren().add(name);
        VBox otherFeatures = createOtherFeaturesVBox(mapOfFeatures.get("tempo"), mapOfFeatures.get("loudness"));

        featurePane.add(chart, 0, 0);
        featurePane.add(namePane, 0, 1);

        HBox featureHBox = new HBox();
        featureHBox.setAlignment(Pos.CENTER);
        featureHBox.setSpacing(20);
        featureHBox.setPadding(new Insets(70,10,10,10));
        featureHBox.getChildren().add(featurePane);
        featureHBox.getChildren().add(otherFeatures);

        grid.add(header, 0, 0);
        grid.add(featureHBox, 0, 1);

    }

    ImageView getIcon(String name) {
        ImageView icon = new ImageView();
        icon.setFitHeight(15);
        icon.setFitWidth(15);

        switch (name) {
            case "danceability": {
                icon.setImage(dancePng);
                break;
            }
            case "energy": {
                icon.setImage(energyPng);
                break;
            }
            case "valence": {
                icon.setImage(happyPng);
                break;
            }
            case "instrumentalness": {
                icon.setImage(instrumentPng);
                break;
            }
            default: {
                System.out.println(name + " did not match any of the icons");
            }
        }

        return icon;
    }

    void setFeatureName(String featureName, GridPane gridPane, Double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        FlowPane namePane = new FlowPane();
        namePane.setAlignment(Pos.CENTER);
        namePane.setMaxHeight(40);
        namePane.setMaxWidth(250);

        Text name = new Text(featureName + " ");
        name.setFont(Font.font("Segoe UI", 20));
        name.setFill(Color.WHITE);

        Text valueText = new Text("" + Double.valueOf(df.format(value)));
        valueText.setFont(Font.font("Segoe UI", 20));
        valueText.setFill(Color.color(.325, .969, .549));

        namePane.getChildren().addAll(name, valueText);
        gridPane.getChildren().remove(1);
        gridPane.add(namePane, 0, 1);
    }

    VBox createOtherFeaturesVBox(double tempo, double loudness) {
        VBox otherFeatures = new VBox();
        otherFeatures.setPrefWidth(250);
        otherFeatures.setPrefHeight(350);

        String borderStyle = "-fx-border-color: white; -fx-border-width: 0 0 1 0; ";
        DecimalFormat df = new DecimalFormat("#.##");


        FlowPane tempoPane = new FlowPane();
        tempoPane.setPrefHeight(otherFeatures.getPrefHeight() / 3);
        tempoPane.setStyle(borderStyle);
        tempoPane.setAlignment(Pos.CENTER);
        Text tempoText = new Text(Double.valueOf(df.format(tempo)) + " ");
        tempoText.setFont(Font.font("Segoe UI", FontWeight.BOLD,25));
        tempoText.setFill(Color.WHITE);
        Text bpmText = new Text("BPM");
        bpmText.setFont(Font.font("Segoe UI", FontWeight.BOLD,25));
        bpmText.setFill(Color.color(.361, .843, .929));
        tempoPane.getChildren().addAll(tempoText, bpmText);

        FlowPane loudnessPane = new FlowPane();
        loudnessPane.setPrefHeight(otherFeatures.getPrefHeight() / 3);
        loudnessPane.setAlignment(Pos.CENTER);
        Text loudnessText = new Text(Double.valueOf(df.format(loudness)) + " ");
        loudnessText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        loudnessText.setFill(Color.WHITE);
        Text dbText = new Text("dB");
        dbText.setFont(Font.font("Segoe UI", FontWeight.BOLD,25));
        dbText.setFill(Color.color(.361, .843, .929));
        loudnessPane.getChildren().addAll(loudnessText, dbText);

        otherFeatures.getChildren().addAll(tempoPane, loudnessPane);

        return otherFeatures;
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
        System.exit(0);
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

    void clearBtnStyles(Button btn) {
        meBtn.setStyle("");
        artistsBtn.setStyle("");
        tracksBtn.setStyle("");
        playlistsBtn.setStyle("");
        btn.setStyle("-fx-border-color: #22b244; -fx-border-width: 0 0 0 3; ");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPlayImg(API_Data.isPlaying.getText().equals("true"));
        progressBar.setMinWidth(slider.getPrefWidth());
        progressBar.setMaxWidth(slider.getPrefWidth());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setProgress(newValue.doubleValue() / 100.0);
        });
        API_Data.progressOfSong.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isPressed) {
                slider.setValue(Double.valueOf(API_Data.progressOfSong.getText()) * 100.0);
            }
        });

        API_Data.isPlaying.textProperty().addListener((observable, oldValue, newValue) -> {
            setPlayImg(API_Data.isPlaying.getText().equals("true"));
        });
        artistsText.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER){
                getArtists();
                ev.consume();
            }
        });
        playlistsText.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER){
                getPlaylists();
                ev.consume();
            }
        });
        tracksText.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER){
                getTracks();
                ev.consume();
            }
        });
        artistsScroll.setFitToWidth(true);
        artistsBtn.setStyle("-fx-border-color: #22b244; -fx-border-width: 0 0 0 3; ");
        timePicker.getItems().addAll(timeList);
        timePicker.setValue(timeList[0]);
        timePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fillMeScene();
            }
        });
    }
}
