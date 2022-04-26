package com.ttdeye.stock.service;

import com.ttdeye.stock.entity.TtdeyeFileLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件记录 服务类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
public interface ITtdeyeFileLogService extends IService<TtdeyeFileLog> {

    /**
     * 上传图片并保存上传记录
     * @param file
     * @param fileType
     * @param loginAccount
     * @return
     */
    String saveFile(MultipartFile file, Integer fileType, String loginAccount);

}
