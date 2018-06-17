package com.nkcs.ereader.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * @author faunleaf
 * @date 2018/3/16
 */

@Entity
public class Chapter extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;
    private String title;
    @Index
    private Long bookId;
    private Integer sequence;

    private Long start;
    private Long end;
    private Boolean hasRead;
    @Generated(hash = 489034662)
    public Chapter(Long id, String title, Long bookId, Integer sequence, Long start,
            Long end, Boolean hasRead) {
        this.id = id;
        this.title = title;
        this.bookId = bookId;
        this.sequence = sequence;
        this.start = start;
        this.end = end;
        this.hasRead = hasRead;
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
    public Integer getSequence() {
        return this.sequence;
    }
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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
    public Boolean getHasRead() {
        return this.hasRead;
    }
    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }
}
