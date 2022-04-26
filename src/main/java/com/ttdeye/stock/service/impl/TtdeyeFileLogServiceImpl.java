package com.ttdeye.stock.service.impl;

import com.ttdeye.stock.common.utils.OSSClientUtils;
import com.ttdeye.stock.entity.TtdeyeFileLog;
import com.ttdeye.stock.mapper.TtdeyeFileLogMapper;
import com.ttdeye.stock.service.ITtdeyeFileLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * <p>
 * 文件记录 服务实现类
 * </p>
 *
 * @author 张永明
 * @since 2022-04-25
 */
@Service
public class TtdeyeFileLogServiceImpl extends ServiceImpl<TtdeyeFileLogMapper, TtdeyeFileLog> implements ITtdeyeFileLogService {

    @Autowired
    private OSSClientUtils ossClientUtils;

    @Autowired
    private TtdeyeFileLogMapper ttdeyeFileLogMapper;


    /**
     * 上传图片并保存上传记录
     * @param file
     * @param fileType
     * @param loginAccount
     * @return
     */
    public String saveFile(MultipartFile file,Integer fileType,String loginAccount){
        //SPU校验完成，保存上传文件记录
        String url = ossClientUtils.uploadImg2Oss(file);
        TtdeyeFileLog ttdeyeFileLog = new TtdeyeFileLog();
        ttdeyeFileLog.setFileUrl(url);
        ttdeyeFileLog.setFileType(fileType);
        ttdeyeFileLog.setCreateTime(new Date());
        ttdeyeFileLog.setCreateLoginAccount(loginAccount);
        ttdeyeFileLogMapper.insert(ttdeyeFileLog);
        return url;
    }
}
