package hn.jepz.www.yugiohlifecounter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;


public class DadoActivity extends Activity {
    private ImageView imDado1;
    private ImageView imDado2;
    private ImageView imDado3;
    private ImageView imDado4;
    private ImageView imDado5;
    private ImageView imDado6;
    private ImageView imDadoA;
    private ImageView imDadoB;

    private int mContador, mVeces,mDuracion;
    private JSONArray dadoaa, dadoid;
    private int  lado, direccion;
    private float inicio = 0;
    private float fin= 90;

    private void aplicarRotacion (float inicio, float fin) {

        final float centerX  = imDadoA.getWidth() / 2.0f;
        final float centerY  = imDadoA.getHeight() / 2.0f;

        final Flip3dAnimation mRotacion =
                new Flip3dAnimation(inicio, fin, centerX, centerY, direccion);
        mRotacion.setDuration(mDuracion);
        if (mContador == 0) {
            mRotacion.setFillAfter(true);
            mRotacion.setInterpolator(new AccelerateInterpolator());
        }
        mRotacion.setAnimationListener(new MostrarSiguienteVista(imDadoA, imDadoB));

        imDadoA.startAnimation(mRotacion);

    }

    public final class MostrarSiguienteVista implements Animation.AnimationListener {
        ImageView dadoA;
        ImageView dadoB;

        public MostrarSiguienteVista(ImageView dadoA, ImageView dadoB) {
            this.dadoA = dadoA;
            this.dadoB = dadoB;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dadoA.post(new IntercambiarVistas(dadoA, dadoB));
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
    public final class IntercambiarVistas implements Runnable {
        ImageView dadoA;
        ImageView dadoB;

        public IntercambiarVistas ( ImageView dadoA, ImageView dadoB) {
            this.dadoA = dadoA;
            this.dadoB = dadoB;
        }

        public void run() {
            final float centerX = dadoA.getWidth() / 2.0f;
            final float centerY = dadoA.getHeight() / 2.0f;
            Flip3dAnimation rotacion;

            dadoA.setVisibility(View.GONE);
            dadoB.setVisibility(View.VISIBLE);
            dadoB.requestFocus();
            rotacion = new Flip3dAnimation(fin*-1,inicio*-1,centerX,centerY, direccion);

            rotacion.setDuration(mDuracion);
            rotacion.setFillAfter(true);
            if (mContador == mVeces) {
                rotacion.setInterpolator(new DecelerateInterpolator());
            }
            rotacion.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mContador <= mVeces) {
                        if (mContador/mVeces >= 0.4) {
                            mDuracion = mDuracion + ((200)*mContador/mVeces);
                        }
//                        reinciaValores();
                        girarDado();
                        aplicarRotacion(inicio, fin);
                    }
                    mContador++;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            dadoB.startAnimation(rotacion);
        }
    }
    private void girarAdelante(){
        inicio=0;
        fin=90;
        direccion=1;
        JSONArray tempaa, tempid;
        tempaa = new JSONArray();
        tempid = new JSONArray();
        try {
            tempaa.put(dadoaa.getString(3));
            tempaa.put(dadoaa.getString(0));
            tempaa.put(dadoaa.getString(1));
            tempaa.put(dadoaa.getString(2));

            tempid.put(tempaa.getString(0));
            tempid.put(dadoid.getString(1));
            tempid.put(tempaa.getString(2));
            tempid.put(dadoid.getString(3));

            dadoaa = tempaa;
            dadoid = tempid;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void girarAtras(){
        inicio=0;
        fin=-90;
        direccion=1;
        JSONArray tempaa, tempid;
        tempaa = new JSONArray();
        tempid = new JSONArray();
        try {
            tempaa.put(dadoaa.getString(1));
            tempaa.put(dadoaa.getString(2));
            tempaa.put(dadoaa.getString(3));
            tempaa.put(dadoaa.getString(0));

            tempid.put(tempaa.getString(0));
            tempid.put(dadoid.getString(1));
            tempid.put(tempaa.getString(2));
            tempid.put(dadoid.getString(3));

            dadoaa = tempaa;
            dadoid = tempid;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void girarDerecha(){
        inicio=0;
        fin=90;
        direccion=0;
        JSONArray tempaa, tempid;
        tempaa = new JSONArray();
        tempid = new JSONArray();
        try {
            tempid.put(dadoid.getString(3));
            tempid.put(dadoid.getString(0));
            tempid.put(dadoid.getString(1));
            tempid.put(dadoid.getString(2));

            tempaa.put(tempid.getString(0));
            tempaa.put(dadoaa.getString(1));
            tempaa.put(tempid.getString(2));
            tempaa.put(dadoaa.getString(3));

            dadoaa = tempaa;
            dadoid = tempid;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void girarIzquierda(){
        inicio=0;
        fin=-90;
        direccion=0;
        JSONArray tempaa, tempid;
        tempaa = new JSONArray();
        tempid = new JSONArray();
        try {
            tempid.put(dadoid.getString(1));
            tempid.put(dadoid.getString(2));
            tempid.put(dadoid.getString(3));
            tempid.put(dadoid.getString(0));

            tempaa.put(tempid.getString(0));
            tempaa.put(dadoaa.getString(1));
            tempaa.put(tempid.getString(2));
            tempaa.put(dadoaa.getString(3));

            dadoaa = tempaa;
            dadoid = tempid;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void girarDado() {
        imDado1.setVisibility(View.GONE);
        imDado2.setVisibility(View.GONE);
        imDado3.setVisibility(View.GONE);
        imDado4.setVisibility(View.GONE);
        imDado5.setVisibility(View.GONE);
        imDado6.setVisibility(View.GONE);
        Random randomGenerator = new Random();
        lado= randomGenerator.nextInt(4);
        try {
            Log.v("DadoActivity", "Lado que Gira: " + lado);
            Log.v("DadoActivity", "Lado que Viene(A): " + dadoaa.getInt(0));
            switch (dadoaa.getInt(0)) {
                case 1: {
                    imDadoA = imDado1;
                    break;
                }
                case 2: {
                    imDadoA = imDado2;
                    break;
                }
                case 3: {
                    imDadoA = imDado3;
                    break;
                }
                case 4: {
                    imDadoA = imDado4;
                    break;
                }
                case 5: {
                    imDadoA = imDado5;
                    break;
                }
                case 6: {
                    imDadoA = imDado6;
                    break;
                }

            }
            imDadoA.setVisibility(View.VISIBLE);
            switch (lado) {
                case 0: {
                    girarAdelante();
                    break;
                }
                case 1: {
                    girarAtras();
                    break;
                }
                case 2: {
                    girarDerecha();
                    break;
                }
                case 3: {
                    girarIzquierda();
                    break;
                }
                default: {
                    break;
                }
            }
            Log.v("DadoActivity", "Lado al que va(B): " + dadoaa.getInt(0));
            switch (dadoaa.getInt(0)) {
                case 1: {
                    imDadoB = imDado1;
                    break;
                }
                case 2: {
                    imDadoB = imDado2;
                    break;
                }
                case 3: {
                    imDadoB = imDado3;
                    break;
                }
                case 4: {
                    imDadoB = imDado4;
                    break;
                }
                case 5: {
                    imDadoB = imDado5;
                    break;
                }
                case 6: {
                    imDadoB = imDado6;
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*Reinicia los valores*/
    private void reinciaValores() {
        mContador = 0;
        Random randomGenerator = new Random();
        mVeces  = randomGenerator.nextInt(7) + 5;
        mDuracion =30;
        direccion = 0;
        imDado1.setVisibility(View.GONE);
        imDado2.setVisibility(View.GONE);
        imDado3.setVisibility(View.GONE);
        imDado4.setVisibility(View.GONE);
        imDado5.setVisibility(View.GONE);
        imDado6.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dadoid = new JSONArray();
        dadoaa = new JSONArray();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dado);
        dadoaa.put(5);
        dadoaa.put(1);
        dadoaa.put(2);
        dadoaa.put(6);
        dadoid.put(5);
        dadoid.put(3);
        dadoid.put(2);
        dadoid.put(4);

        imDado1= (ImageView) findViewById(R.id.imDado1);
        imDado2= (ImageView) findViewById(R.id.imDado2);
        imDado3= (ImageView) findViewById(R.id.imDado3);
        imDado4= (ImageView) findViewById(R.id.imDado4);
        imDado5= (ImageView) findViewById(R.id.imDado5);
        imDado6= (ImageView) findViewById(R.id.imDado6);
        imDado1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                girarDado();
                aplicarRotacion(inicio, fin);
            }
        });
        imDado2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                girarDado();
                aplicarRotacion(inicio, fin);
            }
        });
        imDado3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                girarDado();
                aplicarRotacion(inicio, fin);
            }
        });
        imDado4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                girarDado();
                aplicarRotacion(inicio, fin);
            }
        });
        imDado5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                girarDado();
                aplicarRotacion(inicio, fin);
            }
        });
        imDado6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                girarDado();
                aplicarRotacion(inicio, fin);
            }
        });
        reinciaValores();
        girarDado();
        aplicarRotacion(inicio, fin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
