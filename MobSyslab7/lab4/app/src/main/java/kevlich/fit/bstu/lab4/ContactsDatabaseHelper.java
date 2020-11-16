package kevlich.fit.bstu.lab4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ContactsDB";
    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PROFILE = "profile";
    public static final String KEY_FAVORITE = "favorite";


    public ContactsDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "("+
                KEY_ID + " integer primary key autoincrement," +
                KEY_NAME + " varchar(50),"+
                KEY_EMAIL + " varchar(100),"+
                KEY_LOCATION + " varchar(100),"+
                KEY_PHONE + " varchar(50),"+
                KEY_PROFILE + " varchar(100),"+
                KEY_FAVORITE + " varchar(1) "+
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);

    }

    //Необязательные методы
    //onDownGrade -- противоположенность onUpgrade
    //onOpen -- вызывается при открытиии БД
    //getReadableDatabase -- возвращает БД для чтения
    //getWritableDatabase -- возвращает БД для чтения и записи
}
