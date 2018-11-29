package fi.unpsjb.mebene.achud3;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Martin on 01/07/2015.
 */

public class MedicionDeEntorno {

    int nroDeMedicion = 0;
    long t0 = 0, t1 = 0;
    Calendar fechaYhora;
    long cantMediciiones;
    Aceleracion aceleracion;
    Giro giro;
    CampoMagnetico campoMagnetico;
    Clima clima;
    Cronometro cronometro;
    Velocidad velocidad;
    Odometro odometro;
    SharedPreferences sharedPref;
    public static final int KMH = 0, MPH = 1, MS = 2, MKM = 3, MM = 4, KM=5, MILL=6, M=7;
    Context l_context;
    public String[] ultimaMedicion=null;

    //Estructura de datos de archivo
    public enum EDA {
        T0_SSS_ABS,
        T0_HH_MED,
        T0_mm_MED,
        T0_ss_MED,
        T0_SSS_MED,
        T1_HH_MED,
        T1_mm_MED,
        T1_ss_MED,
        T1_SSS_MED,
        NRO_MED,
        CR_HH_MED,
        CR_mm_MED,
        CR_ss_MED,
        CR_SSS_MED,
        VEL,
        VEL_MAX,
        VEL_UNI,
        VEL_REP,
        ODO,
        ODO_UNI,
        ALT,
        ACEL_X,
        ACEL_MA_AB_X,
        ACEL_MA_X,
        ACEL_MI_X,
        ACEL_Y,
        ACEL_MA_AB_Y,
        ACEL_MA_Y,
        ACEL_MI_Y,
        ACEL_Z,
        ACEL_MA_AB_Z,
        ACEL_MA_Z,
        ACEL_MI_Z
    }

    public enum EDAG {
        ACEL_GR_X1,
        ACEL_GR_X2,
        ACEL_GR_X3,
        ACEL_GR_X4,
        ACEL_GR_Y1,
        ACEL_GR_Y2,
        ACEL_GR_Y3,
        ACEL_GR_Y4,
        ACEL_GR_Z1,
        ACEL_GR_Z2,
        ACEL_GR_Z3,
        ACEL_GR_Z4,
    }

    //**********************************************************************************************************************//
    public MedicionDeEntorno(SharedPreferences l_sharedPref, Context context) {

        sharedPref = l_sharedPref;
        l_context = context;
        cantMediciiones = 0;

//        Log.i("Tag444", "Shared: " +  sharedPref.getString("list_preference_unidades", String.valueOf(KMH)));

        velocidad = new Velocidad(l_context, false, false, Integer.parseInt(sharedPref.getString("list_preference_unidades", String.valueOf(KMH))));
        odometro = new Odometro(l_context, false, false, Integer.parseInt(sharedPref.getString("list_preference_unidades", String.valueOf(KM))));
       //  velocidad = new Velocidad(l_context, false, false, 0);
        aceleracion = new Aceleracion(false, false);
        giro = new Giro(false, false);
        campoMagnetico = new CampoMagnetico(false, false);
        clima = new Clima(false, false);
        cronometro = new Cronometro(false, false);
    }

    @Override
    public String toString() {
        return toDisplay();
    }


    public String toDisplay() {
        String[] datos = ultimaMedicion;
        String salida = "";


        if (cronometro.activo) {
            salida = salida +
                    "Cron.: "+datos[EDA.CR_HH_MED.ordinal()]+":"+datos[EDA.CR_mm_MED.ordinal()]+":"+datos[EDA.CR_ss_MED.ordinal()]+","+datos[EDA.CR_SSS_MED.ordinal()]+"\n\n";
        };

        if (odometro.activo) {
            salida = salida +
                    "Recorrido: "+datos[EDA.ODO.ordinal()]+" "+datos[EDA.ODO_UNI.ordinal()]+ "\n\n";
        };

        if (aceleracion.activo) {
            salida = salida +
                    "Acel. X:\t" + datos[EDA.ACEL_X.ordinal()] + "\tmax.:" + datos[EDA.ACEL_MA_AB_X.ordinal()] + "\n" +
                    "Acel. Y:\t" + datos[EDA.ACEL_Y.ordinal()] + "\tmax.:" + datos[EDA.ACEL_MA_AB_Y.ordinal()] + "\n" +
                    "Acel. Z:\t" + datos[EDA.ACEL_Z.ordinal()] + "\tmax.:" + datos[EDA.ACEL_MA_AB_Z.ordinal()] + "\n\n";
        };

        if (velocidad.activo) {
            salida = salida +
                    "Vel.:\t"+datos[EDA.VEL.ordinal()]+" "+datos[EDA.VEL_UNI.ordinal()]+ "\n" +
                    "Vel. max.:\t"+datos[EDA.VEL_MAX.ordinal()]+" "+datos[EDA.VEL_UNI.ordinal()]+ "\n\n";
        }

        if (giro.activo) salida = salida + giro + "\n";
        if (campoMagnetico.activo) salida = salida + campoMagnetico + "\n";

        return salida;
    }

    public String toDisplayFinal() {
        String[] datos = ultimaMedicion;
        String salida = "";

        salida = salida +
                "Se registraron "+datos[EDA.NRO_MED.ordinal()]+" lecturas."+"\n\n";

        if (cronometro.activo) {
            salida = salida +
                    "Tiempo de medicion: "+datos[EDA.CR_HH_MED.ordinal()]+":"+datos[EDA.CR_mm_MED.ordinal()]+":"+datos[EDA.CR_ss_MED.ordinal()]+","+datos[EDA.CR_SSS_MED.ordinal()]+"\n\n";
        };

        if (odometro.activo) {
            salida = salida +
                    "Recorrido: "+datos[EDA.ODO.ordinal()]+" "+datos[EDA.ODO_UNI.ordinal()]+ "\n\n";
        };

        if (aceleracion.activo) {
            salida = salida +
                    "Aceleraciones Maximas:\n"+
                    "X:\t" + datos[EDA.ACEL_MA_AB_X.ordinal()] + "\n" +
                    "Y:\t" + datos[EDA.ACEL_MA_AB_Y.ordinal()] + "\n" +
                    "Z:\t" + datos[EDA.ACEL_MA_AB_Z.ordinal()] + "\n\n";
        };

        if (velocidad.activo) {
            salida = salida +
                    "Vel. max.:\t"+datos[EDA.VEL_MAX.ordinal()]+" "+datos[EDA.VEL_UNI.ordinal()]+ "\n\n";
        }

        //if (giro.activo) salida = salida + giro + "\n";
        //if (campoMagnetico.activo) salida = salida + campoMagnetico + "\n";

        return salida;
    }

    public String[] getIntervaloMedicion() {

        if(t1>0)
            t0 = t1 ;//+ 1;

        t1 = cronometro.getT0();
        nroDeMedicion++;

        //Log.i("tag555", "Los Ts: " + t0 +" -> "+ t1);
        //Log.i("tag555", "Los Ts: " + getTimeFormatedFromMillis(t0,"SSS") +" -> "+ getTimeFormatedFromMillis(t1,"SSS"));

        String salida[] = new String[EDA.values().length];
        for (int i = 0; i < salida.length; i++) {
            salida[i] = "_";
        }

        salida[EDA.NRO_MED.ordinal()] = String.valueOf(nroDeMedicion);

        salida[EDA.T0_SSS_ABS.ordinal()] = String.valueOf(t0);

        salida[EDA.T0_HH_MED.ordinal()] = getTimeFormatedFromMillis(t0,"HH");
        salida[EDA.T0_mm_MED.ordinal()] = getTimeFormatedFromMillis(t0,"mm");
        salida[EDA.T0_ss_MED.ordinal()] = getTimeFormatedFromMillis(t0,"ss");
        salida[EDA.T0_SSS_MED.ordinal()] = getTimeFormatedFromMillis(t0,"SSS");

        salida[EDA.CR_HH_MED.ordinal()] = getTimeFormatedFromMillis(t0,"HH");
        salida[EDA.CR_mm_MED.ordinal()] = getTimeFormatedFromMillis(t0,"mm");
        salida[EDA.CR_ss_MED.ordinal()] = getTimeFormatedFromMillis(t0,"ss");
        salida[EDA.CR_SSS_MED.ordinal()] = getTimeFormatedFromMillis(t0,"SSS");

        salida[EDA.T1_HH_MED.ordinal()] = getTimeFormatedFromMillis(t1,"HH");
        salida[EDA.T1_mm_MED.ordinal()] = getTimeFormatedFromMillis(t1,"mm");
        salida[EDA.T1_ss_MED.ordinal()] = getTimeFormatedFromMillis(t1,"ss");
        salida[EDA.T1_SSS_MED.ordinal()] = getTimeFormatedFromMillis(t1,"SSS");

        if (velocidad.activo) {
            salida[EDA.VEL.ordinal()] = velocidad.getVel();
            salida[EDA.VEL_MAX.ordinal()] = velocidad.getVelMax();
            salida[EDA.VEL_UNI.ordinal()] = velocidad.getUnidad();
            salida[EDA.VEL_REP.ordinal()] = Integer.toString(velocidad.getRepeticion());
        }

        if (odometro.activo) {
            salida[EDA.ODO.ordinal()] = odometro.getOdo();
            salida[EDA.ODO_UNI.ordinal()] = odometro.getUnidad();
        }

        if (aceleracion.activo) {
            salida[EDA.ACEL_X.ordinal()] = aceleracion.getX();
            salida[EDA.ACEL_MA_X.ordinal()] = aceleracion.getMaxX();
            salida[EDA.ACEL_MI_X.ordinal()] = aceleracion.getMinX();
            salida[EDA.ACEL_MA_AB_X.ordinal()] = aceleracion.getMaxAbX();

            salida[EDA.ACEL_Y.ordinal()] = aceleracion.getY();
            salida[EDA.ACEL_MA_Y.ordinal()] = aceleracion.getMaxY();
            salida[EDA.ACEL_MI_Y.ordinal()] = aceleracion.getMinY();
            salida[EDA.ACEL_MA_AB_Y.ordinal()] = aceleracion.getMaxAbY();

            salida[EDA.ACEL_Z.ordinal()] = aceleracion.getZ();
            salida[EDA.ACEL_MA_Z.ordinal()] = aceleracion.getMaxZ();
            salida[EDA.ACEL_MI_Z.ordinal()] = aceleracion.getMinZ();
            salida[EDA.ACEL_MA_AB_Z.ordinal()] = aceleracion.getMaxAbZ();
        }

        Log.i("tag555", "Los Ts: " + salida[EDA.T0_SSS_MED.ordinal()] +" -> "+ salida[EDA.T1_SSS_MED.ordinal()]);

        ultimaMedicion = salida;
        return ultimaMedicion;
    }


    public String getTimeFormatedFromMillis(long millis, String formato) {
        //TimeZone tz = TimeZone.getDefault();

        //reemplazar por algo mas matematico
/*
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        final SimpleDateFormat sdfParser = new SimpleDateFormat(formato);
        sdfParser.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfParser.format(cal.getTime());*/


/*
        String formatted = String.format("%02d:%02d:%02d,%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                (millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)))/10;
*/

        String salida="";

        switch (formato){

            case ("HH"):
                salida= String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toHours(millis));
                break;
            case ("mm"):
                salida= String.format(Locale.getDefault(),"%02d", TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
                break;
            case ("ss"):
                salida= String.format(Locale.getDefault(),"%02d", TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                break;
            case ("SSS"):
                salida= String.format(Locale.getDefault(),"%03d", (millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))));
                break;
            default:
                salida= String.format(Locale.getDefault(),"%03d", millis);
                break;
        }



        return salida;
    }

}

//**********************************************************************************************************************//
class Aceleracion {

    public long timestamp;
    public boolean activo, disponible;

    float x,y,z,ax,ay,az,maxX,maxY,maxZ,minX,minY,minZ;
    float [] gravity;
    final float alpha = 0.8f;
    final int delayMax = 30;
    int i;
    DecimalFormat df;

    Aceleracion(boolean ldisponible, boolean lactivo) {
        activo = lactivo;
        disponible = ldisponible;
        x=y=z=ax=ay=az=maxX=maxY=maxZ=minX=minY=minZ=0;
        i=0;
        gravity = new float[3];
        gravity[0] = 0f;
        gravity[1] = 0f;
        gravity[2] = 0f;

        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
    }

    public void push(float lx, float ly, float lz){
        ax=x;
        ay=y;
        az=z;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * lx;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * ly;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * lz;

        x = lx - gravity[0];
        y = ly - gravity[1];
        z = lz - gravity[2];

        if(i>delayMax) {
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
        }
        i++;
    }

    public String getX() {
        if (x>=0) return "+"+df.format(x);
        else return df.format(x);
    }

    public String getY() {
        if (y>=0) return "+"+df.format(y);
        else return df.format(y);
    }

    public String getZ() {
        if (z>=0) return "+"+df.format(z);
        else return df.format(z);
    }

    public String getMaxX() {
        return "+"+df.format(maxX);
    }

    public String getMaxY() {
        return "+"+df.format(maxY);
    }

    public String getMaxZ() {
        return "+"+df.format(maxZ);
    }

    public String getMinX() {
        return df.format(minX);
    }

    public String getMinY() {
        return df.format(minY);
    }

    public String getMinZ() {
        return df.format(minZ);
    }

    public String getMaxAbX() {
        if(Math.abs(maxX)>= Math.abs(minX))return  "+"+df.format(maxX);
        else return  df.format(minX);
    }

    public String getMaxAbY() {
        if(Math.abs(maxY)>= Math.abs(minY))return  "+"+df.format(maxY);
        else return  df.format(minY);
    }

    public String getMaxAbZ() {
        if(Math.abs(maxZ)>= Math.abs(minZ))return  "+"+df.format(maxZ);
        else return  df.format(minZ);
    }

    @Override
    public String toString() {
        return "Aceleracion:\n" +
                "x=" + getX() + ", Max: "+ getMaxAbX() +"\n"+
                "y=" + getY() + ", Max: "+ getMaxAbZ() +"\n"+
                "z=" + getZ() + ", Max: "+ getMaxAbZ() +"\n";
    }

}

//**********************************************************************************************************************//
class Giro {

    public long timestamp;
    public boolean activo, disponible;

    float x,y,z,ax,ay,az,maxX,maxY,maxZ,minX,minY,minZ;

    Giro(boolean ldisponible, boolean lactivo) {
        activo = lactivo;
        disponible = ldisponible;
        x=y=z=ax=ay=az=maxX=maxY=maxZ=minX=minY=minZ=0;
    }

    public void push(float lx, float ly, float lz){
        ax=x;
        ay=y;
        az=z;
        x=lx;
        y=ly;
        z=lz;
        if(x>maxX) maxX=x;
        if(y>maxY) maxY=y;
        if(z>maxZ) maxZ=z;
        if(x<minX) minX=x;
        if(y<minY) minY=y;
        if(z<minZ) minZ=z;
    }

    @Override
    public String toString() {
        return "Giro:\n" +
                "x=" + String.format("%.2f", x) + ", "+ String.format("%.2f", maxX) + ", "+ String.format("%.2f", minX) +"\n"+
                "y=" + String.format("%.2f", y) + ", "+ String.format("%.2f", maxY) +", "+ String.format("%.2f", minY) +"\n"+
                "z=" + String.format("%.2f", z) + ", ="+ String.format("%.2f", maxZ) + ", "+ String.format("%.2f", minZ) +"\n";
    }
}

//**********************************************************************************************************************//
class CampoMagnetico {

    public long timestamp;
    public boolean activo, disponible;

    float x,y,z,ax,ay,az,maxX,maxY,maxZ,minX,minY,minZ;

    CampoMagnetico(boolean ldisponible, boolean lactivo) {
        activo = lactivo;
        disponible = ldisponible;
        x=y=z=ax=ay=az=maxX=maxY=maxZ=minX=minY=minZ=0;
    }

    public void push(float lx, float ly, float lz){
        ax=x;
        ay=y;
        az=z;
        x=lx;
        y=ly;
        z=lz;
        if(x>maxX) maxX=x;
        if(y>maxY) maxY=y;
        if(z>maxZ) maxZ=z;
        if(x<minX) minX=x;
        if(y<minY) minY=y;
        if(z<minZ) minZ=z;
    }

    @Override
    public String toString() {
        return "CampoMag:\n" +
                "x=" + String.format("%.2f", x) + ", "+ String.format("%.2f", maxX) + ", "+ String.format("%.2f", minX) +"\n"+
                "y=" + String.format("%.2f", y) + ", "+ String.format("%.2f", maxY) +", "+ String.format("%.2f", minY) +"\n"+
                "z=" + String.format("%.2f", z) + ", ="+ String.format("%.2f", maxZ) + ", "+ String.format("%.2f", minZ) +"\n";
    }
}

//**********************************************************************************************************************//
class Clima {

    public long timestamp;
    public boolean activo, disponible;

    String temp, presion, humedad, intensidadViento, direViento, locacion;

    Clima(boolean ldisponible, boolean lactivo) {
        activo = lactivo;
        disponible = ldisponible;
        temp = presion = humedad = intensidadViento = direViento = locacion = "";
    }


    @Override
    public String toString() {
        return "Clima{" +
                "temp='" + temp + '\'' +
                ", presion='" + presion + '\'' +
                ", humedad='" + humedad + '\'' +
                ", intensidadViento='" + intensidadViento + '\'' +
                ", direViento='" + direViento + '\'' +
                ", locacion='" + locacion + '\'' +
                '}';
    }
}

//**********************************************************************************************************************//
class Cronometro {

    public boolean activo, disponible;
    public long tInicial, t0;

    Cronometro(boolean ldisponible, boolean lactivo) {
        activo = lactivo;
        disponible = ldisponible;
        tInicial = t0 =0;
    }

    void iniciar(){
        tInicial = System.currentTimeMillis();
    }

    long getT0() {
        t0 = System.currentTimeMillis()- tInicial;
        return t0;
    }


    @Override
    public String toString() {

       // Date date = new Date(this.getTranscurrido());
       //return date.toString();

        //SimpleDateFormat df= new SimpleDateFormat("hh:mm:ss");
        //String formatted = df.format(date );
        long millis = this.getT0();

/*
        long time = end - init;
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final SimpleDateFormat sdfParser = new SimpleDateFormat("HH:mm:ss", new Locale("ES"));
        String sTime = sdfParser.format(cal.getTime());

        String formatted = String.format("%02d:%02d:%02d,%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                (millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)))/10

        );
        return formatted;*/
        return Long.toString(millis);

    }

    public String toString(String formato) {

        // Date date = new Date(this.getTranscurrido());
        //return date.toString();

        //SimpleDateFormat df= new SimpleDateFormat("hh:mm:ss");
        //String formatted = df.format(date );
        long millis = this.getT0();
        TimeZone tz = TimeZone.getDefault();

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        final SimpleDateFormat sdfParser = new SimpleDateFormat(formato);
        sdfParser.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdfParser.format(cal.getTime());

    }
}


//**********************************************************************************************************************//
class Velocidad {

    public boolean activo, disponible;
    public double velocidad, velocidadMaxima, velocidadPromedio;
    int unidad;
    int repeticion=0;
    public String strUnidad;

    Velocidad(Context l_context, boolean ldisponible, boolean lactivo, int l_Unidad) {
        activo = lactivo;
        disponible = ldisponible;
        velocidad = velocidadMaxima = velocidadPromedio = 0;
        unidad = l_Unidad;
        String[] unidadesDeVelocidad = l_context.getResources().getStringArray(R.array.entradasUnidadesDeVelocidad);
        strUnidad = unidadesDeVelocidad[unidad];
            }


    public void setVelocidad (double velMedida) {

        Log.i("Aviso2", "Entre en set vel con velmedida: " + velMedida );

            switch (unidad) {
                case MedicionDeEntorno.KMH:
                    velocidad = velMedida * 3.6;
                    Log.e("Aviso2", "Entre en kmh: " + unidad + " y str: " + strUnidad);break;
                case MedicionDeEntorno.MPH:
                    velocidad = velMedida * 2.23694; Log.e("Aviso2", "Entre en mph: " + unidad + " y str: " + strUnidad);break;
                case MedicionDeEntorno.MS:
                    velocidad = velMedida; Log.e("Aviso2", "Entre en m/s: " + unidad + " y str: " + strUnidad);break;
                case MedicionDeEntorno.MKM:
                    velocidad = (3600/(velMedida * 3.6))/60; Log.e("Aviso2", "Entre en min/km: " + unidad + " y str: " + strUnidad);break;
                case MedicionDeEntorno.MM:
                    velocidad = (3600/(velMedida * 2.23694))/60; Log.e("Aviso2", "Entre en min/milla: " + unidad + " y str: " + strUnidad);break;
            }

        if (velocidad > velocidadMaxima)
            velocidadMaxima=velocidad;
    }

    public String getVel() {
        /*Formatter fmt = new Formatter(new StringBuilder());
        fmt.format("%4.1f", velocidad);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        return strCurrentSpeed;*/
        DecimalFormat df = new DecimalFormat("000.0");
        return df.format(velocidad);
    }

    public String getVelMax() {
        /*Formatter fmt = new Formatter(new StringBuilder());
        fmt.format("%4.1f", velocidadMaxima);
        String strMaxSpeed = fmt.toString();
        strMaxSpeed = strMaxSpeed.replace(' ', '0');

        return strMaxSpeed;*/
        DecimalFormat df = new DecimalFormat("000.0");
        return df.format(velocidadMaxima);
    }

    public int getRepeticion() {
        return repeticion;
    }

    public void resetRepeticion() {
        this.repeticion++;
    }

    @Override
    public String toString() {
        return this.getVel();
    }

    public String getUnidad() {
        return strUnidad;
    }
}

//**********************************************************************************************************************//
class Odometro {

    public boolean activo, disponible;
    public double recorrido;
    int unidad;
    public String strUnidad;

    Odometro(Context l_context, boolean ldisponible, boolean lactivo, int l_Unidad) {
        activo = lactivo;
        disponible = ldisponible;
        unidad = l_Unidad;
        String[] unidadesDeDistancia = l_context.getResources().getStringArray(R.array.entradasUnidadesDeDistancia);
        strUnidad = unidadesDeDistancia[unidad];
        recorrido=0;
    }


    public void addOdometro (float offset) {

        Log.i("Aviso2", "Entre en addOdo con recorrido: " + offset );

        switch (unidad) {
            case MedicionDeEntorno.KM:
                recorrido = recorrido + (offset / 1000 );
                Log.e("Aviso2", "Entre en km: " + unidad + " y str: " + strUnidad);break;
            case MedicionDeEntorno.MILL:
                recorrido = recorrido + (offset / 1609.344 );
                Log.e("Aviso2", "Entre en mp: " + unidad + " y str: " + strUnidad);break;
            case MedicionDeEntorno.M:
                recorrido = recorrido + offset;
                Log.e("Aviso2", "Entre en m: " + unidad + " y str: " + strUnidad);break;

        }

    }

    public String getOdo() {
        /*Formatter fmt = new Formatter(new StringBuilder());
        fmt.format("%4.1f", velocidad);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        return strCurrentSpeed;*/
        DecimalFormat df = new DecimalFormat("000.0");
        return df.format(recorrido);
    }



    @Override
    public String toString() {
        return this.getOdo();
    }

    public String getUnidad() {
        return strUnidad;
    }
}