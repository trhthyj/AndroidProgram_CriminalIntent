package com.mi.www.criminalintent.database;

/**
 * Created by wm on 2017/12/18.
 */

public class CrimeDbSchema {
    public static final class CrimeTable {
        //表名
        public static final String NAME = "crimes";
        //字段
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
