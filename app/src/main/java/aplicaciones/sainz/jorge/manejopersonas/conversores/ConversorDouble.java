/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicaciones.sainz.jorge.manejopersonas.conversores;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Conversor personalizado para XStream
 *
 * @author JJSC
 */
public class ConversorDouble implements Converter {
    /* Atributo privado */
    private NumberFormat nf;

    /*
      Toma el formato del numero
     */
    public ConversorDouble() {
        nf = NumberFormat.getInstance(Locale.getDefault());
    }

    /**
     * Convierte de objeto a xml
     *
     * @param o
     * @param writer
     * @param mc
     */
    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        writer.setValue(nf.format((Double) o));
    }

    /**
     * Convierte de xml a objeto
     *
     * @param reader
     * @param uc
     * @return
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        try {
            return nf.parse(reader.getValue()).doubleValue();
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(Double.class);
    }


}
