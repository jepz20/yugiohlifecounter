package hn.jepz.www.yugiohlifecounter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import hn.jepz.www.yugiohlifecounter.data.PartidaContract;
import hn.jepz.www.yugiohlifecounter.data.PartidaDbHelper;

/**
 * Created by jepz2_000 on 3/20/2015.
 */
public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG = TestDb.class.getSimpleName();
     public void testCreateDb () throws Throwable {
         mContext.deleteDatabase(PartidaDbHelper.DATABASE_NAME);
         SQLiteDatabase db = new PartidaDbHelper(this.mContext).getWritableDatabase();
         assertEquals(true, db.isOpen());

         db.close();
     }

    ContentValues getPartidaContentValues() {

        String testDesc = "Partida de Prueba";
        String jugador1Test = "JOSE";
        String jugador2Test = "YENY";
        int testJugadores = 2;
        String testTipoPartida = "rapida";

        ContentValues values = new ContentValues();
        values.put(PartidaContract.PartidaEntry.COLUMN_DESCRIPCION_PARTIDA,testDesc);
        values.put(PartidaContract.PartidaEntry.COLUMN_CANTIDAD_JUGADORES,testJugadores);
        values.put(PartidaContract.PartidaEntry.COLUMN_JUGADOR_1,jugador1Test);
        values.put(PartidaContract.PartidaEntry.COLUMN_JUGADOR_2,jugador2Test);
        values.put(PartidaContract.PartidaEntry.COLUMN_TIPO_PARTIDA,testTipoPartida);
        return  values;
    }

    ContentValues getMovimientoContentValues() {
        int jugadorTest = 1;
        String operadorTest = "+";
        int turnoTest = 1;
        int valorOperadoTest = 2000;
        int valorOriginalTest = 8000;
        int matchTest = 1;
        int partidaIdTest = 1;
        int movimientoIdTest = 1;

        ContentValues movimientosValues = new ContentValues();
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_MOVIMIENTO_ID, movimientoIdTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_VALOR_ORIGINAL, valorOriginalTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_MATCH, matchTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_VALOR_OPERADO, valorOperadoTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_OPERADOR, operadorTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_JUGADOR, jugadorTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_TURNO, turnoTest);
        movimientosValues.put(PartidaContract.MovimientosEntry.COLUMN_PARTIDA_ID, partidaIdTest);
        return  movimientosValues;
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor) {
        Set<Map.Entry<String,Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx  = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue  = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }
    public void testInsertReadDb() {

        ContentValues partidaValues = getPartidaContentValues();
        PartidaDbHelper dbHelper = new PartidaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long partidaRowId;
        partidaRowId = db.insert(PartidaContract.PartidaEntry.TABLE_NAME, null, partidaValues);

        assertTrue(partidaRowId != -1);
        Log.d(LOG_TAG, "New Row id: " + partidaRowId );

        Cursor cursor = db.query(
                PartidaContract.PartidaEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(partidaValues,cursor);

            //Valores para el movimiento

            ContentValues movimientosValues = getMovimientoContentValues();

            long movimientoLongId;
            movimientoLongId = db.insert(PartidaContract.MovimientosEntry.TABLE_NAME, null, movimientosValues);
            Log.d(LOG_TAG, "New Row id: " + movimientoLongId);
            assertTrue(movimientoLongId != -1);


            Cursor movimientoCursor = db.query(
                    PartidaContract.MovimientosEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (movimientoCursor.moveToFirst()) {
                validateCursor(movimientosValues,movimientoCursor);
            }
            else {
                fail("No hay registros para movimientos");
            }
        } else {
            fail("No hay registros para partida");
        }


    }
}
