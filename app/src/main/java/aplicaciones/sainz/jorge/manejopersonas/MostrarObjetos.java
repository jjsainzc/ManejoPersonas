package aplicaciones.sainz.jorge.manejopersonas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.XMLFormat;


/**
 * Este fragmento recibe parametros dinamicos segun sus metodos estaticos sobrecargados,
 * para mostrar los datos que recibe
 */
public class MostrarObjetos extends Fragment {
    private List<Persona> personas;
    private MainActivity mainActivity;
    private String xml;
    private String tipo;

    public MostrarObjetos() {
    }

    /**
     * Primer metodo
     *
     * @param xml  Recibe un XML
     * @param tipo Tipo a mostrar en el titulo
     * @return
     */
    public static MostrarObjetos newInstance(String xml, String tipo) {
        MostrarObjetos fragment = new MostrarObjetos();
        Bundle args = new Bundle();

        args.putString("xml", xml);
        args.putString("tipo", tipo);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Metodo 2
     *
     * @param personas Recibe una lista de personas que tienen implementado Parcelable
     * @return
     */
    public static MostrarObjetos newInstance(List<Persona> personas) {
        MostrarObjetos fragment = new MostrarObjetos();
        Bundle args = new Bundle();
        /*
         Recoge la lista de parcelable que le envia como argumento la actividad
         principal
         */
        args.putParcelableArrayList("personas", (ArrayList<? extends Parcelable>) personas);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = "";

            if (getArguments().keySet().contains("tipo")) {
                tipo = getArguments().getString("tipo");
            }

            if (getArguments().keySet().contains("xml")) {
                xml = getArguments().getString("xml");
            } else if (getArguments().keySet().contains("personas")) {
                personas = getArguments().getParcelableArrayList("personas");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mostrar_objetos, container, false);
        StringBuilder listado = new StringBuilder();

        String resultado = "";

        if ((personas != null) && (personas.size() > 0)) {
            for (Persona persona : personas) {
                listado.append(persona).append("\n");
                resultado = listado.toString();
            }
        }
        if ((xml != null) && (xml.length() > 0)) {
            try {
                /*
                Formatea el XML para una vista mas comoda
                 */
                resultado = XMLFormat.prettyFormat(xml);
            } catch (DocumentException | IOException e) {
                resultado = e.toString();
            }
        }

        ((TextView) view.findViewById(R.id.resultado)).setText(resultado);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tipo = (tipo.isEmpty()) ? "" : " (" + tipo + ")";
        if (tipo.isEmpty()) {
            mainActivity.setActionBarTitle(getResources().getString(R.string.mostar_objetos_titulo));
        } else {
            mainActivity.setActionBarTitle(getString(R.string.lectura) + tipo);
        }
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }


}
