package com.whl.wako.statemachine.dao.entity;

import com.whl.wako.statemachine.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String serialNo;

    /**
     * 订单状态
     */
    private OrderStatus status;

    /**
     * 创建人
     */
    private String createBy;


}
