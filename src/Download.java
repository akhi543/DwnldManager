/*import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class DownloadEntry extends Task<Void> {

    public static final int NUM_ITERATIONS = 100;
    
    public SimpleStringProperty fileName;
    public SimpleStringProperty urlText;
    public SimpleIntegerProperty fileSize;
    
    private URL url;
    private int downloaded = 0;
    private int size = -1;
    private URL newurl = null;
    
    final int MAX_BUFFER_SIZE=50*1024;
    
    private int status;
    public static final String statuses[]={"Downloading","Error","Paused","Cancelled","Complete"};
    
    public static final int Downloading=0;
    public static final int Error=1;
    public static final int Paused=2;
    public static final int Cancelled=3;
    public static final int Complete=4;
    
    //Constructor
    public DownloadEntry(URL ur) throws Exception{
        url = ur;
        fileName = new SimpleStringProperty(url.toString().substring(url.toString().lastIndexOf("/")+1));
        urlText = new SimpleStringProperty(url.toString());
        fileSize = new SimpleIntegerProperty(size);
    }
    
    @Override
    protected Void call() {
        RandomAccessFile file=null;
        InputStream stream=null;
        try
        {
            HttpURLConnection connect=(HttpURLConnection)url.openConnection();
            connect.setRequestProperty("Range","bytes"+downloaded+"-");
            connect.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            connect.connect();
            if (connect.getResponseCode()/100!=2) {
                this.updateMessage("Error");
                return null;
            }
            if (size==-1) {
                HttpURLConnection sizecon=(HttpURLConnection)url.openConnection();
                sizecon.setRequestMethod("HEAD");
                sizecon.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
                sizecon.getInputStream();
                size=sizecon.getContentLength();
            }
            
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestProperty("Range","bytes"+downloaded+"-");
            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            if (size==-1) {
                newurl=connect.getURL();
                String searchurl=String.valueOf(url);
                con.setRequestProperty("Referer",String.valueOf(newurl)); 
            }
            con.connect();
            if (size==-1) {
                HttpURLConnection sizecon=(HttpURLConnection)url.openConnection();
                sizecon.setRequestMethod("HEAD");
                sizecon.setRequestProperty("Referer",String.valueOf(newurl)); 
                sizecon.getInputStream();
                size = sizecon.getContentLength();
            }
            this.updateMessage("Downloading");
            updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            String filename = url.getFile();
            filename = filename.substring(filename.lastIndexOf("/")+1);
            file = new RandomAccessFile(filename, "rw");
            file.seek(downloaded);
            stream = con.getInputStream();
            
            while (status==Downloading) {
                byte buffer[];
                if (size-downloaded>MAX_BUFFER_SIZE) {
                    buffer=new byte[MAX_BUFFER_SIZE];
                }
                else {
                    buffer=new byte[size-downloaded];
                }
                
                int c=stream.read(buffer);
                if (c==-1){
                    break;
                }
                file.write(buffer,0,c);
                downloaded += c;
                updateProgress((1.0 *downloaded)/size, 1);
                updateMessage("Downloading");
                status = Downloading;
                Thread.sleep(50);
            }
            if (status==Downloading) {
                status=Complete;
                updateMessage("Complete");
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
    
//    public URL getURL()
//    {
//        return url;
//    }
//    
//    public int getSize()
//    {
//        return size;
//    }
//    
//    public String getStatus()
//    {
//        return this.getMessage();
//    }
//    
//    @Override
//    public final double getProgress()
//    {
//        return ((float)downloaded*100)/size;
//    }
    
    void pause()
    {
        this.updateMessage("Paused");
//        stateChanged();
    }
//    
//    void resume()
//    {
//        this.updateMessage("Downloading]);
//        stateChanged();
//        download();
//    }
//    
    public void cancelIt()
    {
        this.updateMessage("Cancelled");
//        stateChanged();
    }
    
    public String getFileName() {
        if(fileName==null) return null;
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName = new SimpleStringProperty(fileName);
    }

    public String getUrlText() {
        if(urlText==null) return null;
        return urlText.get();
    }

    public void setUrlText(String urlText) {
        this.urlText = new SimpleStringProperty(urlText);
    }

    public Integer getFileSize() {
        if(fileSize==null) return null;
        return fileSize.get();
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = new SimpleIntegerProperty(fileSize);
    } 
}
*/