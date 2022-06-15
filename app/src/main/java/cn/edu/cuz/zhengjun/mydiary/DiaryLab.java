package cn.edu.cuz.zhengjun.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.edu.cuz.zhengjun.mydiary.database.DiaryBaseHelper;
import cn.edu.cuz.zhengjun.mydiary.database.DiaryCursorWrapper;
import cn.edu.cuz.zhengjun.mydiary.database.DiaryDbSchema.DiaryTable;

public class DiaryLab {
    private static DiaryLab sDiaryLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DiaryLab get(Context context) {
        if (sDiaryLab == null) {
            sDiaryLab = new DiaryLab(context);
        }
        return sDiaryLab;
    }

    private DiaryLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DiaryBaseHelper(mContext).getWritableDatabase();
    }

    public void addDiary(Diary diary) {
        ContentValues values = getContentValues(diary);
        mDatabase.insert(DiaryTable.NAME, null, values);
    }

    public void updateDiary(Diary diary) {
        String uuidString = diary.getId().toString();
        ContentValues values = getContentValues(diary);
        mDatabase.update(DiaryTable.NAME, values, DiaryTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void deleteDiary(Diary diary) {
        String uuidString = diary.getId().toString();
        mDatabase.delete(DiaryTable.NAME, DiaryTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private DiaryCursorWrapper queryDiaries(String whereClause, String[] whereArgs, boolean asc) {
        String orderBy;
        if(asc){
            orderBy = "date asc";
        } else {
            orderBy = "date desc";
        }
        DiaryCursorWrapper cursor = new DiaryCursorWrapper(mDatabase.query(DiaryTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null,   // having
                orderBy  // orderBy
        ));
        return cursor;
    }

    public List<Diary> getDiaries(int type, boolean asc) {
        List<Diary> diaries = new ArrayList<>();
        String whereArgs = null;
        if(type == 0){
            whereArgs = null;
        } else if(type == 1){
            whereArgs = DiaryTable.Cols.COLLECTED + "==1";
        }
        DiaryCursorWrapper cursor = queryDiaries(whereArgs, null, asc);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                diaries.add(cursor.getDiary());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return diaries;
    }

    public Diary getDiary(UUID id) {
        DiaryCursorWrapper cursor = queryDiaries(DiaryTable.Cols.UUID + " =?", new String[]{id.toString()}, false);
        try {
            if (cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            return cursor.getDiary();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Diary diary) {
        ContentValues values = new ContentValues();
        values.put(DiaryTable.Cols.UUID, diary.getId().toString());
        values.put(DiaryTable.Cols.TITLE, diary.getTitle());
        values.put(DiaryTable.Cols.DATE, diary.getDate().getTime());
        values.put(DiaryTable.Cols.CONTENT, diary.getContent());
        values.put(DiaryTable.Cols.COLLECTED, diary.isCollected() ? 1 : 0);
        return values;
    }

    public File getPhotoFile(Diary diary){
        File fileDir = mContext.getFilesDir();
        return new File(fileDir,diary.getPhotoFilename());
    }

//    public File getVideoFile(Diary diary){
//        File fileDir = mContext.getExternalFilesDir("video")+"/";
//        return new File(fileDir,diary.getVideoFilename());
//    }

}
