package data;

import java.util.TimeZone;

public class DAOconfig {

    public static final String USERNAME = "root";
    public static final String PASSWORD = "1234";
    private static final String DATABASE = "swap";
    private static final String DRIVER = "jdbc:mysql";
    public static final String URL = DRIVER + "://localhost:3306/" + DATABASE + "?serverTimezone=" + TimeZone.getDefault().getID();
}