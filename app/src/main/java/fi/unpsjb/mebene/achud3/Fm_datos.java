package fi.unpsjb.mebene.achud3;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Fm_datos extends Fragment {

    private List<String> item_datos = null;
    private List<String> item_esquemas = null;
    ImageButton ibProcesar, ibInfoData, ibDeleteDato, ibRenameData, ibIrmDown, ibIrmUp, ibAyudaInterfaceDatos;
    AcCore acCore;
    ListView listaArchivosDatos;
    ArrayAdapter<String> fileListAdapter;
    public final int ORDEN_ASCENDENTE = 1;
    public final int ORDEN_DESCENDENTE = -1;
    int order = ORDEN_ASCENDENTE;
    int min_delay_np=0, seg_delay_np=0, millis_delay_np=0, delay_total_in_millis=0;
    int irm=0;
    final int irm_step = 50;
    Spinner listaArchivosEsquemas;
    EditText et_irm;
    String archivoDatosSeleccionado, archivoEsquemaSeleccionado, rutaDatos, rutaDeSalida;
    ProgressDialog progress;
    ImageView ImageViewHuds;


    final public Fm_datos activity = this;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fm_datos, container, false);


        return rootView;
    }

    @Override
    public void onAttach(Context cont) {
        super.onAttach(cont );
        MainActivity ma = (MainActivity) getActivity();
        acCore = ma.acCore;
    }

    /*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity ma = (MainActivity) activity;
        acCore = ma.acCore;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progress = new ProgressDialog(this.getActivity());
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_datos, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


//********************************************************************************************************************************
    @Override
    public void onResume() {
        super.onResume();

        //final int order=1;
        archivoDatosSeleccionado=null;
        item_datos = new ArrayList<String>();
        item_esquemas = new ArrayList<String>();

        ImageViewHuds = (ImageView) getView().findViewById(R.id.iV_Huds_datos);

        rutaDatos = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator+ getResources().getString(R.string.s_datos_dir);
        rutaDeSalida = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator
                +getResources().getString(R.string.s_out_dir);

        TextView ruta = (TextView)  getView().findViewById(R.id.tV_ruta);
        ruta.setText("Ruta de datos:\n"+rutaDatos);

        //et_delay = (EditText) getView().findViewById(R.id.et_del_min);

        Log.e("tag33", "ruta: " + Environment.getExternalStorageDirectory());
        Log.e("tag34", "ruta: " + File.separator);
        Log.e("tag35", "ruta: " + getResources().getString(R.string.app_name));
        Log.e("tag36", "ruta: " + Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator+ getResources().getString(R.string.s_datos_dir));
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++){
        File file = files[i];
        if (file.isFile())
            item_datos.add(file.getName());
        }

        f = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator+ getResources().getString(R.string.s_esquemas_dir));
        files = f.listFiles();

        for (int i = 0; i < files.length; i++){
            File file = files[i];
            if (file.isFile() && acCore.isExt(file.getName(), "xml"))
                item_esquemas.add(file.getName());
        }

        //Localizamos y llenamos las listas
        listaArchivosDatos = (ListView)  getView().findViewById(R.id.lst_archivos_datos);
        fileListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_de_lista, item_datos);
        sortList(order);
        listaArchivosDatos.setAdapter(fileListAdapter);
        listaArchivosDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                archivoDatosSeleccionado = (String) listaArchivosDatos.getItemAtPosition(position);

            }});

        listaArchivosEsquemas = (Spinner)  getView().findViewById(R.id.lstEsquemas);
        //ArrayAdapter fileListEsq = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, item_esquemas);
        ArrayAdapter fileListEsq = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_de_lista, item_esquemas);
        fileListEsq.sort(new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                return arg0.compareTo(arg1);
            }
        });
        listaArchivosEsquemas.setAdapter(fileListEsq);
/*
        listaArchivosEsquemas.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+File.separator
                            + getResources().getString(R.string.s_esquemas_dir) + File.separator +nameNoExt((String)listaArchivosEsquemas.getSelectedItem())+".png");
                    if(file.exists()) {
                        Log.e("tag3434", "abs path: " + file.getAbsolutePath());
                        ImageViewHuds.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    } else{
                        ImageViewHuds.setImageResource(R.drawable.no_preview);
                    }
                }

            return false;
            }}
        );
*/
 /*       listaArchivosEsquemas.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    Log.e("tag34343", "abs path AAA ");
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+File.separator
                            + getResources().getString(R.string.s_esquemas_dir) + File.separator +nameNoExt((String)listaArchivosEsquemas.getSelectedItem())+".png");
                    if(file.exists()) {
                        Log.e("tag34343", "abs path: " + file.getAbsolutePath());
                        ImageViewHuds.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    } else{
                        ImageViewHuds.setImageResource(R.drawable.no_preview);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
*/
        listaArchivosEsquemas.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                        //Object item = parent.getItemAtPosition(pos);
                        //System.out.println(item.toString());     //prints the text in spinner item.

                        File file = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator
                                + getResources().getString(R.string.s_esquemas_dir) + File.separator +nameNoExt((String)listaArchivosEsquemas.getSelectedItem())+".png");
                        if(file.exists()) {
                            Log.e("tag34343", "abs path: " + file.getAbsolutePath());
                            ImageViewHuds.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                             }else {
                            ImageViewHuds.setImageResource(R.mipmap.no_preview);
                            }
                            };
                    public void onNothingSelected(AdapterView<?> parent) {
                        ImageViewHuds.setImageResource(R.mipmap.no_preview);
                    }
                });


        //*****SPINERS
        NumberPicker np_min = (NumberPicker) getView().findViewById(R.id.nP_min_delay);
        NumberPicker np_seg = (NumberPicker) getView().findViewById(R.id.nP_seg_delay);
        NumberPicker np_millis = (NumberPicker) getView().findViewById(R.id.nP_millis_delay);

        np_min.setMinValue(0);
        np_min.setMaxValue(59);
        np_seg.setMinValue(0);
        np_seg.setMaxValue(59);
        np_millis.setDisplayedValues(null);
        final String[] millisValues ={"000","050","100","150","200","250","300","350","400","450","500","550","600","650","700","750","800","850","900","950"};
        np_millis.setMinValue(0);
        //np_millis.setMaxValue(millisValues.length);
        np_millis.setMaxValue(millisValues.length - 1);
        np_millis.setDisplayedValues(millisValues);

        np_min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldval, int newval) {
                min_delay_np = newval;
            }
        });
        np_seg.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldval, int newval) {
                seg_delay_np = newval;
            }
        });
        np_millis.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldval, int newval) {
                millis_delay_np = Integer.valueOf(millisValues[newval]);
            }
        });

        et_irm = (EditText) getView().findViewById(R.id.et_irm);

        et_irm.setText(String.format("%03d", irm));


//***********************************************************************************************************************
        //Botones
        ibProcesar = (ImageButton) getView().findViewById(R.id.ibProcesar);
        ibProcesar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EsquemaHUD esquemaHud;;
                final File f_datos, f_esquema;
                String lastLineFDatos="";

                delay_total_in_millis = (min_delay_np * 60 * 1000) + (seg_delay_np * 1000) + millis_delay_np;
                archivoEsquemaSeleccionado = (String) listaArchivosEsquemas.getSelectedItem();



                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    f_datos = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator
                            +getResources().getString(R.string.s_datos_dir)+ File.separator + archivoDatosSeleccionado);
                    f_esquema = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator
                            +getResources().getString(R.string.s_esquemas_dir)+ File.separator + archivoEsquemaSeleccionado);
                } else{
                    AlertDialog.Builder builderError = new AlertDialog.Builder(getActivity());
                    builderError.setTitle("ERROR");
                    builderError.setMessage("No es posble para la aplicacion acceder al almacenamiento, verifique los permisos");
                    builderError.show();
                    return;
                }

                if(f_esquema.exists()){
                    try{
                        Log.i("tag444", "entre f esquemas");
                        FileInputStream fstream = new FileInputStream(f_esquema);
                        DataInputStream in = new DataInputStream(fstream);

                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();
                        EsquemaHandler handler = new EsquemaHandler();
                        parser.parse(in, handler);
                        esquemaHud = handler.getEsquema();

                        in.close();

                        Log.i("tag555", "esquema Ext: " + esquemaHud.getExt());
                        Log.i("tag555", "esquema Header: " + esquemaHud.getHeader());
                        Log.i("tag555", "esquema intSub: " + esquemaHud.getIntro_sub() );
                        Log.i("tag555", "esquema medsub: " + esquemaHud.getMed_sub() );
                        Log.i("tag555", "esquema Delay: " + esquemaHud.getDelay() );

                    }catch (Exception e){
                        Log.e("Procesar", "Error al procesar esquema" + e);
                        return;}
                }else{
                    Toast.makeText(getActivity(), getResources().getString(R.string.s_elemento_no_seleccionado), Toast.LENGTH_LONG).show();
                    return;
                }


                try {
                    BufferedReader br = new BufferedReader(new FileReader(f_datos));
                    String last = br.readLine();
                    while (last != null) {
                        lastLineFDatos = last;
                        last = br.readLine();
                    }
                }catch (Exception e){ e.printStackTrace(); }
                String[] arrayValores = lastLineFDatos.split(",");
                if(arrayValores.length != MedicionDeEntorno.EDA.values().length) {
                    Toast.makeText(getActivity(), "Archivo de datos no valido", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formateador = new SimpleDateFormat("dd_MM_yy_HH_mm_ss");
                String filename_out = "ACHUD_Out_" + formateador.format(new Date()) +"."+esquemaHud.getExt();
                long lastmodified = f_datos.lastModified();
                Date dateModified = new Date();
                dateModified.setTime(lastmodified);
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd 'a las' HH:mm:ss");


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Generar HUD");
                builder.setMessage(
                                "Datos de origen:\n"+archivoDatosSeleccionado+"\n"
                                +"del "+dateFormater.format(dateModified)+"\n\n"
                                +"Tiempo de medicion: "
                                +arrayValores[MedicionDeEntorno.EDA.CR_HH_MED.ordinal()]+":"
                                +arrayValores[MedicionDeEntorno.EDA.CR_mm_MED.ordinal()]+":"
                                +arrayValores[MedicionDeEntorno.EDA.CR_ss_MED.ordinal()]+","
                                +arrayValores[MedicionDeEntorno.EDA.CR_SSS_MED.ordinal()]+"\n\n"
                                +"Esquema HUD:\n"+archivoEsquemaSeleccionado+"\n\n"
                                +"Tipo de salida: "+esquemaHud.getExt()+"\n\n"
                                +"IRM: "+irm+"\n\n"
                                +"Delay: "+min_delay_np+":"+seg_delay_np+","+millis_delay_np+"\n\n"
                                +"Ruta de destino:\n"+rutaDeSalida+"\n\n"
                                +"Archivo de salida:"
                );
                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                input.setText(filename_out);
                builder.setView(input);

                builder.setPositiveButton("GENERAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String fname_out = input.getText().toString();
                        if(!(fname_out.length()>0)){
                            Toast.makeText(getActivity(), "Nombre no valido", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!(acCore.isExt(fname_out, esquemaHud.getExt()))){
                            fname_out = fname_out + "." + esquemaHud.getExt();
                        }
                        final File f_out = new File(rutaDeSalida, fname_out);

                        if (f_out.exists()) {
                            AlertDialog.Builder builderConf = new AlertDialog.Builder(getActivity());
                            builderConf.setTitle("Existe un archivo de salida con el mismo nombre");
                            builderConf.setMessage("Esta accion reemplazara el archivo \"" + fname_out + "\" original");
                            builderConf.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //procesarDatos(f_out, f_datos, esquemaHud, delay_total_in_millis, irm);
                                    acCore.procesarDatos(activity, f_out, f_datos, esquemaHud, delay_total_in_millis, irm);
                                    dialog.dismiss();
                                }
                            });
                            builderConf.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertConf = builderConf.create();
                            alertConf.show();
                        }
                        else {
//                            progress.show();
                            //procesarDatos(f_out, f_datos, esquemaHud, delay_total_in_millis, irm);
                            acCore.procesarDatos(activity, f_out, f_datos, esquemaHud, delay_total_in_millis, irm);
  //                          progress.dismiss();
                        }
                    }
                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });



        //************************************************************************************************************************
        ibInfoData = (ImageButton) getView().findViewById(R.id.ibInfoData);
        ibInfoData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                File file = new File(rutaDatos + File.separator +archivoDatosSeleccionado);
                if (file.exists()) {
                    String lastLine="";
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String last = br.readLine();
                        while (last != null) {
                            lastLine = last;
                            last = br.readLine();
                        }
                    }catch (Exception e){ e.printStackTrace(); }
                    String[] arrayValores = lastLine.split(",");
                    if(arrayValores.length != MedicionDeEntorno.EDA.values().length){
                        Toast.makeText(getActivity(), "Archivo de datos no valido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int size = (int)(file.length()/1024);
                    DecimalFormat format = new DecimalFormat("###,###.##");
                    long lastmodified = file.lastModified();
                    Date dateModified = new Date();
                    dateModified.setTime(lastmodified);
                    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd 'a las' HH:mm:ss");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Detalles");
                    builder.setMessage(
                            "Nombre:\n"+archivoDatosSeleccionado+"\n\n"
                                    +"Ruta:\n"+rutaDatos+"\n\n"
                                    +"Tamaño:\n"+format.format(size)+"Kb\n\n"
                                    +"Ultima Modificacion:\n"+dateFormater.format(dateModified)+"\n\n"
                                    +"Tiempo de medicion:\n"
                                    +arrayValores[MedicionDeEntorno.EDA.CR_HH_MED.ordinal()]+":"
                                    +arrayValores[MedicionDeEntorno.EDA.CR_mm_MED.ordinal()]+":"
                                    +arrayValores[MedicionDeEntorno.EDA.CR_ss_MED.ordinal()]+","
                                    +arrayValores[MedicionDeEntorno.EDA.CR_SSS_MED.ordinal()]+"\n\n"
                                    +"Cantidad de registros:\n"+arrayValores[MedicionDeEntorno.EDA.NRO_MED.ordinal()]+"\n\n"
                                    +"Intervalo de refresco:\n"+((Integer.valueOf(arrayValores[MedicionDeEntorno.EDA.T0_SSS_ABS.ordinal()])) / Integer.valueOf(arrayValores[MedicionDeEntorno.EDA.NRO_MED.ordinal()]))
                    );
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else{
                    Toast.makeText(getActivity(),"Debe elegir un archivo de la lista", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //************************************************************************************************************************
        ibDeleteDato = (ImageButton) getView().findViewById(R.id.ibDeleteDato);
        ibDeleteDato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //archivoEsquemaSeleccionado = (String) listaArchivosEsquemas.getSelectedItem();
                //int n = acCore.procesarDatos(archivoEsquemaSeleccionado, archivoDatosSeleccionado, et_delay.getText().toString());

                final File file = new File(rutaDatos + File.separator +archivoDatosSeleccionado);

                if (file.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Esta seguro que desea proceder?");
                    builder.setMessage("Esta accion eliminara el archivo \"" + archivoDatosSeleccionado + "\" de forma permanente");
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                            file.delete();
                            onResume();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Toast.makeText(getActivity(),"Debe elegir un archivo de la lista", Toast.LENGTH_SHORT).show();
                }
                Log.i("tag4444", "Se elimino: " + archivoDatosSeleccionado);
            }
        });

        //************************************************************************************************************************
        ibRenameData = (ImageButton) getView().findViewById(R.id.ibRenameData);
        ibRenameData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(archivoDatosSeleccionado != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Renombrar");
                    builder.setMessage("Ingrese el nuevo nombre:");
                    // Set up the input
                    final EditText input = new EditText(getActivity());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    input.setText(archivoDatosSeleccionado);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Renombrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newFileName = input.getText().toString();
                            if (newFileName != null) {
                                if (!acCore.isExt(newFileName, "csv"))
                                    newFileName = newFileName + ".csv";
                                File fileOriginal = new File(rutaDatos + File.separator + archivoDatosSeleccionado);
                                File fileCopy = new File(rutaDatos + File.separator + newFileName);

                                if (!fileCopy.exists()) {
                                    fileOriginal.renameTo(fileCopy);
                                    onResume();
                                } else {
                                    AlertDialog.Builder builderError = new AlertDialog.Builder(getActivity());
                                    builderError.setTitle("No fue posible realizar la copia");
                                    builderError.setMessage("Ya existia un archivo de datos con el nombre selccionado, reintente con un nombre distinto");
                                    builderError.show();
                                }
                                Log.i("tag4444", "Se copio: " + newFileName);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else{
                    Toast.makeText(getActivity(),"Debe elegir un archivo de la lista", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //************************************************************************************************************************
        ibIrmDown = (ImageButton) getView().findViewById(R.id.ib_irm_down);
        ibIrmDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if((irm - irm_step)>0) {
                    irm = irm - irm_step;
                }else{
                    irm = 0;
                }
                et_irm.setText(String.format("%03d", irm));
            }
            });

        //************************************************************************************************************************
        ibIrmUp = (ImageButton) getView().findViewById(R.id.ib_irm_up);
        ibIrmUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                irm = irm + irm_step;
                et_irm.setText(String.format("%03d", irm));
            }
        });

        //************************************************************************************************************************
        ibAyudaInterfaceDatos = (ImageButton) getView().findViewById(R.id.ibAyudaInterfaceDatos);
        ibAyudaInterfaceDatos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Ayuda");
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View vi = inflater.inflate(R.layout.ayuda_datos, null);
                builder.setView(vi);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

//********************************************************************************************************************************
public void shareCreatedFile(final File f_out_result){
    //private void shareCreatedFile(File f_out, File f_datos, EsquemaHUD esquema, int delay, int irm_gui){
    //final File f_out_result=acCore.procesarDatos(f_out, f_datos, esquema, delay, irm_gui);
    Log.i("tag4444", "Se ingerso a compartir");
    if(f_out_result != null){

        StrictMode.VmPolicy.Builder builderSM = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderSM.build());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Archivo HUD creado con exito");
        builder.setMessage(f_out_result.getName());
        builder.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (f_out_result.exists()) {
                    //Uri path = Uri.fromFile(f_out_result);
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    intentShareFile.setType( "application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+f_out_result));
                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Compartido por AC_HUD: \"" + f_out_result.getName()+"\"");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Archivo creado con AC_HUD, mas informacion en www.achud.com.ar");

                    Log.i("tag4444", "Se creo intent en compartir");

                    try {
                        startActivity(Intent.createChooser(intentShareFile, "Share File"));
                        Log.i("tag4444", "Se lanzo intent en compartir");
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(),"No Application Available to View File: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        //Toast.makeText(getActivity(), "Archivo " + f_out_result.getName() + " creado con exito.", Toast.LENGTH_SHORT).show();
    }else{
        Toast.makeText(getActivity(), "No fue posible generar el archivo", Toast.LENGTH_SHORT).show();
    }

}

//********************************************************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("tag4444", "Se ingerso al menu con: " + item.getItemId());
        File file=null;
        switch (item.getItemId()) {
            case (R.id.refresh):
                onResume();
                Toast.makeText(getActivity(),"Vista refrescada", Toast.LENGTH_SHORT).show();
                return true;
            case (R.id.abrir_con_fbrowser):
                file = new File(rutaDatos);
                if (file.exists()) {
                    //Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setDataAndType(Uri.parse(file.getPath()), "*/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.setDataAndType(Uri.fromFile(file), "text/csv");
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(intent);
                        //startActivity(Intent.createChooser(intent, "Open folder"));
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "No Application Available to View File: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i("tag4444", "Se selecciono: " + archivoDatosSeleccionado);
                return true;
            case (R.id.invertOrder):
                if(order == ORDEN_ASCENDENTE) {
                    order = ORDEN_DESCENDENTE;
                    sortList(order);
                }
                else{
                    order = ORDEN_ASCENDENTE;
                    sortList(order);
                }
                onResume();
                Toast.makeText(getActivity(), "Datos reordenados", Toast.LENGTH_SHORT).show();
                return true;
            case (R.id.openFile):

                file = new File(rutaDatos + File.separator +archivoDatosSeleccionado);

                if (file.exists()) {
                    Uri path = Uri.parse(file.getPath());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "text/csv");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(),
                                "No Application Available to View File: " + e,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Debe elegir un archivo de la lista", Toast.LENGTH_SHORT).show();
                }
                Log.i("tag4444", "Se selecciono para editar: " + archivoDatosSeleccionado);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//********************************************************************************************************************************
    public void sortList(int order) {
        if(order >= 0){
            fileListAdapter.sort(new Comparator<String>() {
                @Override
                public int compare(String arg0, String arg1) {
                    File file0 = new File(rutaDatos + File.separator +arg0);
                    File file1 = new File(rutaDatos + File.separator +arg1);
                    return String.valueOf(file0.lastModified()).compareTo(String.valueOf(file1.lastModified()));
                }
            });
            fileListAdapter.notifyDataSetChanged();
        }
        else{
            fileListAdapter.sort(new Comparator<String>() {
                @Override
                public int compare(String arg0, String arg1) {
                    File file0 = new File(rutaDatos + File.separator + arg0);
                    File file1 = new File(rutaDatos + File.separator + arg1);
                    return String.valueOf(file1.lastModified()).compareTo(String.valueOf(file0.lastModified()));
                }
            });
            fileListAdapter.notifyDataSetChanged();
        }
    }


//********************************************************************************************************************************
private String nameNoExt(String name) {
    String filenameArray[] = name.split("\\.");
    String newFilename="";
    for(int i=0; i<(filenameArray.length-1);i++)
        if(newFilename.compareTo("")==0){
            newFilename=filenameArray[i];}
        else{
            newFilename=newFilename+"."+filenameArray[i];}
    return newFilename;
    }

//********************************************************************************************************************************

}