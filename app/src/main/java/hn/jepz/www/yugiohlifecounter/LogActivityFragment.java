package hn.jepz.www.yugiohlifecounter;

import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class LogActivityFragment extends Fragment {

    HistoriaAdapter mHistoriaAdapter;
    public LogActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_historia);
        String[] columnas = new String[] {
                "_id", "numeroJuego","ganador","datosPartida", "valor_inicial",
                "posicionArrayActual", "nombrej1", "nombrej2"
        };

        MatrixCursor matrixcursor = new MatrixCursor(columnas);
        getActivity().startManagingCursor(matrixcursor);
        int valorInicial = getActivity().getIntent().getIntExtra("valorInicial",8000);
        int fin;
        String strDuelo = getActivity().getIntent().getStringExtra("duelo");
        try {
            Log.v("Duelo", "" + strDuelo);
            JSONObject joDuelo = new JSONObject(strDuelo);
            JSONArray jaPartida = joDuelo.getJSONArray("partida");
            String nombrej1 = joDuelo.getString("nombrej1");
            String nombrej2 = joDuelo.getString("nombrej2");
            for (int i = 0; i < jaPartida.length(); i++) {
                JSONObject joPartida = (JSONObject) jaPartida.get(i);
                String strDatosPartida = joPartida.getJSONArray("datosPartida").toString();
                int iNumeroJuego = joPartida.getInt("numeroJuego");
                String strGanador = joPartida.getString("ganador");
                fin = -10;
                if (i == jaPartida.length() -1) {
                    fin = getActivity().getIntent().getIntExtra("posicionArrayActual", -2);
                }
                matrixcursor.addRow(new Object[]{
                        i+1, iNumeroJuego, strGanador, strDatosPartida, valorInicial,fin
                        ,nombrej1,nombrej2
                });

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

//        matrixcursor.addRow(new Object[]{
//                "1", "Encabezado 1", "Puntaje de jugador 1", "Puntaje de Jugador 2"
//        });
        getActivity().stopManagingCursor(matrixcursor);
        mHistoriaAdapter = new HistoriaAdapter(getActivity(),matrixcursor,0);

        listView.setAdapter(mHistoriaAdapter);

        return rootView;
    }
}
