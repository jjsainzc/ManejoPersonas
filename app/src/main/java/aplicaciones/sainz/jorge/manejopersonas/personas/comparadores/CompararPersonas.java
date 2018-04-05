package aplicaciones.sainz.jorge.manejopersonas.personas.comparadores;

import java.util.Comparator;

import aplicaciones.sainz.jorge.manejopersonas.personas.Listado;
import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;

/**
 * Clase utilitaria que permite establecer la forma en que se comparan dos personas para ser usado
 * en los ordenamientos
 */

public class CompararPersonas implements Comparator<Persona> {
    private String criterio;

    public CompararPersonas() {
        criterio = "cedula";
    }

    public CompararPersonas(String criterio) {
        this.criterio = criterio;
    }

    @Override
    public int compare(Persona o1, Persona o2) {
        int res = -1;
        if (criterio.compareToIgnoreCase("nom") == 0) {
            res = o1.getNombre().compareTo(o2.getNombre());
        }
        if (criterio.compareToIgnoreCase("ced") == 0) {
            res = o1.getCedula().compareTo(o2.getCedula());
        }
        if (criterio.compareToIgnoreCase("fecN") == 0) {
            res = o1.getFechaNacimiento().compareTo(o2.getFechaNacimiento());
        }
        if (criterio.compareToIgnoreCase("est") == 0) {
            res = o1.getEstatura().compareTo(o2.getEstatura());
        }
        if (criterio.compareToIgnoreCase("esc") == 0) {
            res = o1.getEstadoCivil().compareTo(o2.getEstadoCivil());
        }
        if (criterio.compareToIgnoreCase("gen") == 0) {
            res = o1.getGenero().compareTo(o2.getGenero());
        }

        // getDireccionOrdenamiento es una variable estatica entera definida en Listado que establece la direccion
        return res * Listado.getDireccionOrdenamiento();
    }
}
