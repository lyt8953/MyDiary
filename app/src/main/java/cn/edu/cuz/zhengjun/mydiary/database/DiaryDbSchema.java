package cn.edu.cuz.zhengjun.mydiary.database;

public class DiaryDbSchema {
    public static final class DiaryTable {
        public static final String NAME = "diary";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String CONTENT = "content";
            public static final String COLLECTED = "collected";
        }
    }
}
