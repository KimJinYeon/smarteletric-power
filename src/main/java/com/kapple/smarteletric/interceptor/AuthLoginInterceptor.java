package com.kapple.smarteletric.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

@Component
public class AuthLoginInterceptor implements HandlerInterceptor {
    private String publicKey = "";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        try{
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
            RSAPublicKey rsaPublicKey = getPublicKeyFromString(publicKey);
            Algorithm rsaAlgorithm = Algorithm.RSA256(rsaPublicKey);
            JWTVerifier jwtVerifier = JWT.require(rsaAlgorithm)
                    .build();
            jwtVerifier.verify(authorizationHeader);
            return true;
        } catch (Exception e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            System.out.println("Exception in verifying = " + e);
            e.printStackTrace();
            return false;
        }
    }

    public static RSAPublicKey getPublicKeyFromString(String key) throws
            IOException, GeneralSecurityException {

        String publicKeyPEM = key;

        /**replace headers and footers of cert, if RSA PUBLIC KEY in your case, change accordingly*/
        publicKeyPEM = publicKeyPEM.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyPEM));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        return pubKey;
    }
}
