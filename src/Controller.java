import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
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
import javafx.geometry.Insets;
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


public class Controller implements Initializable,Observer {
    
    public Button addDownloadButton;
    public Button pauseResumeButton;
    public Button cancelButton;
    public Button deleteButton;
    public  TextField urlTextBox;
    public HBox box1; 
    public static TableView<DownloadEntry> downloadsTable;
    public TableColumn<DownloadEntry, String> fileNameColumn;
    public TableColumn<DownloadEntry, String> urlColumn;
    public TableColumn<DownloadEntry, Integer> fileSizeColumn;
    public TableColumn<DownloadEntry, Double> progressColumn;
    public TableColumn<DownloadEntry, String> statusColumn;
    ExecutorService executor;
    
    public static void handleChange() {
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        downloadsTable = new TableView<DownloadEntry>();
        
        fileNameColumn = new TableColumn<>("File Name");
        urlColumn = new TableColumn<>("URL");
        fileSizeColumn = new TableColumn<>("File Size");
        progressColumn = new TableColumn<>("Progress");
        statusColumn = new TableColumn<>("Status");
        
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, String>("fileName"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, String>("urlText"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, Integer>("fileSize"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, Double>("progress"));
        progressColumn.setCellFactory(ProgressBarTableCell.<DownloadEntry>forTableColumn());
        statusColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, String>("message"));
       
        downloadsTable.getColumns().addAll(fileNameColumn, urlColumn, fileSizeColumn, progressColumn, statusColumn);
        box1.getChildren().add(downloadsTable);
        
        executor = Executors.newFixedThreadPool(10, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            }
        });        
        
    }
    
    public void addDownloadButtonClicked() {
        String urlText = null;
        DownloadEntry task = null;
        try {
            urlText = urlTextBox.getText();
        }
        catch(Exception e) {
            System.out.println("addDownloadButtonClicked: " + e);
        }
        try {
            task = new DownloadEntry(new URL(urlText));
        }
        catch(Exception e) {
            System.out.println("addDownloadButtonClicked: " + e);
        }
        try {
            executor.execute(task);
        }
        catch(Exception e) {
            System.out.println("addDownloadButtonClicked: " + e);
        }
        
    }
//
//    public static void addEntryInTable() {
//        try {
//            downloadsTable.getItems().add(new DownloadEntry(new URL(urlTextBox.getText())));
//        }
//        catch(Exception e) {
//            System.out.println("addEntryInTable: " + e);
//        }
//    }
    
    @Override
    public void update(Observable o, Object arg) {
        
    }
    
}
