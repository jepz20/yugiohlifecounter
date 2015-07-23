package hn.jepz.www.yugiohlifecounter;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.Random;

public class MonedaActivity extends Activity {
    AnimationDrawable animacionMoneda;
    private ImageView imCara;
    private ImageView imCruz;
    private boolean mPrimeraImagen = true;
    private boolean termino = false;
    private int mContador, mVeces,mDuracion;

    private void aplicarRotacion (float inicio, float fin) {
        final float centerX  = imCara.getWidth() / 2.0f;
        final float centerY  = imCara.getHeight() / 2.0f;

        final Flip3dAnimation mRotacion =
                new Flip3dAnimation(inicio, fin, centerX, centerY,0);
        mRotacion.setDuration(mDuracion);
        if (mContador == 0) {
            mRotacion.setFillAfter(true);
            mRotacion.setInterpolator(new AccelerateInterpolator());
        }
        mRotacion.setAnimationListener(new MostrarSiguienteVista(imCara, imCruz));

        if (mPrimeraImagen)
        {
            imCara.startAnimation(mRotacion);
        } else {
            imCruz.startAnimation(mRotacion);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moneda);
        imCara= (ImageView) findViewById(R.id.imCara);
        imCruz= (ImageView) findViewById(R.id.imCruz);
        imCruz.setVisibility(View.GONE);

        imCara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                aplicarRotacion(0, 90);
            }
        });

        imCruz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciaValores();
                aplicarRotacion(0,90);
            }
        });
        reinciaValores();
        aplicarRotacion(0, 90);
    }

    public final class MostrarSiguienteVista implements Animation.AnimationListener {
        ImageView cara;
        ImageView cruz;

        public MostrarSiguienteVista(ImageView cara, ImageView cruz) {
            this.cara = cara;
            this.cruz = cruz;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            cara.post(new IntercambiarVistas(cara, cruz));
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    public final class IntercambiarVistas implements Runnable {
        ImageView cara;
        ImageView cruz;

        public IntercambiarVistas ( ImageView cara, ImageView cruz) {
            this.cara = cara;
            this.cruz = cruz;
        }

        public void run() {
            final float centerX = cara.getWidth() / 2.0f;
            final float centerY = cara.getHeight() / 2.0f;
            Flip3dAnimation rotacion;

            if (mPrimeraImagen) {
                cara.setVisibility(View.GONE);
                cruz.setVisibility(View.VISIBLE);
                cruz.requestFocus();

                rotacion = new Flip3dAnimation(-90,0,centerX,centerY,0);
            } else {
                cruz.setVisibility(View.GONE);
                cara.setVisibility(View.VISIBLE);
                cara.requestFocus();
                rotacion = new Flip3dAnimation(-90,0,centerX,centerY,0);
            }
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
                        mPrimeraImagen = !mPrimeraImagen;
                        aplicarRotacion(0, 90);
                    }
                    mContador++;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            if (mPrimeraImagen) {
                cruz.startAnimation(rotacion);
            } else {
                cara.startAnimation(rotacion);
            }
        }
    }

    /*Reinicia los valores*/
    private void reinciaValores() {
        mContador = 0;
        Random randomGenerator = new Random();
        mVeces= randomGenerator.nextInt(5) + 4;
        mDuracion =50;
    }
}



