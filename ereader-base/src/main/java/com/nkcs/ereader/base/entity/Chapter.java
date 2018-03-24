package com.nkcs.ereader.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author faunleaf
 * @date 2018/3/16
 */

@Entity
public class Chapter extends BaseEntity {

    @Id(autoincrement = true)
    private Long id;
    private String title;
    @Index
    private Long bookId;
    private Long start;
    private Long end;
    @Generated(hash = 2113796737)
    public Chapter(Long id, String title, Long bookId, Long start, Long end) {
        this.id = id;
        this.title = title;
        this.bookId = bookId;
        this.start = start;
        this.end = end;
    }
    @Generated(hash = 393170288)
    public Chapter() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Long getBookId() {
        return this.bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public Long getStart() {
        return this.start;
    }
    public void setStart(Long start) {
        this.start = start;
    }
    public Long getEnd() {
        return this.end;
    }
    public void setEnd(Long end) {
        this.end = end;
    }

}
