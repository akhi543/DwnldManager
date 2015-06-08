
import java.io.InputStream;
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
//    private int status;
    private int downloaded;
    private int size;
    private URL newurl = null;
    
    public static final String statuses[]={"Downloading","Error","Paused","Cancelled","Complete"};
    
    public static final int Downloading = 0;
    public static final int Error = 1;
    public static final int Paused = 2;
    public static final int Cancelled = 3;
    public static final int Complete = 4;
    
    final int MAX_BUFFER_SIZE=50*1024;
    
    public DownloadEntry(URL ur) {
        url = ur;
        fileName = new SimpleStringProperty(url.toString().substring(url.toString().lastIndexOf("/")+1));
        urlText = new SimpleStringProperty(url.toString());
        size = -1;
        downloaded = 0;
        System.out.println("Constr called..............");
        System.out.println(getFileName());
        System.out.println(getUrlText());
        System.out.println(getFileSize());
        this.updateMessage(statuses[Downloading]);
    }
    
    @Override
    protected Void call() {
        RandomAccessFile file=null;
        InputStream stream=null;
        try
        {
            HttpURLConnection connect=(HttpURLConnection)url.openConnection();
            connect.setRequestProperty("Range","bytes"+downloaded+"-");
            connect.connect();
            if (connect.getResponseCode()/100!=2)
            {
                error();
            }
            if (size==-1)
            {
                HttpURLConnection sizecon = (HttpURLConnection)url.openConnection();
                sizecon.setRequestMethod("HEAD");
                sizecon.getInputStream();
                size = sizecon.getContentLength();
                stateChanged();
            }
            
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Range","bytes"+downloaded+"-");
            
            if (size==-1)
            {
                newurl = connect.getURL();
                String searchurl = String.valueOf(url);
                
                con.setRequestProperty("Referer",String.valueOf(newurl)); 
                
            }
            con.connect();
            if (size==-1)
            {
                HttpURLConnection sizecon = (HttpURLConnection)url.openConnection();
                sizecon.setRequestMethod("HEAD");
                sizecon.setRequestProperty("Referer",String.valueOf(newurl)); 
                sizecon.getInputStream();
                size = sizecon.getContentLength();
                stateChanged();
            }
            
            fileSize = new SimpleIntegerProperty(size);
            
//            Controller.addEntryInTable();
            
            file = new RandomAccessFile(getFileName(), "rw");
            file.seek(downloaded);
            stream = con.getInputStream();
            
            while (this.getMessage() == statuses[Downloading])
            {
                byte buffer[];
                if (size-downloaded>MAX_BUFFER_SIZE)
                {
                    buffer=new byte[MAX_BUFFER_SIZE];
                }
                else
                    buffer=new byte[size-downloaded];
                
                int c=stream.read(buffer);
                if (c==-1)
                    break;
                file.write(buffer,0,c);
                downloaded+=c;
                stateChanged();
            }
            if (getMessage()==statuses[Downloading])
            {
                this.updateMessage(statuses[Complete]);
                stateChanged();
            } 
        }
        catch(Exception e)
        {
            System.out.println("call: " + e);
        }
        finally
        {
            if (file!=null)
            {
                try{file.close();}catch(Exception E){}
            }
            if (stream!=null)
            {
                try{stream.close();}catch(Exception E){}
            }
        }
        return null;
    }
    
    
    
    private void stateChanged()
    {
        System.out.println("state Changed called");
        Controller.handleChange();
    }
    
    void printheader(HttpURLConnection conn)
    {
        Map<String, List<String>> map = conn.getHeaderFields();
	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
		System.out.println("Key : " + entry.getKey() + 
                 " ,Value : " + entry.getValue());
        }
    }
//    
//    
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
        this.updateMessage(statuses[Paused]);
        stateChanged();
    }
//    
//    void resume()
//    {
//        this.updateMessage(statuses[Downloading]);
//        stateChanged();
//        download();
//    }
//    
    public void cancelIt()
    {
        this.updateMessage(statuses[Cancelled]);
        stateChanged();
    }
    
    void error()
    {
        this.updateMessage(statuses[Error]);
        stateChanged();
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
