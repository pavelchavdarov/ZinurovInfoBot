package DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Павел on 14.08.2017.
 */
public class HikariCP {
    private static DataSource dataSource;


    public static DataSource getDataSource(){
        if (dataSource == null){
            try {
                URI dbUri = new URI(System.getenv("DATABASE_URL"));
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":"+ dbUri.getPort() + dbUri.getPath();
                HikariConfig poolConfig = new HikariConfig();
                poolConfig.setJdbcUrl(dbUrl);
                if(dbUri.getUserInfo() != null){
                    poolConfig.setUsername(dbUri.getUserInfo().split(":")[0]);
                    poolConfig.setPassword(dbUri.getUserInfo().split(":")[1]);
                }
                poolConfig.setMaximumPoolSize(3);
                poolConfig.addDataSourceProperty("sslmode","require");
                dataSource = (DataSource) new HikariDataSource(poolConfig);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return dataSource;
    }
}
