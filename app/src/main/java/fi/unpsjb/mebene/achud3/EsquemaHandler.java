package fi.unpsjb.mebene.achud3;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Martin on 30/05/2017.
 */

public class EsquemaHandler extends DefaultHandler {

    private EsquemaHUD esquemaHUD;
    private StringBuilder sbTexto;
    private String etiquetaActual;

    GrafVal grafVal=null;
    private boolean inGrafVal=false;



    public EsquemaHUD getEsquema(){
        return esquemaHUD;
    }


    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        super.characters(ch, start, length);

        if (etiquetaActual != null)
            sbTexto.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        super.endElement(uri, localName, name);
        Log.i("Tag777", "EndElement: " + localName);
        if (etiquetaActual != null) {

            String ext="", header="", intro_sub="", med_sub="";
            int delay=0;

            if (localName.equals("Extension")) {
                esquemaHUD.setExt(sbTexto.toString());
            } else if (localName.equals("Delay")) {
                esquemaHUD.setDelay(Integer.valueOf(sbTexto.toString()));
            } else if (localName.equals("IntervaloRef")) {
     //           Log.i("tag4444", "IntRef: " + sbTexto.toString());
       //         Log.i("tag4444", "IntRef: " + Long.valueOf(sbTexto.toString()));
                esquemaHUD.setIntervaloRef(Long.valueOf(sbTexto.toString()));
            } else if (localName.equals("Header")) {
                esquemaHUD.setHeader(sbTexto.toString());
            } else if (localName.equals("IntroSub")) {
                esquemaHUD.setIntro_sub(sbTexto.toString());
            } else if (localName.equals("MedSub")) {
                esquemaHUD.setMed_sub(sbTexto.toString());
            } else if (localName.equals("Footer")) {
                esquemaHUD.setFooter(sbTexto.toString());
            }


            else if (localName.equals("GrafVal")) {



                Log.i("Tag777", "en fin de grafval 1" + grafVal.toString());
/*
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
*/
                if((grafVal.getInputTagName()!=null)&&(grafVal.getOutputTagGrafName()!=null)&&(!(grafVal.getMax()<0)) && (!(grafVal.getMin()<0)) &&  (grafVal.getpChar()!=null) && (grafVal.getnChar()!=null) ){
                    Log.i("Tag777", "en fin de grafval");
  /*                  try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
*/
                    grafVal.setComplete(true);
                    esquemaHUD.grafValVector.add(grafVal);
                }
                inGrafVal = false;
            }


              else if((localName.equals("InputTag")) && inGrafVal && (grafVal!=null)){
                grafVal.setInputTagName(sbTexto.toString());
            } else if((localName.equals("OutputTag")) && inGrafVal && (grafVal!=null)){
                grafVal.setOutputTagGrafName(sbTexto.toString());
            } else if((localName.equals("MaxVal")) && inGrafVal && (grafVal!=null)){
                grafVal.setMax(Float.valueOf(sbTexto.toString()));
            } else if((localName.equals("MinVal")) && inGrafVal && (grafVal!=null)){
                grafVal.setMin(Float.valueOf(sbTexto.toString()));
            } else if((localName.equals("NLineGraf")) && inGrafVal && (grafVal!=null)){
                grafVal.setNLineGraf(Integer.valueOf(sbTexto.toString()));
            } else if((localName.equals("PChar")) && inGrafVal && (grafVal!=null)){
                grafVal.setpChar(sbTexto.toString());
            } else if((localName.equals("NChar")) && inGrafVal && (grafVal!=null)){
                grafVal.setnChar(sbTexto.toString());
            }

            sbTexto.setLength(0);
        }
    }

    @Override
    public void startDocument() throws SAXException {

        super.startDocument();
        Log.i("Tag777", "StartDocument");
        esquemaHUD = new EsquemaHUD();
        sbTexto = new StringBuilder();
        sbTexto.setLength(0);
        etiquetaActual = "";
    }

    @Override
    public void startElement(String uri, String localName,
                             String name, Attributes attributes) throws SAXException {

        super.startElement(uri, localName, name, attributes);

        Log.i("Tag777", "StartElement: " + localName);

        if (localName.equals("IntroSub") && attributes.getLength()==1 && attributes.getQName(0).equals("Time"))
            esquemaHUD.setIntroTime(Long.valueOf(attributes.getValue(0)));

        if (localName.equals("GrafVal")){
            inGrafVal = true;
            grafVal = new GrafVal();
            grafVal.setComplete(false);
        }

        etiquetaActual = localName;
        sbTexto.setLength(0);
    }
}

/*
        if (localName.equals("AcelGraf") && attributes.getLength()==4){
            esquemaHUD.setAcelGraf(true);
            for(int i = 0;i<4;i++){
                try {
                    switch (attributes.getQName(i)) {
                        case "Max":
                            esquemaHUD.setAcelMax(Integer.valueOf(attributes.getQName(i)));
                            break;
                        case "Min":
                            esquemaHUD.setAcelMin(Integer.valueOf(attributes.getQName(i)));
                            break;
                        case "PChar":
                            esquemaHUD.setPositiveChar(attributes.getQName(i));
                            break;
                        case "NChar":
                            esquemaHUD.setNegativeChar(attributes.getQName(i));
                            break;
                        default:
                            esquemaHUD.setAcelGraf(false);
                            break;
                    }
                }catch (Exception e){
                    esquemaHUD.setAcelGraf(false);
                    Log.e("tag23", "Etiqueta mal formada, exception: " + e);
                }
            }
        }
 */