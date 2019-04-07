package com.enjoyshop.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ADDRESS".
*/
public class AddressDao extends AbstractDao<Address, Long> {

    public static final String TABLENAME = "ADDRESS";

    /**
     * Properties of entity Address.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property AddressId = new Property(0, Long.class, "addressId", true, "_id");
        public final static Property UserId = new Property(1, Long.class, "userId", false, "USER_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Phone = new Property(3, String.class, "phone", false, "PHONE");
        public final static Property IsDefaultAddress = new Property(4, boolean.class, "isDefaultAddress", false, "IS_DEFAULT_ADDRESS");
        public final static Property BigAddress = new Property(5, String.class, "bigAddress", false, "BIG_ADDRESS");
        public final static Property SmallAddress = new Property(6, String.class, "smallAddress", false, "SMALL_ADDRESS");
        public final static Property Address = new Property(7, String.class, "address", false, "ADDRESS");
    }


    public AddressDao(DaoConfig config) {
        super(config);
    }
    
    public AddressDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ADDRESS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: addressId
                "\"USER_ID\" INTEGER," + // 1: userId
                "\"NAME\" TEXT," + // 2: name
                "\"PHONE\" TEXT," + // 3: phone
                "\"IS_DEFAULT_ADDRESS\" INTEGER NOT NULL ," + // 4: isDefaultAddress
                "\"BIG_ADDRESS\" TEXT," + // 5: bigAddress
                "\"SMALL_ADDRESS\" TEXT," + // 6: smallAddress
                "\"ADDRESS\" TEXT);"); // 7: address
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ADDRESS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Address entity) {
        stmt.clearBindings();
 
        Long addressId = entity.getAddressId();
        if (addressId != null) {
            stmt.bindLong(1, addressId);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(4, phone);
        }
        stmt.bindLong(5, entity.getIsDefaultAddress() ? 1L: 0L);
 
        String bigAddress = entity.getBigAddress();
        if (bigAddress != null) {
            stmt.bindString(6, bigAddress);
        }
 
        String smallAddress = entity.getSmallAddress();
        if (smallAddress != null) {
            stmt.bindString(7, smallAddress);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(8, address);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Address entity) {
        stmt.clearBindings();
 
        Long addressId = entity.getAddressId();
        if (addressId != null) {
            stmt.bindLong(1, addressId);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(4, phone);
        }
        stmt.bindLong(5, entity.getIsDefaultAddress() ? 1L: 0L);
 
        String bigAddress = entity.getBigAddress();
        if (bigAddress != null) {
            stmt.bindString(6, bigAddress);
        }
 
        String smallAddress = entity.getSmallAddress();
        if (smallAddress != null) {
            stmt.bindString(7, smallAddress);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(8, address);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Address readEntity(Cursor cursor, int offset) {
        Address entity = new Address( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // addressId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // phone
            cursor.getShort(offset + 4) != 0, // isDefaultAddress
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // bigAddress
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // smallAddress
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // address
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Address entity, int offset) {
        entity.setAddressId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPhone(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIsDefaultAddress(cursor.getShort(offset + 4) != 0);
        entity.setBigAddress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSmallAddress(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAddress(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Address entity, long rowId) {
        entity.setAddressId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Address entity) {
        if(entity != null) {
            return entity.getAddressId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Address entity) {
        return entity.getAddressId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
