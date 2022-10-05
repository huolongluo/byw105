package huolongluo.bywx.utils;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A utility class for file io operaton
 */
public class FileUtils {

    /**
     * Is SDCard exist or not
     */
    public static boolean isHasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Format SDCard
     */
    public static void formatSDCard() {
        delFolder(Environment.getExternalStorageDirectory() + "/");
    }

    /**
     * Delete file or folder
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            new File(folderPath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete file in the folder
     */
    private static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        if (tempList == null) {
            return;
        }
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delFolder(temp.getAbsolutePath());
            }
        }
    }

    /**
     * Write File. If the file exists, it is overwritten.
     *
     * @param content  the buffer to be written.
     * @param fileName the name of the file to which this stream writes
     */
    public static void writeFile(byte[] content, String fileName) {
        writeFile(content, fileName, false);
    }

    /**
     * Write File. If the file exists, it is overwritten.
     *
     * @param content  the string to be written.
     * @param fileName the name of the file to which this stream writes
     */
    public static void writeFile(String content, String fileName) {
        writeFile(content, fileName, false);
    }

    /**
     * Write File
     *
     * @param content  the buffer to be written.
     * @param fileName the name of the file to which this stream writes
     * @param append   indicates whether or not to append to an existing file.
     */
    public static void writeFile(byte[] content, String fileName, boolean append) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(fileName, append);
            os.write(content);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Write File
     *
     * @param content  the string to be written.
     * @param fileName the name of the file to which this stream writes
     * @param append   indicates whether or not to append to an existing file.
     */
    public static void writeFile(String content, String fileName, boolean append) {
        if (content == null) {
            return;
        }
        writeFile(content.getBytes(), fileName, append);
    }

    /**
     * Copy file or directory
     *
     * @param srcFile the source file to copy the content
     * @param desFile the destination file to copy the data into.
     */
    public static void copyFile(File srcFile, File desFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (srcFile.isFile()) {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(desFile);
                byte[] buf = new byte[1024 * 8];
                int len = 0;
                while ((len = fis.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
            } else if (srcFile.isDirectory()) {
                File[] f = srcFile.listFiles();
                desFile.mkdir();
                for (int i = 0; i < f.length; i++) {
                    copyFile(f[i].getAbsolutePath(), desFile.getAbsolutePath() + File.separator + f[i].getName());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Copy file or directory
     *
     * @param srcPath the source filepath to copy the content
     * @param desPath the destination filepath to copy the data into.
     */
    public static void copyFile(String srcPath, String desPath) {
        File srcFile = new File(srcPath);
        File desFile = new File(desPath);
        copyFile(srcFile, desFile);
    }

    /**
     * Cut file
     *
     * @param srcPath the source filepath to cut the content
     * @param desPath the destination filepath to cut the data into.
     */
    public static void cutFile(String srcPath, String desPath) {
        copyFile(srcPath, desPath);
        File scrFile = new File(srcPath);
        if (scrFile.exists()) {
            delFolder(scrFile.getAbsolutePath());
        }
    }

    /**
     * Read file to string
     *
     * @param filePath an absolute or relative path specifying the file to read
     * @return the result string to read
     */
    public static String readFile(String filePath) {
        String result = null;
        InputStreamReader isr = null;
        StringBuilder sb = new StringBuilder();
        try {
            isr = new FileReader(filePath);
            int ch = 0;
            while (-1 != (ch = isr.read())) {
                sb.append((char) ch);
            }
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Read Stream to string
     *
     * @param inputStream an absolute or relative path specifying the file to read
     * @return the result string to read
     */
    public static String readStream2String(InputStream inputStream) {
        String result = null;
        InputStreamReader isr = null;
        StringBuilder sb = new StringBuilder();
        try {
            isr = new InputStreamReader(inputStream);
            int ch = 0;
            while (-1 != (ch = isr.read())) {
                sb.append((char) ch);
            }
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Read file to bytes
     *
     * @param filePath an absolute or relative path specifying the file to read
     * @return the result bytes to read
     */
    public static byte[] readFile2bytes(String filePath) {
        byte[] result = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            is = new FileInputStream(filePath);
            byte[] b = new byte[1024 * 8];
            int len = 0;
            while (-1 != (len = is.read(b))) {
                baos.write(b, 0, len);
            }
            baos.flush();
            result = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Read Stream to bytes
     *
     * @return the result bytes to read
     */
    public static byte[] readStream2bytes(InputStream is) {
        byte[] result = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] tempByte = new byte[1024 * 8];
            int len = 0;
            while (-1 != (len = is.read(tempByte))) {
                baos.write(tempByte, 0, len);
            }
            baos.flush();
            result = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void writeFilesInSandbox(Context c, String fileName, String content, int mode) {
        if (content == null) {
            return;
        }
        writeFilesInSandbox(c, fileName, content.getBytes(), mode);
    }

    public static void writeFilesInSandbox(Context c, String fileName, byte[] content, int mode) {
        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(fileName, mode);
            fos.write(content);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFilesFromSandbox(Context c, String fileName) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        String content = null;
        try {
            baos = new ByteArrayOutputStream();
            fis = c.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            content = baos.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
        return content;
    }

    public static LinkedList<File> listLinkedFiles(String strPath) {
        LinkedList<File> list = new LinkedList<File>();
        File dir = new File(strPath);
        File[] file = dir.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory()) list.add(file[i]);
            else System.out.println(file[i].getAbsolutePath());
        }
        File tmp;
        while (!list.isEmpty()) {
            tmp = list.removeFirst();
            if (tmp.isDirectory()) {
                file = tmp.listFiles();
                if (file == null) continue;
                for (int i = 0; i < file.length; i++) {
                    if (file[i].isDirectory()) list.add(file[i]);
                    else System.out.println(file[i].getAbsolutePath());
                }
            } else {
                System.out.println(tmp.getAbsolutePath());
            }
        }
        return list;
    }

    public static ArrayList<File> listFiles(String strPath, String endName) {
        ArrayList<File> arrayList = new ArrayList<File>();
        getFileList(arrayList, strPath, endName);
        return arrayList;
    }

    private static void getFileList(ArrayList<File> arrayList, String path, String endName) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (File file2 : fileList) {
                getFileList(arrayList, file2.getAbsolutePath(), endName);
            }
        } else {
            String namString = file.getAbsolutePath();
            if (namString.trim().equals("")) {
                if (file.length() > 0) {
                    arrayList.add(file);
                }
            } else if (namString.endsWith(endName)) {/* ".amr" */
                arrayList.add(file);
            }
        }
    }

    /**
     * 删除文件内容
     *
     * @param filePath
     * @param size
     */
    public static void deleteContent(String filePath, long size) {
        try {
            RandomAccessFile raFile = new RandomAccessFile(filePath, "rw");
            raFile.seek(size); // 利用RandomAccessFile定位到第size个字节，之后再读文件
            List<byte[]> list = new ArrayList<byte[]>();
            byte[] b = new byte[1024];

            while (-1 != raFile.read(b)) {
                list.add(b); // 将所读取出来的内容以byte数组为单位存放到一个ArrayList当中
            }

            FileOutputStream outFile = new FileOutputStream(filePath);
            for (Iterator<byte[]> i = list.iterator(); i.hasNext(); ) {
                outFile.write(i.next()); // 将ArrayList里的内容重新写回之前的文件
            }
            raFile.close();
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
