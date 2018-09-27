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

    String jdbcDriver;
    String dbUrl;
    String user;
    String password;


    Properties prop = new Properties();

    {
        try (InputStream input = DataSource.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            dbUrl = prop.getProperty("dbUrl");
            user =  prop.getProperty("user");
            password =  prop.getProperty("password");
            jdbcDriver = prop.getProperty("jdbcDriver");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static DataSource dataSource;
    private ComboPooledDataSource comboPooledDataSource;

    private DataSource() throws AppException {
        try {
            comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass(jdbcDriver);
            comboPooledDataSource.setJdbcUrl(getURL());
            comboPooledDataSource.setUser(user);
            comboPooledDataSource.setPassword(password);
        } catch (PropertyVetoException ex1) {
            throw new AppException("Error crate datasource");
        }
    }

    public static DataSource getInstance() throws AppException {
        if (dataSource == null) {
            dataSource = new DataSource();
        }
        return dataSource;
    }

    private String getURL() {
        String abspath = new File("resources/bd/FAR.FDB").getAbsolutePath().replace("\\", "/");
        String path = new File("resources/bd/FAR.FDB").getPath().replace("\\", "/");
        return dbUrl + abspath.substring(0, abspath.indexOf(path)) + "farmapp/src/main/" + path;
    }

    public Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }


}






