package hn.jepz.www.yugiohlifecounter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class LogActivityFragment extends Fragment {

    public LogActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        JSONArray datosPartidaArray;
        TextView txLogJ1;
        TextView txLogJ2;
        int valorInicial = getActivity().getIntent().getIntExtra("valorInicial",8000);
        String strLogJ1 =  Integer.toString(valorInicial)+ "\n";
        String strLogJ2 = Integer.toString(valorInicial) + "\n";
        String ultimoLogJ1="";
        String ultimoLogJ2="";
        txLogJ1 = (TextView) rootView.findViewById(R.id.txLogJ1);
        txLogJ2 = (TextView) rootView.findViewById(R.id.txLogJ2);
        txLogJ1.setTextSize(30);
        txLogJ2.setTextSize(30);
        try {
            datosPartidaArray = new JSONArray(getActivity().getIntent().getStringExtra("datosPartida"));
            int fin = getActivity().getIntent().getIntExtra("posicionArrayActual", datosPartidaArray.length());

            if (fin == 0) {
                datosPartidaArray.length();
            }
            for (int i = 0; i < fin; i++) {
                JSONObject tmp = (JSONObject) datosPartidaArray.get(i);
                if (tmp.getInt("jugador") == 1) {
                    if (!tmp.getString("operador").equals("t")) {
                        strLogJ1 = strLogJ1 + tmp.getString("valor_nuevo") + "\n";
                    } else {
                        strLogJ1 = strLogJ1 + tmp.getString("operador") + "\n";
                    }
                 ultimoLogJ1 = tmp.getString("valor_nuevo");
                }
                else {
                    if (!tmp.getString("operador").equals("t")) {
                        strLogJ2 = strLogJ2 + tmp.getString("valor_nuevo") + "\n";
                    } else {
                        strLogJ2 = strLogJ2 + tmp.getString("operador") + "\n";
                    }                }
                ultimoLogJ2 = tmp.getString("valor_nuevo");
            }
            SpannableString ssLogJ1 = new SpannableString(strLogJ1);
            SpannableString ssLogJ2 = new SpannableString(strLogJ2);
            if (!strLogJ1.equals(Integer.toString(valorInicial) + "\n")) {
                ssLogJ1.setSpan(new StrikethroughSpan(),0,strLogJ1.length()- ultimoLogJ1.length() -1,0);
            }
            if (!strLogJ2.equals(Integer.toString(valorInicial) + "\n")) {
                ssLogJ2.setSpan(new StrikethroughSpan(), 0, strLogJ2.length() - ultimoLogJ2.length() - 1, 0);
            }
            Log.v("LogActivityFragment", strLogJ1);
            txLogJ1.setText(ssLogJ1);
            txLogJ2.setText(ssLogJ2);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }
}
