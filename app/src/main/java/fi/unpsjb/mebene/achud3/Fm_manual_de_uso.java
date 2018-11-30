package fi.unpsjb.mebene.achud3;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by miguelmorales on 20/4/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Fm_manual_de_uso extends Fragment {

    private EditText eT_manual;
    private StringBuilder text = new StringBuilder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fm_manual_de_uso, container, false);

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();

        eT_manual = (EditText) getView().findViewById(R.id.editText_manual);


        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("Manual.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(),"Error reading manual file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

            eT_manual.setText((CharSequence) text);
    }
}
