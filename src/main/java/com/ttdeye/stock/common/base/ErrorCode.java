package com.ttdeye.stock.common.base;

/**
 * 错误码常量
 */
public class ErrorCode {

    public static final String NETWORK_UNUSUAL_CODE = "9999";
    public static final String NETWORK_UNUSUAL_MSG = "网络异常";
    public static final String COMMON_ERROR_CODE = "9898";

    public static final String SUCCESS_CODE = "0";
    public static final String SUCCESS_MSG = "成功";

    public static final class Validation {
        public static final String FORM_VALID_ERROR_CODE = "VALID_1000";
        public static final String FORM_VALID_ERROR_MSG = "参数校验异常";
    }


    public static final class Access {
        public static final String ACCESS_ERROR_CODE = "ACCESS_1100";
        public static final String ACCESS_ERROR_MSG = "访问校验异常";
    }



    public static final class Excel{
        public static final Integer IMPORT_ERROR_CODE = 1201;
        public static final String IMPORT_TEMPLATE_MISMATCH_MSG = "模板导入头部格式错误";

        public static final Integer HEADROW_IS_EMPTY_CODE = 1202;
        public static final String HEADROW_IS_EMPTY_MSG = "导入Excel文件为空";

        public static final Integer TEMPLET_IS_WRONG_CODE = 1203;
        public static final String TEMPLET_IS_WRONG_MSG = "导入Excel文件不符合要求";

        public static final Integer IMPORT_QUANTITY_CODE = 1204;
        public static final String IMPORT_QUANTITY_MSG = "无法处理大于5000条的数据";

        public static final Integer GET_EXCEL_ERROR_CODE = 1205;
        public static final String GET_EXCEL_ERROR_MSG = "文件导入格式有误！";

        public static final Integer EXPORT_EXCEL_IS_NULL_CODE = 1206;
        public static final String EXPORT_EXCEL_IS_NULL_MSG = "导出excel数据为空！";

        public static final Integer READ_EXCEL_ERROR_CODE = 1207;
        public static final String READ_EXCEL_ERROR_MSG = "读取excel失败！";

        public static final Integer TEMPLATE_IS_ERROR_CODE = 1208;
        public static final String TEMPLATE_IS_ERROR_MSG = "未上传正确模板";

        public static final Integer READ_EXCEL_ERROR_CODE2 = 1209;
        public static final String READ_EXCEL_ERROR_MSG2 = "读取excel失败,价格或库存 必须为数字类型";

    }

    public static final class Redis {

        public static final String REDIS_ERROR_CODE = "Redis_1250";
        public static final String REDIS_ERROR_MSG = "Redis缓存异常";
    }


    public static final class Login {
    	
    	public static final String NOT_LOGIN_CODE = "Login_1300";
        public static final String NOT_LOGIN_MSG = "用户未登录";
        
        
        public static final String NOT_PERMSION_CODE = "Login_1301";
        public static final String NOT_PERMSION_MSG = "用户无权限";
    }
}
