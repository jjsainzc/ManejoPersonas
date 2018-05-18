package aplicaciones.sainz.jorge.manejopersonas.personas.proveedor;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import aplicaciones.sainz.jorge.manejopersonas.utilidades.DatabaseCustomUtils;


/**
 * Descriptor de la base de datos, no se puede usar el objeto estatico de la clase principal
 * El onCreate esta vacio porque ya la tabla existe en el directorio, esta clase debe ser
 * instanciada en el constructor del provider.
 * <p>
 * Se debe haber corrido aunque sea una vez la aplicacion para que la base se copie del Assets,
 * si no se esta seguro de que la aplicacion se haya ejecutado al menos una vez, entonces  hay que
 * poner en el provider que se haga el chequeo de existencia de la base y en caso contrario que se
 * copie la misma como se hace desde la aplicacion principal.
 */
class BasePersonas extends SQLiteOpenHelper {

    public BasePersonas(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}

/**
 * @author JJSC, 2018
 * <p>
 * <p>
 * Proveedor de contenido server.
 * <p>
 * Usaremos base de datos para realizar intercambios de informacion.
 * Normalmente usa una clase de apoyo que hereda SQLiteOpenHelper, en este caso
 * utilizaremos la base que ya esta en uso,
 * <p>
 * Debemos usar los propios metodos del proveedor de contenido para realizar las
 * operaciones ya que estos son los que se exportan.
 * <p>
 * <p>
 * El proveedor de contenido usa URI's para su acceso, ejemplo:
 * content://definimos.autoridad/path/{id}
 * <p>
 * Cada content provider debe estar definido para una tabla en especifica de la base de datos,
 * aunque se pueden realizar otras operaciones mas complejas con tablas relacionadas.
 * <p>
 * El proveedor debe estar definido en el manifest.xml de la siguiente forma, para este ejemplo:
 * <p>
 * <provider
 * android:authorities="org.app.mp.provider"
 * android:name=".personas.proveedor.PersonaCP"
 * android:exported="true">
 * </provider>
 */
public class PersonaCP extends ContentProvider {

    /**
     * Definicion de un content uri, son las variables que
     * van a describir a nuestro proveedor
     */
    private static final String URI;
    public static final Uri CONTENT_URI;
    private static final UriMatcher URIMATCHER;
    private static final String AUTHORITY;

    /**
     * Constantes que describen si el acceso devuelve uno o mas registros, los valores
     * pueden ser cualquier numero, siempre que no se repitan.
     */
    private static final int PERSONAS = 0x12;     // Constante para mas de un registro
    private static final int PERSONA_ID = 0x34;   // Constante para un registro


    /**
     * Inicializacion del bloque de estaticos
     */
    static {
        // Nuestra autoridad
        AUTHORITY = "org.app.mp.provider";

        // Nuestro URI que va a ser actualizado en caso de inserciones de nuevos registros
        URI = "content://" + AUTHORITY + "/personas";
        // Compilacion del URI
        CONTENT_URI = Uri.parse(URI);

        // Creacion del verificador
        URIMATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        // Uso de las constantes
        URIMATCHER.addURI(AUTHORITY, "personas", PERSONAS);
        URIMATCHER.addURI(AUTHORITY, "personas/#", PERSONA_ID);
    }

    /*
     Descriptor de la base de datos que debe ser usado en todas
     las operaciones dentro del provider
     */
    private BasePersonas base;


    @Override
    public boolean onCreate() {
        // Hay que pasarle los parametros que corresponden a la base de datos que debe estar usada.
        base = new BasePersonas(getContext(), "base_datos.db", 1);
        return false;
    }

    /**
     * De uso interno para el proveedor, se llama en cada invocacion para devolver
     * el tipo de acceso que se ha solicitado, a traves del verificador.
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = URIMATCHER.match(uri);
        switch (match) {
            case PERSONAS:
                return "vnd.android.cursor.dir/vnd.content.persona";
            case PERSONA_ID:
                return "vnd.android.cursor.item/vnd.content.persona";
        }

        return null;
    }


    /**
     * ===============================================================================================
     * Metodos que tienen una obligada sobreescritura ya que vienen de la clase ContentProvider y son
     * abstractos, si no se desea que algun cliente use algunos de ellos entonces se dejan con la
     * devolucion en null o en 0 (depende del tipo de retorno de cada uno).
     * ================================================================================================
     */


    /**
     * Metodo que devuelve el resultado en forma de cursor, segun el URI
     * con el cual se haya invocado.
     *
     * @param uri
     * @param columns       Arreglo que contiene los nombres de las columnas a devolver
     * @param selection     Incluye formato var1=? AND/OR var2=?
     * @param selectionArgs Arreglo de los valores que corresponden a cada uno de los ? definidos
     *                      anteriormente, para el ejemplo deberian haber dos elementos de arreglos
     *                      para los ? de var1 y var2
     * @param orderBy       Clausula ORDER BY estandard del DB2 SQL
     * @return Un cursor con los resultados.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] columns,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String orderBy) {

        String where = selection;
        String[] whereSel = selectionArgs;

        /*
          Si URI es:
            content://org.app.provider/personas/#

            Donde # es un numero entonces esto significa que es la clave primaria de algun registro
            por lo que entonces construimos el WHERE como corresponde.
         */
        if (URIMATCHER.match(uri) == PERSONA_ID) {
            where = "persona_id=?";
            whereSel = new String[1];
            whereSel[0] = uri.getLastPathSegment();
        }
        Cursor c = null;

        try {
            c = base.getReadableDatabase().query("persona",
                    columns,
                    where,
                    whereSel,
                    null,
                    null,
                    orderBy);
        } catch (NullPointerException | SQLException e) {
            Log.e("PROV SELECT", e.toString());
        }
        return c;
    }

    /**
     * Permite la creacion de nuevos registros
     *
     * @param uri           El URI ejemplo, content://org.app.mp.provider/personas
     * @param contentValues ContentValues (tipo Map) que contiene las columnas y sus valores,
     *                      NOTA: Debe tenerse especial cuidado que no enviar claves primarias ya que
     *                      las mismas estan manejadas con el motor de la base, tambien pueden
     *                      hacerse verificaciones de campos unicos antes de hacer la insercion,
     *                      como hemos puesto en el ejemplo.
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri newUri = null;

        try {
            /*
            Aqui validamos la cedula que esta llegando en el contentValues
             */
            String cedula = contentValues.getAsString("cedula");

            if ((cedula != null) && !cedula.isEmpty()) {
                /*
                 Se pueden ejecutar metodos estaticos, siempre y cuando se le pase el objeto
                 instanciado de la base de datos del provider, no se puede usar el objeto estatico de la base
                 de la clase principal.
                 */
                long c = DatabaseCustomUtils.countRows(base.getReadableDatabase(), "persona", " cedula ='" + cedula + "'");
                if (c == 0) {
                    /*
                     Solo se devolvera un nuevo URI si todas las operaciones son exitosas
                     */
                    long regId = 1;

                    regId = base.getWritableDatabase().insert("persona", null, contentValues);
                    Log.i("PROV INS", String.valueOf(regId));
                    newUri = ContentUris.withAppendedId(CONTENT_URI, regId);
                } else {
                    Log.e("PROV INSERT", "REGISTRO CON CEDULA " + cedula + " YA EXISTE");
                }
            } else {
                Log.e("PROV INSERT", "REGISTRO CON CEDULA NULA O EN BLANCO ");
            }
        } catch (NullPointerException | SQLException e) {
            Log.e("PROV INSERT", e.toString());
        }
        return newUri;
    }

    /*
     ===============================================================================
     TODO: Hacer las implementaciones de las operaciones restantes si son necesarias
     ===============================================================================
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
