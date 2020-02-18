package servlet;

import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-11
 * Time:19:48
 */
@WebServlet("/inbound")
public class InboundServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        int stock = Integer.parseInt(req.getParameter("stock"));
        String unit = req.getParameter("unit");
        String price = req.getParameter("price");
        double doubleprice = Double.valueOf(price);
        int realPrice = new Double(doubleprice * 100).intValue();
        int discount = Integer.parseInt(req.getParameter("discount"));

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection(true);
            String sql = "insert into goods(name,introduce,stock,unit,price,discount)" +
                    "values (?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,introduce);
            preparedStatement.setInt(3,stock);
            preparedStatement.setString(4,unit);
            preparedStatement.setInt(5,realPrice);
            preparedStatement.setInt(6,discount);
            int ret = preparedStatement.executeUpdate();
            if(ret == 1){ //1表示有数据 0表示nothing
                resp.sendRedirect("goodsbrowse.html");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }
    }
}
