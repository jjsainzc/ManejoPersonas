package aplicaciones.sainz.jorge.manejopersonas.comunicaciones;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

/**
 * Consume webservices de tipo SOAP
 * <p>
 * Created by JJSC on 12/11/2016.
 */
public class WebServiceCliente {

    /**
     * Metodo para realizar el consumo
     *
     * @param namespace
     * @param url
     * @param method
     * @param parametros
     * @param doNet
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static Object consume(String namespace,
                                 String url,
                                 String method,
                                 Map<String, String> parametros,
                                 Boolean doNet) throws IOException, XmlPullParserException {
        Object resultado = null;
        SoapObject request;
        final String SOAP_ACTION = namespace + method;

        request = new SoapObject(namespace, method);

        for (String clave : parametros.keySet()) {
            request.addProperty(clave, parametros.get(clave));
        }
        // Inicializacion del protocolo
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        // No .NET
        envelope.dotNet = doNet;

        // Inicializacion del transporte
        HttpTransportSE httpTransport = new HttpTransportSE(url);

        // LLamada al metodo
        httpTransport.call(SOAP_ACTION, envelope);

			/*
             *  Con JAX-WS se puede usar el SoapPrimitive para traer objetos
			 *
			resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
			resultado += resultsRequestSOAP.toString();
			*/

        /*
         *  Para el nusoap JAX-RPC se trata la respuesta como objeto
         */
        resultado = (Object) envelope.getResponse();


        return resultado;
    }

}
