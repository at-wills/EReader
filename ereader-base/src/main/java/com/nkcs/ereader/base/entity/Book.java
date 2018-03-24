package com.nkcs.ereader.base.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.nkcs.ereader.base.db.DaoSession;
import com.nkcs.ereader.base.db.ChapterDao;
import com.nkcs.ereader.base.db.BookDao;

/**
 * @author faunleaf
 * @date 2018/3/16
 */

@Entity
public class Book extends BaseEntity {

    @Id(autoincrement = true)
    private Long id;
    private String title;
    @Index(unique = true)
    private String hash;
    private String cover;
    private Integer totalChapter;
    private Boolean hasFormat;
    private Date created;

    @ToOne(joinProperty = "id")
    private Chapter lastRead;
    private Double progress;

    @ToMany(referencedJoinProperty = "bookId")
    private List<Chapter> chapterList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1097957864)
    private transient BookDao myDao;

    @Generated(hash = 1697284973)
    public Book(Long id, String title, String hash, String cover,
            Integer totalChapter, Boolean hasFormat, Date created,
            Double progress) {
        this.id = id;
        this.title = title;
        this.hash = hash;
        this.cover = cover;
        this.totalChapter = totalChapter;
        this.hasFormat = hasFormat;
        this.created = created;
        this.progress = progress;
    }

    @Generated(hash = 1839243756)
    public Book() {
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

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getTotalChapter() {
        return this.totalChapter;
    }

    public void setTotalChapter(Integer totalChapter) {
        this.totalChapter = totalChapter;
    }

    public Boolean getHasFormat() {
        return this.hasFormat;
    }

    public void setHasFormat(Boolean hasFormat) {
        this.hasFormat = hasFormat;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Double getProgress() {
        return this.progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    @Generated(hash = 1683791001)
    private transient Long lastRead__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 664993600)
    public Chapter getLastRead() {
        Long __key = this.id;
        if (lastRead__resolvedKey == null || !lastRead__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChapterDao targetDao = daoSession.getChapterDao();
            Chapter lastReadNew = targetDao.load(__key);
            synchronized (this) {
                lastRead = lastReadNew;
                lastRead__resolvedKey = __key;
            }
        }
        return lastRead;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 642867227)
    public void setLastRead(Chapter lastRead) {
        synchronized (this) {
            this.lastRead = lastRead;
            id = lastRead == null ? null : lastRead.getId();
            lastRead__resolvedKey = id;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1501524070)
    public List<Chapter> getChapterList() {
        if (chapterList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChapterDao targetDao = daoSession.getChapterDao();
            List<Chapter> chapterListNew = targetDao._queryBook_ChapterList(id);
            synchronized (this) {
                if (chapterList == null) {
                    chapterList = chapterListNew;
                }
            }
        }
        return chapterList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1743307878)
    public synchronized void resetChapterList() {
        chapterList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1115456930)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookDao() : null;
    }

}
