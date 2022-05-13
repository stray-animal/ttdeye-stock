package com.ttdeye.stock.common.utils;

import com.ttdeye.stock.common.domain.JwtObj;
import com.ttdeye.stock.entity.TtdeyeUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @Comment:
 * @Author:       Zhangyongming
 * @Date:         2019/10/17 15:08
 */
public class JwtUtils {
    private final static String base64Secret = "szaisinoMDk4ZjZbudinglingyangiY2Q0NjIxZDM3M2NhZGU-";

    /**
     * 定义泛型方法，方便传入任何类型入参对象
     */
    public static <T> String createJwt(T obj) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //构造jwt
        TtdeyeUser ttdeyeUser = (TtdeyeUser) obj;
        ttdeyeUser.setLoginPassword(null);
        JwtBuilder builder = Jwts.builder().setHeaderParam("type", "jwt")
                .claim("ttdeyeUser", ttdeyeUser)
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
//        long expMillis = nowMillis + GlobalBusinessConstant.EXPIRE_TIMES.WEEKS_ONE;
//        Date exp = new Date(expMillis);
//        builder.setExpiration(exp).setNotBefore(now);
        //生成jwt
        return builder.compact();
    }

    /**
     * 解析jwt
     * @param jsonWebToken
     * @return
     */
    public static Claims parseJWT(String jsonWebToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Secret))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        JwtObj jwtObj = new JwtObj();
        jwtObj.setCusName("xxx有限公司");
        jwtObj.setTaxNo("45623598205641980");
        jwtObj.setMachineNo("0");
        jwtObj.setExpires(300000);
        String jwtStr = createJwt(jwtObj);
        System.out.println("JwtToken是："+jwtStr);
        System.out.println();

        long timestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        try {
            String aesStr = AesUtil.encrypt(jwtStr, String.format("%016d",timestamp));
            System.out.println("AES加密："+aesStr);
            System.out.println();
            String aesDeStr = AesUtil.decrypt(aesStr, String.format("%016d",timestamp));
            System.out.println("AES解密："+aesDeStr);
            jwtStr = aesDeStr;
        }catch (Exception e){
            e.printStackTrace();
        }


        System.out.println();
        Claims claims = parseJWT(jwtStr);
        System.out.println(claims.get("taxNo"));
        System.out.println(claims.get("cusName"));
        System.out.println(claims.get("machineNo"));

    }
}