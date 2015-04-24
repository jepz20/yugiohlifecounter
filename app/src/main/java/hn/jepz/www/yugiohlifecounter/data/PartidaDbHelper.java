package hn.jepz.www.yugiohlifecounter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jepz2_000 on 3/20/2015.
 */
public class PartidaDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "partida.db";

    public PartidaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO crear la tabla de partida y moviemiento

        final String SQL_CREATE_PARTIDA_TABLE =
                "CREATE TABLE " + PartidaContract.PartidaEntry.TABLE_NAME + " ("  +
                        PartidaContract.PartidaEntry.COLUMN_PARTIDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PartidaContract.PartidaEntry.COLUMN_FECHA_PARTIDA + " DATE DEFAULT (datetime('now', 'localtime')), " +
                        PartidaContract.PartidaEntry.COLUMN_DESCRIPCION_PARTIDA + " TEXT, " +
                        PartidaContract.PartidaEntry.COLUMN_CANTIDAD_JUGADORES + " INTEGER NOT NULL, " +
                        PartidaContract.PartidaEntry.COLUMN_JUGADOR_1 + " TEXT, " +
                        PartidaContract.PartidaEntry.COLUMN_JUGADOR_2 + " TEXT, " +
                        PartidaContract.PartidaEntry.COLUMN_JUGADOR_3 + " TEXT, " +
                        PartidaContract.PartidaEntry.COLUMN_JUGADOR_4 + " TEXT, " +
                        PartidaContract.PartidaEntry.COLUMN_TIPO_PARTIDA + " TEXT NOT NULL)";

        final String SQL_CREATE_MOVIMIENTO_TABLE =
                "CREATE TABLE " + PartidaContract.MovimientosEntry.TABLE_NAME + " (" +
                        PartidaContract.MovimientosEntry.COLUMN_MOVIMIENTO_ID + " INTEGER NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_VALOR_ORIGINAL + " INTEGER NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_MATCH + " INTEGER NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_VALOR_OPERADO + " INTEGER NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_HORA + " DATE DEFAULT (datetime('now', 'localtime')), " +
                        PartidaContract.MovimientosEntry.COLUMN_OPERADOR + " TEXT NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_JUGADOR + " INTEGER NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_TURNO + " INTEGER NOT NULL, " +
                        PartidaContract.MovimientosEntry.COLUMN_PARTIDA_ID + " INTEGER NOT NULL, " +
                        " FOREIGN KEY (" + PartidaContract.MovimientosEntry.COLUMN_PARTIDA_ID + ") REFERENCES " +
                        PartidaContract.PartidaEntry.TABLE_NAME + " (" + PartidaContract.PartidaEntry.COLUMN_PARTIDA_ID + "))" ;
        db.execSQL(SQL_CREATE_PARTIDA_TABLE);
        db.execSQL(SQL_CREATE_MOVIMIENTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PartidaContract.PartidaEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PartidaContract.MovimientosEntry.TABLE_NAME);
        onCreate(db);
    }
}
