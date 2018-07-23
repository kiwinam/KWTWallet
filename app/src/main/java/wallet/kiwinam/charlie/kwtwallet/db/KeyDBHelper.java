package wallet.kiwinam.charlie.kwtwallet.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("LogNotTimber")
public class KeyDBHelper extends SQLiteOpenHelper {
    private Context context;
    private SQLiteDatabase db = null;
    public KeyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb;
        sb = new StringBuffer();
        sb.append("CREATE TABLE keyList (");
        sb.append("no INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("name TEXT,");
        sb.append("address TEXT)");

        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { Log.d("SQLite","Update"); }

    public void keyDB(){
        if(db == null){
            db = getWritableDatabase();
        }
    }

    /*
     * 새로운 키를 데이터 베이스에 넣는다.
     */
    public boolean insertNewKey(String name, String address){
        try{
            StringBuffer sb = new StringBuffer();
            SQLiteDatabase keyDB = getWritableDatabase();
            sb.append("INSERT INTO keyList (name,address) ");
            sb.append("VALUES (?,?) ");

            keyDB.execSQL(sb.toString(), new Object[]{
                    name,
                    address
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateWalletName(String name, String address){
        try{
            StringBuffer sb = new StringBuffer();
            SQLiteDatabase keyDB = getWritableDatabase();

            sb.append("UPDATE keyList SET name = ? WHERE address = ?");

            keyDB.execSQL(sb.toString(), new Object[]{
                    name,
                    address
            });
            return true;
        }catch (Exception e){

        }
        return false;
    }

    public String getNameByAddress(String address){
        db = getReadableDatabase();
        String name = "";

        Cursor cursor = db.rawQuery("SELECT 'name' FROM keyList WHERE address = ?", new String[]{address});
        if(cursor.moveToNext()){
            name = cursor.getString(0);
        }else{
            name = "none";
        }
        cursor.close();

        return name;
    }
}
