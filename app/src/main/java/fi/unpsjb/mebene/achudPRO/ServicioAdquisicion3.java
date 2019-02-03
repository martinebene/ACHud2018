package fi.unpsjb.mebene.achudPRO;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ServicioAdquisicion3 extends Service implements SensorEventListener, IBaseGpsListener {

    static final public String BROADCAST_MEDICION = "com.mebene.ACHud.BROADCAST_MEDICION";
    private MedicionDeEntorno medicion;
    private SensorManager sensorManager;
    AsyncMedicion asyncMedicion;
    public Context lcontext = this;
    SharedPreferences sharedPref;
    public long t0, t1;
    Location ubicacionAnterior;
    //private final IBinder mBinder = new LocalBinder();


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ServicioAdquisicion3() {
    }

    //**********************************************************************************************************************//
    @Override
    public void onCreate() {
        super.onCreate();

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "ACHud")
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("ACHud")
                .setContentText("Capturando Datos...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

        asyncMedicion = new AsyncMedicion();


    }


    //**********************************************************************************************************************//
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        medicion = new MedicionDeEntorno(sharedPref, lcontext);


        iniciarSensores();


        asyncMedicion.execute();
        //return super.onStartCommand(intent, flags, startId);
       // Log.i("tag111", "Servicio adquisicion onStart");
        return START_NOT_STICKY;

    }


    //**********************************************************************************************************************//
    @Override
    public void onDestroy() {
        asyncMedicion.stop();
        //Log.i("tag111", "Servicio adquisicion onDestroy");
        super.onDestroy();

    }


    //**********************************************************************************************************************//
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        //Log.i("tag111", "Servicio adquisicion onTaskRemoved");
        asyncMedicion.cancel(true);
        stopSelf();
    }

    //**********************************************************************************************************************//
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;

    }

    //**********************************************************************************************************************//
    public MedicionDeEntorno getMedicion() {
        return medicion;
    }

    //**********************************************************************************************************************//
    private void iniciarSensores() {


        medicion.cronometro.activo = true;
        ubicacionAnterior = null;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            /*
             * reemplazar por un finish o algo asi
             * */

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);
        medicion.velocidad.disponible = true; //meter en una condicion si gps disponible
        medicion.velocidad.activo = sharedPref.getBoolean("check_box_preference_velocidad",true);;
        medicion.odometro.activo = sharedPref.getBoolean("check_box_preference_odometro",true);;

        List<Sensor> listSensors;


        listSensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(!listSensors.isEmpty()){
            Sensor acelerometerSensor = listSensors.get(0);
            sensorManager.registerListener(this, acelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            medicion.aceleracion.disponible = true;
            medicion.aceleracion.activo = sharedPref.getBoolean("check_box_preference_acelerometro",true);
        }else{
            medicion.aceleracion.disponible = false;
        }


    }

    //**********************************************************************************************************************//
    public static String listarSensores(Context c) {

        String salida="";
        SensorManager lsensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);;

        List<Sensor> listaSensores = lsensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor: listaSensores) {
            salida = salida + sensor.getName() + "\n";
        }
        return salida;
    }

    //**********************************************************************************************************************//
    @Override
    public void onSensorChanged(SensorEvent evento) {

        //Log.i("tag", "entro a sensor changed");


        synchronized (this) {
            switch(evento.sensor.getType()) {
                case Sensor.TYPE_GYROSCOPE:
                    // i("Sen", "Orientación "+i+": "+evento.values[i]);
                    medicion.giro.push(evento.values[0], evento.values[1], evento.values[2]);
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    //Log.i("Sen","Acelerómetro "+i+": "+evento.values[i]);
                    medicion.aceleracion.push(evento.values[0], evento.values[1], evento.values[2]);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    //Log.i("Sen","Magnetismo "+i+": "+evento.values[i]);
                    medicion.campoMagnetico.push(evento.values[0], evento.values[1], evento.values[2]);
                    break;
                default:

            }
            //limpiarConsola();
            //agregarTextoAConsola(medicion.toString2());
        }



    }


    //**********************************************************************************************************************//
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        float offset=0;

        if (location != null)
        {
            this.updateSpeed(location);
            if (ubicacionAnterior == null){
                ubicacionAnterior=location;
            }
            offset=location.distanceTo(ubicacionAnterior);
            //Log.e("odo", "dit offset = "+offset);

            if (offset>10){
                medicion.odometro.addOdometro(offset);
                ubicacionAnterior = location;
            }

        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    //**********************************************************************************************************************//
    public void updateSpeed(Location location)
    {
        double nCurrentSpeed = 0;

        if( location!=null )
        {
            nCurrentSpeed = location.getSpeed();
        }
        //String strUnits = "Km/h";
        medicion.velocidad.setVelocidad(nCurrentSpeed);
        medicion.velocidad.resetRepeticion();

    }


/*
    public String getTimeFormatedFromMillis(long millis, String formato)
    {
        TimeZone tz = TimeZone.getDefault();

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        final SimpleDateFormat sdfParser = new SimpleDateFormat(formato);
        sdfParser.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfParser.format(cal.getTime());
    }
*/

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
  /*  public class LocalBinder extends Binder {
        ServicioAdquisicion2 getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServicioAdquisicion2.this;
        }
    }
*/


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class AsyncMedicion extends AsyncTask<Object, Object, Object> {

        public boolean running=false;
        OutputStreamWriter fout;
        String filename_out=null;
        File file_out;
        BufferInterpolacion bufferInterpolacion = new BufferInterpolacion();
        ArrayList<String[]> bufferInterpolado = new ArrayList<String[]>();

        @Override
        protected void onPreExecute() {
            t0=t1=0;
            SimpleDateFormat formateador = new SimpleDateFormat("dd_MM_yy_HH_mm_ss");
            filename_out = "ACHUD_Out_" + formateador.format(new Date())  +".csv";
            try {

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File ruta_sd = Environment.getExternalStorageDirectory();
                    File ruta_datos_dir = new File(ruta_sd.getAbsolutePath(), getResources().getString(R.string.app_name)+ File.separator+ getResources().getString(R.string.s_datos_dir));
                    file_out = new File(ruta_datos_dir.getAbsolutePath(),filename_out);
                    fout = new OutputStreamWriter(new FileOutputStream(file_out));
                } else{
                    this.cancel(true);
                    stopSelf();
 //                   Log.e("tag23", "no disponible alamacenamiento externo");
                }
            }
            catch (Exception ex){
            //    Log.e("Ficheros", "Error al escribir fichero a memoria interna");
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            String msg;

            if (isCancelled()){
                return null;
            }
            running = true;
            medicion.cronometro.iniciar();

        //    Log.i("tag111", "AsyncMedicion iniciando");
            while (running){
                try {
                    Thread.sleep(100);
                    publishProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                    running=false;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate (Object... params) {



            //String arrayDatos[] = medicion.getIntervaloMedicion();
            String arrayDatos[];
            //String[] bufferDatos[] = null;
            //Aca armar buffer e interpolar la vel

            if ((bufferInterpolado = bufferInterpolacion.add(medicion.getIntervaloMedicion())) != null){

                for (int n = 0; n < (bufferInterpolado.size()); n++){
                    arrayDatos = bufferInterpolado.get(n);
                    String linea = "";
                    for (int i = 0; i < arrayDatos.length; i++) {
                        linea = linea + arrayDatos[i];
                        if ((i - 1) < arrayDatos.length)
                            linea = linea + ",";
                    }
                    linea = linea + "\n";

               //     Log.i("tag4444", "Linea Nueva: " + linea);

                    try {
                        fout.write(linea);
                    } catch (IOException e) {
                //        Log.e("Ficheros", "Error al escribir fichero a memoria interna linea");
                        e.printStackTrace();
                    }
                }
                bufferInterpolacion.clear();
            } else {

            }

            Intent intent = new Intent(BROADCAST_MEDICION);
            intent.putExtra("medicion", medicion.toDisplay());
            intent.putExtra("crono", medicion.cronometro.getT0());
            LocalBroadcastManager.getInstance(lcontext).sendBroadcast(intent);
/*
            if (medicion.cronometro.getT0() > 300000){
                this.stop();
            }
*/
        }


        @Override
        protected void onPostExecute(Object o) {
       //     Log.i("tag111", "AsyncMedicion onPostExecute");

            Intent intent = new Intent(BROADCAST_MEDICION);
            if(!(file_out.exists()))
                intent.putExtra("medicion", "Error al crear el archivo de salida de medicion");
            else
                intent.putExtra("medicion", medicion.toDisplayFinal() + "Archivo de datos " + filename_out + " creado con exito.");

            LocalBroadcastManager.getInstance(lcontext).sendBroadcast(intent);

            try {
                fout.flush();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }




            running=false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
       //     Log.i("tag111", "AsyncMedicion onCancelled");
            running=false;
        }

        public boolean isRunning(){
            return running;
        }

        public void stop(){
            this.running=false;
        }

    }

}