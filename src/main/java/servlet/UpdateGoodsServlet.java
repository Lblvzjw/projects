package servlet;

import com.sun.org.apache.bcel.internal.generic.FieldGen;
import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-12
 * Time:22:10
 */
@WebServlet("/updategoods")
public class UpdateGoodsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String goodsId = req.getParameter("goodsID");
        int id = Integer.parseInt(goodsId);
        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        int stock = Integer.parseInt(req.getParameter("stock"));
        String unit = req.getParameter("unit");
        String price = req.getParameter("price");
        double doubleprice = Double.valueOf(price);
        int realPrice = new Double(doubleprice * 100).intValue();
        int discount = Integer.parseInt(req.getParameter("discount"));

        boolean contain = isContains(id);
        if(!contain){
            resp.sendRedirect("goodsbrowse.html");
            System.out.println("该商品不存在");
        }else{
            Goods goods = new Goods();
            goods.setId(id);
            goods.setName(name);
            goods.setIntroduce(introduce);
            goods.setStock(stock);
            goods.setUnit(unit);
            goods.setPrice(realPrice);
            goods.setDiscount(discount);
            boolean effect = undateGoods(goods);
            if(effect){
                resp.sendRedirect("goodsbrowse.html");
                System.out.println("更新成功");
            }else{
                resp.sendRedirect("goodsbrowse.html");
                System.out.println("更新失败");
            }
        }

    }

    private boolean undateGoods(Goods goods) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = DBUtil.getConnection(true);
            String sql = "update goods set name=?,introduce=?,stock=?,unit=?,price=?,discount=? where id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,goods.getName());
            preparedStatement.setString(2,goods.getIntroduce());
            preparedStatement.setInt(3,goods.getStock());
            preparedStatement.setString(4,goods.getUnit());
            preparedStatement.setInt(5,goods.getPriceInt());
            preparedStatement.setInt(6,goods.getDiscount());
            preparedStatement.setInt(7,goods.getId());
            int ret = preparedStatement.executeUpdate();
            return ret == 1;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return false;
    }

    private boolean isContains(int id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet set = null;
        try {
            connection = DBUtil.getConnection(true);
            String sql = "select * from goods where id=?";
            preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,id);
            set = preparedStatement.executeQuery();
            if(set.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return false;
    }
}
