package fi.unpsjb.mebene.achud3;

import java.util.Vector;

/**
 * Created by Martin on 30/05/2017.
 */

public class EsquemaHUD {

    String ext="", header="", intro_sub="", med_sub="", footer="";
    int delay=0;
    long intervaloRef = 0;
    long introTime = 0;
    public Vector<GrafVal> grafValVector;
 /*   boolean isAcelGraf=false;
    int AcelGrafAct = 0;
    int acelMin=0;
    int acelMax=2;

    String positiveChar=">";
    String negativeChar="<";
*/
    public EsquemaHUD() {
        grafValVector= new Vector<GrafVal>();
    }

    public long getIntroTime() {
        return introTime;
    }

    public void setIntroTime(long introTime) {
        this.introTime = introTime;
    }

    public long getIntervaloRef() {
        return intervaloRef;
    }

    public void setIntervaloRef(long intervaloRef) {
        this.intervaloRef = intervaloRef;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getIntro_sub() {
        return intro_sub+"\n";
    }

    public void setIntro_sub(String intro_sub) {
        this.intro_sub = intro_sub;
    }

    public String getMed_sub() {
        return med_sub+"\n";
    }

    public void setMed_sub(String med_sub) {
        this.med_sub = med_sub;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }



/*
    public int getAcelGrafAct() {
        return AcelGrafAct;
    }

    public void setAcelGrafAct(int acelGrafAct) {
        AcelGrafAct = acelGrafAct;
    }

    public boolean isAcelGraf() {
        return isAcelGraf;
    }

    public void setAcelGraf(boolean acelGraf) {
        isAcelGraf = acelGraf;
    }

    public int getAcelMin() {
        return acelMin;
    }

    public void setAcelMin(int acelMin) {
        this.acelMin = acelMin;
    }

    public int getAcelMax() {
        return acelMax;
    }

    public void setAcelMax(int acelMax) {
        this.acelMax = acelMax;
    }

    public String getPositiveChar() {
        return positiveChar;
    }

    public void setPositiveChar(String positiveChar) {
        this.positiveChar = positiveChar;
    }

    public String getNegativeChar() {
        return negativeChar;
    }

    public void setNegativeChar(String negativeChar) {
        this.negativeChar = negativeChar;
    }
    */
}


class GrafVal {
    String outputTagGrafName, inputTagName, pChar, nChar;
    float min, max;
    int NLineGraf;
    boolean isComplete;

        public GrafVal() {
            min = 0;
            max = 0;
            NLineGraf = 0;
            outputTagGrafName=null;
            inputTagName=null;
            pChar=null;
            nChar=null;
            isComplete=false;
    }

    public String getOutputTagGrafName() {
        return outputTagGrafName;
    }

    public void setOutputTagGrafName(String outputTagGrafName) {
        this.outputTagGrafName = outputTagGrafName;
    }

    public String getInputTagName() {
        return inputTagName;
    }

    public void setInputTagName(String inputTagName) {
        this.inputTagName = inputTagName;
    }

    public String getpChar() {
        return pChar;
    }

    public void setpChar(String pChar) {
        this.pChar = pChar;
    }

    public String getnChar() {
        return nChar;
    }

    public void setnChar(String nChar) {
        this.nChar = nChar;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getNLineGraf() {
        return NLineGraf;
    }

    public void setNLineGraf(int NLineGraf) {
        this.NLineGraf = NLineGraf;
    }

    @Override
    public String toString() {
        return "GrafVal{" +
                "outputTagGrafName='" + outputTagGrafName + '\'' +
                ", inputTagName='" + inputTagName + '\'' +
                ", pChar='" + pChar + '\'' +
                ", nChar='" + nChar + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", isComplete=" + isComplete +
                '}';
    }
}