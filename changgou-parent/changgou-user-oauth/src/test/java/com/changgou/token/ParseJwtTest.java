package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.NeiXFBErbEDKFpT_cGm0fm14Pjg45NC13_FptUPEb5TGgteqE7sEvBryeL_lz_Dn0hBXFXp0-Pj2vprfBy6xwbwwffqFfhNQOBkv2hoW6aBUuXHZz2Nek5pAPHT8rMIra1xt_bxvItnvuK8WyqdaRDPHGD0Ih4d5YY8xfN4VuwzU_h8U0ICHP6G9W1okf6LXb3HMS_X9feIN_AFa5umpxzGeY-OcoQnVt8AVPppB3hh-1JcqlvpTjVAtCOhr7EE3OBq4Rp3X0Aas5PJHEKBYqVuagWmmCbWkwJde0S2Eh-UBSMBmqBSPUf38BdpbP4CWbY_z6OdEEV2AOdidrgWbjQ";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh/0X6RGR0i+rowtPPeI3cihjvlaGmi+0WZ+iv0gYYwudK38vSdxIIT/vqn5d4udjU4WHN022YQzaPXurHqfB4kQcBE8ct58iK4Ima5yn/YUyPlol2WIQ6GZpf+1oZMzdbRjjBKQ8zbBRoODgPyUOSy74JOQvqPno6NwDVRKxFZdUjbZ99+shXFqJ2ooMNAduo1I5GYcZTAzldLrCEFCqE4o+kth3nlu6R0QY5RzkelHmv+fowaNMzTyraqAYM14gD5gYC81sKLiu5CPCL37Sw8c3gHaDIQiATWCVEE+zaPD6EtTb9bBDlrctI34MEW55RDPRWyLp2dLcd71UtNf05QIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
