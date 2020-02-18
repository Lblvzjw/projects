package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-11
 * Time:15:49
 */
public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/cash";
    private static final String username = "root";
    private static final String password = "root";

    private static volatile DataSource dataSource;

    private static DataSource getDataSource(){
        if(dataSource == null){
            synchronized (DBUtil.class){
                if(dataSource == null){
                    dataSource = new MysqlDataSource();
                    ((MysqlDataSource)dataSource).setURL(URL);
                    ((MysqlDataSource)dataSource).setUser(username);
                    ((MysqlDataSource)dataSource).setPassword(password);

                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection(boolean autoCommit){
        try {
            Connection connection = getDataSource().getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接异常");
        }
    }

    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        try {
            if(resultSet != null){
                resultSet.close();
            }

            if(preparedStatement != null){
                preparedStatement.close();
            }

            if(connection != null){
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
