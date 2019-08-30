import controllers.ResizeHelper;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        //Main
        Parent root = FXMLLoader.load(getClass().getResource("fxml_files/FXMLLogin.fxml"));
        //
        // Test FXML
        //Parent root = FXMLLoader.load(getClass().getResource("fxml_files/FXMLMain.fxml"));

        Scene scene = new Scene(root);
        //Main
        scene.getStylesheets().add(getClass().getResource("stylesheets/SpotifyLoginStyles.css").toExternalForm());
        // Test Styles
        //scene.getStylesheets().add(getClass().getResource("stylesheets/SpotifyStyles.css").toExternalForm());
        stage.setScene(scene);

        stage.initStyle(StageStyle.UNDECORATED);
        ResizeHelper.addResizeListener(stage, 400, 400, 4000, 2000);

        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}