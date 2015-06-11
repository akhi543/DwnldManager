import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class DownloadEntry extends Task<Void> {
    
    public SimpleStringProperty fileName;
    public SimpleStringProperty urlText;
    public SimpleIntegerProperty fileSize;
    public SimpleDoubleProperty speed;
    
    public URL url;
    public int downloaded;
    public int size = -1;
    public URL refererUrl = null;
    final int MAX_BUFFER_SIZE=50*1024;
    private int status;
    public static final String statuses[]={"Downloading","Error","Paused","Cancelled","Complete"};
    public static final int Downloading=0;
    public static final int Error=1;
    public static final int Paused=2;
    public static final int Cancelled=3;
    public static final int Complete=4;
    public boolean isPaused = false;
    public boolean pauseCancel;
    public final boolean PAUSE = false;
    public final boolean CANCEL = true;
    RandomAccessFile file=null;
    InputStream stream=null;
        
    //Constructor
    public DownloadEntry(URL ur) throws Exception{
        url = ur;
        fileName = new SimpleStringProperty(url.toString().substring(url.toString().lastIndexOf("/")+1));
        urlText = new SimpleStringProperty(url.toString());
        speed = new SimpleDoubleProperty(0.0);
        HttpURLConnection connect=null, sizecon=null, con=null;
        try {
            connect=(HttpURLConnection)url.openConnection();
            connect.setRequestProperty("Range","bytes"+downloaded+"-");
            connect.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            connect.connect();
            if (connect.getResponseCode()/100!=2) {
                this.updateMessage("Error");
                return;
            }
            if (size==-1) {
                sizecon = (HttpURLConnection)url.openConnection();
                sizecon.setRequestMethod("HEAD");
                sizecon.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
                sizecon.getInputStream();
                size=sizecon.getContentLength();
            }

            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Range","bytes"+downloaded+"-");

            if (size==-1) {
                refererUrl = connect.getURL();
                con.setRequestProperty("Referer",String.valueOf(refererUrl)); 
            }
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            con.connect();
            if (size==-1) {
                sizecon=(HttpURLConnection)url.openConnection();
                sizecon.setRequestMethod("HEAD");
                sizecon.setRequestProperty("Referer",String.valueOf(refererUrl)); 
                sizecon.getInputStream();
                size = sizecon.getContentLength();
            }
            fileSize = new SimpleIntegerProperty(size);
        }
        catch(Exception e) {
            System.out.println("constructor: " + e);
        }
        finally {
            if(connect!=null) { connect.disconnect(); }
            if(con!=null) { con.disconnect(); }
            if(sizecon!=null) { sizecon.disconnect(); }
        }
        status = Downloading;
        this.updateMessage("Downloading");
    }

    @Override
    protected Void call() {
        try {
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Range","bytes"+downloaded+"-");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            con.setRequestProperty("Referer", refererUrl.toString());
            con.connect();
            if (con.getResponseCode()/100!=2) {
                this.updateMessage("Error");
                return null;
            }
            
            updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            
            String filename = url.getFile();
            filename = filename.substring(filename.lastIndexOf("/")+1);
            file = new RandomAccessFile(filename, "rw");
            file.seek(downloaded);
            stream = con.getInputStream();
            
            long currTime = System.currentTimeMillis();
            
            int pastDownloaded = downloaded;
            
            while (status==Downloading) {
                byte buffer[];
                if (size-downloaded>MAX_BUFFER_SIZE) {
                    buffer=new byte[MAX_BUFFER_SIZE];
                }
                else {
                    buffer=new byte[size-downloaded];
                }
                
                int c = stream.read(buffer);
                if (downloaded==size){  //previous c==-1
                    break;
                }
                file.write(buffer,0,c);
                downloaded += c;
                updateProgress((1.0 *downloaded)/size, 1);
                updateMessage("Downloading");
                status = Downloading;
                long elapsedTime = System.currentTimeMillis() - currTime;
                speed.set((1.0 * (downloaded - pastDownloaded)) / elapsedTime);  //inkBsps
                if(this.isCancelled()) {
                    speed.set(0.0);
                    if(pauseCancel==PAUSE) {
                        status = Paused;
                        updateMessage("Paused");
                    }
                    else {
                        status = Cancelled;
                        updateMessage("Cancelled");
                    }
                    return null;
                }
            }
            if (status==Downloading) {
                status = Complete;
                updateMessage("Complete");
                String temp = String.format(fileName.get() + " downloaded successfuly.");
                //All interactions with JavaFX objects (including creation) must be done on JFX thread,
                //if you want to access those JFX objects from another thread - use runLater or runAndWait methods. 
                Platform.runLater(() -> {
                    new AlertBox("Success", temp);
                });
            } 
        }
        catch(Exception e) {
            status = Error;
            this.updateMessage("Error");
            System.out.println("call: " + e);
        }
        finally {
            if (file!=null) {
                try{file.close();}catch(Exception E){}
            }
            if (stream!=null) {
                try{stream.close();}catch(Exception E){}
            }
        }
        return null;
    }
    
    
    void pause() {
        if(status==Downloading) {
            pauseCancel = PAUSE;
            //sends interrupt, carries out following lines immediately, interrupt is handled in near future using isCancelled()
            cancel(); 
            System.out.println(statuses[status]);
        }
    }
    
    public void cancelIt() {
        System.out.println(statuses[status]);
        if(status==Downloading || status==Paused) {
            pauseCancel = CANCEL;
            cancel();
            downloaded = 0;
        }
    }
    
    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }
    
    public String getUrlText() {
        return urlText.get();
    }

    public void setUrlText(String urlText) {
        this.urlText.set(urlText);
    }

    public StringProperty urlTextProperty() {
        return urlText;
    }
    
    public Integer getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize.set(fileSize);
    } 

    public IntegerProperty fileSizeProperty() {
        return fileSize;
    }
    
    public Double getSpeed() {
        return speed.get();
    }

    public void setSpeed(Double speed) {
        this.speed.set(speed);
    }
    
    public DoubleProperty speedProperty() {
        return speed;
    }
}
