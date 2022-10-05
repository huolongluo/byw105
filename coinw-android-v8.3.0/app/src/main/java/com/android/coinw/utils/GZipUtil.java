package com.android.coinw.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import huolongluo.byw.log.Logger;

public class GZipUtil {
    //    private static final Logger LOG = Logger.getLogger(GZipUtil.class);
    private static final int BUFF_SIZE = 4096;

    /***************************************************************************
     * 压缩GZip
     *
     * @param data
     * @author taoyi
     * @return
     */
    public static byte[] gZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data);
            gzip.finish();
            gzip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /***************************************************************************
     * 解压GZip
     *
     * @param data
     * @author taoyi
     * @return
     */
    public static byte[] unGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /***************************************************************************
     * 压缩Zip
     *
     * @param data
     * @author zhouxaobo
     * @return
     */
    public static byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /***************************************************************************
     * 解压Zip
     *
     * @param data
     * @author zhouxxiaobo
     * @return
     */
    public static byte[] unZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * @param str 待压缩的字符串
     * @return
     * @throws IOException
     * @author zhouxiaobo
     */
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        System.out.println("压缩后：" + out.toString());
        return out.toString("GBK");
    }

    /**
     * @param str 已压缩的字符串
     * @return
     * @throws IOException
     * @author taoyi
     */
    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("GBK"));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
        return out.toString("GBK");
    }

    /**
     * 解压c++压缩的zlib
     *
     * @param compressedData
     * @return
     */
    public static byte[] unZipInflate(byte[] compressedData) {
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);
        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(
                compressedData.length);
        // Decompress the data
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                //e.printStackTrace();
                Logger.getInstance().error("解压C++压缩包出错！");
                break;
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
            //e.printStackTrace();
            Logger.getInstance().error("流关闭异常！");
        }
        // Get the decompressed data
        return bos.toByteArray();
    }

    public static String inflater(byte[] buff) {
        String str = null;
        int len = buff.length;
        byte[] result = new byte[len];
        if ((null == buff) || (0 == len)) {
            return null;
        }
        try {
            Inflater decompresser = new Inflater();
            int resultlen = 0;
            decompresser.setInput(buff, 0, buff.length);
            resultlen = decompresser.inflate(result);
            decompresser.end();
            str = new String(result, 0, resultlen, "UTF-8");
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (DataFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 压缩压缩的zlib
     *
     * @param compressedData
     * @return
     * @author zhouxiaobo
     */
    public static byte[] ZipDnflate(byte[] compressedData)
            throws DataFormatException {
        Deflater compressor = new Deflater();
        compressor.setInput(compressedData);
        compressor.finish();

        // Create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(
                compressedData.length);
        // Decompress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get the decompressed data
/*      byte [] byteArrey=bos.toByteArray();
        String strZip=new String(byteArrey);*/
        return bos.toByteArray();
    }

    /**
     * 解压协议包体部分 ,压缩格式为zip
     */
    private static Object extract(byte[] compressed) {
        if (compressed == null)
            return null;
        // byte[] compressed;
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        Object decompressed = null;
        try {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            byte[] buffer = new byte[BUFF_SIZE];
            int offset = -1;
            zin = new ZipInputStream(in);
            ZipEntry entry = zin.getNextEntry();
            if (entry != null) {
                while ((offset = zin.read(buffer)) != -1) {
                    out.write(buffer, 0, offset);
                }
            }
            ByteArrayInputStream byteInput = new ByteArrayInputStream(out
                    .toByteArray());
            ObjectInputStream ois = new ObjectInputStream(byteInput);
            decompressed = ois.readObject();
        } catch (ZipException e1) {
            // TODO Auto-generated catch block
            Logger.getInstance().info("获得压缩流数据出现异常！");
            e1.printStackTrace();
        } catch (IOException e1) {
            Logger.getInstance().info("I/O流出现异常！");
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                    zin = null;
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }
}