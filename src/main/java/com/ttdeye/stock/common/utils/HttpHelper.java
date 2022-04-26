package com.ttdeye.stock.common.utils;

import com.google.common.collect.Maps;
import jodd.io.FileNameUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * @Comment: $comment$
 * @Author: Zhangyongming
 * @Date: $date$ $time$
 */
public class HttpHelper {

    private static Logger logger = LoggerFactory.getLogger(HttpHelper.class);
    private static final String DEFAULT_CHARSET = "UTF-8";// 默认请求编码
    private static final int DEFAULT_SOCKET_TIMEOUT = 12 * 1000;// 默认等待响应时间(毫秒)
    private static final int DEFAULT_RETRY_TIMES = 0;// 默认执行重试的次数


    // HTTPS网站一般情况下使用了安全系数较低的SHA-1签名，因此首先我们在调用SSL之前需要重写验证方法，取消检测SSL。
    private static final TrustManager[] TRUST_MANAGERS = new TrustManager[]{new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }};

    private static SSLConnectionSocketFactory sslConnectionSocketFactory;

    private static SSLSocketFactory sslSocketFactory;

    static {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, TRUST_MANAGERS, new SecureRandom());

            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 开启SSL支持
     */
    private static void enableSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, TRUST_MANAGERS, null);
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个默认的可关闭的HttpClient
     *
     * @return
     */
    public static CloseableHttpClient createHttpClient() {
        return createHttpClient(DEFAULT_RETRY_TIMES, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * 创建一个可关闭的HttpClient
     *
     * @param socketTimeout 请求获取数据的超时时间
     * @return
     */
    public static CloseableHttpClient createHttpClient(int socketTimeout) {
        return createHttpClient(DEFAULT_RETRY_TIMES, socketTimeout);
    }

    /**
     * 创建一个可关闭的HttpClient
     *
     * @param socketTimeout 请求获取数据的超时时间
     * @param retryTimes    重试次数，小于等于0表示不重试
     * @return
     */
    public static CloseableHttpClient createHttpClient(int retryTimes, int socketTimeout) {
        Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(5000);// 设置连接超时时间，单位毫秒
        builder.setConnectionRequestTimeout(1000);// 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
        if (socketTimeout >= 0) {
            builder.setSocketTimeout(socketTimeout);// 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
        }
        RequestConfig defaultRequestConfig = builder.setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
        // 开启HTTPS支持
        enableSSL();
        // 创建可用Scheme
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory).build();
        // 创建ConnectionManager，添加Connection配置信息
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (retryTimes > 0) {
            setRetryHandler(httpClientBuilder, retryTimes);
        }
        CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
        return httpClient;
    }

    /**
     * 执行GET请求
     *
     * @param url           远程URL地址
     * @param charset       请求的编码，默认UTF-8
     * @param socketTimeout 超时时间（毫秒）
     * @return HttpResult
     * @throws IOException
     */
    public static HttpResult executeGet(String url, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executeGet(httpClient, url, null, null, charset, true, authToken);
    }

    /**
     * 执行GET请求
     *
     * @param url           远程URL地址
     * @param charset       请求的编码，默认UTF-8
     * @param socketTimeout 超时时间（毫秒）
     * @return String
     * @throws IOException
     */
    public static String executeGetString(String url, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executeGetString(httpClient, url, null, null, charset, true, authToken);
    }

    /**
     * 执行HttpGet请求
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param url             请求的远程地址
     * @param referer         referer信息，可传null
     * @param cookie          cookies信息，可传null
     * @param charset         请求编码，默认UTF8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return HttpResult
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeGet(CloseableHttpClient httpClient, String url, String referer, String cookie, String charset, boolean closeHttpClient, String authToken) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executeGetResponse(httpClient, url, referer, cookie, authToken);
            //Http请求状态码
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param httpClient httpclient对象
     * @param url        执行GET的URL地址
     * @param referer    referer地址
     * @param cookie     cookie信息
     * @return CloseableHttpResponse
     * @throws IOException
     */
    public static CloseableHttpResponse executeGetResponse(CloseableHttpClient httpClient, String url, String referer, String cookie, String authToken) throws IOException {
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        HttpGet get = new HttpGet(url);
        if (cookie != null && !"".equals(cookie)) {
            get.setHeader("Cookie", cookie);
        }
        if (referer != null && !"".equals(referer)) {
            get.setHeader("referer", referer);
        }

        if (authToken != null && !"".equals(authToken)) {
            get.setHeader("X-Auth-Token", authToken);
        }

        return httpClient.execute(get);
    }

    /**
     * 执行HttpGet请求
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param url             请求的远程地址
     * @param referer         referer信息，可传null
     * @param cookie          cookies信息，可传null
     * @param charset         请求编码，默认UTF8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return String
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String executeGetString(CloseableHttpClient httpClient, String url, String referer, String cookie, String charset, boolean closeHttpClient, String authToken) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executeGetResponse(httpClient, url, referer, cookie, authToken);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 简单方式执行POST请求
     *
     * @param url           远程URL地址
     * @param paramsObj     post的参数，支持map<String,String>,JSON,XML
     * @param charset       请求的编码，默认UTF-8
     * @param socketTimeout 超时时间(毫秒)
     * @return HttpResult
     * @throws IOException
     */
    public static HttpResult executePost(String url, Object paramsObj, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executePost(httpClient, url, paramsObj, null, null, charset, true, authToken);
    }

    /**
     * 简单方式执行POST请求
     *
     * @param url           远程URL地址
     * @param paramsObj     post的参数，支持map<String,String>,JSON,XML
     * @param charset       请求的编码，默认UTF-8
     * @param socketTimeout 超时时间(毫秒)
     * @return HttpResult
     * @throws IOException
     */
    public static String executePostString(String url, Object paramsObj, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executePostString(httpClient, url, paramsObj, null, null, charset, true, authToken);
    }

    /**
     * 执行HttpPost请求
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param url             请求的远程地址
     * @param paramsObj       提交的参数信息，目前支持Map,和String(JSON\xml)
     * @param referer         referer信息，可传null
     * @param cookie          cookies信息，可传null
     * @param charset         请求编码，默认UTF8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static HttpResult executePost(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset, boolean closeHttpClient, String authToken) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset, authToken);
            //Http请求状态码
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行HttpPost请求
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param url             请求的远程地址
     * @param paramsObj       提交的参数信息，目前支持Map,和String(JSON\xml)
     * @param referer         referer信息，可传null
     * @param cookie          cookies信息，可传null
     * @param charset         请求编码，默认UTF8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return String
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String executePostString(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset, boolean closeHttpClient, String authToken) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset, authToken);
            return getResult(httpResponse, charset);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * @param httpClient HttpClient对象
     * @param url        请求的网络地址
     * @param paramsObj  参数信息
     * @param referer    来源地址
     * @param cookie     cookie信息
     * @param charset    通信编码
     * @return CloseableHttpResponse
     * @throws IOException
     */
    private static CloseableHttpResponse executePostResponse(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset, String authToken) throws IOException {
        if (httpClient == null) {
            httpClient = createHttpClient();
        }
        HttpPost post = new HttpPost(url);
        if (cookie != null && !"".equals(cookie)) {
            post.setHeader("Cookie", cookie);
        }
        if (referer != null && !"".equals(referer)) {
            post.setHeader("referer", referer);
        }

        if (authToken != null && !"".equals(authToken)) {
            post.setHeader("X-Auth-Token", authToken);
        }
        // 设置参数
        HttpEntity httpEntity = getEntity(paramsObj, charset);
        if (httpEntity != null) {
            post.setEntity(httpEntity);
        }
        return httpClient.execute(post);
    }

    /**
     * 执行文件上传
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param remoteFileUrl   远程接收文件的地址
     * @param localFilePath   本地文件地址
     * @param charset         请求编码，默认UTF-8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeUploadFile(CloseableHttpClient httpClient, String remoteFileUrl, String localFilePath, String charset, boolean closeHttpClient) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            // 把文件转换成流对象FileBody
            File localFile = new File(localFilePath);
            FileBody fileBody = new FileBody(localFile);
            // 以浏览器兼容模式运行，防止文件名乱码。
            HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addPart("uploadFile", fileBody).setCharset(CharsetUtils.get("UTF-8")).build();
            // uploadFile对应服务端类的同名属性<File类型>
            // .addPart("uploadFileName", uploadFileName)
            // uploadFileName对应服务端类的同名属性<String类型>
            HttpPost httpPost = new HttpPost(remoteFileUrl);
            httpPost.setEntity(reqEntity);
            httpResponse = httpClient.execute(httpPost);
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 执行文件上传
     *
     * @param requestUrl    远程接收文件的地址
     * @param fileParamName 文件参数名
     * @param filePathList  上传文件列表
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeUploadFiles(String requestUrl,
                                                String fileParamName,
                                                List<String> filePathList,
                                                String fileNameSuffix) throws IOException {


        Map<Boolean, List<String>> filePathPartition = filePathList.stream()
                .collect(Collectors.partitioningBy(n -> n.contains("://")));

        List<String> networkFilePathList = filePathPartition.get(Boolean.TRUE);
        List<String> localFilePathList = filePathPartition.get(Boolean.FALSE);

        Map<String, InputStream> fileNameToInputStreamMap = Maps.newHashMapWithExpectedSize(filePathList.size());

        // 网络文件
        fileNameToInputStreamMap.putAll(networkFilePathList.stream().map(n -> {
            try {
                return new URL(n);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(

                kUrl -> String.format("%s%s.%s",
                        FileNameUtil.getBaseName(kUrl.getPath()),
                        fileNameSuffix,
                        (StringUtils.isEmpty(FileNameUtil.getExtension(kUrl.getPath())) ?
                                "jpg" : FileNameUtil.getExtension(kUrl.getPath()))
                ),

                vUrl -> {
                    try {

                        URLConnection urlConnection = vUrl.openConnection();
                        if (urlConnection instanceof HttpsURLConnection) {
                            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
                        }

                        return urlConnection.getInputStream();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        return null;
                    }
                })
        ));

        // 本地文件
        fileNameToInputStreamMap.putAll(localFilePathList.stream()
                .map(File::new)
                .collect(Collectors.toMap(
                        kFile -> String.format("%s%s.%s",
                                FileNameUtil.getBaseName(kFile.getName()),
                                fileNameSuffix,
                                (StringUtils.isEmpty(FileNameUtil.getExtension(kFile.getName())) ?
                                        "jpg" : FileNameUtil.getExtension(kFile.getName()))
                        ),
                        vFile -> {
                            try {
                                return new FileInputStream(vFile);
                            } catch (FileNotFoundException e) {
                                logger.error(e.getMessage(), e);
                                return null;
                            }
                        })
                )
        );

        return executeUploadFiles(requestUrl, fileParamName, fileNameToInputStreamMap);
    }

    /**
     * 执行文件上传
     *
     * @param requestUrl               远程接收文件的地址
     * @param fileParamName            文件参数名
     * @param fileNameToInputStreamMap 上传文件列表
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeUploadFiles(String requestUrl,
                                                String fileParamName,
                                                Map<String, InputStream> fileNameToInputStreamMap) throws IOException {

        Charset charset = CharsetUtils.get("UTF-8");

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = createHttpClient(3, 300000);
            HttpPost httpPost = new HttpPost(requestUrl);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setCharset(charset);

            fileNameToInputStreamMap.forEach(
                    (fileName, inputStream) -> entityBuilder.addBinaryBody(fileParamName, inputStream, ContentType.MULTIPART_FORM_DATA, fileName)
            );

            httpPost.setEntity(entityBuilder.build());
            httpResponse = httpClient.execute(httpPost);

            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset.toString());

            return new HttpResult(statusCode, content);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception ignore) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    /**
     * 执行文件上传(以二进制流方式)
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param remoteFileUrl   远程接收文件的地址
     * @param localFilePath   本地文件地址
     * @param charset         请求编码，默认UTF-8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeUploadFileStream(CloseableHttpClient httpClient, String remoteFileUrl, String localFilePath, String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
        CloseableHttpResponse httpResponse = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            if (httpClient == null) {
                httpClient = createHttpClient();
            }
            // 把文件转换成流对象FileBody
            File localFile = new File(localFilePath);
            fis = new FileInputStream(localFile);
            byte[] tmpBytes = new byte[1024];
            byte[] resultBytes = null;
            baos = new ByteArrayOutputStream();
            int len;
            while ((len = fis.read(tmpBytes, 0, 1024)) != -1) {
                baos.write(tmpBytes, 0, len);
            }
            resultBytes = baos.toByteArray();
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(resultBytes, ContentType.APPLICATION_OCTET_STREAM);
            HttpPost httpPost = new HttpPost(remoteFileUrl);
            httpPost.setEntity(byteArrayEntity);
            httpResponse = httpClient.execute(httpPost);
            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            String content = getResult(httpResponse, charset);
            return new HttpResult(statusCode, content);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 执行文件下载
     *
     * @param httpClient      HttpClient客户端实例，传入null会自动创建一个
     * @param remoteFileUrl   远程下载文件地址
     * @param localFilePath   本地存储文件地址
     * @param charset         请求编码，默认UTF-8
     * @param closeHttpClient 执行请求结束后是否关闭HttpClient客户端实例
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static boolean executeDownloadFile(CloseableHttpClient httpClient, String remoteFileUrl, String localFilePath, String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            HttpGet httpget = new HttpGet(remoteFileUrl);
            response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return false;
            }
            in = entity.getContent();
            File file = new File(localFilePath);
            fout = new FileOutputStream(file);
            int l;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
                // 注意这里如果用OutputStream.write(buff)的话，图片会失真
            }
            // 将文件输出到本地
            fout.flush();
            EntityUtils.consume(entity);
            return true;
        } finally {
            // 关闭低层流。
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                }
            }
            if (closeHttpClient && httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 根据参数获取请求的Entity
     *
     * @param paramsObj
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpEntity getEntity(Object paramsObj, String charset) throws UnsupportedEncodingException {
        if (paramsObj == null) {
            logger.info("当前未传入参数信息，无法生成HttpEntity");
            return null;
        }
        if (Map.class.isInstance(paramsObj)) {// 当前是map数据
            @SuppressWarnings("unchecked")
            Map<String, Object> paramsMap = (Map<String, Object>) paramsObj;
            List<NameValuePair> list = getNameValuePairs(paramsMap);
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
            httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            return httpEntity;
        } else if (String.class.isInstance(paramsObj)) {// 当前是string对象，可能是
            String paramsStr = (String) paramsObj;
            StringEntity httpEntity = new StringEntity(paramsStr, charset);
            if (paramsStr.startsWith("{")) {
                httpEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            } else if (paramsStr.startsWith("<")) {
                httpEntity.setContentType(ContentType.APPLICATION_XML.getMimeType());
            } else {
                httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            }
            return httpEntity;
        } else {
            logger.info("当前传入参数不能识别类型，无法生成HttpEntity");
        }
        return null;
    }

    /**
     * 从结果中获取出String数据
     *
     * @param httpResponse http结果对象
     * @param charset      编码信息
     * @return String
     * @throws ParseException
     * @throws IOException
     */
    private static String getResult(CloseableHttpResponse httpResponse, String charset) throws ParseException, IOException {
        String result = null;
        if (httpResponse == null) {
            return result;
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return result;
        }
        result = EntityUtils.toString(entity, charset);
        EntityUtils.consume(entity);// 关闭应该关闭的资源，适当的释放资源 ;也可以把底层的流给关闭了
        return result;
    }

    /**
     * 转化请求编码
     *
     * @param charset 编码信息
     * @return String
     */
    private static String getCharset(String charset) {
        return charset == null ? DEFAULT_CHARSET : charset;
    }

    /**
     * 将map类型参数转化为NameValuePair集合方式
     *
     * @param paramsMap
     * @return
     */
    private static List<NameValuePair> getNameValuePairs(Map<String, Object> paramsMap) {
        List<NameValuePair> list = new ArrayList<>();
        if (paramsMap == null || paramsMap.isEmpty()) {
            return list;
        }
        for (Entry<String, Object> entry : paramsMap.entrySet()) {


            //System.out.println(entry.getKey()+":"+entry.getValue().toString());

            String value = "";
            if (null != entry.getValue()) {
                value = entry.getValue().toString();
            }

            list.add(new BasicNameValuePair(entry.getKey(), value));


        }
        return list;
    }


    /**
     * 为httpclient设置重试信息
     *
     * @param httpClientBuilder
     * @param retryTimes
     */
    private static void setRetryHandler(HttpClientBuilder httpClientBuilder, final int retryTimes) {
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= retryTimes) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求被认为是幂等的，那么就重试
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        httpClientBuilder.setRetryHandler(myRetryHandler);
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String url = "http://101.231.154.154:8047/v4.1/renewal";
        HttpHelper httpHelper = new HttpHelper();
//        params.put("page","100");


        Map<String, Object> parMap = new HashMap<String, Object>();
        parMap.put("licenseNo", "粤S8B7G1");
        parMap.put("token", "388024fc9e164266ae7d04327078161c");
        parMap.put("city", "440100");

        parMap.put("timestamp", String.valueOf(System.currentTimeMillis()));

        String signo = "city=" + parMap.get("city")
                + "&licenseNo=" + parMap.get("licenseNo")
                + "&timestamp=" + parMap.get("timestamp")
                + "&token=" + parMap.get("token")
                + "&SSDL_TEST";

        String sign = HttpHelper.MD5(signo);
        parMap.put("sign", sign);


        System.out.println(signo);
        //city=110100&licenseNo=京A1111&timestamp=2018-07-09T13:45:07.560Z&token=388024fc9e164266ae7d04327078161c&SSDL_TEST

//        //确定计算方法
//        MessageDigest md5=MessageDigest.getInstance("MD5");
//        BASE64Encoder base64en = new BASE64Encoder();
//
//
//        //加密后的字符串
//        String sign =base64en.encode(md5.digest(signo.getBytes("utf-8")));


        try {
            String aaa = executePostString(url, parMap, "UTF-8", 0, null);


            System.out.println(aaa);

            Map<String, String> map = JacksonUtil.jsonStrToStringMap(aaa);

            System.out.println("jsoncompany 值为:" + map.get("company"));

            map.put("city", "510100");

            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static String MD5(String key) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
