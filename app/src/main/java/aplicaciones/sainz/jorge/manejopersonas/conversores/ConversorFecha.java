/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicaciones.sainz.jorge.manejopersonas.conversores;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * Conversor personalizado para XStream
 *
 * @author JJSC
 */
public class ConversorFecha implements Converter {

    private DateFormat dateFormat;
    private String datePat;
    private SimpleDateFormat sdf;

    /**
     * En este constructor se detecta el formato local de la fecha que esta
     * configurada en el celular
     */
    public ConversorFecha() {
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        datePat = ((SimpleDateFormat) dateFormat).toPattern();

        if (datePat.contains("yy") && (!datePat.contains("yyyy"))) {
            datePat = datePat.replace("yy", "yyyy");
        }

        // Forzamos el patron de fecha para que coincida con el servidor
        datePat = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(datePat);
    }

    /**
     * Convierte de objeto a XML
     *
     * @param o      Objeto a convertir
     * @param writer Mecanismo de salida al XML
     * @param mc
     */
    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(((Date) o));
        Date date = calendar.getTime();

        writer.setValue(sdf.format(date));
    }

    /**
     * Convierte XML a Objeto
     *
     * @param reader
     * @param uc
     * @return
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        GregorianCalendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(sdf.parse(reader.getValue()));
        } catch (ParseException e) {
            throw new ConversionException(e.getMessage(), e);
        }
        return calendar.getTime();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

}
