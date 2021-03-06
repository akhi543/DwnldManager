import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.io.File;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.Observable;
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
    final int MAX_BUFFER_SIZE=1024;
    private int status;
    public static final String statuses[]={"Downloading","Error","Paused","Cancelled","Complete"};
    public static final int Downloading=0;
    public static final int Error=1;
    public static final int Paused=2;
    public static final int Cancelled=3;
    public static final int Complete=4;
    double speedlimit=100000;
    public boolean isPaused = false;
    public boolean pauseCancel;
    public final boolean PAUSE = false;
    public final boolean CANCEL = true;
    
    
    //Constructor
    public DownloadEntry(URL ur) throws Exception{
        url = ur;
        fileName = new SimpleStringProperty(url.toString().substring(url.toString().lastIndexOf("/")+1));
        urlText = new SimpleStringProperty(url.toString());
        speed = new SimpleDoubleProperty(0.0);
        fileSize = new SimpleIntegerProperty(size);
        this.updateMessage("Downloading");
    }
    
    public DownloadEntry(URL ur,int dnld,int sz,URL newur)
    {
        url=ur;
        downloaded=dnld;
        size=sz;
        refererUrl=newur;
        fileName = new SimpleStringProperty(url.toString().substring(url.toString().lastIndexOf("/")+1));
        urlText = new SimpleStringProperty(url.toString());
        speed = new SimpleDoubleProperty(0.0);
        fileSize=new SimpleIntegerProperty(size);
        this.updateMessage("Downloading");
        
    }
    
    void getSize() throws Exception
    {
            if (size!=-1)
                return;
            HttpURLConnection connect=(HttpURLConnection)url.openConnection();
            connect.setRequestProperty("Range","bytes="+downloaded+"-");
            connect.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            System.out.println("here");
            connect.connect();
            System.out.println("here2");
            connect.getInputStream();
            refererUrl=connect.getURL();
            connect.disconnect();
            System.out.println("here3");
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestProperty("Range","bytes="+downloaded+"-");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            System.out.println(refererUrl);
            con.setRequestProperty("Referer",String.valueOf(refererUrl)); 
            size = con.getContentLength();
            con.disconnect();
            System.out.println(size);
            fileSize.set(size);
    }

    @Override
    protected Void call() {
        RandomAccessFile file=null;
        InputStream stream=null;
        try {
            getSize();
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            //con.setRequestProperty("Range","bytes"+downloaded+"-");
            con.setRequestProperty("Range","bytes="+downloaded+"-");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            con.setRequestProperty("Referer",String.valueOf(refererUrl)); 
            con.connect();
            
            updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            
            String filename = url.getFile();
            filename = filename.substring(filename.lastIndexOf("/")+1);
            file = new RandomAccessFile(filename, "rw");
            file.seek(downloaded);
            stream = con.getInputStream();
            
            updateMessage("Downloading");
            status = Downloading;
            
            while (status==Downloading) {
                int downloadinonesec=0;
                long startplusone = System.currentTimeMillis()+1000;
                long timer=0;
                System.out.println(speedlimit);
                while (System.currentTimeMillis()<=startplusone)
                //while (timer<=1000)
                {
                    
                    byte buffer[];
                    if (size-downloaded>MAX_BUFFER_SIZE) {
                        buffer=new byte[MAX_BUFFER_SIZE];
                    }
                    else {
                        buffer=new byte[size-downloaded];
                    }
                    //long start=System.currentTimeMillis();
                    int c=stream.read(buffer);
                    //timer+=System.currentTimeMillis()-start;
                    if (c==-1){
                        speed.set(0);
                        status=Complete;
                        break;
                    }
                    file.write(buffer,0,c);
                    downloaded += c;
                    downloadinonesec +=c;
                    updateProgress((1.0 *downloaded)/size, 1);
                    if (downloadinonesec>speedlimit*1024)
                    {
                        System.out.println(speedlimit);
                        System.out.println(downloadinonesec);
                        System.out.println(startplusone-System.currentTimeMillis());
                        try{
                            if (startplusone-System.currentTimeMillis()+10>0)
                        Thread.sleep(startplusone-System.currentTimeMillis()+20);
                        }
                        catch(InterruptedException E)
                        {}
                        System.out.println(startplusone-System.currentTimeMillis());
                    }
                    if(this.isCancelled()) {
                        speed.set(0.0);
                        con.disconnect();
                        if(pauseCancel==PAUSE) {
                            status = Paused;
                            updateMessage("Paused");
                        }
                        else {
                            status = Cancelled;
                            downloaded = 0;
                            updateMessage("Cancelled");
                        }
                        return null;
                    }
                    
                }
                
                speed.set(downloadinonesec/1000);
                
            }
            if (status==Complete) {
                //status=Complete;
                con.disconnect();
                updateMessage("Complete");
                String temp = String.format(fileName.get() + " downloaded successfuly.");
                //All interactions with JavaFX objects (including creation) must be done on JFX thread,
                //if you want to access those JFX objects from another thread - use runLater or runAndWait methods. 
                Platform.runLater(() -> {
                    new AlertBox("Success", temp);
                });
            } 
            
        }
        catch(Exception E) {
            this.updateMessage("Error");
            E.printStackTrace();
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
    
    void pause()
    {
        if(status==Downloading) {
            pauseCancel = PAUSE;
            //sends interrupt, carries out following lines immediately, interrupt is handled in near future using isCancelled()
            cancel(); 
            System.out.println(statuses[status]);
        }
    }
    
    public void cancelIt()
    {
        System.out.println(statuses[status]);
        if(status==Downloading || status==Paused) {
            pauseCancel = CANCEL;
            cancel();
            downloaded = 0;
            updateMessage("Cancelled"); //required if state was puased
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
