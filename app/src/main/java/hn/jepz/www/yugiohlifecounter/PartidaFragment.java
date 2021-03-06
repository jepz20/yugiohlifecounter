package hn.jepz.www.yugiohlifecounter;

/**
 * Created by Jose Eduardo Perdomo on 4/23/2015.
 */

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A Fragmento donde esta la partida.
 */
public class PartidaFragment extends Fragment {
    public static final String PREFERENCIAS_YCL = "PreferenciasYCL";
    public static final String PREF_NOMBREJ1 = "PrefNombreJ1";
    public static final String PREF_NOMBREJ2 = "PrefNombreJ2";
    public static final String PREF_PRIMERA_VEZ = "PrimeraVez";
    public static final String PREF_PRIMERA_VEZ_NOMBRE = "PrimeraVezNombre";
    public static final String PREF_PRIMERA_VEZ_CONTADOR = "PrimeraVezContador";

    private int posicionArrayActual, turno, nuevoTexto,cantidadJugadores, jugadorActual,
            contador1Simulacion, contador2Simulacion,contador1,contador2, valorTempo,
            tempoInicial, numeroJuego, valorInicial, totalJuegos,segundosContenedor;
    private int ladoNumeros; //0 ningun lado, 1 jugador1, 2 jugador2 etc..
    private int estadoTempo; //-1 sin iniciar, 0 detenido, 1 contando
    private TextView tvValorAOperar,tvContador1, tvContador2, tvTemp,tvTemporizador;
    private TextView tvTurno, tvLogJ1, tvLogJ2;
    private TextView tvDado, tvMoneda,tvLog, tvNombreJ1, tvNombreJ2;
    private boolean simulando, borro, interrumpo;
    private boolean mostrarDialogGanador = true;
    private boolean mostrarDialogReinicio = false;
    private boolean mostrarDialogCambioNombreJ1 = false;
    private boolean mostrarDialogCambioNombreJ2 = false;
    private Button  btnSimulacion, btnFinSimulacion, btnAplicaSimulacion, btnRetiradaL1,
        btnEmpateL1, btnEmpateL2, btnRetiradaL2;
    private ImageButton ibtnEditNombreJ1,ibtnEditNombreJ2,ibtnEditNombreJ3,ibtnEditNombreJ4 ;
    private JSONArray datosPartida, partidaSimulacion, partida;
    private JSONObject duelo;
    private ImageView ivJ1J1,ivJ1J2,ivJ1J3,ivJ2J1,ivJ2J2,ivJ2J3, fondoContadorJ1, fondoContadorJ2;
    private String str,ganadorJuego, ganadorDuelo,nombrej1,nombrej2 ;
    private String strLogVacio = ".\n.\n.";
    private LinearLayout contenedorGanados1, contenedorGanados2,contenedorNumeros,
            contenedorJugador1, contenedorJugador2, llLado1, llLado2;
    private RelativeLayout tvEspacioJ1, tvEspacioJ2, tvEspacioJ3, tvEspacioJ4;
    ;
    private Handler mHandler = new Handler();
    private Thread threadContenedorNumeros;
    private BroadcastReceiver contadorReceiver;
    private SharedPreferences misPreferencias;
    private ScrollView svLogJ1, svLogJ2;

    public PartidaFragment() {
    }

    public void     manejarTemporizar () {

        //Si esta contando cancelarlo
        if (estadoTempo == 1) {
            try {
                getActivity().unregisterReceiver(contadorReceiver);
                getActivity().stopService(new Intent(getActivity(), BroadcastCountDownService.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
            estadoTempo = 0;
            int tempo;
            if (valorTempo <= 1) {
                tempo = tempoInicial;
            } else {
                tempo = valorTempo;
            }
            if (valorTempo != tempoInicial) {
                tvTemporizador.setBackgroundColor(getResources().getColor(R.color.simulacion));
            }
            valorTempo =  tempo ;
        }
        //si esta detenido o nunca fue iniciado, iniciar o continuar en lo que estaba
        else if (estadoTempo < 1){

            definirTemporizador();
            Intent iniciarServicio = new Intent(getActivity(), BroadcastCountDownService.class);
            Log.v("MT-1ValorTemppo", "" + valorTempo);
            iniciarServicio.putExtra("valorTempo", valorTempo*1000);
            getActivity().startService(iniciarServicio);
            getActivity().registerReceiver(contadorReceiver, new IntentFilter(BroadcastCountDownService.COUNTDOWN_BR));
            estadoTempo = 1;
        }
    }

    private void modificaFondo(int valor, ImageView tvFondo) {
        if (valor >= valorInicial) {
            tvFondo.setBackgroundResource(R.drawable.gradient_100);
        } else if (valor >= valorInicial*.9) {
            tvFondo.setBackgroundResource(R.drawable.gradient_90);
        } else if (valor >= valorInicial*.8) {
            tvFondo.setBackgroundResource(R.drawable.gradient_80);
        } else if (valor >= valorInicial*.7) {
            tvFondo.setBackgroundResource(R.drawable.gradient_70);
        } else if (valor >= valorInicial*.6) {
            tvFondo.setBackgroundResource(R.drawable.gradient_60);
        } else if (valor >= valorInicial*.5) {
            tvFondo.setBackgroundResource(R.drawable.gradient_50);
        } else if (valor >= valorInicial*.4) {
            tvFondo.setBackgroundResource(R.drawable.gradient_40);
        } else if (valor >= valorInicial*.3) {
            tvFondo.setBackgroundResource(R.drawable.gradient_30);
        } else if (valor >= valorInicial*.2) {
            tvFondo.setBackgroundResource(R.drawable.gradient_20);
        } else if (valor >= valorInicial*.1) {
            tvFondo.setBackgroundResource(R.drawable.gradient_10);
        } else if (valor >= valorInicial*.02) {
            tvFondo.setBackgroundResource(R.drawable.gradient_02);
        } else if (valor >= 0) {
            tvFondo.setBackgroundResource(R.drawable.gradient_0);
        }else {
            tvFondo.setBackgroundResource(R.drawable.gradient_80);
        }

    }

    private void modificaValorAOperar(TextView tvValor, String s) {
        String texto = tvValor.getText().toString();
        //-1 es para borrar
        if (s.equals("-1")) {
            if (tvValor.length() -1 <= 0 || texto.equals("0") || texto.equals("")) {
                tvValor.setText("0");
            } else {
                tvValor.setText(texto.substring(0,tvValor.length()-1));
            }
        } else  if(!((s.equals("0") || s.equals("00") || s.equals("000")) && texto.equals("0"))) {
            if (texto.equals("0")) {
                texto = "";
            }
            if (texto.length() == 2) {
                if (s.equals("000")) {
                    s = "00";
                }
            }

            if (texto.length() == 3) {
                if (s.equals("000") || s.equals("00")) {
                    s = "0";
                }
            }
            texto = texto + s;
            if (texto.length() <= 5) {
                tvValor.setText(texto);
            }
        }
    }

//    private void simular(View rootView, int accion) {
//        RelativeLayout principio = (RelativeLayout) rootView.findViewById(R.id.principio);
//        // 0 = empieza simulacion, 1 = finalizar simulacion, 2 = aplicar simulacion
//        if (accion == 0) {
//            simulando = true;
//            principio.setBackgroundColor(getResources().getColor(R.color.simulacion));
//            try {
//                partidaSimulacion = new JSONArray(datosPartida.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            contador1Simulacion = Integer.parseInt(tvContador1.getText().toString());
//            contador2Simulacion = Integer.parseInt(tvContador2.getText().toString());
//            btnSimulacion.setVisibility(View.INVISIBLE);
//            btnFinSimulacion.setVisibility(View.VISIBLE);
//            btnAplicaSimulacion.setVisibility(View.VISIBLE);
//
//        } else if (accion == 1) {
//            simulando = false;
//            principio.setBackgroundColor(getResources().getColor(R.color.fondo));
//            btnSimulacion.setVisibility(View.VISIBLE);
//            try {
//                datosPartida = new JSONArray(partidaSimulacion.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            posicionArrayActual = datosPartida.length();
//            tvContador1.setText(Integer.toString(contador1Simulacion));
//            tvContador2.setText(Integer.toString(contador2Simulacion));
//            btnFinSimulacion.setVisibility(View.INVISIBLE);
//            btnAplicaSimulacion.setVisibility(View.INVISIBLE);
//            modificaFondo(contador1Simulacion, tvContador1);
//            modificaFondo(contador2Simulacion, tvContador2);
//        } else {
//            simulando = false;
//            principio.setBackgroundColor(getResources().getColor(R.color.fondo));
//            Toast.makeText(getActivity(), "Se aplico la simulacion", Toast.LENGTH_SHORT).show();
//            btnSimulacion.setVisibility(View.VISIBLE);
//            btnFinSimulacion.setVisibility(View.INVISIBLE);
//            btnAplicaSimulacion.setVisibility(View.INVISIBLE);
//        }
//
//    }

    private void verificaNuevoJuego (boolean savedInstance ) {
        String nombreGanador;
        if (numeroJuego >1 && savedInstance) {
            int numeroJuegoAnt = numeroJuego;
            String ganadorJuegoAnt = ganadorJuego;
            modificaVisibilidadGanados(View.VISIBLE);
            for (int i = 0; i < partida.length() ; i++) {
                try {
                    JSONObject partidaTmp = (JSONObject) partida.get(i);
                    numeroJuego = partidaTmp.getInt("numeroJuego");
                    ganadorJuego = partidaTmp.getString("ganador");
                    Log.v("verificaNuevoJuego", "ganadorJuego: " + ganadorJuego);
                    Log.v("verificaNuevoJuego", "numeroJuego: " + numeroJuego);
                    if (!ganadorJuego.equals("0")) {
                        modificaIdentificadorGanados();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            numeroJuego = numeroJuegoAnt;
            ganadorJuego = ganadorJuegoAnt;
        }

        if (!ganadorJuego.equals("0") && !simulando) {
            modificaIdentificadorGanados();
            for (int y = 1; y <= cantidadJugadores; y++) {
                JSONObject temp;
                int contador = 0;
                for (int o = 0; o < partida.length(); o ++) {
                    try {
                        temp = (JSONObject) partida.get(o);
                        if (temp.getString("ganador").equals(Integer.toString(y))) {
                            contador++;
                        }
                        if ( contador >= ((totalJuegos/2) + 1)){
                            ganadorDuelo = Integer.toString(y);
                            duelo.put("ganador", ganadorDuelo);
                            break;
                        }
                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                }
            }
            if (ganadorDuelo.equals("0") && numeroJuego < totalJuegos) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                modificaVisibilidadGanados(View.VISIBLE);
                                numeroJuego = numeroJuego + 1;
                                empezarNuevoJuego(numeroJuego);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                if (ultimoMovimientoEmpate()) {
                                    actionUndo();
                                }
                                mostrarDialogGanador = false;
                                break;
                        }
                    }
                };
                switch (ganadorJuego) {
                    case "1":
                        nombreGanador = nombrej1;
                        break;
                    case "2":
                        nombreGanador = nombrej2;
                        break;
                    default:
                        nombreGanador = nombrej1;
                        break;
                }
                if (mostrarDialogGanador) {
                    if (ladoNumeros != 0) {
                        mostrarContenedorNumeros(ladoNumeros);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialogo_texto_fin_juego) + " " + (numeroJuego+1) + "?")
                            .setTitle(getString(R.string.dialogo_titulo_fin_juego) + " "
                                    + nombreGanador + "!!! ")
                            .setPositiveButton(R.string.boton_positivo_fin_juego, dialogClickListener)
                            .setNegativeButton(R.string.boton_negativo_fin_juego, dialogClickListener)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (ultimoMovimientoEmpate()) {
                                        actionUndo();
                                    }
                                    mostrarDialogGanador = false;
                                }
                            })
                            .show();
                }
            } else if ( ganadorDuelo.equals("0") && numeroJuego >= totalJuegos){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                reiniciarPartida();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                if (ultimoMovimientoEmpate()) {
                                    actionUndo();
                                }
                                mostrarDialogGanador = false;
                                break;
                        }
                    }
                };
                if (mostrarDialogGanador) {
                    if (ladoNumeros != 0) {
                        mostrarContenedorNumeros(ladoNumeros);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialogo_texto_fin_partida_p1_empate) + " " + getString(R.string.dialogo_texto_fin_partida_p2))
                            .setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton(R.string.boton_negativo_fin_juego, dialogClickListener)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (ultimoMovimientoEmpate()) {
                                        actionUndo();
                                    }
                                    mostrarDialogGanador = false;
                                }
                            })
                            .show();
                }
            }
            else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                reiniciarPartida();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                if (ultimoMovimientoEmpate()) {
                                    actionUndo();
                                }
                                mostrarDialogGanador = false;
                                break;
                        }
                    }
                };
                switch (ganadorDuelo) {
                    case "1":
                        nombreGanador = nombrej1;
                        break;
                    case "2":
                        nombreGanador = nombrej2;
                        break;
                    default:
                        nombreGanador = nombrej1;
                        break;
                }
                if (mostrarDialogGanador) {
                    if (ladoNumeros != 0) {
                        mostrarContenedorNumeros(ladoNumeros);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialogo_texto_fin_partida_p1) + " " + nombreGanador + getString(R.string.dialogo_texto_fin_partida_p2))
                            .setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton(R.string.boton_negativo_fin_juego, dialogClickListener)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (ultimoMovimientoEmpate()) {
                                        actionUndo();
                                    }
                                    mostrarDialogGanador = false;
                                }
                            })
                            .show();
                }
            }
        }
    }

    private void modificaIdentificadorGanados() {
        switch (numeroJuego) {
            case 1:
                switch (ganadorJuego) {
                    case "1":
                        ivJ1J1.setImageResource(R.drawable.circulo_verde);
                        ivJ2J1.setImageResource(R.drawable.circulo_rojo);
                        break;
                    case "2" :
                        ivJ1J1.setImageResource(R.drawable.circulo_rojo);
                        ivJ2J1.setImageResource(R.drawable.circulo_verde);
                        break;
                    case "d" :
                        ivJ1J1.setImageResource(R.drawable.circulo_amarillo);
                        ivJ2J1.setImageResource(R.drawable.circulo_amarillo);
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                switch (ganadorJuego) {
                    case "1":
                        ivJ1J2.setImageResource(R.drawable.circulo_verde);
                        ivJ2J2.setImageResource(R.drawable.circulo_rojo);
                        break;
                    case "2" :
                        ivJ1J2.setImageResource(R.drawable.circulo_rojo);
                        ivJ2J2.setImageResource(R.drawable.circulo_verde);
                        break;
                    case "d" :
                        ivJ1J2.setImageResource(R.drawable.circulo_amarillo);
                        ivJ2J2.setImageResource(R.drawable.circulo_amarillo);
                        break;
                    default:
                        break;
                }
                break;
            case 3:
                switch (ganadorJuego) {
                    case "1":
                        ivJ1J3.setImageResource(R.drawable.circulo_verde);
                        ivJ2J3.setImageResource(R.drawable.circulo_rojo);
                        break;
                    case "2" :
                        ivJ1J3.setImageResource(R.drawable.circulo_rojo);
                        ivJ2J3.setImageResource(R.drawable.circulo_verde);
                        break;
                    case "d" :
                        ivJ1J3.setImageResource(R.drawable.circulo_amarillo);
                        ivJ2J3.setImageResource(R.drawable.circulo_amarillo);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void modificaVisibilidadGanados(int visibilidad) {
        ivJ1J1.setVisibility(visibilidad);
        ivJ1J2.setVisibility(visibilidad);
        ivJ1J3.setVisibility(visibilidad);
        ivJ2J1.setVisibility(visibilidad);
        ivJ2J2.setVisibility(visibilidad);
        ivJ2J3.setVisibility(visibilidad);
        contenedorGanados1.setVisibility(visibilidad);
        contenedorGanados2.setVisibility(visibilidad);
    }

    private void empezarNuevoJuego(int gNumeroJuego) {
        if (numeroJuego <= 1) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.v("EmpezarNuevoJuego",numeroJuego + " " + prefs.getString(getString(R.string.pref_key_valor_inicial),getString(R.string.pref_default_valor_inicial)));
            valorInicial = Integer.parseInt(prefs.getString(getString(R.string.pref_key_valor_inicial),getString(R.string.pref_default_valor_inicial)));
            tempoInicial = Integer.parseInt(prefs.getString(getString(R.string.pref_key_tiempo_contador),getString(R.string.pref_default_tiempo_contador)));
            tempoInicial = tempoInicial*60;
            reiniciarTemporizador();
//            duelo = new JSONObject();
        }
        datosPartida = new JSONArray();
        posicionArrayActual = -1;
        numeroJuego = gNumeroJuego;
        contador1 = valorInicial;
        contador2 = valorInicial;
        turno = 1;
        ganadorJuego = "0";
        mostrarDialogGanador = true;
        mostrarDialogReinicio = false;
        mostrarDialogCambioNombreJ1 = false;
        mostrarDialogCambioNombreJ2 = false;
        tvContador1.setText(Integer.toString(contador1));
        fondoContadorJ1.setBackgroundResource(R.drawable.gradient_100);
        tvContador2.setText(Integer.toString(contador2));
        fondoContadorJ2.setBackgroundResource(R.drawable.gradient_100);
        tvValorAOperar.setText("0");
        contenedorNumeros.setVisibility(View.GONE);
        if (!interrumpo) {
            interrumpo = true;
            segundosContenedor = 0;
        }

        tvLogJ1.setText(strLogVacio);
        tvLogJ2.setText(strLogVacio);
        turno=1;
        tvTurno.setText("T:1");
        try {
            JSONArray tempPartida = new JSONArray();
            JSONObject partidaJSON = new JSONObject();
            nombrej1 = misPreferencias.getString(PREF_NOMBREJ1,getActivity().getString(R.string.texto_nombre_jugador_defecto) + " 1");
            nombrej2 = misPreferencias.getString(PREF_NOMBREJ2,getActivity().getString(R.string.texto_nombre_jugador_defecto) + " 2");
            tvNombreJ1.setText(nombrej1);
            tvNombreJ2.setText(nombrej2);
            partidaJSON.put("numeroJuego", Integer.toString(numeroJuego));
            partidaJSON.put("ganador", ganadorJuego );
            partidaJSON.put("datosPartida", datosPartida);
            for (int r = 0; r < numeroJuego -1; r++)  {
                tempPartida.put(partida.get(r));
            }
            partida = tempPartida;
            partida.put(partidaJSON);
            duelo.put("duelo_id", "1");
            duelo.put("cantidad_jugadores", cantidadJugadores);
            duelo.put("nombrej1",nombrej1);
            duelo.put("nombrej2",nombrej2);
            duelo.put("partida",partida);
            duelo.put("ganador", ganadorDuelo);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private  void reiniciarPartida () {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        valorInicial = Integer.parseInt(prefs.getString(getString(R.string.pref_key_valor_inicial),getString(R.string.pref_default_valor_inicial)));
        tempoInicial = Integer.parseInt(prefs.getString(getString(R.string.pref_key_tiempo_contador),getString(R.string.pref_default_tiempo_contador)));
        tempoInicial = tempoInicial*60;
        totalJuegos = 3;
        tvLogJ1.setText(strLogVacio);
        tvLogJ2.setText(strLogVacio);
        cantidadJugadores = 2;
        ganadorJuego = "0";
        ganadorDuelo = "0";
        duelo = new JSONObject();
        borro = true;
        ladoNumeros = 0;
        partida = new JSONArray();
        JSONObject partidaJson  = new JSONObject();

        try {
                /*pongos los datos de la partida en un json */
            partidaJson.put("numeroJuego", Integer.toString(numeroJuego));
            partidaJson.put("ganador", ganadorJuego );
            partidaJson.put("datosPartida", datosPartida);
            partida.put(partidaJson);

                /*Datos del duelo */
            duelo.put("ganador", ganadorDuelo);
            duelo.put("ubicacion_id", "1");
            duelo.put("partida",partida);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nuevoTexto = 0; //0 es que hay nuevo texto; 1 ya existe el texto
        simulando = false;
//        btnSimulacion.setVisibility(View.VISIBLE);
//        btnFinSimulacion.setVisibility(View.INVISIBLE);
//        btnAplicaSimulacion.setVisibility(View.INVISIBLE);
        ivJ1J1.setImageResource(R.drawable.circulo_gris);
        ivJ1J2.setImageResource(R.drawable.circulo_gris);
        ivJ1J3.setImageResource(R.drawable.circulo_gris);
        ivJ2J1.setImageResource(R.drawable.circulo_gris);
        ivJ2J2.setImageResource(R.drawable.circulo_gris);
        ivJ2J3.setImageResource(R.drawable.circulo_gris);
        modificaVisibilidadGanados(View.GONE);
        empezarNuevoJuego(1);

    }
    @TargetApi(11)
    private void animarContador (int inicio, int fin, final TextView textview) {
        if (Build.VERSION.SDK_INT >= 11) {
            ValueAnimator animator = new ValueAnimator();
            animator.setObjectValues(inicio, fin);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    textview.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator.setDuration(150);
            animator.start();

        } else {
            textview.setText(Integer.toString(fin));
        }
    }

    private void agregaMovimiento (JSONObject movimiento) {
        JSONArray tempDatosPartida = new JSONArray();
        if (posicionArrayActual != datosPartida.length() ) {
            try {
                for (int i = 0; i <= posicionArrayActual -1 ; i++) {
                    tempDatosPartida.put(datosPartida.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            datosPartida = tempDatosPartida;
        }
        datosPartida.put(movimiento);
        modificarPartida();

        mostrarDialogGanador = true;
        posicionArrayActual = datosPartida.length();

        verificaNuevoJuego(false);
    }

    private void modificarPartida() {
        try {
            JSONArray tempPartida = new JSONArray();
            JSONObject partidaJSON = new JSONObject();
            partidaJSON.put("numeroJuego", Integer.toString(numeroJuego));
            partidaJSON.put("ganador", ganadorJuego );
            partidaJSON.put("datosPartida", datosPartida);
            for (int r = 0; r < numeroJuego -1; r++)  {
                tempPartida.put(partida.get(r));
            }
            partida = tempPartida;
            partida.put(partidaJSON);

            duelo.put("partida", partida);
            duelo.put("ganador", ganadorDuelo);
            duelo.put("nombrej1",nombrej1 );
            duelo.put("nombrej2",nombrej2 );


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
private void crearThread() {
    threadContenedorNumeros = new Thread(new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis();
            String fecha = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            while (!interrumpo && segundosContenedor <5) {
                try {
                    Thread.sleep(1000);
                    if (segundosContenedor >=4) {
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (borro) {
                                    if (ladoNumeros != 0) {
                                        long millis = System.currentTimeMillis();
                                        String fecha = String.format("%02d:%02d:%02d",
                                                TimeUnit.MILLISECONDS.toHours(millis),
                                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                                        Log.v("HoraG", "Hora:" + fecha);
                                        contenedorNumeros.setVisibility(View.GONE);
                                        ladoNumeros= 0;
                                        interrumpo = true;
                                        segundosContenedor = 0;
                                        borro = false;
                                    }
                                } else {
                                    borro = true;
                                }

                            }
                        });
                    } else {
                        segundosContenedor++;
                    }

                } catch (InterruptedException  e) {
                    e.printStackTrace();
                }
            }
            segundosContenedor = 0;
            Log.v("exit","me sali del loop");
        }
    });
}

    private JSONObject calculaValor(int valorContador, int valorAOperar, String sumaOResta, String jugador, TextView textView, ImageView fondoContador) {
        //Si suma es 0 si resta es 1, en caso que reste multiplicar el valor por -1
        Log.v("TextSizeCalculaValor", "valor: " + textView.getTextSize());
        if (valorContador > 0  || !sumaOResta.equals("-")) {
            int nuevoValor;
            if (sumaOResta.equals("-")) {
                nuevoValor = valorContador + (valorAOperar * -1);
            } else {
                nuevoValor = valorContador + valorAOperar;
            }

            if (nuevoValor <= 0) {
                nuevoValor = 0;
                valorAOperar = valorContador;
                switch (jugador) {
                    case "1" :
                        ganadorJuego = "2";
                        break;
                    case "2" :
                        ganadorJuego = "1";
                        break;
                    default:
                        break;
                }
            } else {
                ganadorJuego = "0";
            }
            nuevoTexto = 0;
            animarContador(valorContador, nuevoValor, textView);
            modificaFondo(nuevoValor, fondoContador);
            JSONObject movimiento = new JSONObject();
            try {
                SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                Date dt = new Date();
                String strValue = timeFormat.format(dt);
                movimiento.put("valor_original", Integer.toString(valorContador));
                movimiento.put("valor_operado", Integer.toString(valorAOperar));
                movimiento.put("valor_nuevo", Integer.toString(nuevoValor));
                movimiento.put("hora", strValue);
                movimiento.put("operador", sumaOResta);
                movimiento.put("jugador", jugador);
                movimiento.put("turno", turno);
                if (jugador.equals("1")) {
                    contador1 = nuevoValor;
                } else {
                    contador2 = nuevoValor;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movimiento;
        } else {
            return null;
        }

    }



    private int calculaValorAOperar(String strValorAOperar) {
        int voint;
        if (strValorAOperar.length() == 0 || strValorAOperar.equals("0") ) {
            voint= 0;
        } else {
            voint= Integer.parseInt(strValorAOperar);
        }
        return voint;
    }

    private void definirTemporizador() {
        tvTemporizador.setBackgroundColor(getResources().getColor(R.color.fondo_contenedor_acciones));
    }

    private void reiniciarTemporizador() {
        if (estadoTempo == 1) {
            try {
                getActivity().unregisterReceiver(contadorReceiver);
                getActivity().stopService(new Intent(getActivity(), BroadcastCountDownService.class));
            } catch (Exception e) {
            }
            super.onPause();
        }
        valorTempo = tempoInicial;
        String text = String.format("%02d:%02d",
                ((valorTempo) % 3600) / 60, ((valorTempo) % 60));
        tvTemporizador.setText(text);
        definirTemporizador();
        estadoTempo = -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_partida_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                actionUndo();
                return true;
            case R.id.action_redo:
                actionRedo();
                return true;
//            case R.id.action_moneda:
//                actionMoneda();
//                return true;
//            case R.id.action_dado:
//                actionDado();
//                return true;
//            case R.id.action_log:
//                actionLog();
//                return true;
            case R.id.action_reiniciar:
                mostrarDialogReinicio = true;
                actionReiniciar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tvTemporizador = (TextView) rootView.findViewById(R.id.tvTemporizador);
        contadorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                actualizaValorTempo(intent);
            }
        };
        segundosContenedor = 0;
        interrumpo = false;
        misPreferencias = getActivity().getSharedPreferences(PREFERENCIAS_YCL,Context.MODE_PRIVATE);
        tvValorAOperar = (TextView) (rootView.findViewById(R.id.tvValorOperado));
        tvContador1 = (TextView) (rootView.findViewById(R.id.contador1));
        tvContador2 = (TextView) (rootView.findViewById(R.id.contador2));
        tvLogJ1 = (TextView) (rootView.findViewById(R.id.tvLog1));
        tvLogJ2 = (TextView) (rootView.findViewById(R.id.tvLog2));
//        btnSimulacion = (Button) rootView.findViewById(R.id.btnSimulacion);
//        btnFinSimulacion = (Button) rootView.findViewById(R.id.btnFinSimulacion);
//        btnAplicaSimulacion = (Button) rootView.findViewById(R.id.btnAplicaSimulacion);
        btnRetiradaL1 = (Button) rootView.findViewById(R.id.btnRetiradaL1);
        btnRetiradaL2 = (Button) rootView.findViewById(R.id.btnRetiradaL2);
        btnEmpateL1 = (Button) rootView.findViewById(R.id.btnEmpateL1);
        btnEmpateL2 = (Button) rootView.findViewById(R.id.btnEmpateL2);
        ibtnEditNombreJ1 = (ImageButton) rootView.findViewById(R.id.ibtnEditNombreJ1);
        ibtnEditNombreJ2 = (ImageButton) rootView.findViewById(R.id.ibtnEditNombreJ2);
        ibtnEditNombreJ3 = (ImageButton) rootView.findViewById(R.id.ibtnEditNombreJ3);
        ibtnEditNombreJ4 = (ImageButton) rootView.findViewById(R.id.ibtnEditNombreJ4);
        llLado1 = (LinearLayout) rootView.findViewById(R.id.llLado1);
        llLado2 = (LinearLayout) rootView.findViewById(R.id.llLado2);
        tvTurno = (TextView) rootView.findViewById(R.id.turno);
        tvDado = (TextView) rootView.findViewById(R.id.tvDado);
        tvMoneda = (TextView) rootView.findViewById(R.id.tvMoneda);
        tvLog = (TextView) rootView.findViewById(R.id.tvLog);

        tvEspacioJ1 = (RelativeLayout) rootView.findViewById(R.id.espacioJ1);
        tvEspacioJ2 = (RelativeLayout) rootView.findViewById(R.id.espacioJ2);
        tvEspacioJ3 = (RelativeLayout) rootView.findViewById(R.id.espacioJ3);
        tvEspacioJ4 = (RelativeLayout) rootView.findViewById(R.id.espacioJ4);
        tvNombreJ1 = (TextView) rootView.findViewById(R.id.tvNombreJ1);
        tvNombreJ2 = (TextView) rootView.findViewById(R.id.tvNombreJ2);
        estadoTempo = -1;
        ivJ1J1 = (ImageView) rootView.findViewById(R.id.ivJ1J1);
        ivJ1J2 = (ImageView) rootView.findViewById(R.id.ivJ1J2);
        ivJ1J3 = (ImageView) rootView.findViewById(R.id.ivJ1J3);
        ivJ2J1 = (ImageView) rootView.findViewById(R.id.ivJ2J1);
        ivJ2J2 = (ImageView) rootView.findViewById(R.id.ivJ2J2);
        ivJ2J3 = (ImageView) rootView.findViewById(R.id.ivJ2J3);
        contenedorGanados1 = (LinearLayout) rootView.findViewById(R.id.contenedorGanados1);
        contenedorGanados2 = (LinearLayout) rootView.findViewById(R.id.contenedorGanados2);
        contenedorNumeros = (LinearLayout) rootView.findViewById(R.id.contenedorNumeros);
        contenedorJugador1 = (LinearLayout) rootView.findViewById(R.id.contenedorJugador1);
        if (rootView.findViewById(R.id.contenedorJugador2) != null) {
            contenedorJugador2 = (LinearLayout) rootView.findViewById(R.id.contenedorJugador2);

        } else {
            contenedorJugador2 = (LinearLayout) rootView.findViewById(R.id.contenedorJugador3);
        }

        fondoContadorJ1 = (ImageView) rootView.findViewById(R.id.fondoContadorJ1);
        fondoContadorJ2 = (ImageView) rootView.findViewById(R.id.fondoContadorJ2);
        contenedorNumeros.setVisibility(View.GONE);
        //Para que muestre el contenedor de numeros
        tvLogJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("svLogJ1", "Click");
                mostrarContenedorNumeros(1);
            }
        });
        //Para que muestre el contenedor de numeros
        tvLogJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("svLogJ2", "Click");
                if (rootView.findViewById(R.id.contenedorJugador2) != null) {
                    mostrarContenedorNumeros(2);

                } else {
                    mostrarContenedorNumeros(3);
                }
            }
        });
        //Para que muestre el contenedor de numeros
        tvNombreJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarContenedorNumeros(1);
            }
        });
         //Para cambiar el nombre
        tvNombreJ1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mostrarDialogCambioNombreJ1 = true;
                alertaModificarNombre(1);
                return true;
            }
        });
        ibtnEditNombreJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogCambioNombreJ1 = true;
                alertaModificarNombre(1);
            }
        });
        //Para cambiar el nombre
        tvNombreJ2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mostrarDialogCambioNombreJ2 = true;
                alertaModificarNombre(2);
                return true;
            }
        });
        ibtnEditNombreJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogCambioNombreJ2 = true;
                alertaModificarNombre(2);
            }
        });
        ibtnEditNombreJ3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogCambioNombreJ2 = true;
                if (rootView.findViewById(R.id.contenedorJugador2) != null) {
                    alertaModificarNombre(2);

                } else {
                    alertaModificarNombre(2);
                }
            }
        });
        //Para que muestre el contenedor de numeros
        tvNombreJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootView.findViewById(R.id.contenedorJugador2) != null) {
                    mostrarContenedorNumeros(2);

                } else {
                    mostrarContenedorNumeros(3);
                }
            }
        });
        tvEspacioJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tvEspacioJ1", "Hice Click en J1");
                mostrarContenedorNumeros(1);
            }
        });
        tvEspacioJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tvEspacioJ2", "Hice Click en J2");
                mostrarContenedorNumeros(2);
            }
        });
        tvEspacioJ3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tvEspacioJ3", "Hice Click en J3");
                mostrarContenedorNumeros(3);
            }
        });
        tvEspacioJ4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("tvEspacioJ4", "Hice Click en J4");
                mostrarContenedorNumeros(1);
            }
        });
        contenedorNumeros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        contenedorJugador1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarContenedorNumeros(1);
            }
        });
        contenedorJugador2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rootView.findViewById(R.id.contenedorJugador2) != null) {
                    mostrarContenedorNumeros(2);

                } else {
                    mostrarContenedorNumeros(3);
                }

            }
        });

        //Handler para desaparecer los numero despues de x segundos

        crearThread();

        tvTemporizador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean primeraVezContador = misPreferencias.getBoolean(PREF_PRIMERA_VEZ_CONTADOR,true);
                ViewTarget target = new ViewTarget(getActivity().findViewById(R.id.tvTemporizador));
                if (primeraVezContador) {
                    new ShowcaseView.Builder(getActivity())
                            .setTarget(target)
                            .setContentTitle(getActivity().getString(R.string.titulo_ayuda_temporizador))
                            .setContentText(getActivity().getString(R.string.descripcion_ayuda_temporizador))
                            .setStyle(R.style.CustomShowcaseTheme)
                            .hideOnTouchOutside()
                            .build();
                    SharedPreferences.Editor edit = misPreferencias.edit();
                    edit.putBoolean(PREF_PRIMERA_VEZ_CONTADOR,false);
                    edit.commit();
                }
                manejarTemporizar();
            }
        });
        tvTemporizador.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                reiniciarTemporizador();
                return true;
            }
        });
        //Inicializo las variables
        reiniciarPartida();

        if (savedInstanceState != null) {
            try {
                totalJuegos = savedInstanceState.getInt("sTotalJuegos");
                numeroJuego = savedInstanceState.getInt("sNumeroJuego");
                ganadorJuego = savedInstanceState.getString("sGanadorJuego");
                ganadorDuelo = savedInstanceState.getString("sGanadorDuelo");

                if (savedInstanceState.getString("sDatosPartida").length() !=0) {
                    datosPartida = new JSONArray(savedInstanceState.getString("sDatosPartida"));
                }

                if (savedInstanceState.getString("sPartida").length() != 0) {
                    partida = new JSONArray(savedInstanceState.getString("sPartida"));
                }

                mostrarDialogGanador = savedInstanceState.getBoolean("sMostrarDialogGanador");
                verificaNuevoJuego(true);
                mostrarDialogReinicio = savedInstanceState.getBoolean("sMostrarDialogReinicio");
                actionReiniciar();
                numeroJuego = savedInstanceState.getInt("sNumeroJuego");
                ganadorJuego = savedInstanceState.getString("sGanadorJuego");

                if (savedInstanceState.getString("sDuelo").length() != 0 ) {
                    duelo= new JSONObject(savedInstanceState.getString("sDuelo"));
                }

                mostrarDialogCambioNombreJ1 = savedInstanceState.getBoolean("sMostrarDialogCambioNombreJ1");
                if (mostrarDialogCambioNombreJ1) {
                    alertaModificarNombre(1);
                }
                mostrarDialogCambioNombreJ2 = savedInstanceState.getBoolean("sMostrarDialogCambioNombreJ2");
                if (mostrarDialogCambioNombreJ2) {
                    alertaModificarNombre(2);
                }
                posicionArrayActual = savedInstanceState.getInt("sPosicionArrayActual");
                turno = savedInstanceState.getInt("sTurno");
                valorInicial = savedInstanceState.getInt("sValorInicial");
                cantidadJugadores = savedInstanceState.getInt("sCantidadJugadores");
                contador1 = savedInstanceState.getInt("sContador1");
                tvContador1.setText(Integer.toString(contador1));
                modificaFondo(contador1, fondoContadorJ1);
                contador2 = savedInstanceState.getInt("sContador2");
                tvContador2.setText(Integer.toString(contador2));
                modificaFondo(contador2, fondoContadorJ2);
                tvValorAOperar.setText(savedInstanceState.getString("sValorAOperar"));
                estadoTempo = savedInstanceState.getInt("sEstadoTempo");
                //Manejo de temporizador
                valorTempo = savedInstanceState.getInt("sValorTempo",0);
//                valorTempo = valorTempo * 1000;
                if (estadoTempo == 1) {
                    estadoTempo = -1;
                } else {
                    String text = String.format("%02d:%02d",
                            ((valorTempo) % 3600) / 60, ((valorTempo) % 60));
                    tvTemporizador.setText(text);
                    estadoTempo = 1;
                }
                manejarTemporizar();
                //Generar log Principal
                generarLogPrincipal();
                //Si cambio la preferencia reinicio todo

//                simulando = savedInstanceState.getBoolean("sSimulando");
//                if (simulando) {
//                    RelativeLayout principio = (RelativeLayout) rootView.findViewById(R.id.principio);
//                    principio.setBackgroundColor(getResources().getColor(R.color.simulacion));
//                    try {
//                        if (savedInstanceState.getString("sPartidaSimulacion").length() > 0){
//                            partidaSimulacion = new JSONArray(savedInstanceState.getString("sPartidaSimulacion"));
//                        } else {
//                            partidaSimulacion = new JSONArray(datosPartida.toString());
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    contador1Simulacion = savedInstanceState.getInt("sContador1Simulacion");
//                    contador2Simulacion = savedInstanceState.getInt("sContador1Simulacion");
//                    btnSimulacion.setVisibility(View.INVISIBLE);
//                    btnFinSimulacion.setVisibility(View.VISIBLE);
//                    btnAplicaSimulacion.setVisibility(View.VISIBLE);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Button btnTemp;

        tvValorAOperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvValorAOperar.setText("0");
            }
        });

        //Inicializa los botones de suma

            Button btnSumaL1 = (Button) rootView.findViewById(R.id.suma1L1);

            btnSumaL1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int jugador;
                    TextView tvContador;
                    ImageView fondoContador;
                    borro = false;
                    switch (ladoNumeros) {
                        case 1:
                            jugador = 1;
                            tvContador = tvContador1;
                            fondoContador = fondoContadorJ1;
                            break;
                        case 2:
                            jugador = 2;
                            tvContador = tvContador2;
                            fondoContador = fondoContadorJ2;
                            break;
                        case 3:
                            jugador = 2;
                            tvContador = tvContador2;
                            fondoContador = fondoContadorJ2;
                            break;
                        default:
                            jugador = 1;
                            tvContador = tvContador1;
                            fondoContador = fondoContadorJ1;
                            break;
                    }


                    int valorAOperar = calculaValorAOperar(tvValorAOperar.getText().toString());
                    int valorContador = Integer.parseInt(tvContador.getText().toString());
                    if (valorAOperar != 0) {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "+", Integer.toString(jugador), tvContador, fondoContador);
                        if (temp != null) {
                            agregaMovimiento(temp);
                            generarLogPrincipal();
                        }
                    }
                }
            });
            Button btnRestaL1 = (Button) rootView.findViewById(R.id.resta1L1);

            btnRestaL1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int jugador;
                    TextView tvContador;
                    ImageView fondoContador;
                    switch ( ladoNumeros) {
                        case 1 :
                            jugador = 1;
                            tvContador = tvContador1;
                            fondoContador = fondoContadorJ1;
                            break;
                        case 2 :
                            jugador = 2;
                            tvContador = tvContador2;
                            fondoContador = fondoContadorJ2;
                            break;
                        case 3:
                            jugador = 2;
                            tvContador = tvContador2;
                            fondoContador = fondoContadorJ2;
                            break;
                        default:
                            jugador = 1;
                            tvContador = tvContador1;
                            fondoContador = fondoContadorJ1;
                            break;
                    }

                    int valorAOperar = calculaValorAOperar(tvValorAOperar.getText().toString());
                    int valorContador = Integer.parseInt(tvContador.getText().toString());
                    borro= false;
                    if (valorAOperar != 0) {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "-", Integer.toString(jugador), tvContador,fondoContador);
                        if (temp != null) {
                            agregaMovimiento(temp);
                            generarLogPrincipal();
                        }
                    }
                }
            });

        btnRetiradaL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jugador, valorAOperar;
                TextView tvContador;
                ImageView fondoContador;
                switch (ladoNumeros) {
                    case 1:
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                    case 2:
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    case 3:
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    default:
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                }

                valorAOperar = calculaValorAOperar(tvContador.getText().toString());
                int valorContador = Integer.parseInt(tvContador.getText().toString());
                borro = false;
                if (valorAOperar != 0) {
                    try {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "-", Integer.toString(jugador), tvContador, fondoContador);
                        if (temp != null) {
                            temp.put("retirada", true);
                            agregaMovimiento(temp);
                            generarLogPrincipal();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

//Inicializa los botones de suma

        Button btnSumaL2 = (Button) rootView.findViewById(R.id.suma1L2);

        btnSumaL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jugador;
                TextView tvContador;
                ImageView fondoContador;
                borro= false;
                switch ( ladoNumeros) {
                    case 1 :
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                    case 2 :
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    case 3:
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    default:
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                }


                int valorAOperar = calculaValorAOperar(tvValorAOperar.getText().toString());
                int valorContador = Integer.parseInt(tvContador.getText().toString());
                if (valorAOperar != 0) {
                    JSONObject temp = calculaValor(valorContador, valorAOperar, "+", Integer.toString(jugador), tvContador, fondoContador);
                    if (temp != null) {
                        agregaMovimiento(temp);
                        generarLogPrincipal();
                    }
                }
            }
        });
        Button btnRestaL2 = (Button) rootView.findViewById(R.id.resta1L2);

        btnRestaL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jugador;
                TextView tvContador;
                ImageView fondoContador;
                switch ( ladoNumeros) {
                    case 1 :
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                    case 2 :
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    case 3:
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    default:
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                }

                int valorAOperar = calculaValorAOperar(tvValorAOperar.getText().toString());
                int valorContador = Integer.parseInt(tvContador.getText().toString());
                borro= false;
                if (valorAOperar != 0) {
                    JSONObject temp = calculaValor(valorContador, valorAOperar, "-", Integer.toString(jugador), tvContador,fondoContador);
                    if (temp != null) {
                        agregaMovimiento(temp);
                        generarLogPrincipal();
                    }
                }
            }
        });

        btnRetiradaL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jugador, valorAOperar;
                TextView tvContador;
                ImageView fondoContador;
                switch (ladoNumeros) {
                    case 1:
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                    case 2:
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    case 3:
                        jugador = 2;
                        tvContador = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    default:
                        jugador = 1;
                        tvContador = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                }

                valorAOperar = calculaValorAOperar(tvContador.getText().toString());
                int valorContador = Integer.parseInt(tvContador.getText().toString());
                borro = false;
                if (valorAOperar != 0) {
                    try {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "-", Integer.toString(jugador), tvContador, fondoContador);
                        if (temp != null) {
                            temp.put("retirada", true);
                            agregaMovimiento(temp);
                            generarLogPrincipal();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

//        btnSimulacion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                simular(rootView, 0);
//            }
//        });
//        btnFinSimulacion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                simular(rootView, 1);
//            }
//        });
//        btnAplicaSimulacion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                simular(rootView, 2);
//            }
//        });

        tvDado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDado();
            }
        });
        tvMoneda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMoneda();
            }
        });

        tvLog.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionLog();
            }
        });
        tvTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
        tvTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turno = turno + 1;
                tvTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
                JSONObject movimiento = new JSONObject();
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                    Date dt = new Date();
                    String strValue = timeFormat.format(dt);
                    movimiento.put("valor_original", 0);
                    movimiento.put("valor_operado", 0);
                    movimiento.put("valor_nuevo", 0);
                    movimiento.put("hora", strValue);
                    movimiento.put("operador", "t");
                    movimiento.put("jugador", "0");
                    movimiento.put("turno", turno);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                agregaMovimiento(movimiento);


            }
        });
        btnEmpateL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject movimiento = new JSONObject();
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                    Date dt = new Date();
                    String strValue = timeFormat.format(dt);
                    movimiento.put("valor_original", 0);
                    movimiento.put("valor_operado", 0);
                    movimiento.put("valor_nuevo", 0);
                    movimiento.put("hora", strValue);
                    movimiento.put("operador", "d");
                    movimiento.put("jugador", "0");
                    movimiento.put("empate", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ganadorJuego = "d";
                agregaMovimiento(movimiento);
                generarLogPrincipal();
            }
        });

        btnEmpateL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject movimiento = new JSONObject();
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                    Date dt = new Date();
                    String strValue = timeFormat.format(dt);
                    movimiento.put("valor_original", 0);
                    movimiento.put("valor_operado", 0);
                    movimiento.put("valor_nuevo", 0);
                    movimiento.put("hora", strValue);
                    movimiento.put("operador", "d");
                    movimiento.put("jugador", "0");
                    movimiento.put("empate", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ganadorJuego = "d";
                agregaMovimiento(movimiento);
                generarLogPrincipal();
            }
        });

        for (int i = -3; i < 10 ; i++) {
            str = "0";
            switch (i){
                case -3: btnTemp = (Button) rootView.findViewById(R.id.btnTripleCero);
                    str = "000";
                    break;
                case -2: btnTemp = (Button) rootView.findViewById(R.id.btnDobleCero);
                    str = "00";
                    break;
                case -1: btnTemp = (Button) rootView.findViewById(R.id.btnBorrar);
                    str = "-1";
                    btnTemp.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            tvValorAOperar.setText("0");
                            return true;
                        }
                    });
                    break;
                case 0: btnTemp = (Button) rootView.findViewById(R.id.btnCero);
                    str = "0";
                    break;
                case 1: btnTemp = (Button) rootView.findViewById(R.id.btnUno);
                    str = "1";
                    break;
                case 2: btnTemp = (Button) rootView.findViewById(R.id.btnDos);
                    str = "2";
                    break;
                case 3: btnTemp = (Button) rootView.findViewById(R.id.btnTres);
                    str = "3";
                    break;
                case 4: btnTemp = (Button) rootView.findViewById(R.id.btnCuatro);
                    str = "4";
                    break;
                case 5: btnTemp = (Button) rootView.findViewById(R.id.btnCinco);
                    str = "5";
                    break;
                case 6: btnTemp = (Button) rootView.findViewById(R.id.btnSeis);
                    str = "6";
                    break;
                case 7: btnTemp = (Button) rootView.findViewById(R.id.btnSiete);
                    str = "7";
                    break;
                case 8: btnTemp = (Button) rootView.findViewById(R.id.btnOcho);
                    str = "8";
                    break;
                case 9: btnTemp = (Button) rootView.findViewById(R.id.btnNueve);
                    str = "9";
                    break;
                default: btnTemp = (Button) rootView.findViewById(R.id.btnOcho);
            }

            btnTemp.setOnClickListener(new View.OnClickListener() {
                private final String strLocal = str;
                @Override
                public void onClick(View v) {
                    String t = tvValorAOperar.getText().toString();
                    int l = t.length();
                    if (nuevoTexto == 0) {
                        nuevoTexto = 1;
                        if (l > 0) {
                            tvValorAOperar.setText(t.substring(l - 1));
                        } else
                            tvValorAOperar.setText("0");
                    }
                    modificaValorAOperar(tvValorAOperar, strLocal);
                    segundosContenedor = 0;
                }
            });
        }

        boolean primeraVez = misPreferencias.getBoolean(PREF_PRIMERA_VEZ,true);
        if (primeraVez) {
            SharedPreferences.Editor edit = misPreferencias.edit();
            edit.putBoolean(PREF_PRIMERA_VEZ,false);
            edit.commit();
            ViewTarget target = new ViewTarget(rootView.findViewById(R.id.contador1));
            new ShowcaseView.Builder(getActivity())
                    .setTarget(target)
                    .setContentTitle(getActivity().getString(R.string.titulo_ayuda_contenedor_numeros))
                    .setContentText(getActivity().getString(R.string.descripcion_ayuda_contenedor_numeros))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .build();
        }

        return rootView;
    }

    private void alertaModificarNombre(int gJugador) {
        final int jugador = gJugador;
        final EditText nombre =  new EditText(getActivity());
        int maxLength = 20;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        nombre.setFilters(fArray);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoNombre = nombre.getText().toString();
                SharedPreferences.Editor edit = misPreferencias.edit();
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (!nuevoNombre.equals("")) {
                            switch (jugador) {
                                case 1:
                                    tvNombreJ1.setText(nuevoNombre);
                                    nombrej1 = nuevoNombre;
                                    edit.putString(PREF_NOMBREJ1, nuevoNombre);
                                    edit.commit();
                                    mostrarDialogCambioNombreJ1 = false;
                                    break;
                                case 2 :
                                    tvNombreJ2.setText(nuevoNombre);
                                    nombrej2 = nuevoNombre;
                                    edit.putString(PREF_NOMBREJ2, nuevoNombre);
                                    edit.commit();
                                    mostrarDialogCambioNombreJ2 = false;
                                    break;
                                default:
                                    break;
                            }
                            modificarPartida();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mostrarDialogCambioNombreJ1 = false;
                        mostrarDialogCambioNombreJ2 = false;
                        break;
                }
            }
        };
        String tituloDialogoCambioNombre;
        switch (jugador) {
            case 1 :
                tituloDialogoCambioNombre = getString(R.string.dialogo_titulo_cambio_nombre) + " " + nombrej1;
                break;
            case 2 :
                tituloDialogoCambioNombre = getString(R.string.dialogo_titulo_cambio_nombre) + " " + nombrej2;
                break;
            default:
                tituloDialogoCambioNombre = getString(R.string.dialogo_titulo_cambio_nombre);
                break;
        }
        if (ladoNumeros != 0) {
            mostrarContenedorNumeros(ladoNumeros);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        nombre.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        nombre.setHint(getActivity().getString(R.string.texto_hint_nombre));
        nombre.setPadding(40, 20, 20, 20);
        builder.setTitle(tituloDialogoCambioNombre)
                .setView(nombre)
                .setPositiveButton(R.string.boton_aceptar, dialogClickListener)
                .setNegativeButton(R.string.boton_cancelar, dialogClickListener)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mostrarDialogCambioNombreJ1 = false;
                        mostrarDialogCambioNombreJ2 = false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public int getLadoNumeros() {
        return ladoNumeros;
    }

    public void mostrarContenedorNumeros( int lado) {
        nuevoTexto = 0;
        if (ladoNumeros == 0) {
            ladoNumeros = lado;
            long millis = System.currentTimeMillis();
            String fecha = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            Log.v("HoraV", "Hora:" + fecha);
            contenedorNumeros.setVisibility(View.VISIBLE);
            switch (lado) {
                case 1 :
//                    ibtnEditNombreJ1.setVisibility(View.VISIBLE);
                    llLado1.setVisibility(View.VISIBLE);
                    llLado2.setVisibility(View.GONE);
                    tvEspacioJ1.setVisibility(View.VISIBLE);
                    tvEspacioJ2.setVisibility(View.GONE);
                    break;
                case 2 :
//                    ibtnEditNombreJ2.setVisibility(View.VISIBLE);
                    llLado1.setVisibility(View.VISIBLE);
                    llLado2.setVisibility(View.GONE);
                    tvEspacioJ1.setVisibility(View.GONE);
                    tvEspacioJ2.setVisibility(View.VISIBLE);
                    break;
                case 3 :
//                    ibtnEditNombreJ2.setVisibility(View.VISIBLE);
                    llLado2.setVisibility(View.VISIBLE);
                    llLado1.setVisibility(View.GONE);
                    tvEspacioJ3.setVisibility(View.VISIBLE);
                    tvEspacioJ4.setVisibility(View.GONE);
                    break;
                case 4 :
//                    ibtnEditNombreJ2.setVisibility(View.VISIBLE);
                    llLado2.setVisibility(View.VISIBLE);
                    llLado1.setVisibility(View.GONE);
                    tvEspacioJ3.setVisibility(View.GONE);
                    tvEspacioJ4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;

            }
            if (interrumpo) {
                interrumpo = false;
                segundosContenedor = 0;
            }
            crearThread();
            threadContenedorNumeros.start();
        } else {
            ladoNumeros = 0;
            contenedorNumeros.setVisibility(View.GONE);
            if (!interrumpo) {
                interrumpo = true;
                segundosContenedor = 0;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (datosPartida != null) {
            outState.putString("sDatosPartida", datosPartida.toString());
        } else {
            outState.putString("sDatosPartida", "");
        }
//        if (partidaSimulacion != null) {
//            outState.putString("sPartidaSimulacion", "");
//        }
        if (partida != null ) {
            outState.putString("sPartida", partida.toString());
        } else {
            outState.putString("sPartida", "");
        }

        if (duelo.length() != 0) {
            outState.putString("sDuelo", duelo.toString());
        } else {
            outState.putString("sDuelo", "");
        }
        outState.putInt("sPosicionArrayActual", posicionArrayActual);
        outState.putInt("sTurno", turno);
        outState.putInt("sJugadorActual", jugadorActual);
        outState.putInt("sCantidadJugadores", cantidadJugadores);
        outState.putInt("sTotalJuegos", totalJuegos);
        outState.putInt("sEstadoTempo", estadoTempo);
        outState.putInt("sValorTempo", valorTempo);
        outState.putInt("sNumeroJuego", numeroJuego);
        outState.putInt("sValorInicial", valorInicial);
//        outState.putInt("sContador1Simulacion", contador1Simulacion);
//        outState.putInt("sContador2Simulacion", contador2Simulacion);
        outState.putInt("sContador1", contador1);
        outState.putInt("sContador2", contador2);
        outState.putInt("sLadoNumeros", ladoNumeros);
        outState.putBoolean("sSimulando", simulando);
        outState.putBoolean("sMostrarDialogGanador", mostrarDialogGanador);
        outState.putBoolean("sMostrarDialogCambioNombreJ1", mostrarDialogCambioNombreJ1);
        outState.putBoolean("sMostrarDialogCambioNombreJ2", mostrarDialogCambioNombreJ2);
        outState.putBoolean("sMostrarDialogReinicio", mostrarDialogReinicio);
        outState.putString("sGanadorJuego", ganadorJuego);
        outState.putString("sGanadorDuelo", ganadorDuelo);
        outState.putString("sValorAOperar", tvValorAOperar.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void actualizaValorTempo(Intent intent) {
        long segundos = intent.getLongExtra("countdown",0)/1000;
        String text = String.format("%02d:%02d",
                (segundos % 3600) / 60, (segundos % 60));
        tvTemporizador.setText(text);
        Log.v("segundosAVT", "" + segundos);
        valorTempo = ( int) segundos;
        Log.v("valorTempoAVT", "" + valorTempo);
    }

    @Override
    public void onStart() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (valorInicial != Integer.parseInt(prefs.getString(getString(R.string.pref_key_valor_inicial), getString(R.string.pref_default_valor_inicial)))
                || tempoInicial != Integer.parseInt(prefs.getString(getString(R.string.pref_key_tiempo_contador),getString(R.string.pref_default_tiempo_contador)))*60) {
            reiniciarPartida();
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        try {
            Log.v("onPuase", "voy a hacer una pausa" + valorTempo);
//            getActivity().unregisterReceiver(contadorReceiver);
//            getActivity().stopService(new Intent(getActivity(), BroadcastCountDownService.class));
        } catch (Exception e) {

        }
        super.onPause();
    }

    @Override
    public void onStop() {
        try{
            Log.v("onStop", "voy a hacer un stop");
//            getActivity().unregisterReceiver(contadorReceiver);
        } catch (Exception e) {

        }
        super.onStop();
    }


    @Override
    public void onDestroy() {
        Log.v("onDestruir", "bua destruir");
        try {
        if (estadoTempo != 1) {
            getActivity().unregisterReceiver(contadorReceiver);
        }
            getActivity().stopService(new Intent(getActivity(), BroadcastCountDownService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //else  {
    //}
        super.onDestroy();
    }

    public void actionUndo() {
        if (posicionArrayActual > 0) {
            try {
                posicionArrayActual = posicionArrayActual - 1;
                JSONObject temp = (JSONObject) (datosPartida.get(posicionArrayActual));
                int valorOperado = temp.getInt("valor_operado");
                String operador = temp.getString("operador");
                String jugador = temp.getString("jugador");
                TextView tempTv;
                ImageView fondoContador;
                switch (jugador) {
                    case "1":
                        tempTv = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                    case "2":
                        tempTv = tvContador2;
                        fondoContador = fondoContadorJ2;
                        break;
                    default:
                        tempTv = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                }
                int valorContador = Integer.parseInt(tempTv.getText().toString());

                switch (operador) {
                    case "t" :
                        turno = turno - 1;
                        tvTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
                        break;
                    case "-" :
                        operador = "+";
                        calculaValor(valorContador, valorOperado, operador, jugador, tempTv,fondoContador);
                        break;
                    case "+" :
                        operador = "-";
                        calculaValor(valorContador, valorOperado, operador, jugador, tempTv,fondoContador);
                        break;
                    case "d" :
                        ganadorJuego = "0";
                        modificarPartida();
                        break;
                    default:
                        break;
                }
                if (operador.equals("t")) {

                } else {
                    if (operador.equals("+")) {

                    } else {

                    }

                }
                if (posicionArrayActual < 0) {
                    posicionArrayActual = 0;
                }
                generarLogPrincipal();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionRedo() {
        if (posicionArrayActual < datosPartida.length()) {
            try {
                JSONObject temp = (JSONObject) (datosPartida.get(posicionArrayActual));
                int valorOperado = temp.getInt("valor_operado");
                String operador = temp.getString("operador");
                String jugador = temp.getString("jugador");
                TextView tempTv;
                ImageView fondoContador;
                switch (jugador) {
                    case "1":
                        tempTv = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                    case "2":
                        tempTv = tvContador2;
                        fondoContador = fondoContadorJ1;
                        break;
                    default:
                        tempTv = tvContador1;
                        fondoContador = fondoContadorJ1;
                        break;
                }
                int valorContador;
                switch (operador) {
                    case "t" :
                        turno = turno + 1;
                        tvTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
                        break;
                    case "+" :
                        valorContador = Integer.parseInt(tempTv.getText().toString());
                        calculaValor(valorContador, valorOperado, operador, jugador, tempTv,fondoContador);
                        mostrarDialogGanador = true;
                        verificaNuevoJuego(false);
                        break;
                    case "-" :
                        valorContador = Integer.parseInt(tempTv.getText().toString());
                        calculaValor(valorContador, valorOperado, operador, jugador, tempTv,fondoContador);
                        mostrarDialogGanador = true;
                        verificaNuevoJuego(false);
                        break;
                    case "d":
                        ganadorJuego = "d";
                        modificarPartida();
                        break;
                    default:
                        break;
                }
                posicionArrayActual = posicionArrayActual + 1;
                if (posicionArrayActual >= datosPartida.length()) {
                    posicionArrayActual = datosPartida.length();
                }
                generarLogPrincipal();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionMoneda() {
        Intent intent = new Intent(getActivity(), MonedaActivity.class);
        startActivity(intent);
    }

    public void actionDado() {
        Intent intent = new Intent(getActivity(), DadoActivity.class);
        startActivity(intent);
    }

    public void actionLog() {
        Intent intent = new Intent(getActivity(), LogActivity.class);
        Bundle logBundle = new Bundle();
        logBundle.putString("datosPartida", datosPartida.toString());
        logBundle.putString("duelo", duelo.toString());
        logBundle.putInt("posicionArrayActual", posicionArrayActual);
        logBundle.putInt("valorInicial", valorInicial);
        intent.putExtras(logBundle);
        startActivity(intent);
    }

    public void generarLogPrincipal() {
        String  strLogTempVN, strLogTempVO,
                strTagMenosInicio, strTagMenosFin, strTagMasInicio, strTagMasFin,
                strTagPuntajeInicio, strTagPuntajeFin;
        JSONArray datosPartidaArray;
        String strLogJ1 = "";
        String strLogJ2 = "";
        JSONObject tmp;
        strTagMenosInicio = "<small><small><small><font color= '#9a0707'>";
        strTagMenosFin = "</font></small></small></small>&nbsp";
        strTagMasInicio = "<small><small><small><font color= '#226f45'>";
        strTagMasFin = "</font></small></small></small>&nbsp";
        strTagPuntajeInicio = "<strike><font color= '#757575'>";
        strTagPuntajeFin = "</font></strike><br/>";
        MyHtmlTagHandler myHtmlHandler = new MyHtmlTagHandler();
        int c1=0, c2 = 0;
        try {
            datosPartidaArray = datosPartida;
            int fin = posicionArrayActual;

            for (int i = 0; i < fin; i++) {

                tmp = (JSONObject) datosPartidaArray.get(i);
                strLogTempVO = tmp.getString("operador") + tmp.getString("valor_operado");
                switch (tmp.getString("operador")) {
                    case "-":
                        strLogTempVO = strTagMenosInicio + strLogTempVO + strTagMenosFin;
                        break;
                    case "+":
                        strLogTempVO = strTagMasInicio + strLogTempVO + strTagMasFin;
                }
                if (!tmp.getString("operador").equals("t")) {
                    if (tmp.getString("operador").equals("d")) {
                        strLogTempVN =  getActivity().getString(R.string.texto_mensaje_empate);
                    } else {
                        strLogTempVN =  tmp.getString("valor_original")  ;
                    }
                    if (tmp.has("retirada")) {
                        if (tmp.getBoolean("retirada")) {
                            strLogTempVN = strLogTempVN + getString(R.string.texto_log_retirada);
                        }
                    }
                    strLogTempVN = strTagPuntajeInicio + strLogTempVN + strTagPuntajeFin;
                    switch (tmp.getInt("jugador") ) {
                        case 1:
                            strLogJ1 = strLogTempVO + strLogTempVN + strLogJ1;
                            c1++;
                            break;
                        case 2:
                            strLogJ2 = strLogTempVO + strLogTempVN +  strLogJ2;
                            c2++;
                            break;
                        case 0:
                            strLogJ1 = strLogTempVN +  strLogJ1;
                            strLogJ2 = strLogTempVN +  strLogJ2;
                            c1++;
                            c2++;
                        default:
                            break;
                    }
                }
            }
            if (c1 == 0) {
                strLogJ1 = ".<br/>.<br/>.";
            }
            if (c2 == 0) {
                strLogJ2 = ".<br/>.<br/>.";
            }
            tvLogJ2.setText(strLogVacio);
            tvLogJ1.setText(Html.fromHtml(strLogJ1, null, myHtmlHandler));
            tvLogJ2.setText(Html.fromHtml(strLogJ2, null, myHtmlHandler));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean ultimoMovimientoEmpate() {
        int posicion;
        try {
            JSONArray datosTemp = datosPartida;
            JSONObject obTemp;
            if ( posicionArrayActual != datosTemp.length() - 1) {
                posicion = posicionArrayActual - 1;
            } else {
                posicion = datosTemp.length() - 1;
            }
            obTemp = (JSONObject) datosTemp.get(posicion);
            if (obTemp.has("empate")) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e ) {
            e.printStackTrace();
        }
        return  true;

    };

    public void actionReiniciar() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        empezarNuevoJuego(numeroJuego);
                        mostrarDialogReinicio = false;
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        mostrarDialogReinicio = false;

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        if (numeroJuego >1 ){
                            reiniciarPartida();
                        }
                        mostrarDialogReinicio = false;
                        break;
                }
            }
        };
        if (numeroJuego <= 1) {
                if (mostrarDialogReinicio) {
                    if (ladoNumeros != 0) {
                        mostrarContenedorNumeros(ladoNumeros);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialogo_texto_reincio_duelo))
                            .setTitle(getString(R.string.dialogo_titulo_reincio))
                            .setPositiveButton(R.string.boton_positivo_fin_juego, dialogClickListener)
                            .setNegativeButton(R.string.boton_negativo_fin_juego, dialogClickListener)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mostrarDialogReinicio = false;
                                }
                            })
                            .show();
                }
            } else {
                if (mostrarDialogReinicio) {
                    if (ladoNumeros != 0) {
                        mostrarContenedorNumeros(ladoNumeros);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialogo_texto_reincio))
                            .setTitle(getString(R.string.dialogo_titulo_reincio))
                            .setPositiveButton(R.string.boton_positivo_reinicio, dialogClickListener)
                            .setNeutralButton(R.string.boton_neutral_reinicio, dialogClickListener)
                            .setNegativeButton(R.string.boton_negativo_reinicio, dialogClickListener)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mostrarDialogReinicio = false;
                                }
                            })
                            .show();
                }
            }
    }
}

