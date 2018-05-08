package aplicaciones.sainz.jorge.manejopersonas.personas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import aplicaciones.sainz.jorge.manejopersonas.MainActivity;
import aplicaciones.sainz.jorge.manejopersonas.R;
import aplicaciones.sainz.jorge.manejopersonas.personas.adaptadores.AdaptadorPersona;
import aplicaciones.sainz.jorge.manejopersonas.personas.comparadores.CompararPersonas;
import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.DatabaseCustomUtils;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.EntradaSalida;

/**
 * Fragmento que es lanzado inicialmente para el control de la lista de Persona
 *
 * No se puede aplicar el patron de diseño Singleton al fragmento
 */
public class Listado extends Fragment {
    // Variable a ser usada en el control del lanzamiento del formulario
    private final int FORMULARIO = 1;

    // Variable estatica que controla la direccion del ordenamiento
    private static int direccionOrdenamiento;

    /**
     * Atributo que hace referencia a la actividad padre para manipuolar cualquiera de sus metodos publicos,
     * su inicializacion esta en el onAttach y su destruccion en el onDetach, ya que son el primero y ultimo
     * metodo que se ejecutan respectivamente en la creacion y destruccion del fragmento
     */
    private MainActivity mainActivity;


    static {
        direccionOrdenamiento = 1;
    }

    /**
     * Interface que controla las acciones de la lista y que es usada en la actividad principal
     */
    public interface OnFragmentListado {
        void onListaAdicion(Persona persona);

        void onListaEdicion(Integer pos, Persona persona);

        void onListaBorrado(Persona persona);
    }

    /**
     * Definicion del objeto que maneja los callback, al igual que el atributo que maneja la referencia de la
     * actividad padre, su inicializacion esta en el onAttach y su destruccion en el onDetach
     */
    private OnFragmentListado mListener;

    /**
     * Atributos para uso interno
     */
    private Persona persona;
    private List<Persona> personas;
    private AdaptadorPersona adaptadorPersona;
    private Intent formulario;
    private SimpleDateFormat patronFechaSQL;

    /**
     * Constructor de la clase que hereda del Fragment solo
     * puede ir public.
     */
    public Listado() {
        patronFechaSQL = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Metodo estatico para crear instancias del fragmento
     *
     * @return
     */
    public static Listado newInstance() {
        return new Listado();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inicializacion de los elementos del fragmento
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
          Hemos declarado en variable el inflado del layout ya que necesitamos
          recuperar los atributos de la lista y el boton flotante
         */
        View view = inflater.inflate(R.layout.fragment_listado, container, false);

        /*
          Para manejar el lanzamiento del formulario a traves de un fragmento
          el mismo debe ser desde la actividad que lanza al fragmento
         */
        formulario = new Intent(mainActivity, Formulario.class);

        /*
          Inicializar la lista dinamica de persona que se va a usar en el Listado

          La actividad principal controla su propia lista ya que es necesario para la funcionalidad
          de guardado manual que esta definida en el menu lateral
         */

        personas = new ArrayList<>();

        /*
          El boton flotante ahora se encuentra en el layout inflado por el fragmento
         */
        FloatingActionButton fab = view.findViewById(R.id.adicion);
        // le cambiamos el icono
        fab.setImageResource(R.drawable.ic_action_persona_add);
        // Le cambiamos el color
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        fab.setOnClickListener(new OnClickListener() {
            /**
             * Metodo usado para lanzar el formulario en modo de adicion
             * @param view
             */
            @Override
            public void onClick(View view) {
                // Adicion (objeto en blanco y pos -1)
                formulario.putExtra("pos", -1);
                formulario.putExtra("persona", (Parcelable) new Persona());
                // Esta forma de lanzar la actividad controla el resultado devuelto
                startActivityForResult(formulario, FORMULARIO);
            }
        });

        /*
          Inicializacion de la lista, definiendo su adaptador y evento
         */
        ListView listado = view.findViewById(R.id.listado);
        // Cuando se crea el adaptador se le pasan todos los datos necesarios
        adaptadorPersona = new AdaptadorPersona(mainActivity, R.layout.item_persona, personas);
        // Permite que los cambios en la lista sean notificados al adaptador
        adaptadorPersona.setNotifyOnChange(true);

        listado.setAdapter(adaptadorPersona);
        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Modificacion objeto obtenido y posicion del click)
                formulario.putExtra("pos", position);
                formulario.putExtra("persona", (Parcelable) personas.get(position));
                // Lanzamiento del la actividad para controlar el resultado.
                startActivityForResult(formulario, FORMULARIO);
            }
        });

        // Crear el menu contextual para click largo en vez de usar el listener
        registerForContextMenu(listado);

        // Permite usar menu superior en el fragmento
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Establece el titulo en el toolbar que pertenece al Activity que lanza al fragmento
        mainActivity.setActionBarTitle(getString(R.string.titulo_listado));

        // Lee el archivo guardado de objetos segun la preferencia guardada
        if (MainActivity.preferenciasPublicas.getBoolean("leer_archivo", true)) {
            //leerArchivo();
            leerRegistrosBD();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.setActionBarTitle(getString(R.string.titulo_listado));

        /*
         Lee el archivo guardado de objetos segun la preferencia guardada
         Hemos ahora sustituido la lectura del archivo por la lectura desde
         la base de datos
          */
        mainActivity.setActionBarTitle(getString(R.string.titulo_listado));
        if (MainActivity.preferenciasPublicas.getBoolean("leer_archivo", true)) {
            //leerArchivo();
            leerRegistrosBD();
        }
    }

    /**
     * Este metodo permite controlar el resultado de la actividad que se lanzo
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Opcion que controla el lanzamiento del formulario
            case FORMULARIO:
                // Codigo de retorno para las operaciones en el listado
                if (resultCode == 200) {
                    // Se recuperan los datos del formulario
                    persona = data.getParcelableExtra("persona");
                    int pos = data.getIntExtra("pos", -1);
                    /*
                      En dependencia de la posicion se hace una insercion o una modificacion en la lista
                      y se notifica a la actividad principal a traves de los callbacks
                     */
                    if (pos < 0) {
                        /*
                        En este caso hay que recuperar la lista de la actividad principal ya que ella es la que
                        maneja las operaciones con la base de datos y actualiza el campo de la clave primaria
                        */
                        mListener.onListaAdicion(persona);
                        personas.clear();
                        personas.addAll(mainActivity.getPersonas());
                    } else {
                        mListener.onListaEdicion(pos, persona);
                        personas.set(pos, persona);
                    }
                    // Se notifica al adaptador de los cambios
                    adaptadorPersona.notifyDataSetChanged();
                }
        }
    }

    /**
     * Creacion del  menu personalizado superior para realizar el reordenamiento
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SubMenu subMenu = menu.addSubMenu("");

        subMenu.add(getResources().getString(R.string.cambiar_orden))
                .setIcon(R.drawable.ic_action_cambiar_orden);

        MenuItem menuItem = subMenu.getItem();
        menuItem.setIcon(R.drawable.ic_action_setting)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.cambiar_orden))) {
            direccionOrdenamiento = (direccionOrdenamiento < 1) ? 1 : -1;
            /*
              En dependencia de las preferencias llama al metodo estatico sort sobrecargado para usar a la
              clase comparadora que establece el criterio de comparacion utilizando el valor de la preferencia guardada
             */
            if (!MainActivity.preferenciasPublicas.getBoolean("ordenamiento_selectivo", false)) {
                Collections.sort(personas);
            } else {
                Collections.sort(personas, new CompararPersonas(MainActivity.preferenciasPublicas.getString("criterios_ordenamiento", "cedula")));
            }
            mainActivity.setPersonas(personas);
            adaptadorPersona.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo para crear el menu contextual
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getResources().getString(R.string.titulo_menu_contextual));
        menu.add(getResources().getString(R.string.opcion_eliminar));
    }

    /**
     * Accion de la seleccion del menu contextual
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        if (item.getTitle().toString().equals(getResources().getString(R.string.opcion_eliminar))) {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final int idx = info.position;

            // Crea un dialogo local de confirmacion del borrado
            new AlertDialog.Builder(mainActivity)
                    .setCancelable(false)
                    .setTitle(getResources().getString(R.string.advertencia))
                    .setMessage(getResources().getString(R.string.advertencia_borrar_registro))
                    .setPositiveButton(getResources().getString(R.string.boton_si), new DialogInterface.OnClickListener() {
                        /**
                         * En caso positivo de debe eliminar el objetos de la lista e informar a todas las
                         * partes involucradas
                         * @param dialog
                         * @param id
                         */
                        public void onClick(DialogInterface dialog, int id) {
                            // toma el objeto
                            persona = personas.get(idx);
                            // Borra de la lista local
                            personas.remove(idx);
                            // Informa a la actividad principal a traves del callbacks
                            mListener.onListaBorrado(persona);
                            // Informa al adaptador de la lista
                            adaptadorPersona.notifyDataSetChanged();
                            dialog.dismiss();
                            /*
                            Snackbar permite establecer una accion a realizar despues de otra accion
                             */
                            Snackbar.make(getView(), R.string.snack_borrado_titulo, Snackbar.LENGTH_LONG)
                                    .setAction(getResources().getString(R.string.snack_restaurar_titulo), new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            /*
                                            Como el caso de la adicion hay que volver a adicionar y recuperar de la lista
                                            de la actividad principal.
                                             */
                                            persona.setPersonaId(-1);
                                            mListener.onListaAdicion(persona);
                                            personas.clear();
                                            personas.addAll(mainActivity.getPersonas());
                                            adaptadorPersona.notifyDataSetChanged();
                                            Toast.makeText(mainActivity, R.string.toast_restaurado, Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.boton_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Asociamos el callback
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListado) {
            mListener = (OnFragmentListado) context;
            mainActivity = (MainActivity) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    .concat(" must implement OnFragmentListado"));
        }
    }

    /**
     * Desvinculamos el callback
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mainActivity = null;
    }

    /**
     * Lee el archivo de objetos e informa a todas las partes involucradas
     */
    private void leerArchivo() {
        File file = new File(MainActivity.dirPrivado + "/" + MainActivity.preferenciasPublicas.getString("nombre_archivo", "fragment_listado.bin"));
        Log.i("FRAGMENT", file.getAbsolutePath());
        try {
            personas.clear();
            personas.addAll(EntradaSalida.<Persona>leerArchivoObjeto(file));
            mainActivity.setPersonas(personas);
            adaptadorPersona.notifyDataSetChanged();
        } catch (ClassNotFoundException | IOException e) {
            Log.e("ERROR lectura", e.toString());

        }
    }

    /**
     * Lectura de registros de la base de datos, creacion de objetos
     * insercion en la lista e informacion a todas las partes necesarias
     */
    private void leerRegistrosBD() {
        List<Map<String, Object>> select = new ArrayList();
        select = DatabaseCustomUtils.select(MainActivity.getDb(), "persona", null, null, null, null, null, null, null);
        StringBuilder sb = new StringBuilder();
        personas.clear();
        if (select != null) {
            for (Map<String, Object> reg : select) {
                Persona persona = new Persona();
                persona.setPersonaId(Integer.parseInt(reg.get("persona_id").toString()));
                persona.setNombre(reg.get("nombre").toString());
                persona.setCedula(reg.get("cedula").toString());
                persona.setEstadoCivil(reg.get("estado_civil").toString());
                persona.setGenero(reg.get("genero").toString());
                try {
                    persona.setFechaNacimiento(patronFechaSQL.parse(reg.get("fecha_nacimiento").toString()));
                } catch (ParseException e) {
                }
                persona.setEstatura(Double.parseDouble(reg.get("estatura").toString()));
                personas.add(persona);
            }
        }
        mainActivity.setPersonas(personas);
        adaptadorPersona.notifyDataSetChanged();
    }

    /**
     * Publicacion para el uso del atributo estatico usado para establecer la direccion de
     * ordenamiento en la clase externa CompararPersonas, que implementa el Comparator
     *
     * @return
     */
    public static int getDireccionOrdenamiento() {
        return direccionOrdenamiento;
    }

    public static void setDireccionOrdenamiento(int direccionOrdenamiento) {
        Listado.direccionOrdenamiento = direccionOrdenamiento;
    }
}
