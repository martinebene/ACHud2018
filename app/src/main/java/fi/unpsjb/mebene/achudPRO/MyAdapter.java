package fi.unpsjb.mebene.achudPRO;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by miguelmorales on 17/4/15.
 */
public class MyAdapter extends BaseExpandableListAdapter {

    private final Context _context;
    private List<String> _grupos, temp_hijos;
    private HashMap<String, List<String>> _datosGrupos;

    public MyAdapter(Context _context, List<String> _grupos, HashMap<String, List<String>> _datosGrupos) {
        this._context = _context;
        this._grupos = _grupos;
        this._datosGrupos = _datosGrupos;
    }

    @Override
    public int getGroupCount() {
        return this._grupos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._datosGrupos.get(this._grupos.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._grupos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._datosGrupos.get(this._grupos.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        ImageView image = null;//Declaramos un nuevo indicador de la flecha desplegable que figurará a la derecha

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fila_grupo, null);
        }
        if (groupPosition == 0) {
            ((ImageView) convertView.findViewById(R.id.image2)).setImageResource(R.mipmap.ic_action_record);
            //((TextView) convertView.findViewById(R.id.text2)).setText(parent.getResources().getString(R.string.s_captura));
        } else if (groupPosition == 1) {
            ((ImageView) convertView.findViewById(R.id.image2)).setImageResource(R.mipmap.ic_action_database);
            //((TextView) convertView.findViewById(R.id.text2)).setText(parent.getResources().getString(R.string.s_datos));
        } else if (groupPosition == 2) {
            ((ImageView) convertView.findViewById(R.id.image2)).setImageResource(R.mipmap.ic_action_settings);
            //((TextView) convertView.findViewById(R.id.text2)).setText("");
        } else if (groupPosition == 3) {
            ((ImageView) convertView.findViewById(R.id.image2)).setImageResource(R.mipmap.ic_action_help);
            //((TextView) convertView.findViewById(R.id.text2)).setText("Descripción grupo 4");
        }

        TextView b = (TextView) convertView.findViewById(R.id.text1);

        int valor = 0;
        //A continuación vamos a asignar el nº de elementos que hay en cada grupo
        valor = this._datosGrupos.get(this._grupos.get(groupPosition)).size();
        b.setTypeface(null, Typeface.BOLD);//Negrita
        //b.setText(headerTitle + " (" + Integer.toString(valor) + ")");//Mostraríamos título + nº elementos en todos los grupos, para ello anular condicional
        //b.setText(headerTitle + " (" + String.valueOf(valor) + ")");

        image = (ImageView) convertView.findViewById(R.id.expandableIcon);
        //Para que todos los grupos muestren el indicador flecha desplegable a la derecha, ver también fila_grupo y anular condicional
        /*int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
        image.setImageResource(imageResourceId);*/

        //Si queremos ocultar algún indicador flecha, utilizar siguiente condición basada en groupPosition
        if ((groupPosition != 1) && (groupPosition != 0)) {
            int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
            image.setImageResource(imageResourceId);
            //Grupos distintos al 1 mostrarían el indicador flecha
            image.setVisibility(View.VISIBLE);
            b.setText(headerTitle); // + " (" + Integer.toString(valor) + ")");//Mostraríamos título + nº elementos en grupos con indicador flecha

        } else {
            //No mostramos el indicador flecha en grupo 1
            image.setVisibility(View.INVISIBLE);
            b.setText(headerTitle);//Título en grupo 1
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fila_hijo_grupo, null);
        }

        //Asignamos imagen de forma individual igual que en el grupo

        switch (groupPosition){
            case 2:
                switch (childPosition){
                    case 0:
                        ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_action_gear);
                        break;
                    case 1:
                        ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_action_bargraph);
                        break;
                }
                break;
            case 3:
                switch (childPosition){
                    case 0:
                        ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_action_book);
                        break;
                    case 1:
                        ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_action_user);
                        break;
                    case 2:
                        ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_action_info);
                        break;
                }
                break;


        }





        /*
        if (childPosition == 0) {
            ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_launcher);
        } else if (childPosition == 1) {
        }
*/
        //Asignamos a modo de ejemplo, la misma imagen a todos los hijos
      /*  int valor = 0;
        valor = this._datosGrupos.get(this._grupos.get(groupPosition)).size();
        for(int x = 0; x < valor; x++){
            ((ImageView) convertView.findViewById(R.id.img_hijo)).setImageResource(R.mipmap.ic_launcher);
        }
        */

        TextView a = (TextView) convertView.findViewById(R.id.lblListItem);
        a.setText(childText);

        ////////////////////////////////////////////////////////////////////////////////////////////??????
        /*if (groupPosition == selectedGroupIndex && childPosition == selectedChildIndex) {
            a.setTextColor(Color.GREEN);
        }else {
            a.setTextColor(Color.BLACK);
        }
        if(isLastChild){
            convertView.setBackgroundResource(R.drawable.list_item_bg_pressed);
        }else{
            convertView.setBackgroundColor(R.drawable.list_item_bg_normal);
        }*/
        ////////////////////////////////////////////////////////////////////////////////////////////??????
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
