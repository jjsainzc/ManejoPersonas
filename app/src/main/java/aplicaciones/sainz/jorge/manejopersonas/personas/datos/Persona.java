package aplicaciones.sainz.jorge.manejopersonas.personas.datos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.Date;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import aplicaciones.sainz.jorge.manejopersonas.personas.Listado;
import aplicaciones.sainz.jorge.manejopersonas.personas.conversores.ConversorPersona;

/**
 * Creacion de una anotacion personalizada que nos va a permitir la exclusion de atributos
 * para el tratamiento por reflexion de los mismos.
 * Hay muchas formas de construir anotaciones personalizadas.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface MiAnotacionPersona {
    String name();

    String value() default "";
}


/**
 * POJO tiene las implementaciones basica para que una clase sea manejable en muchas operaciones.
 * <p>
 * Incluye sobreescritura de metodos heredados de Object
 * Incluye implementacion de tres interfaces basicas.
 * Serializable - Permite el manejo del objto en Entrada/Salida.
 * Comparable<T> - Permite dotar al objeto del metodo compareTo(<T>)
 * Parcelable - Permite convertir al objeto en un elemento eficiente para el paso entre Activities
 *              (solo para Android)
 */

public class Persona implements Serializable, Comparable<Persona>, Parcelable {
    // Excluimos el atributo de la reflexion
    @MiAnotacionPersona(name = "excluir", value = "si")
    private static final long serialVersionUID = -1L;

    // Excluimos el atributo de la reflexion
    @MiAnotacionPersona(name = "excluir", value = "si")
    private Integer personaId;

    // ATRIBUTOS PRIVADOS !! NO ROMPER LA ENCAPSULACION
    private String nombre;
    private String cedula;
    private Date fechaNacimiento;
    private String estadoCivil;
    private String genero;
    private Double estatura;


    /**
     * Constructor vacio (sin parametros) es obligatorio
     */
    public Persona() {
        personaId = -1;
    }

    /**
     * Constructor sobrecargado, es opcional.
     *
     * @param nombre
     * @param cedula
     * @param fechaNacimiento
     * @param estadoCivil
     * @param genero
     * @param estatura
     */
    public Persona(String nombre, String cedula, Date fechaNacimiento, String estadoCivil, String genero, Double estatura) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
        this.estadoCivil = estadoCivil;
        this.genero = genero;
        this.estatura = estatura;
        personaId = -1;
    }

    /*
    ===============================================================
    METODOS PUBLICOS GETTER Y SETTER (publicos)

    Son obligatorios para manejar los valores de los atributos
    ===============================================================
    */
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Double getEstatura() {
        return estatura;
    }

    public void setEstatura(Double estatura) {
        this.estatura = estatura;
    }

    public Integer getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Integer personaId) {
        this.personaId = personaId;
    }

    /**
     * Metodo privado para serializar el objeto usando un conversor personalizado
     *
     * @return
     */
    private String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Persona.class, new ConversorPersona())
                .create();

        return gson.toJson(this);
    }

    /**
     * Metodo sobreescrito de la clase Object, en este caso lo usamos para devolver un
     * formato Json que representa al objeto, esto puede ser usado bajo el criterio que se
     * desee.
     *
     * @return
     */
    @Override
    public String toString() {
        return toJson();

        /*
        return "Persona{" +
                "nombre='" + nombre + '\'' +
                ", cedula='" + cedula + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", estadoCivil='" + estadoCivil + '\'' +
                ", genero='" + genero + '\'' +
                ", estatura=" + estatura +
                '}';
        */
    }

    /**
     * Metodo sobreescrito de la clase Object que permite establecer un criterio unico para
     * la comparacion del POJO
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona persona = (Persona) o;
        return getCedula().equals(persona.getCedula());
    }

    /**
     * Metodo sobreescrito de la clase Object
     * Permite devolver un id unico para ser usados en Hashing
     *
     * @return
     */
    @Override
    public int hashCode() {
        return getCedula().hashCode();
    }

    /**
     * Metodo sobreescrito de la interface Comparable
     * Permite establece un valor inicial para realizar comparaciones del POJO
     * <p>
     * Este puede ser complementando en clase externa con interface Comparator
     *
     * @param o Entra un objeto persona.
     * @return Tres valores de int (<0, ==0, > 0), que significan si es menor, igual o mayor, la
     * implementacion de esta tecnica viene del C de restar los objetos a comparar,
     * Ejemplo:
     * Si a = 40 y b = 50,
     * Si a-b < 0
     * Entonces a < b (a es menor que b)
     */
    @Override
    public int compareTo(@NonNull Persona o) {

        return cedula.compareTo(o.getCedula()) * Listado.getDireccionOrdenamiento();
    }


    /**
     * PARCELABLE
     * ========================================================================================
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.cedula);
        dest.writeLong(this.fechaNacimiento != null ? this.fechaNacimiento.getTime() : -1);
        dest.writeString(this.estadoCivil);
        dest.writeString(this.genero);
        dest.writeValue(this.estatura);
        dest.writeInt(this.personaId);
    }

    protected Persona(Parcel in) {
        this.nombre = in.readString();
        this.cedula = in.readString();
        long tmpFechaNacimiento = in.readLong();
        this.fechaNacimiento = tmpFechaNacimiento == -1 ? null : new Date(tmpFechaNacimiento);
        this.estadoCivil = in.readString();
        this.genero = in.readString();
        this.estatura = (Double) in.readValue(Double.class.getClassLoader());
        this.personaId = in.readInt();
    }

    /**
     * Excluimos la constante interna del Parcelable de la reflexion
     */
    @MiAnotacionPersona(name = "excluir", value = "si")
    public static final Parcelable.Creator<Persona> CREATOR = new Parcelable.Creator<Persona>() {
        @Override
        public Persona createFromParcel(Parcel source) {
            return new Persona(source);
        }

        @Override
        public Persona[] newArray(int size) {
            return new Persona[size];
        }
    };
    // ===========================================================================================

    /**
     * USO DE REFLEXION
     *
     * Metodo que devuelve en un Map todos los atributos y sus valores
     * que NO esten marcados con la anotacion personalizada MiAnotacion con
     * name="excluir" y value="si"
     *
     * Solo es posible devolver la estructura del objeto mediante un metodo publico, ya
     * que es el unico que puede ver los atributos privados del mismo, tampoco es posible usar esto
     * con elementos estaticos, ya que los mismos solo se ven por referencia a la clase.
     */
    public Map<String, Object> getFieldsValues() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        try {
            Class<?> thisClass = null;
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            for (Field f : aClassFields) {
                java.lang.annotation.Annotation[] annotations = f.getDeclaredAnnotations();
                boolean excluir = false;
                for (java.lang.annotation.Annotation annotation : annotations) {
                    if (annotation instanceof MiAnotacionPersona) {
                        MiAnotacionPersona myAnnotation = (MiAnotacionPersona) annotation;
                        if (myAnnotation.name().equals("excluir") && myAnnotation.value().equals("si")) {
                            excluir = true;
                            break;
                        }
                    }
                }
                try {
                    if (!excluir) {
                        resultado.put(f.getName(), f.get(this));
                    }
                } catch (java.lang.IllegalAccessException e) {
                }
            }
        } catch (ClassNotFoundException ex) {
        }
        return resultado;
    }
}
