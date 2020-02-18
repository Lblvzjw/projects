package entity;

import common.OrderStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-13
 * Time:14:34
 */
@Data
public class Order {
    private String id;
    private Integer account_id;
    private String account_name;
    private String create_time;
    private String finish_time;
    private Integer actual_amount;
    private Integer total_money;
    private OrderStatus order_status;

    public List<OrderItem> orderItemList = new ArrayList<>();

    public double getActual_amount() {
        return actual_amount/100.0;
    }
    public Integer getActual_amountInt() {
        return actual_amount;
    }

    public double getTotal_money() {
        return total_money/100.0;
    }
    public Integer getTotal_moneyInt() {
        return total_money;
    }

    public double getDiscount(){
        return (this.total_money - this.actual_amount)/100.0;
    }

    public String getOrder_status() {
        return order_status.getDesc();
    }

    public OrderStatus getOrder_Status(){
        return order_status;
    }
}
