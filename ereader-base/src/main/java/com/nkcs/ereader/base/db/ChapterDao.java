package com.nkcs.ereader.base.db;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.nkcs.ereader.base.entity.Chapter;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAPTER".
*/
public class ChapterDao extends AbstractDao<Chapter, Long> {

    public static final String TABLENAME = "CHAPTER";

    /**
     * Properties of entity Chapter.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property BookId = new Property(2, Long.class, "bookId", false, "BOOK_ID");
        public final static Property Start = new Property(3, Long.class, "start", false, "START");
        public final static Property End = new Property(4, Long.class, "end", false, "END");
    }

    private Query<Chapter> book_ChapterListQuery;

    public ChapterDao(DaoConfig config) {
        super(config);
    }
    
    public ChapterDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAPTER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TITLE\" TEXT," + // 1: title
                "\"BOOK_ID\" INTEGER," + // 2: bookId
                "\"START\" INTEGER," + // 3: start
                "\"END\" INTEGER);"); // 4: end
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_CHAPTER_BOOK_ID ON \"CHAPTER\"" +
                " (\"BOOK_ID\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAPTER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Chapter entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        Long bookId = entity.getBookId();
        if (bookId != null) {
            stmt.bindLong(3, bookId);
        }
 
        Long start = entity.getStart();
        if (start != null) {
            stmt.bindLong(4, start);
        }
 
        Long end = entity.getEnd();
        if (end != null) {
            stmt.bindLong(5, end);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Chapter entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        Long bookId = entity.getBookId();
        if (bookId != null) {
            stmt.bindLong(3, bookId);
        }
 
        Long start = entity.getStart();
        if (start != null) {
            stmt.bindLong(4, start);
        }
 
        Long end = entity.getEnd();
        if (end != null) {
            stmt.bindLong(5, end);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Chapter readEntity(Cursor cursor, int offset) {
        Chapter entity = new Chapter( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // bookId
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // start
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // end
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Chapter entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setBookId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setStart(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setEnd(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Chapter entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Chapter entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Chapter entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "chapterList" to-many relationship of Book. */
    public List<Chapter> _queryBook_ChapterList(Long bookId) {
        synchronized (this) {
            if (book_ChapterListQuery == null) {
                QueryBuilder<Chapter> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.BookId.eq(null));
                book_ChapterListQuery = queryBuilder.build();
            }
        }
        Query<Chapter> query = book_ChapterListQuery.forCurrentThread();
        query.setParameter(0, bookId);
        return query.list();
    }

}
