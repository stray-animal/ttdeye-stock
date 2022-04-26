package com.ttdeye.stock.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Comment: $
 * @Author: Zhangyongming
 * @Date: $ $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageEntity {

    /**
     * 页码
     */
    private Integer page;

    /**
     * 行数
     */
    private Integer rows;

}
