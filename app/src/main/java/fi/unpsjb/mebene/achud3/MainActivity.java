package fi.unpsjb.mebene.achud3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;//Cajón de navegación para icono animado estilo Play Store
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerExpandableList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private List<String> grupos;
    private HashMap<String, List<String>> datosGrupos;
    private int ultimaPosicionExpList = -1;
    private boolean evitarInicio = false;
    private int ultimaMarcaPosicionGrupo = -1;
    private int ultimaMarcaPosicionHijo = -1;
    private MyAdapter adapter;
    private boolean tengoPermisoSD = false;
    private boolean tengoPermisoGPS = false;

    public AcCore acCore;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2 ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Opcional: Al iniciar la app se bloquea la pantalla en vertical para evitar perdida de datos (mDrawerTitle y mTitle)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Otra opción es añadir en el AndroidManifest a la activity la siguiente linea: android:configChanges="orientation|keyboardHidden|screenSize"
        //De ésta forma el Activity no se reinicia y la app gestiona directamente el comportamiento que tendrá al rotar la pantalla.
        //Éste comportamiento lo conotrolaremos desde onConfigurationChanged: if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {...

        //mTitle = mDrawerTitle = getTitle();//De esta forma se obtiene el título de la app

       // int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        toolbar = (Toolbar) findViewById(R.id.toolbar);//Añadimos include en activity_main y estilo en styles
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerExpandableList = (ExpandableListView) findViewById(R.id.explist_slidermenu);
        mDrawerExpandableList.setGroupIndicator(null);//Indicador flecha desplegable izquierda oculta

        //A continuación añadimos cabecera general...
        View header = getLayoutInflater().inflate(R.layout.cabecera_general, null);
        mDrawerExpandableList.addHeaderView(header, null, false);
        //...y pie de página
        View footer = getLayoutInflater().inflate(R.layout.pie_pagina, null);
        mDrawerExpandableList.addFooterView(footer, null, false);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);




        cargarDatos();

            if (toolbar != null) {
                toolbar.setTitle(mDrawerTitle);
                toolbar.setSubtitle(mTitle);
                //toolbar.setLogo(R.drawable.logo);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                //Si cerramos menú, mostramos título y subtítulo
                getSupportActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setSubtitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Si abrimos menú, personalizamos la acción
                getSupportActionBar().setTitle("Menú");
                getSupportActionBar().setSubtitle("Selecciona opción");
                invalidateOptionsMenu();

            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);//Mostrar icono menu animado
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerExpandableList.setTextFilterEnabled(true);

        mDrawerExpandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                if (ultimaPosicionExpList != -1 && groupPosition != ultimaPosicionExpList) {
                    //Cuando abrimos un grupo se cierra el anterior que estuviera abierto
                    mDrawerExpandableList.collapseGroup(ultimaPosicionExpList);
                }
                ultimaPosicionExpList = groupPosition;

                abrirUltPosMarc(groupPosition);//Nos mostrará al abrir el grupo, la última selección marcada
            }
        });
        mDrawerExpandableList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                cerrarUltPosMarc(groupPosition);//Establece al cerrar el grupo, la última selección marcada
            }
        });

        mDrawerExpandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                int grup_pos = (int) adapter.getGroupId(groupPosition);
                //Al seleccionar sobre antiguo grupo 1 marcamos con color (list_selector) y lanzamos Fm_1
                switch (grup_pos) {
                    case 0:
                        displayView1(0);
                        ultimaMarcaPosicionGrupo = grup_pos;
                        ultimaMarcaPosicionHijo = -1;
                        Log.e("Aviso", "El Grupo es: " + grup_pos + " y SIN hijo");
                        break;
                    case 1:
                        displayView1(1);
                        ultimaMarcaPosicionGrupo = grup_pos;
                        ultimaMarcaPosicionHijo = -1;
                        Log.e("Aviso", "El Grupo es: " + grup_pos + " y SIN hijo");
                        break;
                    default:
                        break;
                }

                return false;
            }
        });

        mDrawerExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int grup_pos = (int) adapter.getGroupId(groupPosition);
                int child_pos = (int) adapter.getChildId(groupPosition, childPosition);
                //Al seleccionar, se marcará el hijo con un color, contamos para ello con lis_selector2, ver values/color y ...bg_normal más ...bg_pressed
                switch (grup_pos) {
                    ////El siguiente case lo dejamos sin uso, ya que grupo 1 no tiene actualmente hijos
                    case 0:
                                displayView1(0);
                                displayView2(parent, 0, 0);
                                Log.e("Aviso", "El Grupo es: " + grup_pos + " y el hijo es: " + child_pos);
                        break;
                    case 1:
                                displayView1(1);
                                displayView2(parent, 1, 0);
                                Log.e("Aviso", "El Grupo es: " + grup_pos + " y el hijo es: " + child_pos);
                        break;
                    case 2:
                        switch (child_pos) {
                            case 0:
                                displayView1(2);
                                displayView2(parent, 2, 0);
                                Log.e("Aviso", "El Grupo es: " + grup_pos + " y el hijo es: " + child_pos);
                                break;
                            case 1:
                                displayView1(3);
                                displayView2(parent, 2, 1);
                                Log.e("Aviso", "El Grupo es: " + grup_pos + " y el hijo es: " + child_pos);
                                break;
                            default:
                                break;
                        }
                        break;
                    case 3:
                        switch (child_pos) {
                            case 0:
                                displayView1(4);
                                displayView2(parent, 3, 0);
                                Log.e("Aviso", "El Grupo es: " + grup_pos + " y el hijo es: " + child_pos);
                                break;
                            case 1:
                                displayView1(5);
                                displayView2(parent, 3, 1);
                                Log.e("Aviso", "El Grupo es: " + grup_pos + " y el hijo es: " + child_pos);
                                break;
                            default:
                                break;
                        }
                        break;

                    default:
                        break;
                }

                ultimaMarcaPosicionGrupo = grup_pos;
                ultimaMarcaPosicionHijo = child_pos;
                mDrawerLayout.closeDrawer(mDrawerExpandableList);
                return false;
            }
        });

        if (savedInstanceState == null) {
            displayView1(0);//Se inicia la app llamando a la posición del fragment Fm_1 y marcando grupo 1
            displayView2(mDrawerExpandableList, 0, 0);//Se marca el hijo 1 del grupo 1
            evitarInicio = true;
        }






    }

    private void cargarDatos() {

        grupos = new ArrayList<String>();
        datosGrupos = new HashMap<String, List<String>>();

        grupos.add( getResources().getString(R.string.menu_captura));
        grupos.add( getResources().getString(R.string.menu_datos));
        grupos.add( getResources().getString(R.string.menu_configuracion));
        grupos.add( getResources().getString(R.string.menu_ayuda));

        List<String> s_menu_captura = new ArrayList<String>();

        List<String> s_menu_datos = new ArrayList<String>();

        List<String> s_menu_conf = new ArrayList<String>();
        s_menu_conf.add( getResources().getString(R.string.s_menu_aplicacion));
        s_menu_conf.add( getResources().getString(R.string.s_menu_huds));

        List<String> s_menu_ayuda = new ArrayList<String>();
        s_menu_ayuda.add( getResources().getString(R.string.s_menu_manual));
        s_menu_ayuda.add(getResources().getString(R.string.s_menu_acerca_de));



        datosGrupos.put(grupos.get(0), s_menu_captura);
        datosGrupos.put(grupos.get(1), s_menu_datos);
        datosGrupos.put(grupos.get(2), s_menu_conf);
        datosGrupos.put(grupos.get(3), s_menu_ayuda);

        adapter = new MyAdapter(this, grupos, datosGrupos);
        mDrawerExpandableList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void displayView2 (ExpandableListView parent, int groupPosition, int childPosition) {

        int index;
        index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
        parent.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);

        parent.setItemChecked(index, true);

        ultimaMarcaPosicionGrupo = groupPosition;
        ultimaMarcaPosicionHijo = childPosition;
        //Ejemplo enumerado del porqué de los index:
                /*
                Grupo 1 [index 0]

                    Hijo 1 [index 1]
                    Hijo 2 [index 2]

                Grupo 2 [index 3]

                    Hijo 1 [index 4]
                    HIjo 2 [index 5]
                    Hijo 3 [index 6]

                Grupo 3 [index 7]
                    ...
                */
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void displayView1 (int position) {

        Fragment fragment = null;
        mDrawerExpandableList.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        int childCount = mDrawerExpandableList.getChildCount();

        switch (position) {
            case 0:
                Log.e("Aviso", "Entre en dv1 con pos: " + position);
                fragment = new Fm_cam_generica();
                mDrawerTitle = getResources().getString(R.string.menu_captura);
                mTitle = getResources().getString(R.string.s_menu_camara_generica);
                mDrawerExpandableList.clearChoices();
               /* if (evitarInicio == true) {//Evitamos el borrado de la marca en el inicio de la app
                    mDrawerExpandableList.clearChoices();
                }*/
               // mDrawerExpandableList.expandGroup(0);//Opcional mostramos el grupo inicial expandido
                mDrawerExpandableList.setItemChecked(1, true);//En el inicio y al seleccionar hijo nos marca el grupo 1
                //mDrawerExpandableList.setItemChecked(2, false);//Anulamos marca de grupo distinto a éste
                //mDrawerExpandableList.setItemChecked(0, false);//Volvemos a opción 0 ningún items marcado
                for (int i = 0; i < childCount; i++) {
                    if (i != 1) {mDrawerExpandableList.setItemChecked(i, false);
                    }
                }
                break;
            case 1:
                Log.e("Aviso", "Entre en dv1 con pos: " + position);
                fragment = new Fm_datos();
                mDrawerTitle = getResources().getString(R.string.menu_datos);
                mTitle = getResources().getString(R.string.menu_datos);
                mDrawerExpandableList.clearChoices();
                mDrawerExpandableList.setItemChecked(2, true);
                for (int i = 0; i < childCount; i++) {
                    if (i != 2) {mDrawerExpandableList.setItemChecked(i, false); }
                }
                break;
            case 2:
                Log.e("Aviso", "Entre en dv1 con pos: " + position);
                fragment = new Fm_settings();
                mDrawerTitle = getResources().getString(R.string.menu_configuracion);
                mTitle = getResources().getString(R.string.s_menu_aplicacion);
                mDrawerExpandableList.clearChoices();
                mDrawerExpandableList.setItemChecked(3, true);
                for (int i = 0; i < childCount; i++) {
                    if (i != 3) {mDrawerExpandableList.setItemChecked(i, false); }
                }
                break;
            case 3:
                Log.e("Aviso", "Entre en dv1 con pos: " + position);
                fragment = new Fm_huds();
                mDrawerTitle = getResources().getString(R.string.menu_configuracion);
                mTitle = getResources().getString(R.string.s_menu_huds);
                mDrawerExpandableList.clearChoices();
                mDrawerExpandableList.setItemChecked(3, true);
                for (int i = 0; i < childCount; i++) {
                    if (i != 3) {mDrawerExpandableList.setItemChecked(i, false); }
                }
                break;
            case 4:
                Log.e("Aviso", "Entre en dv1 con pos: " + position);
                fragment = new Fm_manual_de_uso();
                mDrawerTitle = getResources().getString(R.string.menu_ayuda);
                mTitle = getResources().getString(R.string.s_menu_manual);
                mDrawerExpandableList.clearChoices();
                mDrawerExpandableList.setItemChecked(4, true);
                for (int i = 0; i < childCount; i++) {
                    if (i != 4) {mDrawerExpandableList.setItemChecked(i, false); }
                }
                break;
            case 5:
                Log.e("Aviso", "Entre en dv1 con pos: " + position);
                fragment = new Fm_acerca_de();
                mDrawerTitle = getResources().getString(R.string.menu_ayuda);
                mTitle = getResources().getString(R.string.s_menu_acerca_de);
                mDrawerExpandableList.clearChoices();
                mDrawerExpandableList.setItemChecked(4, true);
                for (int i = 0; i < childCount; i++) {
                    if (i != 4) {mDrawerExpandableList.setItemChecked(i, false); }
                }
                break;
            default:
                break;
        }



        if (tengoPermisoSD && tengoPermisoGPS) {

            acCore = new AcCore(this);

            if (fragment != null) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

                mDrawerExpandableList.setSelection(position);
                //setTitle(grupos.get(position));
                getSupportActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setSubtitle(mTitle);
                mDrawerLayout.closeDrawer(mDrawerExpandableList);
            } else {
                Log.e("Aviso", "Error cuando se crea el fragment");
            }
        } else {


            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                Log.e("Aviso", " No tenia permiso de Escribir SD1");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }
            } else {
                Log.e("Aviso", " SI tenia permiso de Escribir SD1");
                tengoPermisoSD = true;
            }

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                Log.e("Aviso", " No tenia permiso GPS1");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                }
            } else {
                Log.e("Aviso", " SI tenia permiso GPS1");
                tengoPermisoGPS = true;
            }/*
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
        }

    }

    private void abrirUltPosMarc (int groupPosition) {

        if (groupPosition == ultimaMarcaPosicionGrupo) {
            if (ultimaMarcaPosicionHijo != -1) {
                int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(ultimaMarcaPosicionGrupo, ultimaMarcaPosicionHijo));
                mDrawerExpandableList.setItemChecked(index, true);
            } else {
                int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(ultimaMarcaPosicionGrupo));
                mDrawerExpandableList.setItemChecked(index, true);
            }
        } else {
            if (mDrawerExpandableList.isGroupExpanded(ultimaMarcaPosicionGrupo)){
                if (ultimaMarcaPosicionHijo != -1) {
                    int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(ultimaMarcaPosicionGrupo, ultimaMarcaPosicionHijo));
                    mDrawerExpandableList.setItemChecked(index, true);
                } else {
                    int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(ultimaMarcaPosicionGrupo));
                    mDrawerExpandableList.setItemChecked(index, true);
                }
            } else {
                mDrawerExpandableList.setItemChecked(-1, true);
            }
        }
    }

    private void cerrarUltPosMarc (int groupPosition) {

        if (groupPosition == ultimaMarcaPosicionGrupo) {
            if (ultimaMarcaPosicionGrupo != -1){
                int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(ultimaMarcaPosicionGrupo));
                mDrawerExpandableList.setItemChecked(index, true);
            } else {
                mDrawerExpandableList.setItemChecked(-1, true);
            }
        } if (mDrawerExpandableList.isGroupExpanded(ultimaMarcaPosicionGrupo)){
            if (ultimaMarcaPosicionHijo != -1){
                int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(ultimaMarcaPosicionGrupo, ultimaMarcaPosicionHijo));
                mDrawerExpandableList.setItemChecked(index, true);
            } else {
                int index = mDrawerExpandableList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(ultimaMarcaPosicionGrupo));
                mDrawerExpandableList.setItemChecked(index, true);
            }
        } else {
            mDrawerExpandableList.setItemChecked(-1, true);
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onKeyDown (int keycode, KeyEvent event){
    //Con el botón físico menu, cerramos y abrimos también el mDrawerLayout
        if (keycode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }else{
            return super.onKeyDown(keycode, event);
        }
    }

    @Override
    public void onBackPressed() {
    //Si tenemos el mDrawerLayout abierto, con el botón físico atrás se cerraría
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Aviso", " DI ok al permiso de Escribir SD");
                    //tengoPermisoSD = true;
                } else {
                    Log.e("Aviso", " NO DI ok al permiso de Escribir SD");
                    //finish();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Aviso", " DI ok al permiso de GPS");
                    //tengoPermisoGPS = true;
                } else {
                    Log.e("Aviso", " NO DI ok al permiso de GPS");
                    //finish();
                }
                return;
            }
        }
    }

}

