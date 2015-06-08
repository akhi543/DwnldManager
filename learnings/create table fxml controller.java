import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;


public class FXMLDocumentController implements Initializable{
    
    public Button addDownloadButton;
    public TextField urlTextBox;
    public HBox box1; 
    public TableView<DownloadEntry> downloadsTable;
    public TableColumn<DownloadEntry, Double> progressColumn;
    public TableColumn<DownloadEntry, String> statusColumn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        downloadsTable = new TableView<DownloadEntry>();
        
        progressColumn = new TableColumn<>("Progress");
        statusColumn = new TableColumn<>("Status");
        
        progressColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, Double>("progress"));
        progressColumn.setCellFactory(ProgressBarTableCell.<DownloadEntry>forTableColumn());
        statusColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, String>("message"));
       
        downloadsTable.getItems().add(new DownloadEntry());
        downloadsTable.getItems().add(new DownloadEntry());
        downloadsTable.getItems().add(new DownloadEntry());
        
        
        downloadsTable.getColumns().addAll(progressColumn, statusColumn);
        box1.getChildren().add(downloadsTable);
        
        ExecutorService executor = Executors.newFixedThreadPool(downloadsTable.getItems().size(), new ThreadFactory() {
          @Override
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
          }
        });        
        
        System.out.println(downloadsTable.getItems());
        for (DownloadEntry task : downloadsTable.getItems()) {
            executor.execute(task);
        }
      
    }
    
    public void addDownloadButtonClicked() {
        
    }
    
}










import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class DownloadEntry extends Task<Void>{

    public static final int NUM_ITERATIONS = 100;
    
    @Override
    protected Void call() throws Exception {
        this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
        this.updateMessage("Waiting...");
        Thread.sleep(1000);
        this.updateMessage("Running...");
        
        for (int i = 0; i < NUM_ITERATIONS; i++) {
          updateProgress((1.0 * i) / NUM_ITERATIONS, 1);
          Thread.sleep(50);
        }
        this.updateMessage("Done");
        this.updateProgress(1, 1);
        return null;
    }
      
}














<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.text.*?>
<?import javafx.scene.effect.*?>
<?import javafx.geometry.*?>
<?import java.lang.*?>
<?import java.util.*?>
<?import javafx.scene.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<AnchorPane id="AnchorPane" minHeight="460.0" minWidth="470.0" prefHeight="460.0" prefWidth="470.0" xmlns="http://javafx.com/javafx/8" xmlns:fx="http://javafx.com/fxml/1" fx:controller="FXMLDocumentController">
    <children>
      <HBox alignment="CENTER" layoutX="67.0" spacing="25.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0">
         <children>
            <TextField fx:id="urlTextBox" minWidth="-Infinity" promptText="Enter URL here..">
               <font>
                  <Font size="15.0" />
               </font>
            </TextField>
            <Button fx:id="addDownloadButton" mnemonicParsing="false" onAction="#addDownloadButtonClicked" text="Add Download">
               <font>
                  <Font size="15.0" />
               </font>
            </Button>
         </children>
         <padding>
            <Insets bottom="8.0" left="8.0" right="8.0" top="8.0" />
         </padding>
      </HBox>
      <HBox alignment="CENTER" layoutX="74.0" layoutY="107.0" prefHeight="100.0" prefWidth="200.0" AnchorPane.bottomAnchor="70.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0" AnchorPane.topAnchor="60.0">
         <children>
            <TableView fx:id="downloadsTable" maxWidth="1.7976931348623157E308" minWidth="-Infinity">
              <columns>
                <TableColumn fx:id="fileNameColumn" prefWidth="75.0" text="File Name" />
                <TableColumn fx:id="urlColumn" prefWidth="75.0" text="URL" />
                <TableColumn fx:id="fileSizeColumn" prefWidth="75.0" text="Size" />
                  <TableColumn prefWidth="75.0" text="Progress" />
                <TableColumn fx:id="statusColumn" prefWidth="75.0" text="Status" />
              </columns>
               <HBox.margin>
                  <Insets />
               </HBox.margin>
               <opaqueInsets>
                  <Insets />
               </opaqueInsets>
               <effect>
                  <InnerShadow />
               </effect>
            </TableView>
         </children>
      </HBox>
      <HBox alignment="CENTER" layoutX="139.0" layoutY="346.0" prefWidth="300.0" spacing="10.0" AnchorPane.bottomAnchor="15.0" AnchorPane.leftAnchor="0.0" AnchorPane.rightAnchor="0.0">
         <children>
            <Button mnemonicParsing="false" text="Pause" textAlignment="CENTER">
               <font>
                  <Font name="Arial" size="15.0" />
               </font>
               <opaqueInsets>
                  <Insets />
               </opaqueInsets>
            </Button>
            <Button layoutX="10.0" layoutY="10.0" mnemonicParsing="false" text="Cancel" textAlignment="CENTER">
               <font>
                  <Font name="Arial" size="15.0" />
               </font>
               <opaqueInsets>
                  <Insets />
               </opaqueInsets>
            </Button>
            <Button layoutX="66.0" layoutY="10.0" mnemonicParsing="false" text="Delete" textAlignment="CENTER">
               <font>
                  <Font name="Arial" size="15.0" />
               </font>
               <opaqueInsets>
                  <Insets />
               </opaqueInsets>
            </Button>
         </children>
         <padding>
            <Insets bottom="8.0" left="8.0" right="8.0" top="8.0" />
         </padding>
         <opaqueInsets>
            <Insets />
         </opaqueInsets>
         <effect>
            <Lighting>
               <bumpInput>
                  <ColorAdjust />
               </bumpInput>
            </Lighting>
         </effect>
      </HBox>
    </children>
</AnchorPane>
