/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicaciones.sainz.jorge.manejopersonas.personas.conversores;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;


/**
 * Conversor personalizado para Gson
 *
 * @author JJSC 2018
 */
public class ConversorPersona implements JsonSerializer<Persona>, JsonDeserializer<Persona> {

    private SimpleDateFormat patronFechaSQL;

    public ConversorPersona() {
        this.patronFechaSQL = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public JsonElement serialize(Persona persona, Type type, JsonSerializationContext jsc) {
        JsonObject json = new JsonObject();
        Object valor;
         /*
          Se usa la reflexion del objeto para producir un json
           */
        Map<String, Object> campos = persona.getFieldsValues();
        for (String clave : campos.keySet()) {
            valor = campos.get(clave);
            if (valor instanceof Date) {
                valor = patronFechaSQL.format((Date) valor);
            }
            try {
                json.addProperty(clave, valor.toString());
            } catch (NullPointerException e) {
            }
        }
        return json;
    }

    @Override
    public Persona deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        Persona persona = new Persona();
        JsonObject json = (JsonObject) je;

        persona.setNombre(json.get("nombre").toString());
        persona.setCedula(json.get("cedula").toString());
        persona.setEstadoCivil(json.get("estado_civil").toString());
        persona.setGenero(json.get("genero").toString());
        try {
            persona.setFechaNacimiento(patronFechaSQL.parse(json.get("fecha_nacimiento").toString()));
        } catch (ParseException e) {
        }
        persona.setEstatura(Double.parseDouble(json.get("estatura").toString()));
        return persona;

    }

}
