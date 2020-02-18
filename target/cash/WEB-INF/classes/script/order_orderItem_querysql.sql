select od.id as order_id,
       od.account_id as account_id,
       od.account_name as account_name,
       od.create_time as create_time,
       od.finish_time as finish_time,
       od.actual_amount as actual_amount,
       od.total_money as total_money,
       od.order_status as order_status,
       ori.order_id as oriOrder_id,
       ori.goods_id as goods_id,
       ori.goods_name as goods_name,
       ori.goods_introduce as goods_introduce,
       ori.goods_num as goods_num,
       ori.goods_unit as goods_unit,
       ori.goods_price as goods_price,
       ori.goods_discount as goods_discount
from `order` as od left join order_item as ori on od.id=ori.order_id
where od.account_id = ? order by order_id;