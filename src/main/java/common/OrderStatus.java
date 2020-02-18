package common;

import lombok.Getter;

/**
 * Creat with IntelliJ IDEA.
 * Description：
 * User:LiuBen
 * Date:2020-02-13
 * Time:15:40
 */
@Getter
public enum OrderStatus {
    PAYING(0,"待支付"),OK(1,"支付完成");

    private int flag;
    private String desc;
    OrderStatus(int flag,String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public static OrderStatus getOrderStatus(int flag){
        for(OrderStatus OS : OrderStatus.values()){
            if(OS.flag == flag){
                return OS;
            }
        }
        throw new RuntimeException("该状态不存在！");
    }
}
