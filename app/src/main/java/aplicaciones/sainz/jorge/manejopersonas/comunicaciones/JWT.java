/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicaciones.sainz.jorge.manejopersonas.comunicaciones;

import org.apache.commons.codec.binary.Base64;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que crea, verifica y decodifica un JWT
 * <p>
 * Hay que incluir las dependencias junto con el jar para Java PC
 * <p>
 * jackson-annotations-2.8.0.jar
 * jackson-core-2.8.9.jar
 * jackson-databind-2.8.9.jar
 * jjwt-0.9.0.jar
 * <p>
 * Para el caso de Android se puede adicionar en el gradle
 * implementation 'io.jsonwebtoken:jjwt:0.9.0'
 * <p>
 * En ambos casos se debe usar algun mecanismo para codificar/decodificar base64
 * commons-codec-1.7.jar
 *
 * @author ALIENWARE
 */
public class JWT {
    /**
     * Decodifica un token
     *
     * @param jwtToken
     * @return
     * @throws
     */
    public static Map decodeToken(String jwtToken) {
        Map<String, String> result = new HashMap();

        String[] splitStr = jwtToken.split("\\.");
        String base64EncodedHeader = splitStr[0];
        String base64EncodedBody = splitStr[1];

        String header;
        header = new String(Base64.decodeBase64(base64EncodedHeader.getBytes()));
        result.put("header", header);
        String body;
        body = new String(Base64.decodeBase64(base64EncodedBody.getBytes()));
        result.put("body", body);

        return result;
    }

    /**
     * Verifica un token segun clave y algoritmo
     *
     * @param key
     * @param token
     * @return
     */
    public static Boolean verifyToken(String key, String token, String alg) {
        Map jwt = decodeToken(token);

        String compactJws = Jwts.builder()
                .setSubject(jwt.get("body").toString())
                .signWith(SignatureAlgorithm.valueOf(alg), key)
                .compact();
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws);
            return true;
        } catch (SignatureException e) {
            return false;
        }
    }

    /**
     * Crea un token en base a un algoritmo
     *
     * @param key
     * @param payload
     * @param signatureAlg
     * @return
     */
    public static String createToken(String key, String payload, String signatureAlg) {
        String resp = Jwts.builder()
                .setSubject(payload)
                .signWith(SignatureAlgorithm.valueOf(signatureAlg), new String(Base64.encodeBase64(key.getBytes())))
                .compact();
        return resp;
    }
}
