package fi.unpsjb.mebene.achud3;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BufferInterpolacion {

    static final int INTERPOLADO = 1;
    static final int NO_INTERPOLADO = 2;
    int ultimaLectura = 0;
    Double ultimoValor = 0.0;
    int lecturaActual = 0;
    Double valorActual = 0.0;
    Double paso = 0.0;

    ArrayList<String[]> buffer = new ArrayList<String[]>();

    public BufferInterpolacion() {
        buffer.clear();
    }


    ArrayList<String[]> add (String[] medicion){

        String[] datos;
        Double velCorregida;
        DecimalFormat df = new DecimalFormat("000.0");

        if(Integer.valueOf(medicion[MedicionDeEntorno.EDA.VEL_REP.ordinal()]) != ultimaLectura) {
            lecturaActual = Integer.valueOf(medicion[MedicionDeEntorno.EDA.VEL_REP.ordinal()]);
            valorActual = Double.valueOf(medicion[MedicionDeEntorno.EDA.VEL.ordinal()]);

            buffer.add(medicion);

            if ((buffer.size() < 25)) {

                if ((buffer.size() > 1) && (ultimoValor != 0.0)) {
                    paso = (valorActual - ultimoValor) / (buffer.size() - 1);
                } else
                    paso = 0.0;


                for (int i = 1; i < (buffer.size() - 1); i++) { //arranco del 2do elemento y freno en el ante ultimo
                    datos = buffer.get(i);
                    velCorregida = Double.valueOf(datos[MedicionDeEntorno.EDA.VEL.ordinal()]) + (paso * i);
                    datos[MedicionDeEntorno.EDA.VEL.ordinal()] = df.format(velCorregida);
                    buffer.set(i, datos);
                }
            } else{
                for (int i = 0; i < (buffer.size()); i++) {  //acomodar para i = 10

                    if (i > 10) {
                        datos = buffer.get(i);
                        datos[MedicionDeEntorno.EDA.VEL.ordinal()] = "---";
                        buffer.set(i, datos);
                    }
                }

            }


            ultimoValor = valorActual;
            ultimaLectura = lecturaActual;
            return buffer;

        }
        buffer.add(medicion);
        return null;
    }

/*
    boolean interpolando (){
        return true;
    }

*/
    String[] get (){
        return buffer.get(0);
    }

    void clear (){
        buffer.clear();
    }
}

