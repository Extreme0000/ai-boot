package fun.aiboot.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {
    private static final String signKeyStr = "itheimaitptlqwertyuiop1122334455660000000000000"; //签名密钥
    private static final Long expire = 43200000L;  //过期时间

    // 将字符串密钥转换为 SecretKey
    private static Key getSigningKey() {
        return new SecretKeySpec(signKeyStr.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return
     */
    public static String generateJwt(Map<String, Object> claims){
        log.info("生成JWT令牌：{}", claims);
        return Jwts.builder()
                .addClaims(claims)
                .signWith(getSigningKey()) // 使用新的签名方法
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt){
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // 使用新的解析方式
                .parseClaimsJws(jwt)
                .getBody();
    }
}
