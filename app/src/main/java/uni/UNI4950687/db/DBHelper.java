package uni.UNI4950687.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import uni.UNI4950687.db.SupplierContract.SupplierEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Supplier.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SupplierEntry.TABLE_NAME + " (" +
                    SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SupplierEntry.COLUMN_NAME_NAME + " TEXT," +
                    SupplierEntry.COLUMN_NAME_AREA + " VARCHAR(255)," +
                    SupplierEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    SupplierEntry.COLUMN_NAME_TEL + " VARCHAR(255)," +
                    SupplierEntry.COLUMN_NAME_DELIVERY + " VARCHAR(255))";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
