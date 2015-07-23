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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A Fragmento donde esta la partida.
 */
public class PartidaFragment extends Fragment {
    private int posicionArrayActual, turno, nuevoTexto,cantidadJugadores, jugadorActual, contador1Simulacion,
            contador2Simulacion,contador1,contador2, valorTempo, tempoInicial, numeroJuego, valorInicial, totalJuegos;
    private int ladoNumeros; //0 ningun lado, 1 jugador1, 2 jugador2 etc..
    private int estadoTempo; //-1 sin iniciar, 0 detenido, 1 contando
    private TextView tvValorAOperar,tvContador1, tvContador2, tvTemp,tvTemporizador, tvEspaciadoDerecho, tvEspaciadoIzquierdo;
    private boolean simulando, borro;
    private boolean mostrarDialog = true;
    private Button btnTurno, btnSimulacion, btnFinSimulacion, btnAplicaSimulacion;
    private JSONArray datosPartida, partidaSimulacion, partida;
    private JSONObject duelo;
    private String str,ganadorJuego, ganadorDuelo ;
    private ImageView ivJ1J1,ivJ1J2,ivJ1J3,ivJ2J1,ivJ2J2,ivJ2J3;
    private LinearLayout contenedorGanados1, contenedorGanados2,contenedorNumeros, contenedorJugador1, contenedorJugador2;
    private Handler mHandler = new Handler();
    Thread threadContenedorNumeros;
    private BroadcastReceiver contadorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            actualizaValorTempo(intent);
        }
    };

    public PartidaFragment() {
    }

    public void manejarTemporizar () {
        //Si esta contando cancelarlo
        if (estadoTempo == 1) {
            try {
                getActivity().unregisterReceiver(contadorReceiver);
                getActivity().stopService(new Intent(getActivity(), BroadcastCountDownService.class));
            } catch (Exception e) {
            }
            estadoTempo = 0;
            int tempo;
            if (valorTempo <= 1) {
                tempo = tempoInicial;
            } else {
                tempo = valorTempo * 1000;
            }
            valorTempo =  tempo ;
            tvTemporizador.setBackgroundColor(getResources().getColor(R.color.simulacion));
        }
        //si esta detenido o nunca fue iniciado, iniciar o continuar en lo que estaba
        else if (estadoTempo < 1){
            definirTemporizador();
            Intent iniciarServicio = new Intent(getActivity(), BroadcastCountDownService.class);
            iniciarServicio.putExtra("valorTempo", valorTempo);
            getActivity().startService(iniciarServicio);
            getActivity().registerReceiver(contadorReceiver, new IntentFilter(BroadcastCountDownService.COUNTDOWN_BR));
            estadoTempo = 1;
        }
    }

    private void modificaFondo(int valor, TextView tvFondo) {
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

    private void simular(View rootView, int accion) {
        RelativeLayout principio = (RelativeLayout) rootView.findViewById(R.id.principio);
        // 0 = empieza simulacion, 1 = finalizar simulacion, 2 = aplicar simulacion
        if (accion == 0) {
            simulando = true;
            principio.setBackgroundColor(getResources().getColor(R.color.simulacion));
            try {
                partidaSimulacion = new JSONArray(datosPartida.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            contador1Simulacion = Integer.parseInt(tvContador1.getText().toString());
            contador2Simulacion = Integer.parseInt(tvContador2.getText().toString());
            btnSimulacion.setVisibility(View.INVISIBLE);
            btnFinSimulacion.setVisibility(View.VISIBLE);
            btnAplicaSimulacion.setVisibility(View.VISIBLE);

        } else if (accion == 1) {
            //TODO mejorar botones de undo y redo
            simulando = false;
            principio.setBackgroundColor(getResources().getColor(R.color.fondo));
            btnSimulacion.setVisibility(View.VISIBLE);
            try {
                datosPartida = new JSONArray(partidaSimulacion.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            posicionArrayActual = datosPartida.length();
            tvContador1.setText(Integer.toString(contador1Simulacion));
            tvContador2.setText(Integer.toString(contador2Simulacion));
            btnFinSimulacion.setVisibility(View.INVISIBLE);
            btnAplicaSimulacion.setVisibility(View.INVISIBLE);
            modificaFondo(contador1Simulacion, tvContador1);
            modificaFondo(contador2Simulacion, tvContador2);
        } else {
            simulando = false;
            principio.setBackgroundColor(getResources().getColor(R.color.fondo));
            Toast.makeText(getActivity(), "Se aplico la simulacion", Toast.LENGTH_SHORT).show();
            btnSimulacion.setVisibility(View.VISIBLE);
            btnFinSimulacion.setVisibility(View.INVISIBLE);
            btnAplicaSimulacion.setVisibility(View.INVISIBLE);
        }

    }

    private void verificaNuevoJuego (boolean savedInstance ) {
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
            if (ganadorDuelo.equals("0")) {
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
                                mostrarDialog = false;
                                break;
                        }
                    }
                };
                if (mostrarDialog) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getString(R.string.dialogo_texto_fin_juego) + " " + (numeroJuego+1) + "?")
                            .setTitle(getString(R.string.dialogo_titulo_fin_juego) + ganadorJuego + "!!! ")
                            .setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener)
                            .show();
                }
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                reiniciarPartida();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                mostrarDialog = false;
                                break;
                        }
                    }
                };
                if (mostrarDialog) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("EL GANADOR DEL DUELO ES EL JUGADOR " + ganadorDuelo+ "!!! \n Deseas Iniciar un nuevo Duelo?").setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        }
    }

    private void modificaIdentificadorGanados() {
        //TODO cuando se reversa despues de ganar siempre quedan los iconos que se gano
        if (numeroJuego == 1) {
            if ( ganadorJuego.equals("1")) {
                ivJ1J1.setImageResource(R.drawable.circulo_verde);
                ivJ2J1.setImageResource(R.drawable.circulo_rojo);
            } else {
                ivJ1J1.setImageResource(R.drawable.circulo_rojo);
                ivJ2J1.setImageResource(R.drawable.circulo_verde);
            }
        }

        if (numeroJuego == 2) {
            if ( ganadorJuego.equals("1")) {
                ivJ1J2.setImageResource(R.drawable.circulo_verde);
                ivJ2J2.setImageResource(R.drawable.circulo_rojo);
            } else {
                ivJ1J2.setImageResource(R.drawable.circulo_rojo);
                ivJ2J2.setImageResource(R.drawable.circulo_verde);
            }
        }

        if (numeroJuego == 3) {
            if ( ganadorJuego.equals("1")) {
                ivJ1J3.setImageResource(R.drawable.circulo_verde);
                ivJ2J3.setImageResource(R.drawable.circulo_rojo);
            } else {
                ivJ1J3.setImageResource(R.drawable.circulo_rojo);
                ivJ2J3.setImageResource(R.drawable.circulo_verde);
            }
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
        datosPartida = new JSONArray();
        posicionArrayActual = -1;
        numeroJuego = gNumeroJuego;
        contador1 = valorInicial;
        contador2 = valorInicial;
        turno = 1;
        ganadorJuego = "0";
        tvContador1.setText(Integer.toString(contador1));
        tvContador1.setBackgroundResource(R.drawable.gradient_100);
        tvContador2.setText(Integer.toString(contador2));
        tvContador2.setBackgroundResource(R.drawable.gradient_100);
        tvValorAOperar.setText("0");
    }

    private  void reiniciarPartida () {
        valorInicial = 8000;
        cantidadJugadores = 2;
        totalJuegos = 3;
        ganadorJuego = "0";
        ganadorDuelo = "0";
        tempoInicial = 2400000;
        duelo = new JSONObject();
        turno=1;
        borro = true;
        ladoNumeros = 0;
        btnTurno.setText("T:1");
        partida = new JSONArray();
        JSONObject partidaJson  = new JSONObject();
        try {

            //TODO quitar valos estaticos
                /*pongos los datos de la partida en un json */
            partidaJson.put("numeroJuego", Integer.toString(numeroJuego));
            partidaJson.put("ganador", ganadorJuego );
            partidaJson.put("datosPartida", datosPartida);
            partida.put(partidaJson);

                /*Datos del duelo */
            duelo.put("duelo_id", "1");
            duelo.put("cantidad_jugadores", cantidadJugadores);
            duelo.put("ganador", ganadorDuelo);
            duelo.put("ubicacion_id", "1");
            duelo.put("partida",partida);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nuevoTexto = 0; //0 es que hay nuevo texto; 1 ya existe el texto
        simulando = false;
        reiniciarTemporizador();
        btnSimulacion.setVisibility(View.VISIBLE);
        btnFinSimulacion.setVisibility(View.INVISIBLE);
        btnAplicaSimulacion.setVisibility(View.INVISIBLE);
        contenedorNumeros.setVisibility(View.GONE);
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
        JSONArray tempPartida = new JSONArray();
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

        try {
            datosPartida.put(movimiento);
            JSONObject partidaJSON = new JSONObject();
            partidaJSON.put("numeroJuego", Integer.toString(numeroJuego));
            partidaJSON.put("ganador", ganadorJuego );
            partidaJSON.put("datosPartida", datosPartida);
            for (int r = 0; r < numeroJuego -1; r++)  {
                tempPartida.put(partida.get(r));
            }
            partida = tempPartida;
            partida.put(partidaJSON);

            duelo.put("partida",partida);
            duelo.put("ganador", ganadorDuelo);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        posicionArrayActual = datosPartida.length();

        verificaNuevoJuego(false);
    }

    private JSONObject calculaValor(int valorContador, int valorAOperar, String sumaOResta, String jugador, TextView textView) {
        //Si suma es 0 si resta es 1, en caso que reste multiplicar el valor por -1
        int nuevoValor;
        if (sumaOResta.equals("-")) {
            nuevoValor = valorContador + (valorAOperar * -1);
        } else {
            nuevoValor = valorContador + valorAOperar;
        }

        if (nuevoValor <= 0) {
            nuevoValor = 0;
            valorAOperar = valorContador;
            if ( jugador.equals("1") ) {
                ganadorJuego = "2";
            } else {
                ganadorJuego = "1";
            }

        }
        nuevoTexto = 0;
        animarContador(valorContador, nuevoValor, textView);
        modificaFondo(nuevoValor, textView);
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
    }



    private int calculaValorAOperar(TextView vo) {
        int voint;
        String strValorAOperar = vo.getText().toString();
        if (strValorAOperar.length() == 0 || strValorAOperar.equals("0") ) {
            voint= 0;
        } else {
            voint= Integer.parseInt(strValorAOperar);
        }
        return voint;
    }

    private void definirTemporizador() {
        tvTemporizador.setBackgroundColor(getResources().getColor(R.color.rojo));
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
                ((valorTempo/1000) % 3600) / 60, ((valorTempo/1000) % 60));
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
            case R.id.action_moneda:
                actionMoneda();
                return true;
            case R.id.action_dado:
                actionDado();
                return true;
            case R.id.action_log:
                actionLog();
                return true;
            case R.id.action_reiniciar:
                actionReiniciar();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tvTemporizador = (TextView) rootView.findViewById(R.id.temporizador);
        tvValorAOperar = (TextView) (rootView.findViewById(R.id.tvValorOperado));
        tvContador1 = (TextView) (rootView.findViewById(R.id.contador1));
        tvContador2 = (TextView) (rootView.findViewById(R.id.contador2));
        btnSimulacion = (Button) rootView.findViewById(R.id.btnSimulacion);
        btnFinSimulacion = (Button) rootView.findViewById(R.id.btnFinSimulacion);
        btnAplicaSimulacion = (Button) rootView.findViewById(R.id.btnAplicaSimulacion);
        btnTurno = (Button) rootView.findViewById(R.id.turno);
        tvEspaciadoDerecho = (TextView) rootView.findViewById(R.id.espaciadoDerecho);
        tvEspaciadoIzquierdo = (TextView) rootView.findViewById(R.id.espaciadoIzquierdo);
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
        contenedorJugador2 = (LinearLayout) rootView.findViewById(R.id.contenedorJugador2);
        contenedorNumeros.setVisibility(View.GONE);
        contenedorJugador1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.CENTER_VERTICAL);
                mostrarContenedorNumeros(1);
            }
        });
        contenedorJugador2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.CENTER_VERTICAL);
                mostrarContenedorNumeros(2);
            }
        });

        //Handler para desaparecer los numero despues de x segundos

        threadContenedorNumeros = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!threadContenedorNumeros.isInterrupted()) {
                    try {
                        Thread.sleep(4000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (borro) {
                                    if (ladoNumeros != 0) {
                                        contenedorNumeros.setVisibility(View.GONE);
                                        ladoNumeros= 0;
                                        if (threadContenedorNumeros.isAlive()) {
                                            threadContenedorNumeros.interrupt();
                                        }
                                        borro = false;
                                    }
                                } else {
                                    borro = true;
                                }

                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        });
        tvTemporizador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                valorTempo = savedInstanceState.getInt("sValorTempo",0);
                String text = String.format("%02d:%02d", ((valorTempo/1000)% 3600) / 60, ((valorTempo/1000)% 60));
                tvTemporizador.setText(text);
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
                Log.v("PartidaFragment", "numeroJuego: " + numeroJuego );
                Log.v("PartidaFragment", "partida: " + partida.toString());

                mostrarDialog = savedInstanceState.getBoolean("sMostrarDialog");
                verificaNuevoJuego(true);
                numeroJuego = savedInstanceState.getInt("sNumeroJuego");
                ganadorJuego = savedInstanceState.getString("sGanadorJuego");

                if (savedInstanceState.getString("sDuelo").length() != 0 ) {
                    duelo= new JSONObject(savedInstanceState.getString("sDuelo"));
                }
                posicionArrayActual = savedInstanceState.getInt("sPosicionArrayActual");
                turno = savedInstanceState.getInt("sTurno");
                valorInicial = savedInstanceState.getInt("sValorInicial");
                cantidadJugadores = savedInstanceState.getInt("sCantidadJugadores");
                contador1 = savedInstanceState.getInt("sContador1");
                tvContador1.setText(Integer.toString(contador1));
                modificaFondo(contador1, tvContador1);
                contador2 = savedInstanceState.getInt("sContador2");
                tvContador2.setText(Integer.toString(contador2));
                modificaFondo(contador2, tvContador2);
                tvValorAOperar.setText(savedInstanceState.getString("sValorAOperar"));

                //Manejo de temporizador
                estadoTempo = savedInstanceState.getInt("sEstadoTempo", 0);
                if (estadoTempo == 1) {
                    getActivity().registerReceiver(contadorReceiver, new IntentFilter(BroadcastCountDownService.COUNTDOWN_BR));
                } else if (estadoTempo == 0) {
                    valorTempo = savedInstanceState.getInt("sValorTempo",0);
                    text = String.format("%02d:%02d", ((valorTempo/1000)% 3600) / 60, ((valorTempo/1000)% 60));
                    tvTemporizador.setText(text);
                    tvTemporizador.setBackgroundColor(getResources().getColor(R.color.simulacion));
                }
                //manejarTemporizar();

                simulando = savedInstanceState.getBoolean("sSimulando");
                if (simulando) {
                    RelativeLayout principio = (RelativeLayout) rootView.findViewById(R.id.principio);
                    principio.setBackgroundColor(getResources().getColor(R.color.simulacion));
                    try {
                        if (savedInstanceState.getString("sPartidaSimulacion").length() > 0){
                            partidaSimulacion = new JSONArray(savedInstanceState.getString("sPartidaSimulacion"));
                        } else {
                            partidaSimulacion = new JSONArray(datosPartida.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    contador1Simulacion = savedInstanceState.getInt("sContador1Simulacion");
                    contador2Simulacion = savedInstanceState.getInt("sContador1Simulacion");
                    btnSimulacion.setVisibility(View.INVISIBLE);
                    btnFinSimulacion.setVisibility(View.VISIBLE);
                    btnAplicaSimulacion.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        Button btnTemp;

        tvValorAOperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvValorAOperar.setText("");
            }
        });

        //Inicializa los botones de suma

            Button btnSuma = (Button) rootView.findViewById(R.id.suma1);

            btnSuma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int jugador;
                    TextView tvContador;
                    borro= false;
                    if (ladoNumeros == 1) {
                        jugador = 1;
                        tvContador = tvContador1;
                    } else {
                        jugador = 2;
                        tvContador = tvContador2;
                    }

                    int valorAOperar = calculaValorAOperar(tvValorAOperar);
                    int valorContador = Integer.parseInt(tvContador.getText().toString());
                    if (valorAOperar != 0) {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "+", Integer.toString(jugador), tvContador);
                        if (temp != null) {
                            agregaMovimiento(temp);
                        }
                    }
                }
            });
            Button btnResta = (Button) rootView.findViewById(R.id.resta1);

            btnResta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int jugador;
                    TextView tvContador;

                    if (ladoNumeros == 1) {
                        jugador = 1;
                        tvContador = tvContador1;
                    } else {
                        jugador = 2;
                        tvContador = tvContador2;
                    }
                    int valorAOperar = calculaValorAOperar(tvValorAOperar);
                    int valorContador = Integer.parseInt(tvContador.getText().toString());
                    borro= false;
                    if (valorAOperar != 0) {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "-", Integer.toString(jugador), tvContador);
                        if (temp != null) {
                            agregaMovimiento(temp);
                        }
                    }
                }
            });

        btnSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simular(rootView, 0);
            }
        });
        btnFinSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simular(rootView, 1);
            }
        });
        btnAplicaSimulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simular(rootView, 2);
            }
        });


        btnTurno.setText(getString(R.string.boton_turno) + ": "  + Integer.toString(turno));
        btnTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turno = turno + 1;
                btnTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
                JSONObject movimiento = new JSONObject();
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                    Date dt = new Date();
                    String strValue = timeFormat.format(dt);
                    movimiento.put("valor_original", 0);
                    movimiento.put("valor_operado", 0);
                    movimiento.put("valor_nuevo", 0);
                    movimiento.put("hora", strValue );
                    movimiento.put("operador", "t");
                    movimiento.put("jugador", "0");
                    movimiento.put("turno", turno);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                agregaMovimiento(movimiento);


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
                    borro= false;
                }
            });
        }

        return rootView;
    }

    private void mostrarContenedorNumeros( int lado) {
        if (ladoNumeros == 0) {
            ladoNumeros = lado;
            contenedorNumeros.setVisibility(View.VISIBLE);
            if (lado == 1 ) {
                tvEspaciadoIzquierdo.setVisibility(View.VISIBLE);
                tvEspaciadoDerecho.setVisibility(View.GONE);
            } else if (lado == 2) {
                tvEspaciadoIzquierdo.setVisibility(View.GONE);
                tvEspaciadoDerecho.setVisibility(View.VISIBLE);
            }
            if (!threadContenedorNumeros.isAlive()) {
                threadContenedorNumeros.start();
            }
        } else if (ladoNumeros != lado) {
            ladoNumeros = lado;
            if (lado == 1 ) {
                tvEspaciadoIzquierdo.setVisibility(View.VISIBLE);
                tvEspaciadoDerecho.setVisibility(View.GONE);
            } else if (lado == 2) {
                tvEspaciadoIzquierdo.setVisibility(View.GONE);
                tvEspaciadoDerecho.setVisibility(View.VISIBLE);
            }
            threadContenedorNumeros.interrupt();
            if (!threadContenedorNumeros.isAlive()) {
                threadContenedorNumeros.start();
            }
        } else {
            ladoNumeros = 0;
            contenedorNumeros.setVisibility(View.GONE);
            if (threadContenedorNumeros.isAlive()) {
                threadContenedorNumeros.interrupt();
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
        if (partidaSimulacion != null) {
            outState.putString("sPartidaSimulacion", "");
        }
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
        outState.putInt("sContador1Simulacion", contador1Simulacion);
        outState.putInt("sContador2Simulacion", contador2Simulacion);
        outState.putInt("sContador1", contador1);
        outState.putInt("sContador2", contador2);
        outState.putInt("sLadoNumeros", ladoNumeros);
        outState.putBoolean("sSimulando", simulando);
        outState.putBoolean("sMostrarDialog", mostrarDialog);
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
        valorTempo = ( int) segundos;
    }

    @Override
    public void onPause() {
        try {
            getActivity().unregisterReceiver(contadorReceiver);
        } catch (Exception e) {

        }
        super.onPause();
    }

    @Override
    public void onStop() {
        try{
            getActivity().unregisterReceiver(contadorReceiver);
        } catch (Exception e) {

        }
        super.onStop();
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
                switch (jugador) {
                    case "1":
                        tempTv = tvContador1;
                        break;
                    case "2":
                        tempTv = tvContador2;
                        break;
                    default:
                        tempTv = tvContador1;
                        break;
                }
                int valorContador = Integer.parseInt(tempTv.getText().toString());
                if (operador.equals("t")) {
                    turno = turno - 1;
                    btnTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
                } else {
                    if (operador.equals("+")) {
                        operador = "-";
                    } else {
                        operador = "+";
                    }
                    calculaValor(valorContador, valorOperado, operador, jugador, tempTv);
                }

                if (posicionArrayActual <= 0) {
                    posicionArrayActual = 0;
                }

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
                switch (jugador) {
                    case "1":
                        tempTv = tvContador1;
                        break;
                    case "2":
                        tempTv = tvContador2;
                        break;
                    default:
                        tempTv = tvContador1;
                        break;
                }

                if (operador.equals("t")) {
                    turno = turno + 1;
                    btnTurno.setText(getString(R.string.boton_turno) + ": " + Integer.toString(turno));
                } else {
                    int valorContador = Integer.parseInt(tempTv.getText().toString());
                    calculaValor(valorContador, valorOperado, operador, jugador, tempTv);
                    mostrarDialog = true;
                    verificaNuevoJuego(false);
                }
                posicionArrayActual = posicionArrayActual + 1;
                if (posicionArrayActual >= datosPartida.length()) {
                    posicionArrayActual = datosPartida.length();
                }
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
        logBundle.putInt("posicionArrayActual", posicionArrayActual);
        logBundle.putInt("valorInicial", valorInicial);
        intent.putExtras(logBundle);
        startActivity(intent);
    }

    public void actionReiniciar() {
        try {
            if (duelo.getJSONArray("partida").length() <= 1) {
                reiniciarPartida();
            } else {
                empezarNuevoJuego(numeroJuego);
            }
        } catch ( JSONException e) {
            e.printStackTrace();
        }
    }
}

