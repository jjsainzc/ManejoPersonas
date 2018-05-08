package aplicaciones.sainz.jorge.manejopersonas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aplicaciones.sainz.jorge.manejopersonas.comunicaciones.ConexionesRS;
import aplicaciones.sainz.jorge.manejopersonas.comunicaciones.WebServiceCliente;
import aplicaciones.sainz.jorge.manejopersonas.personas.Listado;
import aplicaciones.sainz.jorge.manejopersonas.conversores.ConversorDouble;
import aplicaciones.sainz.jorge.manejopersonas.conversores.ConversorFecha;
import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;
import aplicaciones.sainz.jorge.manejopersonas.preferencias.Preferencias;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.DatabaseCustomUtils;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.EntradaSalida;

/**
 * Ejemplo Manejo de personas
 * <p>
 * Curso Android (Modulo 1 y 2)
 *
 * @author Jorge Jesus Sainz Casalla
 * Instructor de lenguajes de programacion
 *
 * NOTA: Solo hay uso de ñ en los comentarios, no hay uso de acentos.
 * <p>
 * CEC (Enero-Marzo 2018)
 * <p>
 * <p>
 * Temas que abarca.
 * -------------------------------------------------------------------------------------------------
 * - Diseño general, layouts, uso de componentes, recursos.
 * - Patrones de diseños basados en estaticos.
 * - Navegacion lateral y uso de boton flotante.
 * - Menus superiores y contextuales.
 * - Uso de Snackbar para recuperar la accion.
 * - Estilos de texto usando Spannable y Html
 * - Eventos con declaraciones en clase y en componentes
 * - Preferencias publicas del usuario, usando fragmento.
 * - Dialogos.
 * - Entrada/Salida, flujos de bytes y caracteres
 * - Ordenamiento dinamicos de lista usando Comparator
 * - Componente y tratamiento de fecha.
 * - ListView y Spinner, adaptadores y optimizaciones.
 * - Lanzamiento de Activities, simples y en espera de un resultado.
 * - Parcelables.
 * - Validacion de datos de un formulario usando expresiones regulares y
 * base de datos para evitar repeticiones de campos unicos
 * - Uso de fragmentos dinamicos y callbacks, paso de parametros por arguments.
 * - Uso de XML y Json para serializar objetos y guardar archivo.
 * - Conversores personalizados para Gson y Xstream
 * - Uso de base de datos.
 * - Uso de anotaciones y reflexion para construir coleccion de atributos y valores
 * - Creacion de proceso en segundo plano usando una clase que hereda de AsyncTask
 * - Uso de metodo estatico en clase utilitaria para hacer conexion universal a RESTfull y
 * Servlet, asi como tambien para scripts de PHP siempre y cuando devuelvan un codigo
 * HTML correcto.
 * - Formateo de XML (utilidad).
 * - Guardado de un archivo XML que representa el listado en el DOWNLOADS con el nombre
 * personas.xml
 * - Lectura de un webservice tipo SOAP
 * NOTA: Cuando se usa ksoap2-android-assembly-2.4-jar-with-dependencies.jar, no se puede
 * usar xmlpull-1.1.3.1.jar de XStream porque entran en conflicto de multiDex, entonces
 * dejar solo la biblioteca de ksoap
 * - Trabajo con mapas, lectura, adicion de marcas, cambios de visualizacion.
 * - Proveedor de contenido, definicion y creacion de acciones.
 * -----------------------------------------------------------------------------------------------
 * <p>
 * Funcionalidad general
 * <p>
 * A partir de un objeto Persona (POJO) realizar un mantenimiento de datos usando una pantalla de
 * fragment_listado (Listado) que tiene la responsabilidad de adicionar, modificar, borrar y ordenar
 * los mismos, una segunda pantalla (Formulario) es la que realiza las acciones  de adicionar y modificacion.
 * <p>
 * La pantalla de Listado puede cambiar el ordenamiento mediante un menu superior.
 * <p>
 * El menu lateral brinda la posibilidad de establecer preferencias, guardarArchivoObjeto manualmente
 * la lista, mostrar los objetos serializados en json y leer de webservices tipo SOAP y RESTfull
 * <p>
 * Para mantener el menu lateral se usan fragmentos que son manejados en un FrameLayout en el
 * layout principal.
 * <p>
 * El almacenamiento de la lista se hace en el directorio privado de la aplicacion usando la modalidad
 * binaria que corresponde a la jerarquia de flujo de bytes y en el directorio DOWNLOADS usando la
 * jerarquia  de flujo de caracteres (XML).
 * <p>
 * La clase principal MainActivity implementa la interface OnFragmentListado del fragmento Listado.
 * Serializa los objetos con Json para mostrarlos en una pantalla aparte.
 * Lee una lista XML de webservices tipo SOAP y RESTfull y la muestra en una pantalla aparte.
 * <p>
 * Inclusion de un mapa en fragmento, adicion de marcas y cambios de visualizacion.
 * <p>
 * Creacion de un proveedor de contenidos que permita la recuperacion e insercion de datos.
 **/
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Listado.OnFragmentListado {

    // Componentes del contenedor
    private FrameLayout contenedor;
    private Drawable fondo;

    // Lista del objeto a tratar
    private List<Persona> personas;

    // Variables de caminos publicos y privados para entrada salida
    public static String dirPrivado;
    public static String dirPublico;

    // Variable para el tratamiento de las preferencias
    public static SharedPreferences preferenciasPublicas;

    // Controlador de dos click para salir de la aplicacion
    private boolean doubleBackToExitPressedOnce;

    /*
     Atributos para uso de la base de datos.

     El apuntador a la base debe ser estatico para que pueda ser usado desde Activites independientes,
     solo esta vivo si la aplicacion esta corriendo, esto significa que para Content Provider, o Services
     que se ejecutan cuando la aplicacion ya no esta activa, no es valido y hay que hacer en cada uno
     la instanciacion de una clase con herencia de SQLiteOpenHelper que permita el trabajo con la misma.
      */
    private static SQLiteDatabase db;

    /*
      Tratamiento del XML
     */
    private XStream xs;
    private String xml;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
          Al layout activity_main.xml se le elimino el bloque del boton flotante
         */
        setContentView(R.layout.activity_main);

        personas = new ArrayList<>();

        // Para entrada/salida
        dirPrivado = getApplicationContext().getFilesDir().getAbsolutePath();
        dirPublico = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        // Preferencias
        preferenciasPublicas = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        contenedor = (FrameLayout) findViewById(R.id.contenedor);

        // Codigo generado
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  Se elimino la parte del boton flotante pues va a estar en el Listado */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Trata el fondo y le pone una imagen como marca de agua
        fondo = contenedor.getBackground();
        //contenedor.setBackgroundResource(R.drawable.muestra);

        /*
        Inicializacion de la base de datos
        Todo el trabajo con la misma se realiza mediante la clase SQLiteDatabase, si se necesita modificar
        su estructura esto es posible verificando y manipulando la version de la misma, existe un metodo
        para ejecutar sentencias nativas, db.rawQuery() tiene varias sobrecargas con diferentes opciones.
         */
        String databaseDir = "/data/data/" + getPackageName() + "/databases";
        String databaseName = "base_datos.db";

        // Chequear que exista y no si copiarla desde el Assets
        if (!DatabaseCustomUtils.checkDatabase(databaseDir + "/" + databaseName)) {
            /*
            La base de datos se encuentra dentro del directorio Assets vacia, en forma de esqueleto
             */
            DatabaseCustomUtils.copyDatabaseFromAssets(getApplicationContext(), databaseName, databaseDir);
        }
        /*
         Apertura de la base de datos
         Objeto estatico solo valido mientras la aplicacion esta activa.
          */
        db = SQLiteDatabase.openDatabase(databaseDir + "/" + databaseName, null, SQLiteDatabase.OPEN_READWRITE);
        if (db.getVersion() == 0) db.setVersion(1);
        Log.i("DB VERSION", String.valueOf(db.getVersion()));

        /*
         Inicializamos XStream, las ultimas versiones requieren subir el minimo API por encima del 22
          */
        xs = new XStream(new DomDriver());
        // Establecemos los aliases
        xs.alias("personas", List.class);
        xs.alias("persona", Persona.class);
        // Conversores personalizados para el tratamiento de los tipos
        xs.registerConverter(new ConversorDouble());
        xs.registerConverter(new ConversorFecha());
        // Omitir el atributo que corresponde a la clave primaria
        xs.omitField(Persona.class, "personaId");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*
              Control de los clicks para la salida de la aplicacion
             */
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.presione_de_nuevo, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    /**
     * Metodo para la creacion del menu superior
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Metodos para el tratamiento del menu superior (accion realizada)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        /*
          Manejo de la navegacion de las paginas del menu lateral usando fragmentos, esto
          es necesario ya que se quiere conservar el menu lateral lanzado en la actividad principal
         */
        Fragment fragmento = null;

        if (id == R.id.preferencias) {
            startActivity(new Intent(this, Preferencias.class));
            /*
              Este codigo representa otra forma de manejar fragmentos pero no funciona
              ya que se sobrepone al actual

              getFragmentManager().beginTransaction().replace(R.id.contenedor, new PreferenciasFrag()).commit();
             */
        } else if (id == R.id.mostrar_objetos) {
            if ((personas != null) && (personas.size() > 0)) {
                // Pasamos como parametro la lista de objetos al fragmento
                fragmento = MostrarObjetos.newInstance(personas);
            } else {
                alerta(this, "ADVERTENCIA", "Lista vacia");
            }

        } else if (id == R.id.listado) {
            fragmento = Listado.newInstance();
        } else if (id == R.id.guardar_archivo) {
            /*
            Exportamos a un archivo XML la coleccion que esta en uso
             */
            if ((personas != null) && (personas.size() > 0)) {
                xml = exportaXML();
                try {
                    // Guardamos el archivo usando una clase utilitaria
                    EntradaSalida.writeFile(new File(dirPublico + "/personas.xml"), xml.getBytes());
                    Toast.makeText(this, R.string.terminado, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    alerta(this, "ERROR", e.toString());
                }
            } else {
                alerta(this, "ADVERTENCIA", "Lista vacia");
            }

        } else if (id == R.id.leer_webservice) {
            new Hilo("SOAP").execute();
        } else if (id == R.id.leer_rest) {
            new Hilo("REST").execute();
        } else if (id == R.id.mapa) {
            fragmento = GoogleMapsFragmento.newInstance();
        }
        // Metodo para mostrar los fragmentos
        showFragment(fragmento);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Metodo para mostrar un fragmento
     *
     * @param fragmento
     */
    private void showFragment(Fragment fragmento) {
        /*
          Lanzamiento de los fragmentos usando el FrameLayout que tiene el id contenedor
         */
        if (fragmento != null) {
            final Fragment frag = fragmento;
            // contenedor.setBackground(fondo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contenedor, frag);
                    transaction.commit();
                }
            });
        }
    }

    /**
     * Metodo para cambiar el titulo en el Toolbar
     *
     * @param title
     */
    public void setActionBarTitle(String title) {
        try {
            getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
        }
    }

    /**
     * Getter/Setter de atributos
     */
    public List<Persona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Persona> personas) {
        this.personas.clear();
        this.personas.addAll(personas);
    }

    public static SQLiteDatabase getDb() {
        return db;
    }


    /**
     * ==============================================================================
     * CALLBACKS DE LA INTERFACE LISTADO
     * <p>
     * Metodos callbacks de Listado que son usados para mantener la lista de personas
     * local a esta actividad principal ya que en el menu lateral hay una opcion
     * de guardado manual, es necesario realizar tambien las operacion con la base
     * de datos
     * ===============================================================================
     */
    @Override
    public void onListaAdicion(Persona persona) {
        Log.i("PERSONA add", persona.toString());

        /*
        Guardamos el registro para obtener la clave primaria en el caso de adicion
        y poder actualizar el campo que la maneja
         */
        long pk = guardarRegistroBD(persona);
        persona.setPersonaId((int) pk);
        personas.add(persona);
        if (preferenciasPublicas.getBoolean("guardar_archivo", true)) {
            guardarArchivoObjeto();
        }
    }

    @Override
    public void onListaEdicion(Integer pos, Persona persona) {
        Log.i("PERSONA set", persona.toString());
        /*
        En este caso no es necesario obtener la clave primaria
         */
        guardarRegistroBD(persona);
        personas.set(pos, persona);
        if (preferenciasPublicas.getBoolean("guardar_archivo", true)) {
            guardarArchivoObjeto();
        }
    }

    @Override
    public void onListaBorrado(Persona persona) {
        borrarRegistroBD(persona);
        // Hay que eliminar con el objeto ya que por indice no funciona
        personas.remove(persona);
        if (preferenciasPublicas.getBoolean("guardar_archivo", true)) {
            guardarArchivoObjeto();
        }
    }
    /**
     * ===========================================================================================
     */

    /**
     * ===========================================================================================
     * UTILIDADES PARA ALMACENAR Y BORRAR REGISTROS
     * ===========================================================================================
     */

    /**
     * Metodo que almacena la lista de objetos en un archivo
     * Se esta usando una utilidad de la clase EntradaSalida
     */
    private void guardarArchivoObjeto() {
        Log.i("PERSONAS GUARDAR", String.valueOf(personas.size()));

        File file = new File(dirPrivado + "/" + preferenciasPublicas.getString("nombre_archivo", "fragment_listado.bin"));
        try {
            EntradaSalida.escribirArchivoObjeto(file, personas);
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
    }

    /**
     * Metodo para guardar registro en la base de datos
     * Chequea el campo donde se guarda la clave primaria
     * para hacer una insercion o modificacion
     */
    private long guardarRegistroBD(Persona persona) {
        long pk = persona.getPersonaId();
        /*
            Obtiene por reflexion de la clase los atributos que corresponden
            a cada columna de la base de datos.
         */
        Map<String, Object> registro = new HashMap<>();
        for (String campo : persona.getFieldsValues().keySet()) {
            Object valor = persona.getFieldsValues().get(campo);
            /*
              El metodo transformaAtributo2CampoBD transforma los patrones  de escritura
              nombreAtributo (camelCase) a nombre_atributo (estilo minuscula) para construir un Map
              cuya clave se corresponde al nombre normalizado de la base de datos
             */
            registro.put(DatabaseCustomUtils.transformaAtributo2CampoBD(campo), valor);
        }
        Log.i("REGISTRO", registro.toString());
        if (pk == -1) {
            pk = DatabaseCustomUtils.insert(db, "persona", registro);
        } else {
            DatabaseCustomUtils.update(db, "persona", "persona_id", String.valueOf(pk), registro);
        }
        return pk;
    }

    /**
     * Borra el registro de la base de datos
     *
     * @param persona
     */
    private void borrarRegistroBD(Persona persona) {
        DatabaseCustomUtils.delete(db, "persona", "persona_id", persona.getPersonaId().toString());
    }

    /**
     * ===========================================================================================
     */


    /**
     * Exporta la lista a un archivo XML
     * Uso de conversores personalizados
     *
     * @return
     */
    private String exportaXML() {
        // Construimos el XML
        return xs.toXML(personas);
    }


    /**
     * ============================================================================================
     * COMUNICACIONES
     * ============================================================================================
     */
    /**
     * Metodo para leer de un webservice tipo SOAP
     *
     * @return
     * @throws IOException
     */
    private String leerSOAP() throws IOException, XmlPullParserException {
        Object resultado = null;
        String hostname = preferenciasPublicas.getString("hostname", "192.168.1.11:8080");

        /*
        Estos parametros se toman del WSDL, no hay automatismo ya que le ejecucion debe ser lo mas
         liviana posible, por lo que la clase de consumo esta construida manualmente
         */
        final String NAMESPACE = "http://ws/";
        final String URL = "http://" + hostname + "/AndroidBaseDatos/PersonaSOAP?wsdl";
        final String METHOD_NAME = "getPersonasXML";

        /*
        Aunque se puedan manipular objetos, es recomendable usar XML o Json para el intercambio de datos
         */
        resultado = WebServiceCliente.consume(NAMESPACE, URL, METHOD_NAME, new HashMap(), false);

        return resultado.toString();
    }

    /**
     * Metodo para leer un webservice tipo Jersey (RESTfull) o Servlet
     *
     * @param resource Recurso o nombre de la aplicacion web a ejecutar
     * @param script   Endpoint, path o metodo a ejecutar
     * @return
     * @throws IOException
     */
    private Map<String, String> leerRS(String resource, String script) throws IOException {
        Map resultado = new HashMap();

        // El hostname y el puerto es usualmente configurado en las preferencias
        String hostname = preferenciasPublicas.getString("hostname", "192.168.1.11:8080");

        /*
        Los parametros del metodo estan documentados dentro de la clase
         */
        resultado = ConexionesRS.connectREST(
                "http://" + hostname + "/" + resource,
                "/" + script,
                new HashMap(),
                "application/x-www-form-urlencoded;charset=UTF-8",
                "application/xml",
                "",
                "GET",
                Integer.parseInt(preferenciasPublicas.getString("read_timeout", "10")) * 1000,
                Integer.parseInt(preferenciasPublicas.getString("connect_timeout", "10")) * 1000);

        return resultado;
    }
    /**
     * ============================================================================================
     */

    /**
     * Clase hilo para realizar la lectura de la parte de comunicacion
     * <p>
     * Los tres valores genericos indican <Valor1,Valor2,Valor3>
     * Valor1 - Tipo de entrada del doInBackground (ejecutor en segundo plano)
     * Valor2 - Tipo de entrada del onProgressUpdate (ejecutor del progreso)
     * Valor3 - Tipo de salida del doInBackground y de entrada para el onPostExecute (ejecutor final )
     */
    class Hilo extends AsyncTask<Void, Void, Boolean> {

        // Dialogo de mostrar progreso
        private ProgressDialog progreso;

        private StringBuilder errores;
        private Map<String, String> resultado;
        private String xml;

        private String tipo;

        public Hilo() {
            tipo = "SOAP";
        }

        public Hilo(String tipo) {
            this.tipo = tipo;
        }

        /**
         * Se ejecuta antes de iniciar el proceso en segundo plano
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            errores = new StringBuilder();
            resultado = new HashMap();

            progreso = new ProgressDialog(MainActivity.this);
            progreso.setCancelable(true);
            progreso.setMessage(getString(R.string.leyendo_espere));
            /*
            Listener que permite capturar si el dialogo se cancela para entonces
            cancelar el proceso
             */
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Hilo.this.cancel(true);
                }
            });
            progreso.show();
        }

        /**
         * Trabajo en segundo plano
         *
         * @param voids es el tipo definido en el primer generico
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... voids) {

            /*
              Metodo que se usa para ir publicando el progreso del trabajo
              que se haya programado.
             */
            publishProgress(/* tipos de valores que se pasan */);

            /**
             Metodo de chequeo si se ha cancelado la operacion
             */
            isCancelled();

            xml = "";
            try {
                /*
                 Si la conexion esta en progreso no se puede cancelar para eso
                 estan los timeout configurados
                  */
                if (tipo.equalsIgnoreCase("SOAP")) {
                    xml = leerSOAP();
                }
                if (tipo.equalsIgnoreCase("REST")) {
                    resultado = leerRS("AndroidBaseDatos/webresources", "personas_rs");
                    // Si el codigo de retorno no es del rango de los 200 hay algun inconveniente
                    if (Integer.parseInt(resultado.get("code")) < 300) {
                        xml = resultado.get("body");
                    } else {
                        // En el campo del map regresa el mensaje correspondiente
                        errores.append(resultado.get("message")).append("\n");
                        return false;
                    }
                }
            } catch (NullPointerException | XmlPullParserException | IOException e) {
                errores.append(e.toString()).append("\n");
                return false;
            }
            return true;
        }

        /**
         * Metodo que se dispara cuando se ejecuta el publishProgress();
         *
         * @param values Valores definidos en el segundo generico de la clase
         */
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            /*
            Si en el proceso se quiere actualizar algun componente UI y este diera algun error
            entonces debe usarse otro hilo mas interno para realizar la operacion, el mas recomendado es:
                     runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
             */
        }

        /**
         * Metodo que se ejecuta en la terminacion del proceso en segundo plano
         *
         * @param aBoolean Valor definido en el tercer generico
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                alerta(MainActivity.this, "ERROR", errores.toString());
            } else {
                Log.i("XML al fragmento", xml);
                Fragment fragmento = MostrarObjetos.newInstance(xml, tipo);
                showFragment(fragmento);
            }
            progreso.dismiss();
        }

        /**
         * Metodo que se ejecuta si se ha cancelado el proceso.
         * NOTA: No es posible cancelar las operaciones de conexion en curso, para eso estan los
         * timeout que se les programan.
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
            progreso.dismiss();
            Toast.makeText(MainActivity.this, R.string.operacion_cancelada, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ============================================================================================
     * OTRAS UTILIDADES
     * ============================================================================================
     */

    /**
     * Metodos estaticos para esconder el teclado y mostrar un cuadro de dialogo de tipo alerta
     */
    public static void hideKeyb(Activity act) {
        InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Dialogo basico de alerta que solo tiene un boton y que se le puede pasar el titulo y el contenido
     *
     * @param context Contexto en el que se va a mostrar
     * @param titulo  Titulo del dialogo
     * @param cadena  Contenido del dialogo
     */
    public static void alerta(Context context, String titulo, String cadena) {
        new AlertDialog.Builder(context)
                .setMessage(cadena)
                .setCancelable(true).setTitle(titulo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create().show();
    }


}
