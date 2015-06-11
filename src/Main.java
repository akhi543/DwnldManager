import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(Main.class, (String[]) null);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            AnchorPane root = (AnchorPane) FXMLLoader.load(Main.class.getResource("FXMLDocument.fxml"));
            primaryStage.setTitle("Download Manager");
            primaryStage.setScene(new Scene(root, 470, 460));
            primaryStage.setMinWidth(470);
            primaryStage.setMinHeight(460);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                try {
                    Platform.exit();
                    System.exit(0);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        catch(Exception e){
            System.out.println(e);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
}