//import java.io.*;
//import java.io.RandomAccessFile;
//import java.net.*;
//import java.util.*;
//import sun.rmi.log.ReliableLog;
//
//public class Download extends Observable implements Runnable {
//    
//    
//    
//    
//    private void download()
//    {
//        Thread th=new Thread(this);
//        th.start();
//    }
//    
//    
//    @Override
//    public void run()
//    {
//        
//    }
//    
//    private void stateChanged()
//    {
//        setChanged();
//        notifyObservers();
//    }
//    
//    public static void main(String args[]) throws Exception
//    {
//        URL u=new URL("http://mp3light.net/assets/songs/393000-393999/393375-see-you-again-feat-charlie-puth--1428288074.mp3");
//        //URL u=new URL("http://hqwallbase.com/images/big/colours_of_nature-1531327.jpg");
//        Download d=new Download(u);
//    }
//    
//    void printheader(HttpURLConnection conn)
//    {
//        Map<String, List<String>> map = conn.getHeaderFields();
//	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//		System.out.println("Key : " + entry.getKey() + 
//                 " ,Value : " + entry.getValue());
//        }
//    }
//
//}
//
