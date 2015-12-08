package ua.klo.lp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {

    public SqlHelper(Context context) {
        super(context, "LetsPing", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table azsLetsPing ("
                + "id integer primary key autoincrement," + "adress text,"
                + "prov text," + "telProv text," + "subnet text,"
                + "admin text," + "telAzs text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
