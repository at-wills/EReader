package com.nkcs.ereader.base.utils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;

/**
 * @author faunleaf
 * @date 2018/4/21
 */

public class FileUtils {

    /**
     * 获取文件大小
     *
     * @param file
     * @return long
     */
    public static long getFileSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children) {
                    size += getFileSize(f);
                }
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size
     * @return String
     */
    public static String formatFileSize(long size) {
        if (size <= 0) {
            return "0B";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 删除文件
     *
     * @param fileName
     */
    public static synchronized void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isDirectory()){
                File[] files = file.listFiles();
                for (File subFile : files){
                    String path = subFile.getPath();
                    deleteFile(path);
                }
            }
            file.delete();
        }
    }

    public final static String CHARSET_GBK = "GBK";
    public final static String CHARSET_UTF8 = "UTF-8";

    /**
     * 获取文件的编码格式
     *
     * @param fileName
     * @return
     */
    public static String getCharset(String fileName) {
        BufferedInputStream bis = null;
        String charset = CHARSET_GBK;
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(fileName));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }
            if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = CHARSET_UTF8;
                checked = true;
            }

            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }

                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (!(0x80 <= read && read <= 0xBF)) {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = CHARSET_UTF8;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(bis);
        }
        return charset;
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fileToMD5(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            return streamToMD5(inputStream);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String streamToMD5(InputStream inputStream) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = inputStream.read(buffer)) > 0) {
                mdTemp.update(buffer, 0, numRead);
            }
            return toHexString(mdTemp.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static String toHexString(byte[] md) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        int j = md.length;
        char str[] = new char[j * 2];
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[2 * i] = hexDigits[byte0 >>> 4 & 0xf];
            str[i * 2 + 1] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
