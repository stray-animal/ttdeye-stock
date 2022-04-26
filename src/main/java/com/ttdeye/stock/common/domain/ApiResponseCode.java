package com.ttdeye.stock.common.domain;

import lombok.ToString;

/**
 * API 响应码 TODO 待完成
 *
 * @author clayzhang
 */
@ToString
public enum ApiResponseCode implements IApiResponseCode {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 失败
     */
    FAILED(1, "失败"),

    COMMON_FAILED_CODE(100002, "通用错误码"),
    COMMON_FAILED_CODE2(900998, "通用错误码"),

    /**
     * 三方失败
     */
    THIRD_PARTY_FAILED(100099, "请求成功,但报价失败！"),

    /**
     * 登录已失效
     */
    LOGIN_EXPIRED(100001, "登录已失效"),

    /**
     * 系统异常
     */
    QUERY_EXCEPTION(300001, "查询失败，请联系系统管理员！错误代码"),

    /**
     * 系统异常
     */
    SYSTEM_EXCEPTION(300002, "服务异常 请联系管理员"),

    /**
     * 系统异常
     */
    NO_LAST_INSURE_INFO(300003, "无上年投保信息！"),

    /**
     * 系统异常
     */
    QUOTE_EXCEPTION(300006, "未选择任何险种，无法报价。"),

    /**
     * 系统异常
     */
    GET_CITY_EXCEPTION(300008, "城市信息异常！"),

    /**
     * 系统异常
     */
    CITY_UNSUPPORTED(300010, "尚未开通该城市服务！"),

    PARAMETER_EXCEPTION(300020, "参数校验异常！"),

    EXPRESS_EXCEPTION(300021, "等待快递员揽件！"),

    EXPRESS_ERROR(300022, "此快递公司暂不支持查询"),

    UERNAME_OR_PASSWORD_ERROR(100003,"用户名或密码错误"),
    VERIFY_CODE_ERROR(100010,"验证码错误"),
    PRESENT_USER_NOT_ALLOW_LOGIN(100011,"当前用户不允许登录"),
    USER_IS_NOT_EXIST(100042,"用户不存在"),
    API_TOKEN_ERROR(900001,"TOKEN非法"),

    // ===================================================== 续保 ==================================================== //

    /**
     * 车险尚未到期
     */
    RENEWAL_NOT_RENEWABLE(300011, "车险尚未到期，可在到期前 %d 天内进行投保!"),

    /**
     * 获取续保信息失败
     */
    RENEWAL_FAILED(300019, "核保失败！原因:"),

    // ===================================================== 报价 ==================================================== //

    /**
     * 报价失败
     */
    QUOTE_FAILED(300029, "核保失败！原因:"),

    // ===================================================== 核保 ==================================================== //

    /**
     * 核保失败，需要填写验证码
     */
    VERIFY_NEED_VERIFICATION(300032, "核保失败，需要填写验证码"),

    /**
     * 核保失败，需要上传影像资料
     */
    VERIFY_NEED_UPLOAD(300033, "核保失败，需要上传影像资料！"),

    /**
     * 核保中
     */
    VERIFY_IN_PROGRESS(300035, "核保中，请等待！"),

    /**
     * 核保失败，并且该车已存在工单，请先完成现有工单
     */
    VERIFY_WORKORDER_EXIT(300038, "核保失败，并且该车已存在工单，请先完成现有工单"),
    /**
     * 核保失败
     */
    VERIFY_FAILED(300039, "核保失败！原因:"),


    // ===================================================== 支付 ==================================================== //

    ODER_STATUS_AND_OPERATE_CORRECT(300041, "订单状态和操作不一致！"),
    EXIST_UNHANDLE_WORK_ORDER(300042, "当前订单,存在未处理的工单！"),
    WECHAT_PAY_FAILED_CODE(300060, "微信支付失败"),
    WZ_ORDER_CORRECT(300051, "违章订单相关错误"),
    IMAGES_PROCESS_FAILED(300200, "影像资料保存失败"),

    //***************************************************违章数据 兼顾一体机***********************************************************//
    WZ_PARAM_DEFECT(300012, "缺少违章请求参数"),
    WZ_CAR_INFO_DEFECT(300011, "请确认车辆输入的信息是否完整"),
    WZ_CITY_RULE_NOT_EXIST(300014, "该城市不支持违章查询"),
    WZ_ORDER_STATUS(300015, "违章记录处理中"),
    WZ_NO_VIOLATION_THE_CITY(300013, "没有查询到违章记录"),
    WZ_NO_VIOLATION_AND_INSERT_ERROR(300105, "没有违章记录并且插入数据 返回错误"),

    // ===================================================== 违章 ==================================================== //
    WZ_VIOLATION_PARAMS_ERROR(300016, "查询违章参数错误");

    /* 违章相关编码 31XX */


    // ===================================================== 其他 ==================================================== //
    /**
     * TODO recode
     */

    public Integer code;
    public String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    ApiResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}


