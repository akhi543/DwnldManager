import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.Priority;


public class Controller implements Initializable {
    
    public Button addDownloadButton;
    public Button pauseResumeButton;
    public Button cancelButton;
    public Button deleteButton;
    public TextField urlTextBox;
    public HBox box1; 
    public TableView<DownloadEntry> downloadsTable;
    public TableColumn<DownloadEntry, String> fileNameColumn;
    public TableColumn<DownloadEntry, String> urlColumn;
    public TableColumn<DownloadEntry, Integer> fileSizeColumn;
    public TableColumn<DownloadEntry, Double> speedColumn;
    public TableColumn<DownloadEntry, Double> progressColumn;
    public TableColumn<DownloadEntry, String> statusColumn;
    ExecutorService executor;
    List<DownloadEntry> downloadsList;
    List<Boolean> paused;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        downloadsTable = new TableView<DownloadEntry>();
        
        fileNameColumn = new TableColumn<>("File Name");
        urlColumn = new TableColumn<>("URL");
        fileSizeColumn = new TableColumn<>("File Size");
        progressColumn = new TableColumn<>("Progress");
        statusColumn = new TableColumn<>("Status");
        speedColumn = new TableColumn<>("Speed(KBps)");
        
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, String>("urlText"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, Integer>("fileSize"));
        speedColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, Double>("speed"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, Double>("progress"));
        progressColumn.setCellFactory(ProgressBarTableCell.<DownloadEntry>forTableColumn());
        statusColumn.setCellValueFactory(new PropertyValueFactory<DownloadEntry, String>("message"));
       
        downloadsTable.getColumns().addAll(fileNameColumn, speedColumn, fileSizeColumn, progressColumn, statusColumn, urlColumn);
        box1.getChildren().add(downloadsTable);
        box1.setHgrow(downloadsTable, Priority.ALWAYS);
        
        downloadsTable.setOnMouseClicked(e -> {
            int idx = downloadsTable.getSelectionModel().getSelectedIndex();
            if(paused.get(idx)) {
                pauseResumeButton.setText("Resume");
            }
            else {
                pauseResumeButton.setText("Pause");
            }
        });
        
        executor = Executors.newFixedThreadPool(4);
        paused = new ArrayList<>();       
        downloadsList = new ArrayList<DownloadEntry>();       
     
    }
    
    public void addDownloadButtonClicked() {
        try{
            DownloadEntry task = new DownloadEntry(new URL("http://img.planespotters.net/photo/272000/original/A6-EYJ-Etihad-Airways-Airbus-A330-200_PlanespottersNet_272357.jpg"));
            downloadsList.add(task);
            downloadsTable.getItems().add(task);
            paused.add(false);
            System.out.println(executor);
            executor.execute(task);
            System.out.println(executor);
        }
        catch(Exception e) {
            System.out.println("addDownloadButtonClicked: " + e);
        }
    }

    
    public void pauseResumeButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
        try {
            if(paused.get(idx)==false) {
                downloadsList.get(idx).pause();  //sends interrupt, not necessary that interrupt is handled
                System.out.println(downloadsList.get(idx).downloaded);
                pauseResumeButton.setText("Resume");
                paused.set(idx, Boolean.TRUE);
            }
            else {
                URL t = downloadsList.get(idx).url;
                int d = downloadsList.get(idx).downloaded;
                System.out.println(t);
                System.out.println(d);
                DownloadEntry task = new DownloadEntry(t);
                System.out.println(executor);
                task.downloaded = d;
                executor.execute(downloadsList.get(idx));
                System.out.println(executor);
                pauseResumeButton.setText("Pause");
                paused.set(idx, Boolean.FALSE);
            }
        }
        catch(Exception e) {
            System.out.println("executor.wait(): " + e);
        }
    }
    
    public void cancelButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
        downloadsList.get(idx).cancel();
    }
    
    public void deleteButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
    }
    
    
    
}
