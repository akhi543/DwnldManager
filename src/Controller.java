import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;


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
        
        
        downloadsTable.setRowFactory(new Callback<TableView<DownloadEntry>, TableRow<DownloadEntry>>() {  
            @Override  
            public TableRow<DownloadEntry> call(TableView<DownloadEntry> tableView) {  
                final TableRow<DownloadEntry> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  
                final MenuItem removeMenuItem = new MenuItem("Speed Limit");  
                removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {  
                    @Override  
                    public void handle(ActionEvent event) {  
                        TextInputDialog dialog = new TextInputDialog("walter");
                        dialog.setTitle("Text Input Dialog");
                        dialog.setHeaderText("Set Speed Limit");
                        dialog.setContentText("Enter Speed in kBps (0 for unlimited)");
                        
                        // Traditional way to get the response value.
                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()){
                            Double lim=Double.valueOf(String.valueOf(result.get()));
                            if (lim==0)
                                lim=10000000.0;
                            row.getItem().speedlimit=lim;
                        System.out.println("SpeedLimit " + lim);
                        } 
                    }     
                });  
                contextMenu.getItems().add(removeMenuItem);  
               // Set context menu on row, but use a binding to make it only show for non-empty rows:  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row ;  
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
            //System.out.println("addDownloadButtonClicked: " + e);
            e.printStackTrace();
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
                int sz=downloadsList.get(idx).size;
                int dnld=downloadsList.get(idx).downloaded;
                URL newur=downloadsList.get(idx).refererUrl;
                downloadsList.remove(idx);
                DownloadEntry task = new DownloadEntry(t,dnld,sz,newur);
                downloadsList.add(idx, task);
                executor.execute(task);
                pauseResumeButton.setText("Pause");
                downloadsList.get(idx).isPaused = false;
            }
        }
        catch(Exception e) {
            //System.out.println("executor.wait(): " + e);
            e.printStackTrace();
        }
    }
    
    public void cancelButtonClicked() {
        int idx = downloadsTable.getSelectionModel().getSelectedIndex();
        if(idx==-1) {
            new AlertBox("Error", "Please select an entry in table");
            return;
        }
        downloadsList.get(idx).cancelIt();
    }
    
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
