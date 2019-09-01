import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author MartinMYZ
 * @description
 * @created at 2019/8/20
 * "NOTHING IS TRUE, EVERYTHING IS PERMITTED"
 */
public class JwtTest {
    /****
     * 创建Jwt令牌
     */
    @Test
    public void testCreateJwt(){
        JwtBuilder builder= Jwts.builder()
                .setId("888")             //设置唯一编号
                .setSubject("小白")       //设置主题  可以是JSON数据
                .setIssuedAt(new Date())  //设置签发日期
            /*    .setExpiration(new Date())*/
                .signWith(SignatureAlgorithm.HS256,"itcast");//设置签名 使用HS256算法，并设置SecretKey(字符串)


        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("name","王五");
        userInfo.put("age",27);
        userInfo.put("address","深圳社会主义先行示范区");
        builder.addClaims(userInfo);

        //构建 并返回一个字符串
        System.out.println( builder.compact() );
    }

    /**
     * 解析jwt令牌数据
     */
    @Test
    public void testParseJwt(){
        String compactJwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJpYXQiOjE1NjYzMTA1NTEsImFkZHJlc3MiOiLmt7HlnLPnpL7kvJrkuLvkuYnlhYjooYznpLrojIPljLoiLCJuYW1lIjoi546L5LqUIiwiYWdlIjoyN30.xWbui7Z5eM_CF5X8mdr-VSE1-8rEmqkZlEqr5Omc_js";
        Claims claims = Jwts.parser().setSigningKey("itcast").parseClaimsJws(compactJwt).getBody();
        System.out.println(claims);
    }

}
