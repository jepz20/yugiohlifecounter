package hn.jepz.www.yugiohlifecounter;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.util.ArrayList;

/**
 * Created by jepz2_000 on 9/26/2015.
 */
public class HistoriaAdapter extends android.support.v4.widget.CursorAdapter {

    public HistoriaAdapter(Context context, Cursor c, int flags) {
        super(context,c,flags);
    };

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_historia, parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int c1=0, c2 = 0;
        int iNumeroJuego = cursor.getInt(1);
        String strGanador = cursor.getString(2);
        String strdatosPartida = cursor.getString(3);
        int valorInicial = cursor.getInt(4);
        int fin = cursor.getInt(5);
        ArrayList<String> alJ1, alJ2;
        String strLogJ1;
        String strLogJ2;
        String ultimoLogJ1="";
        String ultimoLogJ2="";
        String strEncabezadoJ1, strEncabezadoJ2, strLogTempVN,strLogTempVO,
                strTagMenosInicio, strTagMenosFin, strTagMasInicio, strTagMasFin,
                strTagPuntajeInicioJ1, strTagPuntajeFinJ1,strTagPuntajeInicioJ2,
                strTagPuntajeFinJ2;
        String strNumeroDuelo = context.getString(R.string.texto_numero_duelo_log) + " #" + iNumeroJuego;
        TextView tvLogJ1 = (TextView) view.findViewById(R.id.tvLogJ1);
        TextView tvLogJ2 = (TextView) view.findViewById(R.id.tvLogJ2);
        TextView tvEncabezadoJ1 = (TextView) view.findViewById(R.id.tvEncabezadoJ1);
        TextView tvEncabezadoJ2 = (TextView) view.findViewById(R.id.tvEncabezadoJ2);
        TextView tvNumeroJuegoLog = (TextView) view.findViewById(R.id.tvNumeroJuegoLog);
        strEncabezadoJ1 = context.getString(R.string.texto_nombre_jugador_defecto) + "1";
        strEncabezadoJ2 = context.getString(R.string.texto_nombre_jugador_defecto) + "2";
        MyHtmlTagHandler myHtmlHandler = new MyHtmlTagHandler();
        tvNumeroJuegoLog.setText(strNumeroDuelo);
        tvEncabezadoJ1.setText(strEncabezadoJ1);
        tvEncabezadoJ2.setText(strEncabezadoJ2);
        strTagMenosInicio = "<small><font color= '#9a0707'>";
        strTagMenosFin = "</font></small><br/>";
        strTagMasInicio = "<small><font color= '#226f45'>";
        strTagMasFin = "</font></small><br/>";
        strTagPuntajeInicioJ1 = "<pepe>";
        strTagPuntajeFinJ1 = "</pepe><br/>";
        strTagPuntajeInicioJ2 = "<pepe2>";
        strTagPuntajeFinJ2 = "</pepe2><br/>";
        strLogJ1 =  strTagPuntajeInicioJ1 + Integer.toString(valorInicial) + strTagPuntajeFinJ1;
        strLogJ2 = strTagPuntajeInicioJ2 + Integer.toString(valorInicial) + strTagPuntajeFinJ2;
        Log.v("ganador", ""+ strGanador);
        switch (strGanador) {
            case "1":
                tvEncabezadoJ1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_verde,0,0,0);
                tvEncabezadoJ2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_rojo,0,0,0);
                break;
            case "2":
                tvEncabezadoJ1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_rojo,0,0,0);
                tvEncabezadoJ2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_verde,0,0,0);
                break;
            case "d":
                tvEncabezadoJ1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_gris,0,0,0);
                tvEncabezadoJ2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_gris,0,0,0);
                break;
            default:
                break;
        }

        try {
            JSONArray jaDatosPartida = new JSONArray(strdatosPartida);

            if ( fin == -10) {
                fin = jaDatosPartida.length();
            }
            for (int i = 0; i < fin; i++) {
                JSONObject tmp = (JSONObject) jaDatosPartida.get(i);
                strLogTempVO = tmp.getString("operador") + tmp.getString("valor_operado");
                switch (tmp.getString("operador")) {
                    case "-":
                        strLogTempVO = strTagMenosInicio + strLogTempVO + strTagMenosFin;
                        break;
                    case "+":
                        strLogTempVO = strTagMasInicio + strLogTempVO + strTagMasFin;
                }
                strLogTempVN = tmp.getString("valor_nuevo") ;
                if (tmp.has("retirada")) {
                    if (tmp.getBoolean("retirada")) {
                        strLogTempVN = strLogTempVN + context.getString(R.string.texto_log_retirada);
                    }
                }

                switch (tmp.getInt("jugador")) {
                    case 1:
                        strLogTempVN = strTagPuntajeInicioJ1 + strLogTempVN + strTagPuntajeFinJ1;
                        if (!tmp.getString("operador").equals("t")) {
                            strLogJ1 = strLogJ1.replace("pepe","strike");
                            strLogJ1 = strLogJ1 +  strLogTempVO  +
                                    strLogTempVN;
                        } else {
                            strLogJ1 = strLogJ1 + tmp.getString("operador") + "\n";
                        }
                            ultimoLogJ1 = strLogTempVN;
                        break;
                    case 2:
                        strLogTempVN = strTagPuntajeInicioJ2 + strLogTempVN + strTagPuntajeFinJ2;
                        strLogJ2 = strLogJ2.replace("pepe2","strike");
                        if (!tmp.getString("operador").equals("t")) {
                            strLogJ2 = strLogJ2 +  strLogTempVO +
                                    strLogTempVN ;
                        } else {
                            strLogJ2 = strLogJ2 + tmp.getString("operador") + "\n";
                        }
                        ultimoLogJ2 = strLogTempVN;
                        break;
                    case 0:
                        if (tmp.getString("operador").equals("d"))
                        {
                            strLogTempVN = strTagPuntajeInicioJ1 +
                                    context.getString(R.string.texto_mensaje_empate) +
                                    strTagPuntajeFinJ1;
                            strLogJ1 = strLogJ1.replace("pepe","strike");
                            strLogJ1 = strLogJ1 + strLogTempVN ;
                            strLogJ2 = strLogJ2.replace("pepe2","strike");
                            strLogJ2 = strLogJ2 + strLogTempVN ;
                        }
                        break;
                    default:
                         break;
                }
            }
            tvLogJ1.setText(Html.fromHtml(strLogJ1,null,myHtmlHandler));
            tvLogJ2.setText(Html.fromHtml(strLogJ2,null,myHtmlHandler));
//            tvLogJ1.setText(strLogJ1);
//            tvLogJ2.setText(strLogJ2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public class MyHtmlTagHandler implements Html.TagHandler {

        public void handleTag(boolean opening, String tag, Editable output,
                              XMLReader xmlReader) {
            if(tag.equalsIgnoreCase("strike") || tag.equals("s")) {
                processStrike(opening, output);
            }
        }

        private void processStrike(boolean opening, Editable output) {
            int len = output.length();
            if(opening) {
                output.setSpan(new StrikethroughSpan(), len, len, Spannable.SPAN_MARK_MARK);
            } else {
                Object obj = getLast(output, StrikethroughSpan.class);
                int where = output.getSpanStart(obj);

                output.removeSpan(obj);

                if (where != len) {
                    output.setSpan(new StrikethroughSpan(), where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        private Object getLast(Editable text, Class kind) {
            Object[] objs = text.getSpans(0, text.length(), kind);

            if (objs.length == 0) {
                return null;
            } else {
                for(int i = objs.length;i>0;i--) {
                    if(text.getSpanFlags(objs[i-1]) == Spannable.SPAN_MARK_MARK) {
                        return objs[i-1];
                    }
                }
                return null;
            }
        }


    }
}
