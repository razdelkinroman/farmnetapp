package ru.farmnet.app.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import ru.farmnet.app.exception.AppException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Подключение к firebird
 */
public class DataSource {

    private static String jdbcDriver;
    private static String dbUrl;
    private static String user;
    private static String password;

    static {
        Properties prop = new Properties();
        try (InputStream input = DataSource.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            dbUrl = prop.getProperty("dbUrl");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            jdbcDriver = prop.getProperty("jdbcDriver");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static ComboPooledDataSource dataSource;

    private static ComboPooledDataSource getInstance() throws AppException {
        if (dataSource == null) {
            try {
                dataSource = new ComboPooledDataSource();
                dataSource.setDriverClass(jdbcDriver);
                dataSource.setJdbcUrl(getURL());
                dataSource.setUser(user);
                dataSource.setPassword(password);
            } catch (PropertyVetoException ex1) {
                throw new AppException("Error crate datasource");
            }
        }
        return dataSource;
    }

    private static String getURL() {
        String abspath = new File("resources/bd/FAR.FDB").getAbsolutePath().replace("\\", "/");
        String path = new File("resources/bd/FAR.FDB").getPath().replace("\\", "/");
        return dbUrl + abspath.substring(0, abspath.indexOf(path)) + "farmapp/src/main/" + path;
    }
    public static Connection getConnection() throws AppException, SQLException {
        return getInstance().getConnection();
    }
}






