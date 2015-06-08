
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public AlertBox(String title, String message) {
        Stage window = new Stage();
        window.setTitle(title);
        window.initModality(Modality.APPLICATION_MODAL);
        
        Label lab = new Label(message);
        Button btn = new Button("Close");
        btn.setOnAction(e -> window.close());
        VBox root = new VBox();
        root.getChildren().addAll(lab,btn);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 500, 100);
        window.setScene(scene);
        window.showAndWait();
    }
    
}
