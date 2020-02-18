package entity;

import lombok.Data;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-12
 * Time:9:07
 */
@Data
public class Goods {
    private Integer id;
    private String name;
    private String introduce;
    private Integer stock;
    private String unit;
    private Integer price;
    private Integer discount;
    private Integer buyGoodsNum;

    public double getPrice() {
        return price/100.0;
    }
    public Integer getPriceInt() {
        return price;
    }

}
