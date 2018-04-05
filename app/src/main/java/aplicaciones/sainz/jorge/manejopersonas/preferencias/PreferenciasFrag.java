package aplicaciones.sainz.jorge.manejopersonas.preferencias;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import aplicaciones.sainz.jorge.manejopersonas.R;

/**
 * Inyeccion de las preferencias que estan en los recursos a traves de un fragmento,
 * es la modalidad que no esta obsoleta
 */

public class PreferenciasFrag extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);

    }
}
