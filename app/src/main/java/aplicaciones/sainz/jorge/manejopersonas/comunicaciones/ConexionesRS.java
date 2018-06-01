package aplicaciones.sainz.jorge.manejopersonas.comunicaciones;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *  @author Created by JJSC on 30/07/2016.
 * <p>
 * Conexion universal para RESTfull y Servlets.
 * Tambien puede usarse para scripts de PHP con GET y POST
 */
public class ConexionesRS {
    private static String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    /**
     * Metodo universal para conectar RETfull y Servlet
     * @author Jorge Sainz, 2017
     *
     * @param urlStr         http://host:port/resource
     * @param script         metodo
     * @param encodedParam   true si se quiere hacer un URLEncoder.encode(value, "UTF-8")
     * @param doOutput       false para GET y true  para PUT o POST ya que fuerza la salida del body
     * @param param          Map que contiene clave=valor para los parametros
     * @param contentType    un contenido valido para el envio, usualmente application/x-www-form-urlencoded;charset=UTF-8
     * @param accept         un contenido valido para el retorno application/json, application/xml, text/plain
     * @param data           data que no se envia en los parametros usualmente en POST y PUT
     * @param method         metodo valido GET, POST, PUT, DELETE
     * @param authToken      Si este campo esta lleno se refiere a un token jwt, para usar con un encabezado
     *                       Authorization: Bearer header.payload.signAlg
     * @param connectTimeout Tiempo maximo de espera en la conexion
     * @param readTimeout    Tiempo maximo de espera en la lectura
     * @return Map [code=,message=,body=]
     * @throws IOException Errores de entrada / salida del socket
     */
    public static synchronized Map<String, String> connectREST(String urlStr,
                                                               String script,
                                                               Boolean encodedParam,
                                                               Boolean doOutput,
                                                               Map<String, String> param,
                                                               String contentType,
                                                               String accept,
                                                               String data,
                                                               String method,
                                                               String authToken,
                                                               int connectTimeout,
                                                               int readTimeout) throws IOException {

        Map<String, String> res = new LinkedHashMap();
        StringBuilder sb = new StringBuilder();
        String paramStr = "";
        String body;
        int code;

        if ((param != null) && !param.isEmpty()) {
            String llave;
            String valor;
            sb.append("?");
            for (Iterator it = param.keySet().iterator(); it.hasNext(); ) {
                llave = (String) it.next();
                sb.append(llave).append("=");
                if (encodedParam) {
                    valor = encodeValue(param.get(llave));
                } else {
                    valor = param.get(llave);
                }

                sb.append(valor)
                        .append("&");
            }
            paramStr = sb.toString().substring(0, sb.toString().length() - 1);
        }

        try {
            URL url = new URL(urlStr + script + paramStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(doOutput);
            connection.setRequestProperty("Content-Type", contentType);
            //connection.setRequestProperty("Content-Length", Integer.toString(paramStr.length()));
            connection.setRequestProperty("Accept", accept);
            if (!authToken.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer ".concat(authToken));
            }

            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if ((data != null) && (!data.isEmpty())) {
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(data);
                osw.flush();
                osw.close();
            }
            code = connection.getResponseCode();

            if (code < 300) {
                InputStream response = connection.getInputStream();
                String encoding = connection.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;

                //String body = IOUtils.toString(response, encoding);
                body = getStringFromInputStream(response);
            } else {
                body = "";
            }

            res.put("code", String.valueOf(code));
            res.put("body", body);
            res.put("message", connection.getResponseMessage());

        } catch (IOException e) {
            throw new IOException(e);
        }
        return res;
    }

    /**
     * Convierte un mecanismo de entrada tipo Stream en cadena de caracter
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static String getStringFromInputStream(InputStream is) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new IOException(e);
                }
            }
        }
        return sb.toString();
    }

    /**
     * ==========================================================================
     * Sobrecargas del metodo connectREST
     * ==========================================================================
     */

    public static synchronized Map<String, String> connectREST(String urlStr,
                                                               String script,
                                                               Map<String, String> param,
                                                               String contentType,
                                                               String data,
                                                               String method, String auth) throws IOException {

        return connectREST(urlStr,
                script,
                false,
                false,
                param,
                contentType,
                "application/json",
                data,
                method, auth, 5000, 5000);

    }

    public static synchronized Map<String, String> connectREST(String urlStr,
                                                               String script,
                                                               Map<String, String> param,
                                                               String data,
                                                               String method,
                                                               String auth
    ) throws IOException {

        return connectREST(urlStr,
                script,
                false,
                false,
                param,
                "application/x-www-form-urlencoded;charset=UTF-8",
                "application/json",
                data,
                method,
                auth,
                5000,
                5000);

    }

    public static synchronized Map<String, String> connectREST(String urlStr,
                                                               String script,
                                                               Map<String, String> param,
                                                               String data,
                                                               String method,
                                                               String auth,
                                                               int connectTimeout,
                                                               int readTimeout) throws IOException {

        return connectREST(urlStr,
                script,
                false,
                false,
                param,
                "application/x-www-form-urlencoded;charset=UTF-8",
                "application/json",
                data,
                method,
                auth,
                connectTimeout,
                readTimeout);

    }

    public static synchronized Map<String, String> connectREST(String urlStr,
                                                               String script,
                                                               Map<String, String> param,
                                                               String contentType,
                                                               String accept,
                                                               String data,
                                                               String method,
                                                               String auth,
                                                               int connectTimeout,
                                                               int readTimeout) throws IOException {

        return connectREST(urlStr,
                script,
                false,
                false,
                param,
                contentType,
                accept,
                data,
                method,
                auth,
                connectTimeout,
                readTimeout);

    }


}
