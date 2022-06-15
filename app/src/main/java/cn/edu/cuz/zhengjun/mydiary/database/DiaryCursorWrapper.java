package cn.edu.cuz.zhengjun.mydiary.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import cn.edu.cuz.zhengjun.mydiary.Diary;
import cn.edu.cuz.zhengjun.mydiary.database.DiaryDbSchema.DiaryTable;

public class DiaryCursorWrapper extends CursorWrapper {
    public DiaryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Diary getDiary() {
        String uuidString = getString(getColumnIndex(DiaryTable.Cols.UUID));
        String title = getString(getColumnIndex(DiaryTable.Cols.TITLE));
        long date = getLong(getColumnIndex(DiaryTable.Cols.DATE));
        String content = getString(getColumnIndex(DiaryTable.Cols.CONTENT));
        int collected = getInt(getColumnIndex(DiaryTable.Cols.COLLECTED));

        Diary diary = new Diary(UUID.fromString(uuidString));
        diary.setTitle(title);
        diary.setDate(new Date(date));
        diary.setContent(content);
        diary.setCollected(collected != 0);
        return diary;
    }
}
