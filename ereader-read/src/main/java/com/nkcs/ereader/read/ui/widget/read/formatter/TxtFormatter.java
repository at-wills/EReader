package com.nkcs.ereader.read.ui.widget.read.formatter;

import android.util.Log;

import com.nkcs.ereader.base.entity.Book;
import com.nkcs.ereader.base.entity.Chapter;
import com.nkcs.ereader.base.utils.FileUtils;
import com.nkcs.ereader.base.utils.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author faunleaf
 * @date 2018/4/20
 */

public class TxtFormatter extends BookFormatter {

    /**
     * "序(章)|前言|楔子|引子"
     */
    private final static String PRECHAPTER_PATTERN = "^(.{0,8})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50)|(\u5f15\u5b50))(.{0,30})$";

    /**
     * "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
     */
    private final static String[] CHAPTER_PATTERNS = new String[] {
            "^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(.{0,8})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\f\t])(.{0,30})$",
            "^(.{0,8})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])$",
            "^(.{0,8})(\u6b63\u6587)(.{0,30})$",
            "^(.{0,8})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$" };

    private final static int BUFFER_SIZE = 512 * 1024;

    private final static int MIN_CHAPTER_LENGTH = 30;

    private Pattern mPreChapterPattern = Pattern.compile(PRECHAPTER_PATTERN, Pattern.MULTILINE);
    private Pattern mChapterPattern = null;

    private String mCharset;


    public TxtFormatter(Book book) {
        super(book);
        mCharset = FileUtils.getCharset(book.getPath());
    }

    @Override
    public List<Chapter> getChapterList() throws Exception {
        List<Chapter> chapterList = new ArrayList<>();
        RandomAccessFile bookStream = getFileStream(mBook.getPath());
        if (!checkExistChapter(bookStream)) {
            FileUtils.close(bookStream);
            return chapterList;
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        long curOffset = 0;
        int blockCount = 0;
        int length;

        while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
            String blockContent = new String(buffer, 0, length, mCharset);
            int seekPos = 0;
            int blockOffset = 0;
            if (curOffset == 0) {
                Matcher preMatcher = mPreChapterPattern.matcher(blockContent);
                if (preMatcher.find()) {
                    String chapterContent = blockContent.substring(seekPos, preMatcher.start());
                    seekPos += (chapterContent.length() + preMatcher.group().length());
                    blockOffset += (chapterContent.getBytes(mCharset).length + preMatcher.group().getBytes(mCharset).length);

                    Chapter preChapter = new Chapter();
                    preChapter.setTitle(preMatcher.group());
                    preChapter.setBookId(mBook.getId());
                    preChapter.setSequence(chapterList.size());
                    preChapter.setStart(curOffset + blockOffset);
                    preChapter.setEnd(preChapter.getStart());
                    preChapter.setHasRead(false);
                    chapterList.add(preChapter);
                }
            }

            int seekOffset = seekPos;
            Matcher matcher = mChapterPattern.matcher(blockContent.substring(seekOffset));
            while (matcher.find()) {
                String chapterContent = blockContent.substring(seekPos, matcher.start() + seekOffset);
                seekPos += (chapterContent.length() + matcher.group().length());
                blockOffset += (chapterContent.getBytes(mCharset).length + matcher.group().getBytes(mCharset).length);

                // 是否存在章节
                if (chapterList.size() > 0) {
                    Chapter lastChapter = chapterList.get(chapterList.size() - 1);
                    lastChapter.setEnd(lastChapter.getEnd() + chapterContent.getBytes(mCharset).length);

                    // 舍弃长度太短的章节
                    if (lastChapter.getEnd() - lastChapter.getStart() < MIN_CHAPTER_LENGTH) {
                        chapterList.remove(lastChapter);
                    }
                }

                Chapter curChapter = new Chapter();
                curChapter.setTitle(matcher.group());
                curChapter.setBookId(mBook.getId());
                curChapter.setSequence(chapterList.size());
                curChapter.setStart(curOffset + blockOffset);
                curChapter.setEnd(curChapter.getStart());
                curChapter.setHasRead(false);
                chapterList.add(curChapter);
            }

            curOffset += length;
            Chapter lastChapter = chapterList.get(chapterList.size() - 1);
            lastChapter.setEnd(curOffset);

            ++blockCount;
            // 当添加的block太多的时候，执行gc
            if (blockCount % 10 == 0) {
                System.gc();
                System.runFinalization();
            }
        }

        FileUtils.close(bookStream);
        System.gc();
        System.runFinalization();

        return chapterList;
    }

    @Override
    public BufferedReader loadChapter(Chapter chapter) throws Exception {
        RandomAccessFile bookStream = getFileStream(mBook.getPath());
        bookStream.seek(chapter.getStart());
        int len = (int)(chapter.getEnd() - chapter.getStart());
        byte[] content = new byte[len];
        bookStream.read(content, 0, len);
        FileUtils.close(bookStream);

        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content), mCharset));
    }

    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    private boolean checkExistChapter(RandomAccessFile bookStream) throws IOException {
        // 获取一个缓冲区的数据进行检查
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = bookStream.read(buffer, 0, buffer.length);
        for (String str : CHAPTER_PATTERNS) {
            Pattern pattern = Pattern.compile(str, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(new String(buffer, 0, length, mCharset));
            if (matcher.find()) {
                mChapterPattern = pattern;
                bookStream.seek(0);
                return true;
            }
        }

        bookStream.seek(0);
        return false;
    }

    private RandomAccessFile getFileStream(String filename) throws Exception {
        try {
            return new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception(ERROR_LOAD_BOOK);
        }
    }
}
