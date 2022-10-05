package huolongluo.byw.util;

import android.util.Log;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;

import huolongluo.byw.byw.base.BaseApp;

/**
 * Created by Administrator on 2018/8/16 0016.
 */

public class StreamUtils {


    public interface  ReadListener{

        void onListener(String text);
    }

    public static  void saveText(String text,String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeText(text,fileName);
            }
        }).start();
    }

    public static void writeText(String text,String fileName){
        BufferedWriter out = null;
        FileLock fileLock = null;
        File file = null;
        Log.e("缓存文件了"," fileName=  "+fileName+ "======  保存== ："+text);
        try {
            String name = fileName;
            file =FileUtil.getOwnCacheDirectory(BaseApp.getSelf(),name);

            if (!file.exists()) {
                file.createNewFile();
            }else {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file, true);

            fileLock = outputStream.getChannel().tryLock();
            if (fileLock != null) {
                out = new BufferedWriter(new OutputStreamWriter(outputStream));


                out.write(text);
                out.flush();
                out.close();
                out = null;


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OverlappingFileLockException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileLock != null && fileLock.isValid()) {
                try {
                    fileLock.release();
                    fileLock = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readText(String fileName, ReadListener listener){
          StringBuilder stringBuilder=new StringBuilder();
        String name = fileName;
       File file =FileUtil.getOwnCacheDirectory(BaseApp.getSelf(),name);
        InputStream in=null;
//读取文件(字节流)
        try {
             in = new FileInputStream(file);
            byte[] bytes = new byte[2048];
            int n = -1;

            while ((n = in.read(bytes,0,bytes.length)) != -1) {
                //转换成字符串
                String str = new String(bytes,0,n, StandardCharsets.UTF_8);
                System.out.println(str);
                //写入相关文件
                stringBuilder.append(str);
            }
            //关闭流
            in.close();
            in=null;

            Log.e("缓存文件了"," fileName=  "+fileName+"======  读取== ："+stringBuilder.toString());
          listener.onListener(stringBuilder.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                    in=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void getText(String fileName,ReadListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                readText(fileName,listener);
            }
        }).start();
    }

}
