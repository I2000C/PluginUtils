package me.i2000c.plugin_utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader{
    private static final int BUFFER_SIZE = 1024;
    
    public static void downloadFile(String url, File path, boolean showProgress) throws IOException{
        //<editor-fold defaultstate="collapsed" desc="Code">
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try{            
            connection.connect();            
            try(InputStream inputStream = connection.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(path)){
                byte[] buffer = new byte[BUFFER_SIZE];
                int totalBytes = connection.getContentLength();
                int currentBytes = 0;
                int readedBytes = inputStream.read(buffer);
                while(readedBytes > 0){
                    outputStream.write(buffer, 0, readedBytes);
                    readedBytes = inputStream.read(buffer);
                    currentBytes += readedBytes;
                    if(showProgress){
                        Logger.log(String.format("&e  %-3d %%", (100 * currentBytes) / totalBytes));
                    }
                }
                if(showProgress){
                    Logger.log(String.format("&e  %-3d %%", 100));
                }
            }
            connection.disconnect();
        }catch(IOException ex){
            connection.disconnect();
            throw ex;
        }
//</editor-fold>
    }
}
