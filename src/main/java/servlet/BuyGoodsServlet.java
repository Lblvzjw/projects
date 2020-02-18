package servlet;

import common.OrderStatus;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-13
 * Time:17:06
 */
@WebServlet("/buyGoodsServlet")
public class BuyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html,charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Order order = (Order) req.getSession().getAttribute("order");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setFinish_time(LocalDateTime.now().format(dateTimeFormatter));
        order.setOrder_status(OrderStatus.OK);
        allInsert(order);

        List<Goods> goodsList =(List<Goods>)req.getSession().getAttribute("goodsList");
        for (Goods goods : goodsList) {
            boolean isUpdate = updateAfterBuy(goods);
            if(isUpdate){
                System.out.println("更新正常");
            }else{
                throw new RuntimeException("更新库存失败！");
            }
        }
        resp.sendRedirect("buyGoodsSuccess.html");
    }

    private boolean updateAfterBuy(Goods goods) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int updateGoodsStock = goods.getStock() - goods.getBuyGoodsNum();
        try{
            connection = DBUtil.getConnection(true) ;
            String sql = "update goods set stock=? where id=?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,updateGoodsStock);
            preparedStatement.setInt(2,goods.getId());
            return preparedStatement.executeUpdate() == 1;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return false;
    }


    private void allInsert(Order order){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = DBUtil.getConnection(false);
            String ordersql = "insert into `order`(id,account_id,account_name,create_time,finish_time,actual_amount,total_money,order_status)" +
                    "values(?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(ordersql);
            preparedStatement.setString(1,order.getId());
            preparedStatement.setInt(2,order.getAccount_id());
            preparedStatement.setString(3,order.getAccount_name());
            preparedStatement.setString(4,order.getCreate_time());
            preparedStatement.setString(5,order.getFinish_time());
            preparedStatement.setInt(6,order.getActual_amountInt());
            preparedStatement.setInt(7,order.getTotal_moneyInt());
            preparedStatement.setInt(8,order.getOrder_Status().getFlag());
            if(preparedStatement.executeUpdate() == 0){
                throw new RuntimeException("订单入库失败！");
            }

            String orderItemsql = "insert into order_item (order_id, goods_id, goods_name, goods_introduce, goods_num, goods_unit, goods_price, goods_discount) VALUES (?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(orderItemsql);
            for (OrderItem orderItem : order.orderItemList) {
                preparedStatement.setString(1,order.getId());
                preparedStatement.setInt(2,orderItem.getGoods_id());
                preparedStatement.setString(3,orderItem.getGoods_name());
                preparedStatement.setString(4,orderItem.getGoods_introduce());
                preparedStatement.setInt(5,orderItem.getGoods_num());
                preparedStatement.setString(6,orderItem.getGoods_unit());
                preparedStatement.setInt(7,orderItem.getGoodsPriceInt());
                preparedStatement.setInt(8,orderItem.getGoods_discount());
                preparedStatement.addBatch();
            }
            int[] flags = preparedStatement.executeBatch();
            for (int flag: flags) {
                if(flag == 0){
                    throw new RuntimeException("订单项入库异常！");
                }
            }
            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (RuntimeException e1){
            try {
                assert connection != null;
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        finally {
            DBUtil.close(connection,preparedStatement,null);
        }
    }
}
