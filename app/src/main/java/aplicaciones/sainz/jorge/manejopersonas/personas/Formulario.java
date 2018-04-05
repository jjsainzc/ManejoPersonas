package aplicaciones.sainz.jorge.manejopersonas.personas;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import aplicaciones.sainz.jorge.manejopersonas.MainActivity;
import aplicaciones.sainz.jorge.manejopersonas.R;
import aplicaciones.sainz.jorge.manejopersonas.personas.datos.Persona;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.DatabaseCustomUtils;
import aplicaciones.sainz.jorge.manejopersonas.utilidades.DateNumberFormatDetector;

/**
 * Es la actividad asociada al formulario para realizar el mantenimiento de los datos
 * <p>
 * Implementa tres interfaces para controlar los click, los cambios de focos y el evento para
 * la seleccion de la fecha del componente DatePicker
 */
public class Formulario extends AppCompatActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        View.OnFocusChangeListener {

    /**
     * Recuperamos los atributos del layout que vamos a controlar
     */
    private RadioGroup genero;
    private RadioButton generoSel;
    private Spinner estadoCivil;
    private String estadoCivilSel;
    private EditText nombre;
    private EditText cedula;
    private EditText estatura;
    private EditText fechaNacimiento;
    private Date fecha;

    /**
     * Atributos que seran usados en la devolucion del resultado
     */
    private int pos;
    private Persona persona;
    private Intent resultado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        setTitle("Formulario");

        /*
          Establecemos funcionalidad de retroceso mediante el boton izquierdo en el toolbar
         */
        getSupportActionBar().setIcon(R.drawable.usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*
          Recuperamos los datos del fragment Listado
         */
        persona = getIntent().getParcelableExtra("persona");
        pos = getIntent().getIntExtra("pos", -1);

        /*
          Inicializamos los datos para la devolucion del resultado
         */
        resultado = new Intent();
        resultado.putExtra("pos", pos);
        setResult(400, resultado);

        /*
          Vinculamos los atributos del layout
         */
        nombre = findViewById(R.id.nombre);
        cedula = findViewById(R.id.cedula);
        estatura = findViewById(R.id.estatura);

        /*
          La fecha tiene su control en el evento controlado en la clase
         */
        fechaNacimiento = findViewById(R.id.fechaNacimiento);
        fechaNacimiento.setHint(DateNumberFormatDetector.getDatePattern());
        fechaNacimiento.setTextIsSelectable(true);
        fechaNacimiento.setOnFocusChangeListener(this);

        /*
          Spinner con su adaptador y evento
         */
        estadoCivil = findViewById(R.id.estadoCivil);
        ArrayAdapter<CharSequence> estadoCivilAdapter = ArrayAdapter.createFromResource(this,
                R.array.estado_civil, android.R.layout.simple_spinner_dropdown_item);
        estadoCivil.setAdapter(estadoCivilAdapter);
        estadoCivil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.hideKeyb(Formulario.this);
                estadoCivilSel = parent.getAdapter().getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        genero = findViewById(R.id.genero);
        genero.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                generoSel = findViewById(checkedId);
            }
        });
        generoSel = findViewById(R.id.masculino);

        /*
          Llenado del formulario
         */
        if ((pos >= 0) && (persona != null)) {
            nombre.setText(persona.getNombre());
            cedula.setText(persona.getCedula());
            estatura.setText(persona.getEstatura().toString());

            // Creamos la fecha usando la clase que detecta el formato de fecha configurado en el equipo
            fechaNacimiento.setText(DateNumberFormatDetector.getSimpleDateFormat().format(persona.getFechaNacimiento()));
            /*
              La recuperacion del dato para hacer la seleccion en el Spinner necesita el indice del arreglo,
              por eso hacemos una busqueda binaria dentro del mismo que hemos obtenido de los recursos y como parametro
              le damos el valor del atributo del objeto.
             */
            estadoCivil.setSelection(
                    // Devuelve el indice del arreglo
                    Arrays.binarySearch(
                            getResources().getStringArray(R.array.estado_civil), // Arreglo de los recursos
                            persona.getEstadoCivil()                             // Parametro de busqueda
                    )
            );
            if (persona.getGenero().equalsIgnoreCase("femenino")) {
                generoSel = findViewById(R.id.femenino);
            }

        } else {
            estadoCivil.setSelection(0);

        }
        generoSel.setChecked(true);

        /*
          Los dos botones del layout han sido sustituidos por los menus en la parte superior
          entonces los hacemos invisibles
         */
        findViewById(R.id.guardar).setVisibility(View.INVISIBLE);
        findViewById(R.id.cancelar).setVisibility(View.INVISIBLE);

        // Que hacer cuando se da click a los dos RadioButtons
        findViewById(R.id.masculino).setOnClickListener(this);
        findViewById(R.id.femenino).setOnClickListener(this);
    }

    /**
     * Metodo para validar los datos del formulario.
     * <p>
     * Si existe error se usa el metodo de alerta que hemos definido en la actividad principal y
     * que es un estatico
     *
     * @return
     */
    private Boolean validar() {
        boolean resultado = true;
        String temp;

        temp = nombre.getText().toString();
        if (temp.isEmpty()) {
            MainActivity.alerta(this, "ERROR", getString(R.string.error_nombre));
            nombre.requestFocus();
            return false;
        }
        temp = cedula.getText().toString();
        if (temp.isEmpty()) {
            MainActivity.alerta(this, "ERROR", getString(R.string.cedula_blanco));
            cedula.requestFocus();
            return false;
        }
        temp = cedula.getText().toString();
        // Validacion usando expresion regular
        if (!Pattern.compile("^[0-9]{0,10}$").matcher(temp).matches()) {
            MainActivity.alerta(this, "ERROR", getString(R.string.cedula_incorrecta));
            cedula.requestFocus();
            return false;
        }

        /*
         Validacion de la cedula que es campo unico en la
         base de datos para evitar repeticiones,
         se usa el campo que maneja la clave primaria
         */
        String condicion;
        if (persona.getPersonaId() == -1) {
            condicion = " cedula ='" + cedula.getText().toString() + "'";
        } else {
            condicion = " persona_id <> " + persona.getPersonaId() + " AND cedula ='" + cedula.getText().toString() + "'";
        }
        long c = DatabaseCustomUtils.countRows(MainActivity.getDb(), "persona", condicion);
        if (c > 0) {
            MainActivity.alerta(this, "ERROR", getString(R.string.cedula_registrada));
            cedula.requestFocus();
            return false;
        }

        temp = estatura.getText().toString();
        try {
            Double e = Double.parseDouble(temp);
        } catch (NumberFormatException e1) {
            MainActivity.alerta(this, "ERROR", getString(R.string.estatura_invalida));
            estatura.requestFocus();
            return false;
        }
        return resultado;
    }

    /**
     * Creacion del menu superior personalizado para las acciones guadar y cancelar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.guardar))
                .setIcon(R.drawable.ic_action_done)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(getResources().getString(R.string.cancelar))
                .setIcon(R.drawable.ic_action_cancel)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Accion de retroceso con el boton Home
            case android.R.id.home:
                finish();
                break;
            default:
                // Guardar y cancelar
                if (item.getTitle().equals(getResources().getString(R.string.guardar))) {
                    if (validar()) {
                        persona.setNombre(nombre.getText().toString());
                        persona.setCedula(cedula.getText().toString());
                        persona.setEstadoCivil(estadoCivilSel);
                        persona.setGenero(generoSel.getText().toString());
                        persona.setEstatura(Double.parseDouble(estatura.getText().toString()));
                        try {
                            persona.setFechaNacimiento(DateNumberFormatDetector.getSimpleDateFormat().parse(fechaNacimiento.getText().toString()));
                        } catch (ParseException e) {
                            MainActivity.alerta(this, "ERROR", e.toString());
                        }
                        // Preparacion del resultado a devolver
                        resultado.putExtra("persona", (Parcelable) persona);
                        setResult(200, resultado);
                        finish();
                    }
                }
                if (item.getTitle().equals(getResources().getString(R.string.cancelar))) {
                    setResult(400, null);
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.masculino:
            case R.id.femenino:
                MainActivity.hideKeyb(this);
                break;
        }

    }

    /**
     * Metodo para la accion cuando se selecciona la fecha del componente DatePicker
     *
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, dayOfMonth);
        fechaNacimiento.setText(DateNumberFormatDetector.getSimpleDateFormat().format(newDate.getTime()));
    }


    /**
     * Cuando hay focus en la fecha hay que contruir el DatePicker de dos formas
     * <p>
     * - Tomando el dato de la fecha si existe.
     * - Poniendo la fecha actual si el dato no existe.
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {

            case R.id.fechaNacimiento:
                if (hasFocus) {
                    MainActivity.hideKeyb(Formulario.this);
                    Calendar newCalendar = Calendar.getInstance();
                    if (!fechaNacimiento.getText().toString().isEmpty()) {
                        try {
                            newCalendar.setTime(DateNumberFormatDetector.getSimpleDateFormat().parse(fechaNacimiento.getText().toString()));
                        } catch (ParseException e) {
                        }
                    }

                    new DatePickerDialog(Formulario.this,
                            Formulario.this,
                            newCalendar.get(Calendar.YEAR),
                            newCalendar.get(Calendar.MONTH),
                            newCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                break;
        }
    }
}
