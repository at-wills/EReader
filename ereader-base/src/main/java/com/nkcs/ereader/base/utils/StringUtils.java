package com.nkcs.ereader.base.utils;

import android.support.annotation.StringRes;

import com.nkcs.ereader.base.BaseApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author faunleaf
 * @date 2018/4/1
 */

public class StringUtils {

    public static String toFirstCapital(String str) {
        return str.substring(0, 1).toUpperCase()+str.substring(1);
    }

    public static String getString(@StringRes int id) {
        return BaseApplication.getContext().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        return BaseApplication.getContext().getResources().getString(id, formatArgs);
    }

    public static String dateConvert(long time,String pattern) {
        return dateConvert(new Date(time), pattern);
    }

    public static String dateConvert(Date date,String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将文本中的半角字符，转换成全角字符
     *
     * @param input
     */
    public static String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                // 半角空格
                c[i] = (char) 12288;
                continue;
            }
            // 根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i]> 32 && c[i]< 127) {
                // 其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 将文本中的全角字符，转换成半角字符
     *
     * @param input
     */
    public static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                // 全角空格
                c[i] = (char) 32;
                continue;
            }

            if (c[i]> 65280&& c[i]< 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 拼接字符串
     *
     * @param delimiter
     * @param elements
     */
    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence element : elements) {
            sb.append(element).append(delimiter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
