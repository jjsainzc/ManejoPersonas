package aplicaciones.sainz.jorge.manejopersonas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase ejemplo de ubicacion que tiene la localizacion geografica y una descripcion
 */
class Lugar {
    private Double latitud;
    private Double longitud;
    private String descripcion;

    public Lugar() {
    }

    public Lugar(Double latitud, Double longitud, String descripcion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

/**
 * @author JJSC, 2018
 * <p>
 * Trabajo con mapas, requerimientos:
 * <p>
 * - Sacar una clave de API en Google, y adicionar el metadata en el manifiesto
 * - Adicionar permisos en Manifiesto
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * En mi opinion le adiciono esta tambien por si acaso:
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <p>
 * - Adicionar la dependencia
 * com.google.android.gms:play-services-maps:x.x.x
 * - Para esta version, el mapa se carga de forma asincrona por lo que hay que usar
 * un callback que permita realizar las operaciones de configuracion inicial del
 * mismo despues que haya sido cargado.
 */
public class GoogleMapsFragmento extends Fragment {
    // Manejador del fragmento
    private SupportMapFragment mapFragment;
    // Objeto de mapa
    private GoogleMap mMap;
    // Objeto de progreso
    private ProgressDialog progreso;
    // Arreglo con los tipos de mapas
    private static final CharSequence[] MAP_TYPE_ITEM;
    // Una ubicacion
    private static final LatLng CEC;
    // Lista de lugares
    private List<Lugar> lugares;

    // Objeto para manejar el apuntador a la clase padre
    private MainActivity mainActivity;

    static {
        // Localizacion del CEC (no va a cambiar a no ser que demuelan el edificio ;) )
        CEC = new LatLng(-0.209083, -78.486857);
        MAP_TYPE_ITEM = new CharSequence[]{
                "Road Map", "Satellite", "Terrain", "Hybrid"
        };
    }

    /**
     * El constructor inicializa los lugares en la lista,
     * pero esta opcion puede estar en una base de datos o leida del internet
     */
    public GoogleMapsFragmento() {
        lugares = new ArrayList<>();
        lugares.add(new Lugar(-0.2097139, -78.4950827, "Casa cultura"));
        lugares.add(new Lugar(-0.2030057, -78.491437, "Plaza Foch"));
    }


    public static GoogleMapsFragmento newInstance() {
        GoogleMapsFragmento fragment = new GoogleMapsFragmento();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progreso = new ProgressDialog(mainActivity);
        progreso.setTitle(getResources().getString(R.string.cargando_mapa));
        progreso.setMessage(getResources().getString(R.string.espere));
        progreso.setCancelable(false);
        progreso.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);
        /*
        Si el fragmento no esta creado, se invoca al manejador de fragmentos del mapa, y se
        lanza en el FrameLayout del layout que ya estaba definido para la clase MainActivity, no es
        necesario hacer otro layout adicional para esto.
         */
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                /**
                 * Este metodo se dispara despues que el mapa ya ha sido cargado
                 * @param googleMap
                 */
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            progreso.dismiss();
                            /*
                               Ejemplo de evento click en el mapa que pone una marca y una descripcion
                             */
                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(latLng.toString())
                                    );
                                }
                            });
                        }
                    });


                    // Adicion de una marca
                    mMap.addMarker(new MarkerOptions().position(CEC).title("Centro de Educacion Continua"));
                    // Se mueve la camara del mapa hacia la ubicacion, y se configura el zoom
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CEC, 16));
                    // Se establece el tipo de visualizacion
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            });
        }
        // Para lanzar un fragmento dentro de otro
        getChildFragmentManager().beginTransaction().replace(R.id.contenedor, mapFragment).commit();

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setActionBarTitle("Mapa");
    }

    public void onButtonPressed(Uri uri) {
    }

    /**
     * Vinculacion del apuntador a la actividad padre
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();

    }

    /**
     * Desvinculacion del apuntador a la actividad padre
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mapFragment = null;
        mainActivity = null;
    }

    /**
     * Creacion del menu superior, esta usada la modalidad dinamica
     * pero los titulos estan manejados en los recursos
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(R.string.cambiar_tipo_mapa)
                .setIcon(R.drawable.ic_show_layers)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(R.string.adicionar_lugares)
                .setIcon(R.drawable.ic_action_add_location)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(R.string.clean_m)
                .setIcon(R.drawable.ic_pin_drop)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


    }

    /**
     * Manejo de la accion del menu superior
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.cambiar_tipo_mapa))) {
            cambiarTipoMapa();
        }
        if (item.getTitle().equals(getResources().getString(R.string.clean_m))) {
            /*
              Limpia el mapa de todas las marcas
             */
            mMap.clear();
        }
        if (item.getTitle().equals(getResources().getString(R.string.adicionar_lugares))) {
            LatLng l = CEC;
            for (Lugar lu : lugares) {
                l = new LatLng(lu.getLatitud(), lu.getLongitud());
                // Adiciona marcas ern el mapa
                mMap.addMarker(new MarkerOptions()
                        .position(l)
                        .title(lu.getDescripcion())
                );
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
        }
        return true;
    }

    /**
     * Cambia el tipo de visualizacion del mapa, escogiendo el mismo desde un
     * dialogo de seleccion simple
     */
    private void cambiarTipoMapa() {
        final String fDialogTitle = getString(R.string.seleccione_tipo_mapa);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(fDialogTitle);

        int checkItem = mMap.getMapType() - 1;

        builder.setSingleChoiceItems(
                MAP_TYPE_ITEM,
                checkItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                break;
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;

                        }
                        dialog.dismiss();
                    }
                }
        );
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }


}
