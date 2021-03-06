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
    private static final String DEFAULT_CHARSET = "UTF-8";// ??????????????????
    private static final int DEFAULT_SOCKET_TIMEOUT = 12 * 1000;// ????????????????????????(??????)
    private static final int DEFAULT_RETRY_TIMES = 0;// ???????????????????????????


    // HTTPS???????????????????????????????????????????????????SHA-1????????????????????????????????????SSL?????????????????????????????????????????????SSL???
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
     * ??????SSL??????
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
     * ?????????????????????????????????HttpClient
     *
     * @return
     */
    public static CloseableHttpClient createHttpClient() {
        return createHttpClient(DEFAULT_RETRY_TIMES, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * ????????????????????????HttpClient
     *
     * @param socketTimeout ?????????????????????????????????
     * @return
     */
    public static CloseableHttpClient createHttpClient(int socketTimeout) {
        return createHttpClient(DEFAULT_RETRY_TIMES, socketTimeout);
    }

    /**
     * ????????????????????????HttpClient
     *
     * @param socketTimeout ?????????????????????????????????
     * @param retryTimes    ???????????????????????????0???????????????
     * @return
     */
    public static CloseableHttpClient createHttpClient(int retryTimes, int socketTimeout) {
        Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(5000);// ???????????????????????????????????????
        builder.setConnectionRequestTimeout(1000);// ?????????connect Manager??????Connection ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (socketTimeout >= 0) {
            builder.setSocketTimeout(socketTimeout);// ??????????????????????????????????????????????????? ?????????????????????????????????????????????????????????????????????????????????????????????
        }
        RequestConfig defaultRequestConfig = builder.setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
        // ??????HTTPS??????
        enableSSL();
        // ????????????Scheme
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory).build();
        // ??????ConnectionManager?????????Connection????????????
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (retryTimes > 0) {
            setRetryHandler(httpClientBuilder, retryTimes);
        }
        CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
        return httpClient;
    }

    /**
     * ??????GET??????
     *
     * @param url           ??????URL??????
     * @param charset       ????????????????????????UTF-8
     * @param socketTimeout ????????????????????????
     * @return HttpResult
     * @throws IOException
     */
    public static HttpResult executeGet(String url, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executeGet(httpClient, url, null, null, charset, true, authToken);
    }

    /**
     * ??????GET??????
     *
     * @param url           ??????URL??????
     * @param charset       ????????????????????????UTF-8
     * @param socketTimeout ????????????????????????
     * @return String
     * @throws IOException
     */
    public static String executeGetString(String url, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executeGetString(httpClient, url, null, null, charset, true, authToken);
    }

    /**
     * ??????HttpGet??????
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param url             ?????????????????????
     * @param referer         referer???????????????null
     * @param cookie          cookies???????????????null
     * @param charset         ?????????????????????UTF8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
     * @return HttpResult
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResult executeGet(CloseableHttpClient httpClient, String url, String referer, String cookie, String charset, boolean closeHttpClient, String authToken) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executeGetResponse(httpClient, url, referer, cookie, authToken);
            //Http???????????????
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
     * @param httpClient httpclient??????
     * @param url        ??????GET???URL??????
     * @param referer    referer??????
     * @param cookie     cookie??????
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
     * ??????HttpGet??????
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param url             ?????????????????????
     * @param referer         referer???????????????null
     * @param cookie          cookies???????????????null
     * @param charset         ?????????????????????UTF8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
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
     * ??????????????????POST??????
     *
     * @param url           ??????URL??????
     * @param paramsObj     post??????????????????map<String,String>,JSON,XML
     * @param charset       ????????????????????????UTF-8
     * @param socketTimeout ????????????(??????)
     * @return HttpResult
     * @throws IOException
     */
    public static HttpResult executePost(String url, Object paramsObj, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executePost(httpClient, url, paramsObj, null, null, charset, true, authToken);
    }

    /**
     * ??????????????????POST??????
     *
     * @param url           ??????URL??????
     * @param paramsObj     post??????????????????map<String,String>,JSON,XML
     * @param charset       ????????????????????????UTF-8
     * @param socketTimeout ????????????(??????)
     * @return HttpResult
     * @throws IOException
     */
    public static String executePostString(String url, Object paramsObj, String charset, int socketTimeout, String authToken) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(socketTimeout);
        return executePostString(httpClient, url, paramsObj, null, null, charset, true, authToken);
    }

    /**
     * ??????HttpPost??????
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param url             ?????????????????????
     * @param paramsObj       ????????????????????????????????????Map,???String(JSON\xml)
     * @param referer         referer???????????????null
     * @param cookie          cookies???????????????null
     * @param charset         ?????????????????????UTF8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static HttpResult executePost(CloseableHttpClient httpClient, String url, Object paramsObj, String referer, String cookie, String charset, boolean closeHttpClient, String authToken) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            charset = getCharset(charset);
            httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset, authToken);
            //Http???????????????
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
     * ??????HttpPost??????
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param url             ?????????????????????
     * @param paramsObj       ????????????????????????????????????Map,???String(JSON\xml)
     * @param referer         referer???????????????null
     * @param cookie          cookies???????????????null
     * @param charset         ?????????????????????UTF8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
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
     * @param httpClient HttpClient??????
     * @param url        ?????????????????????
     * @param paramsObj  ????????????
     * @param referer    ????????????
     * @param cookie     cookie??????
     * @param charset    ????????????
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
        // ????????????
        HttpEntity httpEntity = getEntity(paramsObj, charset);
        if (httpEntity != null) {
            post.setEntity(httpEntity);
        }
        return httpClient.execute(post);
    }

    /**
     * ??????????????????
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param remoteFileUrl   ???????????????????????????
     * @param localFilePath   ??????????????????
     * @param charset         ?????????????????????UTF-8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
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
            // ???????????????????????????FileBody
            File localFile = new File(localFilePath);
            FileBody fileBody = new FileBody(localFile);
            // ?????????????????????????????????????????????????????????
            HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addPart("uploadFile", fileBody).setCharset(CharsetUtils.get("UTF-8")).build();
            // uploadFile?????????????????????????????????<File??????>
            // .addPart("uploadFileName", uploadFileName)
            // uploadFileName?????????????????????????????????<String??????>
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
     * ??????????????????
     *
     * @param requestUrl    ???????????????????????????
     * @param fileParamName ???????????????
     * @param filePathList  ??????????????????
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

        // ????????????
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

        // ????????????
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
     * ??????????????????
     *
     * @param requestUrl               ???????????????????????????
     * @param fileParamName            ???????????????
     * @param fileNameToInputStreamMap ??????????????????
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
     * ??????????????????(?????????????????????)
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param remoteFileUrl   ???????????????????????????
     * @param localFilePath   ??????????????????
     * @param charset         ?????????????????????UTF-8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
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
            // ???????????????????????????FileBody
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
     * ??????????????????
     *
     * @param httpClient      HttpClient????????????????????????null?????????????????????
     * @param remoteFileUrl   ????????????????????????
     * @param localFilePath   ????????????????????????
     * @param charset         ?????????????????????UTF-8
     * @param closeHttpClient ?????????????????????????????????HttpClient???????????????
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
                // ?????????????????????OutputStream.write(buff)????????????????????????
            }
            // ????????????????????????
            fout.flush();
            EntityUtils.consume(entity);
            return true;
        } finally {
            // ??????????????????
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
     * ???????????????????????????Entity
     *
     * @param paramsObj
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpEntity getEntity(Object paramsObj, String charset) throws UnsupportedEncodingException {
        if (paramsObj == null) {
            logger.info("??????????????????????????????????????????HttpEntity");
            return null;
        }
        if (Map.class.isInstance(paramsObj)) {// ?????????map??????
            @SuppressWarnings("unchecked")
            Map<String, Object> paramsMap = (Map<String, Object>) paramsObj;
            List<NameValuePair> list = getNameValuePairs(paramsMap);
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
            httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            return httpEntity;
        } else if (String.class.isInstance(paramsObj)) {// ?????????string??????????????????
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
            logger.info("???????????????????????????????????????????????????HttpEntity");
        }
        return null;
    }

    /**
     * ?????????????????????String??????
     *
     * @param httpResponse http????????????
     * @param charset      ????????????
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
        EntityUtils.consume(entity);// ??????????????????????????????????????????????????? ;????????????????????????????????????
        return result;
    }

    /**
     * ??????????????????
     *
     * @param charset ????????????
     * @return String
     */
    private static String getCharset(String charset) {
        return charset == null ? DEFAULT_CHARSET : charset;
    }

    /**
     * ???map?????????????????????NameValuePair????????????
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
     * ???httpclient??????????????????
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
                    // ???????????????????????????????????????????????????
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
        parMap.put("licenseNo", "???S8B7G1");
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
        //city=110100&licenseNo=???A1111&timestamp=2018-07-09T13:45:07.560Z&token=388024fc9e164266ae7d04327078161c&SSDL_TEST

//        //??????????????????
//        MessageDigest md5=MessageDigest.getInstance("MD5");
//        BASE64Encoder base64en = new BASE64Encoder();
//
//
//        //?????????????????????
//        String sign =base64en.encode(md5.digest(signo.getBytes("utf-8")));


        try {
            String aaa = executePostString(url, parMap, "UTF-8", 0, null);


            System.out.println(aaa);

            Map<String, String> map = JacksonUtil.jsonStrToStringMap(aaa);

            System.out.println("jsoncompany ??????:" + map.get("company"));

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
