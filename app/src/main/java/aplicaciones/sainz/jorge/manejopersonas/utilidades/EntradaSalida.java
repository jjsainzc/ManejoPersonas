package aplicaciones.sainz.jorge.manejopersonas.utilidades;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JJSC
 * <p>
 * Clase utilitaria para el tratamiento de entrada/salida
 * <p>
 * Archivos y directorios
 */
public class EntradaSalida {

    public static void borrarArchivos(List<File> files) {
        for (File file : files) {
            if (file.exists()) file.delete();
        }
    }

    public static boolean borrarDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        return borrarDir(file) && file.delete();
    }

    private static boolean borrarDir(File dir) {
        File[] children = dir.listFiles();
        boolean childrenDeleted = true;
        for (int i = 0; children != null && i < children.length; i++) {
            File child = children[i];
            if (child.isDirectory()) {
                childrenDeleted = borrarDir(child) && childrenDeleted;
            }
            if (child.exists()) {
                childrenDeleted = child.delete() && childrenDeleted;
            }
        }
        return childrenDeleted;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static List<String> findSubStrFile(String path, String... cad) throws IOException {
        ArrayList<String> sb = new ArrayList<>();

        Matcher matcher;
        String mat;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            while ((line = br.readLine()) != null) {
                mat = line.replaceAll("\n", ""); //.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                for (String c : cad) {
                    matcher = Pattern.compile(c).matcher(mat);
                    while (matcher.find()) {
                        sb.add(matcher.group(1));
                    }
                }
            }
            br.close();

        }
        return sb;
    }

    private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static void escribirArchivoObjeto(File file, List lista) throws IOException {

        ObjectOutputStream oos;

        oos = new ObjectOutputStream(new FileOutputStream(file));

        for (Object obj : lista) {
            oos.writeObject(obj);
            oos.flush();
        }
        oos.close();
    }

    public static <E> List<E> leerArchivoObjeto(File file) throws IOException, ClassNotFoundException {
        List<E> lista = new ArrayList<>();
        E obj;

        InputStream fis = new FileInputStream(file);
        ObjectInputStream ois;

        ois = new ObjectInputStream(fis);

        while (fis.available() > 0) {
            obj = (E) ois.readObject();
            lista.add(obj);
        }
        ois.close();

        return lista;
    }

    public static void copyFile(File in, File out) throws IOException {
        FileInputStream fileIn = new FileInputStream(in);
        OutputStream fileOut = new FileOutputStream(out);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) > 0) {
            fileOut.write(buffer, 0, bytesRead);
        }
        fileOut.close();
        fileIn.close();
    }

    public static String readFile(String f) throws FileNotFoundException, IOException {
        File file = new File(f);
        byte[] fileData = new byte[(int) file.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.readFully(fileData);
        dis.close();
        return new String(fileData, "UTF-8");
    }

    public static byte[] readFile(File file) throws FileNotFoundException, IOException {
        byte[] resultado = {};

        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        while ((fileIn.read(buffer)) > 0) {
            resultado = concatenateByteArrays(resultado, buffer);
        }
        fileIn.close();
        return resultado;
    }

    public static void writeFile(File file, byte[] bytes) throws FileNotFoundException, IOException {
        OutputStream fileOut = new FileOutputStream(file);
        fileOut.write(bytes, 0, bytes.length);
        fileOut.flush();
        fileOut.close();
    }

    @SuppressWarnings("unchecked")
    private boolean saveFileFromStringList(String sFileName, List<String> body) {
        FileWriter writer;
        try {
            writer = new FileWriter(sFileName);
        } catch (IOException e1) {
            return false;
        }

        for (String aBody : body) {
            try {
                writer.append(aBody);
                writer.append('\n');
            } catch (IOException e) {
                return false;
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
