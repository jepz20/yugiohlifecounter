package hn.jepz.www.yugiohlifecounter;

/**
 * Created by Jose Eduardo Perdomo on 4/23/2015.
 */

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
            contador2Simulacion, estadoTempo, valorTempo, tempoInicial, numeroJuego, valorInicial, totalJuegos;
    private TextView tvValorAOperar,tvContador1, tvContador2, tvTemp,tvTemporizador ;
    private boolean simulando;
    private Button btnTurno, btnReiniciar, btnSimulacion, btnFinSimulacion, btnAplicaSimulacion,
            btnUndo, btnRedo;
    private JSONArray datosPartida, partidaSimulacion, partida;
    private JSONObject duelo;
    private String str,ganadorJuego, ganadorDuelo ;
    private CountDownTimer cdtTempo;
    private ImageView ivJ1J1,ivJ1J2,ivJ1J3,ivJ2J1,ivJ2J2,ivJ2J3;

    public PartidaFragment() {
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
//                Log.v("modifcaValor", ";ength: " + Integer.toString(tvValor.length()) + "texto: " + texto);
            if (tvValor.length() -1 <= 0 || texto.equals("0") || texto.equals("")) {
                tvValor.setText("0");
            } else {
                tvValor.setText(texto.substring(0,tvValor.length()-1));
            }
        } else  if(!((s.equals("0") || s.equals("00")) && texto.equals("0"))) {
            if (texto.equals("0")) {
                texto = "";
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
//                Log.v("InicioSimulacion", partidaSimulacion.toString());
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
//                    Log.v("FinSimulacion", datosPartida.toString());
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

    private void verificaNuevoJuego () {

        if (!ganadorJuego.equals("0") && !simulando) {
            modificaIdentificadorGanados();
            numeroJuego = numeroJuego + 1;
            for (int y = 1; y <= cantidadJugadores; y++) {
                JSONObject temp;
                int contador = 0;
                for (int o = 0; o < partida.length(); o ++) {
                    try {
                        temp = (JSONObject) partida.get(o);
//                            Log.v("Temp", temp.toString());
                        if (temp.getString("ganador").equals(Integer.toString(y))) {
                            contador++;
                        }
                        if ( contador >= ((totalJuegos/2) + 1)){
                            ganadorDuelo = Integer.toString(y);
                            duelo.put("ganador", ganadorDuelo);
//                                Log.v("Duelo Fin", duelo.toString());
                            break;
                        }
//                            Log.v("Loop Ganador", "contador: " + contador);
//                            Log.v("Loop Ganador", "y: " + y);
//                            Log.v("Loop Ganador", "partidaget: " + temp.getString("ganador"));
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
                                empezarNuevoJuego(numeroJuego);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Gano el Jugador " + ganadorJuego + "!!! \n Deseas Iniciar el Juego No." + numeroJuego + "?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                reiniciarPartida();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("EL GANADOR DEL DUELO ES EL JUGADOR " + ganadorDuelo+ "!!! \n Deseas Iniciar un nuevo Duelo?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
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
    }

    private void empezarNuevoJuego(int gNumeroJuego) {
        datosPartida = new JSONArray();
        posicionArrayActual = -1;
        numeroJuego = gNumeroJuego;
        turno = 1;
        ganadorJuego = "0";
        tvContador1.setText(Integer.toString(valorInicial));
        tvContador1.setBackgroundResource(R.drawable.gradient_100);
        tvContador2.setText(Integer.toString(valorInicial));
        tvContador2.setBackgroundResource(R.drawable.gradient_100);
        tvValorAOperar.setText("0");
        btnRedo.setEnabled(false);
        btnUndo.setEnabled(false);
    }

    private  void reiniciarPartida () {
        valorInicial = 8000;
        cantidadJugadores = 2;
        totalJuegos = 3;
        ganadorJuego = "0";
        ganadorDuelo = "0";
        tempoInicial = 2400000;
        duelo = new JSONObject();
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
        empezarNuevoJuego(1);

        btnSimulacion.setVisibility(View.VISIBLE);
        btnFinSimulacion.setVisibility(View.INVISIBLE);
        btnAplicaSimulacion.setVisibility(View.INVISIBLE);
        modificaVisibilidadGanados(View.INVISIBLE);
        ivJ1J1.setImageResource(R.drawable.circulo_gris);
        ivJ1J2.setImageResource(R.drawable.circulo_gris);
        ivJ1J3.setImageResource(R.drawable.circulo_gris);
        ivJ2J1.setImageResource(R.drawable.circulo_gris);
        ivJ2J2.setImageResource(R.drawable.circulo_gris);
        ivJ2J3.setImageResource(R.drawable.circulo_gris);

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
//                Log.v("PartidaJson", partida.toString());

            duelo.put("partida",partida);
            duelo.put("ganador", ganadorDuelo);
//                Log.v("dueloJSON", duelo.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        posicionArrayActual = datosPartida.length();

        verificaNuevoJuego();
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
            movimiento.put("hora", strValue);
            movimiento.put("operador", sumaOResta);
            movimiento.put("jugador", jugador);
            movimiento.put("turno", turno);
//                Log.v("movimiento", movimiento.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movimiento;
    }

    /*Procedimiento para lanzar monedas*/
    private int lanzarMoneda() {
        int vecesGira = (int) ((Math.random()*Math.random()) *5 + 7);
        int cara = (int) (Math.random()*2 + 1) ;
        Toast.makeText(getActivity(), "Veces Gira: " +  vecesGira + " cara: " + cara, Toast.LENGTH_SHORT ).show();
        ImageView ivCara = new ImageView(getActivity());
        ivCara.setImageResource(R.drawable.cara);
        ImageView ivCruz = new ImageView(getActivity());
        ivCruz.setImageResource(R.drawable.cruz);

        FlipAnimator moneda = new FlipAnimator(ivCara, ivCruz, 0,0);
        //RelativeLayout layout = (RelativeLayout)
        return cara;
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
        cdtTempo = new CountDownTimer(valorTempo, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
                long segundos = millisUntilFinished / 1000;
                String text = String.format("%02d:%02d",
                        (segundos % 3600) / 60, (segundos % 60));
                tvTemporizador.setText(text);
                valorTempo = (int) millisUntilFinished / 1000;
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvTemporizador.setText("FIN");
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        };
    }

    private void reiniciarTemporizador() {
        if (estadoTempo == 1) {
            cdtTempo.cancel();
        }
        valorTempo = tempoInicial;
        String text = String.format("%02d:%02d",
                ((valorTempo/1000) % 3600) / 60, ((valorTempo/1000) % 60));
        tvTemporizador.setText(text);
        definirTemporizador();
        estadoTempo = 0;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tvTemporizador = (TextView) rootView.findViewById(R.id.temporizador);
        tvValorAOperar = (TextView) (rootView.findViewById(R.id.valor_operado));
        tvContador1 = (TextView) (rootView.findViewById(R.id.contador1));
        tvContador2 = (TextView) (rootView.findViewById(R.id.contador2));
        btnUndo = (Button) rootView.findViewById(R.id.undo);
        btnRedo = (Button) (rootView.findViewById(R.id.redo));
        btnSimulacion = (Button) rootView.findViewById(R.id.btnSimulacion);
        btnFinSimulacion = (Button) rootView.findViewById(R.id.btnFinSimulacion);
        btnAplicaSimulacion = (Button) rootView.findViewById(R.id.btnAplicaSimulacion);
        btnTurno = (Button) rootView.findViewById(R.id.turno);
        btnReiniciar = (Button) rootView.findViewById(R.id.btnReiniciar);
        ivJ1J1 = (ImageView) rootView.findViewById(R.id.ivJ1J1);
        ivJ1J2 = (ImageView) rootView.findViewById(R.id.ivJ1J2);
        ivJ1J3 = (ImageView) rootView.findViewById(R.id.ivJ1J3);
        ivJ2J1 = (ImageView) rootView.findViewById(R.id.ivJ2J1);
        ivJ2J2 = (ImageView) rootView.findViewById(R.id.ivJ2J2);
        ivJ2J3 = (ImageView) rootView.findViewById(R.id.ivJ2J3);
        tvTemporizador.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (estadoTempo == 1) {
                    cdtTempo.cancel();
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
                else {
                    definirTemporizador();
                    cdtTempo.start();
                    estadoTempo = 1;
                }
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
        Button btnTemp;

        tvValorAOperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvValorAOperar.setText("");
            }
        });

        //Inicializa los botones de suma
        for ( jugadorActual = 1; jugadorActual <= cantidadJugadores; jugadorActual++) {
            switch (jugadorActual) {
                case 1: btnTemp = (Button) rootView.findViewById(R.id.suma1);
                    tvTemp = tvContador1;
                    break;
                case 2: btnTemp = (Button) rootView.findViewById(R.id.suma2);
                    tvTemp = tvContador2;
                    break;
                default: btnTemp= (Button) rootView.findViewById(R.id.suma1);
                    tvTemp = tvContador1;
                    break;
            }
            btnTemp.setOnClickListener(new View.OnClickListener() {
                private final int jugador = jugadorActual;
                private final TextView tvContador = tvTemp;
                @Override
                public void onClick(View v) {
                    int valorAOperar = calculaValorAOperar(tvValorAOperar);
                    int valorContador = Integer.parseInt(tvContador.getText().toString());
                    if (valorAOperar != 0) {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "+", Integer.toString(jugador), tvContador);
                        if (temp != null) {
                            agregaMovimiento(temp);
                            rootView.findViewById(R.id.undo).setEnabled(true);
                            rootView.findViewById(R.id.redo).setEnabled(false);
                        }
                    }
                }
            });
        }
        // Inicializa los botones de la resta
        for ( jugadorActual = 1; jugadorActual <= cantidadJugadores; jugadorActual++) {
            switch (jugadorActual) {
                case 1: btnTemp = (Button) rootView.findViewById(R.id.resta1);
                    tvTemp = tvContador1;
                    break;
                case 2: btnTemp = (Button) rootView.findViewById(R.id.resta2);
                    tvTemp = tvContador2;
                    break;
                default: btnTemp= (Button) rootView.findViewById(R.id.resta1);
                    tvTemp = tvContador1;
                    break;
            }
            btnTemp.setOnClickListener(new View.OnClickListener() {
                private final int jugador = jugadorActual;
                private final TextView tvContador = tvTemp;
                @Override
                public void onClick(View v) {
                    int valorAOperar = calculaValorAOperar(tvValorAOperar);
                    int valorContador = Integer.parseInt(tvContador.getText().toString());

                    if (valorAOperar != 0) {
                        JSONObject temp = calculaValor(valorContador, valorAOperar, "-", Integer.toString(jugador), tvContador);
                        if (temp != null) {
                            agregaMovimiento(temp);
                            rootView.findViewById(R.id.undo).setEnabled(true);
                            rootView.findViewById(R.id.redo).setEnabled(false);
                        }
                    }
                }
            });
        }

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        btnReiniciar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                reiniciarPartida();
                return true;
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
                    movimiento.put("hora", strValue );
                    movimiento.put("operador", "t");
                    movimiento.put("jugador", "0");
                    movimiento.put("turno", turno);
                    Log.v("movimiento", movimiento.toString());
                    rootView.findViewById(R.id.undo).setEnabled(true);
                    rootView.findViewById(R.id.redo).setEnabled(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                agregaMovimiento(movimiento);


            }
        });


        btnUndo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posicionArrayActual > 0) {
                    try {
                        posicionArrayActual = posicionArrayActual - 1;
                        JSONObject temp = (JSONObject) (datosPartida.get(posicionArrayActual));
                        int valorOperado = temp.getInt("valor_operado");
                        String operador = temp.getString("operador");
                        String jugador = temp.getString("jugador");
                        TextView tempTv;
                        switch (jugador) {
                            case "1" : tempTv = tvContador1;
                                break;
                            case "2": tempTv = tvContador2;
                                break;
                            default: tempTv = tvContador1;
                                break;
                        }
                        int valorContador = Integer.parseInt(tempTv.getText().toString());
                        if (operador.equals("t")) {
                            turno = turno - 1;
                            btnTurno.setText(getString(R.string.boton_turno) + ": "  + Integer.toString(turno));
                        } else {
                            if (operador.equals("+")) {
                                operador = "-";
                            } else {
                                operador = "+";
                            }
                            calculaValor(valorContador, valorOperado, operador, jugador, tempTv);
                            rootView.findViewById(R.id.redo).setEnabled(true);
                        }

                        if (posicionArrayActual <= 0) {
                            rootView.findViewById(R.id.undo).setEnabled(false);
                            posicionArrayActual = 0;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    rootView.findViewById(R.id.undo).setEnabled(false);
                }
            }
        });

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            rootView.findViewById(R.id.undo).setEnabled(true);
                        }
                        posicionArrayActual = posicionArrayActual + 1;
                        if (posicionArrayActual >= datosPartida.length()) {
                            rootView.findViewById(R.id.redo).setEnabled(false);
                            posicionArrayActual = datosPartida.length();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    rootView.findViewById(R.id.redo).setEnabled(false);
                }
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
                }
            });
        }
        btnUndo.setEnabled(false);
        btnRedo.setEnabled(false);

        Button btnMoneda = (Button)  rootView.findViewById(R.id.lanzar_moneda);

        btnMoneda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarMoneda();
                RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.loMoneda);
                ImageView ivCara = (ImageView) layout.findViewById(R.id.ivCara);
                ivCara.setImageResource(R.drawable.cara);
                ImageView ivCruz = (ImageView) layout.findViewById(R.id.ivCruz);
                ivCruz.setImageResource(R.drawable.cruz);
                layout.setVisibility(View.INVISIBLE);
//                    ivCruz.setVisibility(View.INVISIBLE);
                ivCara.setVisibility(View.INVISIBLE);
                FlipAnimator moneda = new FlipAnimator(ivCruz,ivCara, 100,100);
                layout.startAnimation(moneda);
            }
        });
        return rootView;
    }
}