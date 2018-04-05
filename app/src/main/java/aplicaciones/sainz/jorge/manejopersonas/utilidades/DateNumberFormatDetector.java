/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicaciones.sainz.jorge.manejopersonas.utilidades;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Clase que detecta los formatos de fecha y numero configurados en el dispositivo
 *
 * @author JJSC
 */
public class DateNumberFormatDetector {
    private static final Locale fmtLocale;
    private static final DateFormat dateFormatter;

    private final static NumberFormat numFormatter;
    private final static DecimalFormat decimalFormat;
    private final static SimpleDateFormat simpleDateFormat;
    private final static String datePattern;

    static {
        fmtLocale = Locale.getDefault();
        dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, fmtLocale);
        String temp = ((SimpleDateFormat) dateFormatter).toPattern();
        if (temp.contains("yy") && !temp.contains("yyyy")) {
            temp = temp.replaceFirst("yy", "yyyy");
        }
        datePattern = temp;
        simpleDateFormat = new SimpleDateFormat(datePattern);
        numFormatter = NumberFormat.getInstance(fmtLocale);
        decimalFormat = new DecimalFormat(((DecimalFormat) numFormatter).toPattern());
    }

    public static String getDatePattern() {
        return datePattern;
    }

    public static NumberFormat getNumFormatter() {
        return numFormatter;
    }

    public static DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

}
