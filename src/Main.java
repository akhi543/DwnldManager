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
            System.out.println("1");
            AnchorPane root = (AnchorPane) FXMLLoader.load(Main.class.getResource("FXMLDocument.fxml"));
            System.out.println("2");
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root));
            //primaryStage.setMinWidth(470);
            //primaryStage.setMinHeight(460);
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