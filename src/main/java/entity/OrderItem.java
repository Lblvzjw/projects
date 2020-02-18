package entity;

import lombok.Data;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-13
 * Time:14:40
 */
@Data
public class OrderItem {
    private String order_id;
    private Integer goods_id;
    private String goods_name;
    private String goods_introduce;
    private Integer goods_num;
    private String goods_unit;
    private Integer goods_price;
    private Integer goods_discount;

    public double getGoods_price() {
        return goods_price/100.0;
    }

    public Integer getGoodsPriceInt(){
        return goods_price;
    }
}
