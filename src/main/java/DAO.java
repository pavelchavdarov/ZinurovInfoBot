import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Павел on 10.08.2017.
 */
public class DAO {
    private static Connection connection;
    private static final String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
    //Имя пользователя БД
    private static final String name = "user1";
    //Пароль
    private static final String password = "1";
    public static Connection getConnection() {
        if (connection == null){
                try {
                Class.forName("org.postgresql.Driver");
                System.out.println("Драйвер PostgreSQL подключен");
                connection = DriverManager.getConnection(url, name, password);
                System.out.println("Соединение установлено");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return connection;
    }
}

