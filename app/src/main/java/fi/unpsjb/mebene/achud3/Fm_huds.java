package fi.unpsjb.mebene.achud3;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Fm_huds extends Fragment {


    private List<String> item_esquemas = null;
    ImageButton ibEditar, ibDeleteHud, ibCopyHud;
    AcCore acCore;
    ListView listaArchivosEsquemas;
    ArrayAdapter<String> fileListAdaptrer;
    String rutaHuds;
    String archivoEsquemaSeleccionado;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fm_huds, container, false);

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_huds, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//********************************************************************************************************************************
    @Override
    public void onResume() {
        super.onResume();

        item_esquemas = new ArrayList<String>();
        archivoEsquemaSeleccionado = null;

        final ImageView ImageViewHuds = (ImageView) getView().findViewById(R.id.imageViewHuds);

        ImageViewHuds.setImageResource(R.mipmap.no_preview);

        rutaHuds = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator+ getResources().getString(R.string.s_esquemas_dir);
        TextView ruta = (TextView)  getView().findViewById(R.id.tV_ruta_huds);
        ruta.setText("Ruta de HUD's: "+ rutaHuds);


        Log.e("tag33", "ruta: " + Environment.getExternalStorageDirectory());
        Log.e("tag34", "ruta: " + File.separator);
        Log.e("tag35", "ruta: " + getResources().getString(R.string.app_name));
        Log.e("tag36", "ruta: " + Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name)+ File.separator+ getResources().getString(R.string.s_esquemas_dir));
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++){
        File file = files[i];
        if (file.isFile() && acCore.isExt(file.getName(), "xml"))
            item_esquemas.add(file.getName());
        Log.i("tag4444", "archivo: " + file.getName());
        }

        //Localizamos y llenamos las listas
        listaArchivosEsquemas = (ListView)  getView().findViewById(R.id.lst_archivos_esquemas);
        fileListAdaptrer = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_de_lista, item_esquemas);
        fileListAdaptrer.sort(new Comparator<String>()
        {@Override
            public int compare(String arg0, String arg1)
            { return arg0.compareToIgnoreCase(arg1); }});
        listaArchivosEsquemas.setAdapter(fileListAdaptrer);

        listaArchivosEsquemas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                archivoEsquemaSeleccionado = (String) listaArchivosEsquemas.getItemAtPosition(position);
                File file = new File(rutaHuds + File.separator +nameNoExt(archivoEsquemaSeleccionado)+".png");
                if(file.exists()) {
                    ImageViewHuds.setImageBitmap(BitmapFactory.decodeFile(rutaHuds + File.separator + nameNoExt(archivoEsquemaSeleccionado) + ".png"));
                } else{
                    ImageViewHuds.setImageResource(R.mipmap.no_preview);
                }
            }});


//Botones
//********************************************************************************************************************************
        ibEditar = (ImageButton) getView().findViewById(R.id.ibEditHud);
        ibEditar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //archivoEsquemaSeleccionado = (String) listaArchivosEsquemas.getSelectedItem();
                //int n = acCore.procesarDatos(archivoEsquemaSeleccionado, archivoDatosSeleccionado, et_delay.getText().toString());

                File file = new File(rutaHuds + File.separator +archivoEsquemaSeleccionado);

                if (file.exists()) {
                    Uri path = Uri.parse(file.getPath());
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setDataAndType(path, "text/plain");
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
                    Toast.makeText(getActivity(),
                            "Debe elegir un archivo de la lista",
                            Toast.LENGTH_SHORT).show();
                }

                Log.i("tag4444", "Se selecciono: " + archivoEsquemaSeleccionado);
            }
        });

//********************************************************************************************************************************
        ibDeleteHud = (ImageButton) getView().findViewById(R.id.ibDeleteHud);
        ibDeleteHud.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //archivoEsquemaSeleccionado = (String) listaArchivosEsquemas.getSelectedItem();
                //int n = acCore.procesarDatos(archivoEsquemaSeleccionado, archivoDatosSeleccionado, et_delay.getText().toString());

                final File file = new File(rutaHuds + File.separator +archivoEsquemaSeleccionado);

                if (file.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Esta accion eliminara el archivo\n" + archivoEsquemaSeleccionado);
                    builder.setMessage("Esta seguro que desea proceder?");
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
                Log.i("tag4444", "Se elimino: " + archivoEsquemaSeleccionado);
            }
        });


//********************************************************************************************************************************
        ibCopyHud = (ImageButton) getView().findViewById(R.id.ibCopyHud);
        ibCopyHud.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(archivoEsquemaSeleccionado != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Esta accion creara una copia del HUD seleccioando");
                    builder.setMessage("Ingrese el nombre de la copia:");
                    // Set up the input
                    final EditText input = new EditText(getActivity());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    input.setText("Copia_de_" + archivoEsquemaSeleccionado);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Copiar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newFileName = input.getText().toString();
                            if(newFileName!=null) {
                                if (!acCore.isExt(newFileName, "xml"))
                                    newFileName = newFileName + ".xml";
                                File fileOriginal = new File(rutaHuds + File.separator + archivoEsquemaSeleccionado);
                                File fileCopy = new File(rutaHuds + File.separator + newFileName);
                                if (!fileCopy.exists()) {
                                    try {
                                        acCore.copyFile(fileOriginal, fileCopy);
                                    } catch (IOException e) {
                                        Toast.makeText(getActivity(), "No se pudo copiar el archivo: " + "\n" + e, Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                    onResume();
                                } else {
                                    AlertDialog.Builder builderError = new AlertDialog.Builder(getActivity());
                                    builderError.setTitle("No fue posible realizar la copia");
                                    builderError.setMessage("Ya existia un HUD con el nombre selccionado, reintente con un nombre distinto");
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





    }

//********************************************************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("tag4444", "Se ingerso al menu con: " + item.getItemId());

        switch (item.getItemId()) {
            case (R.id.refresh):
                onResume();
                Toast.makeText(getActivity(),"Vista refrescada", Toast.LENGTH_SHORT).show();
                return true;
            case (R.id.abrir_con_fbrowser):
                final File file = new File(rutaHuds);
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
                Log.i("tag4444", "Se selecciono: " + rutaHuds);
                return true;
            case (R.id.restoreHUDs):

                File dir = new File(rutaHuds);
                File[] files = dir.listFiles();

                for (int i = 0; i < files.length; i++){
                    File fileToCopy = files[i];
                    Log.i("tag4444", "file to copy: " + fileToCopy.getName());
                    if(acCore.isExt(fileToCopy.getName(), "back")){
                        Log.i("tag4444", "file to copy is back: " + fileToCopy.getName());
                        Log.i("tag4444", "file to copy sin back: " +  nameNoExt(fileToCopy.getName()));
                        File fileRestored = new File(rutaHuds + File.separator + nameNoExt(fileToCopy.getName()));
                        try {
                            acCore.copyFile(fileToCopy, fileRestored);
                        } catch (IOException e) {
                            AlertDialog.Builder builderError = new AlertDialog.Builder(getActivity());
                            builderError.setTitle("ERROR en restauracion");
                            builderError.setMessage("No fue posible realizar la restauracion");
                            builderError.show();
                            e.printStackTrace();
                        }
                    }
                }
                onResume();
                Toast.makeText(getActivity(), "HUD's restaurados", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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