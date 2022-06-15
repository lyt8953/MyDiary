package cn.edu.cuz.zhengjun.mydiary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.edu.cuz.zhengjun.mydiary.database.DiaryDbSchema.DiaryTable;

public class DiaryBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "diary.db";

    public DiaryBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DiaryTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DiaryTable.Cols.UUID + ", " +
                DiaryTable.Cols.TITLE + ", " +
                DiaryTable.Cols.DATE + ", " +
                DiaryTable.Cols.COLLECTED + ", " +
                DiaryTable.Cols.CONTENT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
