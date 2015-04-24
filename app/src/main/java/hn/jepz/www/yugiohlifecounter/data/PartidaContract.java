package hn.jepz.www.yugiohlifecounter.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jepz2_000 on 3/20/2015.
 */
public class PartidaContract {
    public static final String CONTENT_AUTHORITY = "hn.jepz.www.yugiohlifecounter";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PARTIDA = "partida";
    public static final String PATH_MOVIMIENTO = "movimiento";

    public static final class PartidaEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARTIDA).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor:dir/" + CONTENT_AUTHORITY + "/" + PATH_PARTIDA;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor:item/" + CONTENT_AUTHORITY + "/" + PATH_PARTIDA;

        public static final String TABLE_NAME = "partida";
        public static final String COLUMN_PARTIDA_ID = "partida_id";
        public static final String COLUMN_FECHA_PARTIDA = "fecha_partida";
        public static final String COLUMN_DESCRIPCION_PARTIDA = "descripcion_partida";
        public static final String COLUMN_CANTIDAD_JUGADORES = "cantidad_jugadores";
        public static final String COLUMN_JUGADOR_1 = "jugador1";
        public static final String COLUMN_JUGADOR_2 = "jugador2";
        public static final String COLUMN_JUGADOR_3 = "jugador3";
        public static final String COLUMN_JUGADOR_4 = "jugador4";
        public static final String COLUMN_TIPO_PARTIDA = "tipo_partida";

        public static Uri buildPartidaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getPartidaFromUri (Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public  static final class MovimientosEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIMIENTO).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor:dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIMIENTO;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor:item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIMIENTO;
        public static final String TABLE_NAME = "movimiento";
        public static final String COLUMN_MOVIMIENTO_ID = "movimiento_id";
        public static final String COLUMN_VALOR_ORIGINAL = "valor_original";
        public static final String COLUMN_MATCH = "match";
        public static final String COLUMN_VALOR_OPERADO= "valor_operado";
        public static final String COLUMN_HORA = "hora";
        public static final String COLUMN_OPERADOR = "operador";
        public static final String COLUMN_JUGADOR = "jugador";
        public static final String COLUMN_TURNO = "turno";
        public static final String COLUMN_PARTIDA_ID = "partida_id";

        public static Uri buildMovimientoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getPartidaFromUri (Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
