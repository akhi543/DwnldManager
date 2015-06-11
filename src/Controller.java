import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    ObservableList<DownloadEntry> downloadsList;
    
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
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("urlText"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressColumn.setCellFactory(ProgressBarTableCell.<DownloadEntry>forTableColumn());
        
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
       
        downloadsTable.getColumns().addAll(fileNameColumn, speedColumn, fileSizeColumn, progressColumn, statusColumn, urlColumn);
        box1.getChildren().add(downloadsTable);
        HBox.setHgrow(downloadsTable, Priority.ALWAYS);
        
        downloadsTable.setOnMouseClicked(e -> {
            int idx = downloadsTable.getSelectionModel().getSelectedIndex();
            if(downloadsList.get(idx).isPaused) {
                pauseResumeButton.setText("Resume");
            }
            else {
                pauseResumeButton.setText("Pause");
            }
        });
        
        executor = Executors.newFixedThreadPool(4);       
        downloadsList = FXCollections.observableArrayList();
        downloadsTable.setItems(downloadsList);
    }
    
    public void addDownloadButtonClicked() {
        try{
            String urlText = urlTextBox.getText();
            DownloadEntry task = new DownloadEntry(new URL(urlText));
            downloadsList.add(task);
            executor.execute(task);
        }
        catch(Exception e) {
            System.out.println("addDownloadButtonClicked: " + e);
        }
    }

    
    public void pauseResumeButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
        if(idx==-1) {
            new AlertBox("Error", "Please select an entry in table");
            return;
        }
        try {
            if(downloadsList.get(idx).isPaused==false) {
                downloadsList.get(idx).pause();  
                pauseResumeButton.setText("Resume");
                downloadsList.get(idx).isPaused = true;
            }
            else {
                URL t = downloadsList.get(idx).url;
                int d = downloadsList.get(idx).downloaded;
                downloadsList.remove(idx);
                DownloadEntry task = new DownloadEntry(t);
                task.downloaded = d;
                downloadsList.add(idx, task);
                executor.execute(task);
                pauseResumeButton.setText("Pause");
                downloadsList.get(idx).isPaused = false;
            }
        }
        catch(Exception e) {
            System.out.println("executor.wait(): " + e);
        }
    }
    
    //stops download, makes download irresumable(puts downloaded = 0)
    public void cancelButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
        if(idx==-1) {
            new AlertBox("Error", "Please select an entry in table");
            return;
        }
        downloadsList.get(idx).cancelIt();
    }
    
    //cancels and deletes entry from table
    public void deleteButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
        if(idx==-1) {
            new AlertBox("Error", "Please select an entry in table");
            return;
        }
        downloadsList.get(idx).cancelIt();
        downloadsList.remove(idx);
    }
}
