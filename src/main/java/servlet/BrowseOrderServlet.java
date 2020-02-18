package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.OrderStatus;
import entity.Account;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-14
 * Time:9:27
 */
@WebServlet("/browseOrder")
public class BrowseOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html,charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Account account = (Account) req.getSession().getAttribute("user");
        List<Order> orderList = this.orderQuery(account.getId());
        if(orderList.isEmpty()){
            throw new RuntimeException("您没有订单信息！");
        }else{
            ObjectMapper objectMapper = new ObjectMapper();
            PrintWriter printWriter = resp.getWriter();
            objectMapper.writeValue(printWriter,orderList);
            printWriter.write(printWriter.toString());
        }
    }

    private List<Order> orderQuery(int accountId) {
        List<Order> list = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DBUtil.getConnection(true);
            String sql = this.getSql();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,accountId);
            resultSet = preparedStatement.executeQuery();
            Order order = null;
            OrderItem orderItem = null;
            while(resultSet.next()){
                if(order == null){
                    order = new Order();
                    this.parseOrder(order,resultSet);
                    list.add(order);
                }
                String order_id = resultSet.getString("oriOrder_id");
                if(!order_id.equals(order.getId())){
                    order = new Order();
                    this.parseOrder(order,resultSet);
                    list.add(order);
                }
                orderItem = parseOrderItem(order_id,resultSet);
                order.orderItemList.add(orderItem);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return list;
    }

    private void parseOrder(Order order,ResultSet resultSet)throws SQLException{
        order.setId(resultSet.getString("order_id"));
        order.setAccount_id(resultSet.getInt("account_id"));
        order.setAccount_name(resultSet.getString("account_name"));
        order.setCreate_time(resultSet.getString("create_time"));
        order.setFinish_time(resultSet.getString("finish_time"));
        order.setActual_amount(resultSet.getInt("actual_amount"));
        order.setTotal_money(resultSet.getInt("total_money"));
        order.setOrder_status(OrderStatus.getOrderStatus(resultSet.getInt("order_status")));
    }

    private OrderItem parseOrderItem(String oriOrder_id,ResultSet resultSet)throws SQLException{
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder_id(oriOrder_id);
        orderItem.setGoods_id(resultSet.getInt("goods_id"));
        orderItem.setGoods_name(resultSet.getString("goods_name"));
        orderItem.setGoods_introduce(resultSet.getString("goods_introduce"));
        orderItem.setGoods_num(resultSet.getInt("goods_num"));
        orderItem.setGoods_unit(resultSet.getString("goods_unit"));
        orderItem.setGoods_price(resultSet.getInt("goods_price"));
        orderItem.setGoods_discount(resultSet.getInt("goods_discount"));
        return orderItem;
    }

    private String getSql(){
        InputStream inputStream = this.getClass()
                                    .getClassLoader().getResourceAsStream("script/order_orderItem_querysql.sql");
        if(inputStream == null){
            throw  new RuntimeException("加载数据库文件失败!");
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        try{
            String line = bufferedReader.readLine();
            while(line != null){
                sb.append(" ").append(line);
                line = bufferedReader.readLine();
            }
        }catch (IOException e){
            throw  new RuntimeException("数据库读取错误！");
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
