package aplicaciones.sainz.jorge.manejopersonas.preferencias;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import aplicaciones.sainz.jorge.manejopersonas.R;

/**
 * Lanzamiento de preferencias usando fragmento
 */
public class Preferencias extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.titulo_activity_preferencias));
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFrag())
                .commit();

    }
}


