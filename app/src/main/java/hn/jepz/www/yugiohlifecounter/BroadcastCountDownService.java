package hn.jepz.www.yugiohlifecounter;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

/**
 * Created by jepz2_000 on 6/22/2015.
 */
public class BroadcastCountDownService  extends Service{

    private final static String TAG = "BroadcastCDService";

    public final static String COUNTDOWN_BR = "hn.jepz.wwww.yugiohlifecounter.countdown.br";

    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt;

    private int valorTempo;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        cdt.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        valorTempo = intent.getIntExtra("valorTempo",0);
        cdt = new CountDownTimer(valorTempo,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bi.putExtra("countdown",millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
            }
        };
        cdt.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
