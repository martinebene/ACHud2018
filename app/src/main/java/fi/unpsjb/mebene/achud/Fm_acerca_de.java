package fi.unpsjb.mebene.achud;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by miguelmorales on 20/4/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Fm_acerca_de extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fm_acerca_de, container, false);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        String version = "_";
        int code = 0;
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
            code = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        EditText editText = (EditText) getView().findViewById(R.id.editText_acerca_de);
        editText.setText("AcHud (Action Cam Head Up Display)\nVersion:"+version+" BETA Code:"+code+"\nSoporte: achud.app@gmail.com");
    }
}
