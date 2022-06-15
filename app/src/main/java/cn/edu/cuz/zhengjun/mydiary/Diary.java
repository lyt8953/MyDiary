package cn.edu.cuz.zhengjun.mydiary;

import java.util.Date;
import java.util.UUID;

public class Diary {
    private UUID mId;
    private String mTitle = "";      //  标题
    private String mContent = "";      //  标题
    private Date mDate;         //  时间
    private boolean mCollected;

    public Diary(){
        this(UUID.randomUUID());
    }

    public Diary(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public boolean isCollected() {
        return mCollected;
    }

    public void setCollected(boolean collected) {
        mCollected = collected;
    }

    public  String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public  String getVideoFilename(){
        return "IMG_" + getId().toString() + ".mp4";
    }
}
