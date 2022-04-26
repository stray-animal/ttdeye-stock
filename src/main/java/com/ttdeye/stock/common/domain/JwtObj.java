package com.ttdeye.stock.common.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: songgt
 * @date: 2019-03-05 15:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtObj implements Serializable {
    private static final long serialVersionUID = 1691489461127248816L;

    private String taxNo;
    private String cusName;
    private String machineNo;
    private long expires;
}