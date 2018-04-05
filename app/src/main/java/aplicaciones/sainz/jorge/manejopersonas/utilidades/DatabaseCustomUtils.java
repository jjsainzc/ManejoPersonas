package aplicaciones.sainz.jorge.manejopersonas.utilidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aplicaciones.sainz.jorge.manejopersonas.MainActivity;

/**
 * Created by JJSC on 11/12/2017.
 * <p>
 * Utilitario personalizado para hacer operaciones basicas con SQLite
 */

public class DatabaseCustomUtils {

    /**
     * Transforma el patron de atributo al patron de base de datos
     */
    public static String transformaAtributo2CampoBD(String src) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < src.length(); i++) {
            Character c = src.charAt(i);
            resultado.append(Character.isUpperCase(c) ? ((i > 0) ? "_" : "") + String.valueOf(c).toLowerCase() : String.valueOf(c));
        }

        return resultado.toString();
    }

    /**
     * Copia una base de datos que esta en Assets
     *
     * @param context  Contexto de la aplicacion
     * @param dbName   Nombre de la base con extension .db
     * @param destPath Camino destino de la copia debe ser (/data/data/nombre.del.paquete/databases
     */
    public static void copyDatabaseFromAssets(Context context, String dbName, String destPath) {
        try {
            File dirDatabase = new File(destPath);
            if (!dirDatabase.exists()) dirDatabase.mkdir();

            InputStream assetsDB = context.getAssets().open(dbName);
            OutputStream privateDB = new FileOutputStream(new File(destPath + "/" + dbName));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = assetsDB.read(buffer)) > 0) {
                privateDB.write(buffer, 0, bytesRead);
            }

            privateDB.close();
            assetsDB.close();
        } catch (IOException e) {
            throw new Error("Error copiando base de datos!");
        }
    }

    /**
     * Exporta y restaura la base de datos, el directorio publico escogido es el DOWNLOADS
     *
     * @param context      Contexto de la aplicacion para determinar el camino del directorio privado
     * @param databaseName Nombred¡ del archivo que contiene la base de datso
     * @param backup       True hace backup o false hace restaura
     * @throws IOException
     */
    public static void copyDataBase(Context context, String databaseName, Boolean backup) throws IOException {
        final String DB_NAME = databaseName;
        final String externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        final String internalDir = String.format("//data//data//%s//databases//", context.getPackageName());
        new File(internalDir).mkdirs();
        InputStream inputDBStream = null;
        OutputStream outputDBStream = null;

        if (backup) {
            inputDBStream = new FileInputStream(new File(internalDir + "/" + DB_NAME));
            outputDBStream = new FileOutputStream(new File(externalDir + "/" + DB_NAME));
        } else {
            inputDBStream = new FileInputStream(new File(externalDir + "/" + DB_NAME));
            outputDBStream = new FileOutputStream(internalDir + "/" + DB_NAME);
        }

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputDBStream.read(buffer)) > 0) {
            outputDBStream.write(buffer, 0, bytesRead);
        }
        outputDBStream.close();
        inputDBStream.close();

    }

    /**
     * Chequea si existe una base de datos
     *
     * @param dbPath camino completo donde se encuentra el archivo con extension .db
     * @return Verdadero o falso
     */
    public static boolean checkDatabase(String dbPath) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
            db.close();
        } catch (SQLiteCantOpenDatabaseException ignored) {
            return false;
        }
        return true;
    }

    /**
     * Convierte un ContentValue en un Map
     *
     * @param contentValues
     * @return Map<String                               ,                                                               Object>
     */
    public static Map<String, Object> valuesToMap(ContentValues contentValues) {
        Map<String, Object> map = new LinkedHashMap();
        if (contentValues != null) {
            for (String clave : contentValues.keySet()) {
                map.put(clave, contentValues.get(clave));
            }
        }
        return map;
    }

    /**
     * Convierte un Map en un ContentValue
     *
     * @param datos
     * @return ContentValue
     */
    public static ContentValues mapToValues(Map<String, ?> datos) {
        ContentValues valores = new ContentValues();
        String v;
        String k;

        for (Object d : datos.keySet()) {
            k = (String) d;
            try {
                if (datos.get(k) instanceof Date) {
                    v = new java.sql.Date(((Date) datos.get(k)).getTime()).toString();
                } else if (datos.get(k) instanceof Boolean) {
                    v = ((Boolean) datos.get(k)) ? "1" : "0";
                } else {
                    v = datos.get(k).toString();
                }
                valores.put(k, v);
            } catch (NullPointerException e) {
            }
        }
        return valores;
    }

    /**
     * Convierte un Cursor en un List con Map
     *
     * @param cursor
     * @return List<Map                               <                               String                               ,                                                               Object>>
     */
    public static List<Map<String, Object>> cursorToListMap(Cursor cursor) {
        List<Map<String, Object>> resultado = new ArrayList();
        Map<String, Object> registro;
        ContentValues fila = new ContentValues();

        if (cursor != null) {
            try {
                cursor.moveToFirst();
                do {
                    registro = new LinkedHashMap();
                    fila.clear();

                    DatabaseUtils.cursorRowToContentValues(cursor, fila);

                    registro.putAll(valuesToMap(fila));
                    /*
                    for (String colName : c.getColumnNames()) {
                        registro.put(colName, c.getString(c.getColumnIndex(colName)));
                    }
                    */
                    resultado.add(registro);

                } while (cursor.moveToNext());
            } catch (CursorIndexOutOfBoundsException e) {
                resultado = null;
            }
        }
        return resultado;
    }

    /**
     * Cuenta registros en una tabla de un db
     *
     * @param db    Database
     * @param tabla Nombre de la tabla
     * @param where Condicion opcional
     * @return long (cantidad de registro)
     */
    public static long countRows(SQLiteDatabase db, String tabla, String where) {
        String condicion = ((where != null) && !where.isEmpty()) ? " WHERE " + where : "";
        Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + tabla + condicion, null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    /**
     * Inserta un registro en una tabla de una base de datos
     *
     * @param db    Base de datos
     * @param tabla Nombre de la tabla
     * @param datos Map que contiene en clave - valor las columnas y los valores correspondientes
     * @return Clave primaria del registro insertado
     */
    public static long insert(SQLiteDatabase db, String tabla, Map<String, ?> datos) {
        long pk = -1;
        if ((db != null) && (datos != null) && (datos.size() > 0)) {
            pk = db.insert(tabla, null, mapToValues(datos));
        }
        return pk;
    }

    /**
     * Actualiza un registro segun clave primaria
     *
     * @param db      Base de datos
     * @param tabla   Nombre de la tabla
     * @param pkName  Nombre de la clave primaria
     * @param pkValue Valor de la clave primaria
     * @param datos   Map que contiene las columnas a actualizar y sus valores correspondientes
     * @return Cantidad de registros afectados
     */
    public static int update(SQLiteDatabase db, String tabla, String pkName, String pkValue, Map<String, ?> datos) {
        if ((db != null) && (datos != null) && (datos.size() > 0)
                && (pkName != null) && !pkName.isEmpty() && (pkValue != null)
                && !pkValue.isEmpty()) {

            return db.update(tabla, mapToValues(datos), pkName + "=\""
                    + pkValue + "\"", null);

        }
        return 0;
    }

    /**
     * Borra un registro de una tabla
     * ADVERTENCIA: Si se deja en null el nombre y el valor de la clave primaria se VACIA TODA LA TABLA
     *
     * @param db      Base de tados
     * @param tabla   Nombre de la tabla
     * @param pkName  Nombre de la clave primaria
     * @param pkValue Valor de la clave primaria
     * @return Cantidad de registros afectados
     */
    public static int delete(SQLiteDatabase db, String tabla, String pkName, String pkValue) {
        if (db != null) {
            if ((pkName != null) && !pkName.isEmpty() && (pkValue != null)
                    && !pkValue.isEmpty()) {

                return db.delete(tabla, pkName + "=\"" + pkValue + "\"", null);
            } else {
                return db.delete(tabla, null, null);
            }
        }
        return 0;
    }

    /**
     * Realiza un select de una tabla
     *
     * @param db       Base de datos
     * @param tabla    Nombre de la tabla
     * @param columnas Set de String con las columnas
     * @param pkName   Nombre de la clave primaria (opcional)
     * @param pkValue  Valor de la clave primaria (opcional)
     * @param where    Condicion (opcional)
     * @param groupBy  Agrupacion (opcional)
     * @param having   having (opcional)
     * @param orderBy  Oolumnas para establece ordenamientos (opcional)
     * @return Una lista que contiene un Map con los registros
     */
    public static List<Map<String, Object>> select(SQLiteDatabase db, String tabla, Set<String> columnas, String pkName, String pkValue, String where, String groupBy, String having, String orderBy) {
        List<Map<String, Object>> resultado = new ArrayList();
        String[] cols;
        String where_ = "";

        if ((columnas != null) && !columnas.isEmpty()) {
            cols = columnas.toArray(new String[columnas.size()]);
        } else {
            cols = null;
        }

        if ((pkName != null) && !pkName.isEmpty() && (pkValue != null) && !pkValue.isEmpty()) {
            where_ = ((where != null) ? where : "") + pkName + "=\"" + pkValue + "\"";
        }

        if (db != null) {
            Cursor c = db.query(tabla, cols, where_, null, groupBy, having, orderBy);
            resultado = cursorToListMap(c);
            c.close();
        }
        return resultado;
    }
}
