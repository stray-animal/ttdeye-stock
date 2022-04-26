package com.ttdeye.stock.common.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.ttdeye.stock.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Random;

/**
 * 阿里云 OSS工具类
 *
 * @author Monkey
 * @date 2017年9月30日下午3:38:09
 * @version 1.0
 */
@Slf4j
@Component
public class OSSClientUtils {

    public static final Logger logger = LoggerFactory.getLogger(OSSClientUtils.class);

    @Value("${aliyun_endpoint}")
    private String endpoint;

    @Value("${aliyun_accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun_accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun_bucketName}")
    private String bucketName;
    // 文件存储目录
    private String filedir = "stock/";


    /**
     * 上传图片
     *
     * @param url
     */
    public String uploadImg2Oss(String url) {
        File fileOnServer = new File(url);
        FileInputStream fin;
        String serverUrl = null;
        try {
            fin = new FileInputStream(fileOnServer);
            String[] split = url.split("/");
            serverUrl = this.uploadFile2OSS(fin, split[split.length - 1]);
        } catch (FileNotFoundException e) {
            throw new ApiException("图片上传失败");
        }
        return serverUrl;
    }

    public String uploadImg2Oss(MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("上传图片大小不能超过10M！");
        }
        String serverUrl = null;
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        Random random = new Random();
        String name = random.nextInt(10000) + System.currentTimeMillis() + substring;
        try {
            InputStream inputStream = file.getInputStream();
            serverUrl = this.uploadFile2OSS(inputStream, name);
            return serverUrl;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("图片上传失败");
        }
    }

    /**
     * 获得图片路径
     *
     * @param fileUrl
     * @return
     */
    private String getImgUrl(String fileUrl, OSSClient ossClient) {
        log.info(fileUrl);
        if (!StringUtils.isEmpty(fileUrl)) {
            String[] split = fileUrl.split("/");
            return this.getUrl(this.filedir + split[split.length - 1], ossClient);
        }
        return null;
    }

    /**
     * 上传到OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param instream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFile2OSS(InputStream instream, String fileName) {

        fileName = filedir + fileName;
        String url = "";
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, instream);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            metadata.setObjectAcl(CannedAccessControlList.PublicRead);
            putObjectRequest.setMetadata(metadata);
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            log.info("上传成功！");
            if (result != null) {
                url = getUrl(fileName, ossClient);
                url = url.substring(0, url.indexOf("?"));
            }
        } catch (OSSException oe) {
            log.info("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.info("Error Message:" + oe.getErrorMessage());
            log.info("Error Code:" + oe.getErrorCode());
            log.info("Request ID:" + oe.getRequestId());
            log.info("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.info("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.info("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return url;
    }


    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param
     * @return String
     */
    public static String getcontentType(String filenameExtension) {
        if ("bmp".equalsIgnoreCase(filenameExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(filenameExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(filenameExtension) || "jpg".equalsIgnoreCase(filenameExtension)
                || "png".equalsIgnoreCase(filenameExtension)) {
            return "image/jpeg";
        }
        if ("html".equalsIgnoreCase(filenameExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(filenameExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(filenameExtension)) {
            return "application/vnd.visio";
        }
        if ("pptx".equalsIgnoreCase(filenameExtension) || "ppt".equalsIgnoreCase(filenameExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("docx".equalsIgnoreCase(filenameExtension) || "doc".equalsIgnoreCase(filenameExtension)) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(filenameExtension)) {
            return "text/xml";
        }
        return "image/jpeg";
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    private String getUrl(String key, OSS ossClient) {

        boolean flag = false;
        if (ossClient == null) {
            flag = true;
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }
        try {
            // 设置URL过期时间为1000年 3600l* 1000*24*365*1000
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 1000);
            // 生成URL
            URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
            if (url != null) {
                return url.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (true) {
                ossClient.shutdown();
            }
        }
        return null;
    }


    public void test() throws FileNotFoundException {
        String imgPath = "/Users/zhangyongming/Downloads/软件开发文档.docx";
        InputStream input = new FileInputStream(new File(imgPath));
        String url = uploadImg2Oss(imgPath);
        log.info(url);
        /*
         * String bucketName = "xcauto-heimdallr-dev"; new
         * OSSClientUtil().createBucket(bucketName);
         */
    }


    public AssumeRoleResponse buildAliyunSTSCredentials() throws ClientException, com.aliyuncs.exceptions.ClientException {
        // STS,这里我以杭州举例,具体你的Bucket地域节点是哪里的就填哪里
        DefaultProfile.addEndpoint(endpoint, "cn-shenzhen", "Sts", "sts.cn-hangzhou.aliyuncs.com");
        //这里需要填充你子账户的accessKeyId与accessKeySecret
        IClientProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setMethod(MethodType.POST);
        request.setProtocol(ProtocolType.HTTPS);
        //设置临时访问凭证的有效时间为3600秒
        request.setDurationSeconds(60 * 60 * 1L*10000*365);
        // 要扮演的角色ID-刚才你创建的角色详情里面ARN
        request.setRoleArn("acs:ram::12************41:role/RamTestAppReadOnly");
        // 要扮演的角色名称-刚才你创建的角色详情里面角色名称
        request.setRoleSessionName("external-username");
        // request.setPolicy(policy);

        // 生成临时授权凭证
        final AssumeRoleResponse response = client.getAcsResponse(request);
        // 临时凭据AccessKeyId
        String appKey = response.getCredentials().getAccessKeyId();
        // 临时凭据AccessKeySecret
        String appSecret = response.getCredentials().getAccessKeySecret();
        // 临时凭据SecurityToken
        String securityToken = response.getCredentials().getSecurityToken();
        String expiration = response.getCredentials().getExpiration();
        return response;

    }

//
//    // 重写的方法以作参考
//    public String getAuthorization(AssumeRoleResponse assumeRoleResponse, String fileName) {
//        // 创建OSSClient实例。填充你的Endpoint与桶名称
//        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        //这里替换成你自己的Endpoint和BucketName即可
//        String endpoint = properties.getEndpoint();
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = properties.getBucketName();
//        // 填写Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。
//        // String objectName = fileName;
//        if (assumeRoleResponse == null) {
//            return null;
//        }
//        // 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
//        String accessKeyId = assumeRoleResponse.getCredentials().getAccessKeyId();
//        String accessKeySecret = assumeRoleResponse.getCredentials().getAccessKeySecret();
//        // 从STS服务获取的安全令牌（SecurityToken）。
//        String securityToken = assumeRoleResponse.getCredentials().getSecurityToken();
//        //获取oss client
//        OSS ossClient = new OSSClient(properties.getEndpoint(), credentialProvider, configuration);
//        // 设置签名URL过期时间为3600秒（1小时）。
//        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
//        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
//        URL url = ossClient.generatePresignedUrl(bucketName, fileName, expiration);
//        System.out.println(url);
//        // 关闭OSSClient。
//        ossClient.shutdown();
//        return url.toString();
//
//
//    }


}