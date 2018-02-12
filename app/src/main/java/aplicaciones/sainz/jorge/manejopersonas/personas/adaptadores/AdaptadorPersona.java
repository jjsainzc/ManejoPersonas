package aplicaciones.sainz.jorge.manejopersonas.personas.adaptadores;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import aplicaciones.sainz.jorge.manejopersonas.R;
import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.DateNumberFormatDetector;

/**
 * Clase adaptadora que vincula la fuente de datos y el ListView
 * Contiene el patron de diseño para optimizacion
 */

public class AdaptadorPersona extends ArrayAdapter<Persona> {
    private Activity context;
    private int resource;
    private List<Persona> personas;


    public AdaptadorPersona(@NonNull Activity context, int resource, @NonNull List<Persona> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        personas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         ViewHolder viewHolder;

        /**
         * Si no esta creado el layout se crea una vez y se vinculan sus atributos en la clase estatica
         */
        if (convertView == null) {
            LayoutInflater inflador = context.getLayoutInflater();
            convertView = inflador.inflate(resource, null);

            viewHolder = new ViewHolder();
            viewHolder.nombre = convertView.findViewById(R.id.item_nombre);
            viewHolder.cedula = convertView.findViewById(R.id.item_cedula);
            viewHolder.fechaNacimiento = convertView.findViewById(R.id.item_fechaNacimiento);
            viewHolder.estadoCivil = convertView.findViewById(R.id.item_estadoCivil);
            viewHolder.estatura = convertView.findViewById(R.id.item_estatura);
            viewHolder.genero = convertView.findViewById(R.id.item_genero);

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        /**
         * Establecer el estilo de un texto usando Spannable
         */
        CharSequence nombre = personas.get(position).getNombre();
        int longitud = nombre.length();

        // Definimos el texto que vamos a formatear.
        SpannableStringBuilder ssb1 = new SpannableStringBuilder(nombre);

        // Span para poner el texto en negrita.
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        // Span para poner el texto en cursiva.
        StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);

        ssb1.setSpan(boldSpan, 0, longitud, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ssb1.setSpan(italicSpan, 0, longitud, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        viewHolder.nombre.setText(ssb1, TextView.BufferType.SPANNABLE);

        /**
         * Dar estilo usando Html
         */
        viewHolder.cedula.setText(Html.fromHtml("<font color=\"#0F7E0C\"'><b><i>"+personas.get(position).getCedula()+"</b></i></font>"));



        viewHolder.fechaNacimiento.setText(DateNumberFormatDetector.getSimpleDateFormat().format(personas.get(position).getFechaNacimiento()));
        viewHolder.estadoCivil.setText(personas.get(position).getEstadoCivil());
        viewHolder.estatura.setText(personas.get(position).getEstatura().toString());
        viewHolder.genero.setText(personas.get(position).getGenero());

        return convertView;
    }

    /**
     * Clase estatica que almacena los atributos del layout para contruir la lista
     */
    static class ViewHolder {
        private TextView nombre;
        private TextView cedula;
        private TextView fechaNacimiento;
        private TextView estatura;
        private TextView genero;
        private TextView estadoCivil;
    }

}


