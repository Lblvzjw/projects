package servlet;

import common.OrderStatus;
import entity.Account;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-13
 * Time:14:46
 */
@WebServlet("/pay")
public class ReadyBuyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String goodsIDAndNums = req.getParameter("goodsIDandNum");
        String[] items = goodsIDAndNums.split(",");
        List<Goods> list = new ArrayList<>();
        for (String item : items) {
            String[] idAndNum = item.split("-");
            int goodsId = Integer.parseInt(idAndNum[0]);
            int goodsNum = Integer.parseInt(idAndNum[1]);
            Goods goods = this.getGoods(goodsId);
            if(goods != null){
                if(goods.getStock() < goodsNum){
                    throw new RuntimeException("库存不足！");
                }
                goods.setBuyGoodsNum(goodsNum);
                list.add(goods);
            }else{
                throw new RuntimeException( goodsId + "商品不存在！");
            }
        }

        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("user");

        Order order = new Order();

        order.setId(String.valueOf(System.currentTimeMillis()));
        order.setAccount_id(account.getId());
        order.setAccount_name(account.getUsername());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setCreate_time(LocalDateTime.now().format(dateTimeFormatter));

        int totPrice = 0;
        int actPrice = 0;
        for (Goods goods : list) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder_id(order.getId());
            orderItem.setGoods_id(goods.getId());
            orderItem.setGoods_name(goods.getName());
            orderItem.setGoods_introduce(goods.getIntroduce());
            orderItem.setGoods_num(goods.getBuyGoodsNum());
            orderItem.setGoods_unit(goods.getUnit());
            orderItem.setGoods_price(goods.getPriceInt());
            orderItem.setGoods_discount(goods.getDiscount());
            order.orderItemList.add(orderItem);

            int money = goods.getPriceInt()* goods.getBuyGoodsNum();
            totPrice += money;
            actPrice += (int)(money * goods.getDiscount()/100.0);
        }
        order.setTotal_money(totPrice);
        order.setActual_amount(actPrice);
        order.setOrder_status(OrderStatus.PAYING);

        resp.getWriter().println("<html>");
        resp.getWriter().println("<p>"+"【用户名称】："+order.getAccount_name()+"</p>");
        resp.getWriter().println("<p>"+"【订单编号】："+order.getId()+"</p>");
        resp.getWriter().println("<p>"+"【订单状态】："+order.getOrder_Status().getDesc()+"</p>");
        resp.getWriter().println("<p>"+"【创建时间】"+order.getCreate_time()+"</p>");
        resp.getWriter().println("<table border=\"1\">");
        resp.getWriter().println("<tr>"+"<th>编号</th>"+"<th>名称</th>"+"<th>数量</th>"+"<th>单位</th>"+"<th>单价(元)</th>"+"</tr>");
        int nums = 1;
        for (OrderItem orderItem:order.orderItemList) {
            resp.getWriter().println("<tr style=\"text-align: center\">"+"<td>"+nums+++"</td>"+"<td>"+orderItem.getGoods_name()+"</td>"+"<td>"+orderItem.getGoods_num()+"</td>"+"<td>"+orderItem.getGoods_unit()+"</td>"+"<td>"+orderItem.getGoods_price()+"</td>"+"</tr>");

        }
        resp.getWriter().println("</table>");
        resp.getWriter().println("<p>"+"【总金额】"+order.getTotal_money()+"</p>");
        resp.getWriter().println("<p>"+"【优惠金额】"+order.getDiscount()+"</p>");
        resp.getWriter().println("<p>"+"【应支付金额】"+order.getActual_amount()+"</p>");
        resp.getWriter().println("<button ><a href=\"buyGoodsServlet\">确认</a></button>");
        resp.getWriter().println("<button ><a href=\"pay.html\">取消</a></button>");
        resp.getWriter().println("</html>");

        session.setAttribute("order",order);
        session.setAttribute("goodsList",list);
    }

    private Goods getGoods(int goodsId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Goods goods = null;
        try{
            connection = DBUtil.getConnection(true);
            String sql = "select id,name,introduce,stock,unit,price,discount from goods where id=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,goodsId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                goods = this.parseGoods(resultSet);
                System.out.println(goods.toString());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return goods;
    }

    private Goods parseGoods(ResultSet resultSet) throws SQLException{
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setName(resultSet.getString("name"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setStock(resultSet.getInt("stock"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setDiscount(resultSet.getInt("discount"));
        return goods;
    }


}
