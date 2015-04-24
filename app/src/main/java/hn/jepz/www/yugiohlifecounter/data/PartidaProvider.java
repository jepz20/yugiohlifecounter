package hn.jepz.www.yugiohlifecounter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Jose Eduardo Perdomo on 3/22/2015.
 */
public class PartidaProvider extends ContentProvider {

    private static final int PARTIDA = 100;
    private static final int PARTIDA_ID = 101;
    private static final int MOVIMIENTO= 200;
    private static final int MOVIMIENTO_ID = 201;

    private PartidaDbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PartidaContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,PartidaContract.PATH_PARTIDA, PARTIDA);
        matcher.addURI(authority,PartidaContract.PATH_PARTIDA + "/#", PARTIDA_ID);
        matcher.addURI(authority,PartidaContract.PATH_MOVIMIENTO, MOVIMIENTO);
        matcher.addURI(authority,PartidaContract.PATH_MOVIMIENTO + "/#", MOVIMIENTO_ID);

        return matcher;
    }

    private static final SQLiteQueryBuilder sMovimientosPorPartidaBuilder;

    static {
        sMovimientosPorPartidaBuilder = new SQLiteQueryBuilder();
        sMovimientosPorPartidaBuilder.setTables(
                PartidaContract.PartidaEntry.TABLE_NAME + " INNER JOIN " +
                        PartidaContract.MovimientosEntry.TABLE_NAME +
                        " ON " + PartidaContract.MovimientosEntry.TABLE_NAME +
                        "." + PartidaContract.MovimientosEntry.COLUMN_PARTIDA_ID +
                        " = " + PartidaContract.PartidaEntry.TABLE_NAME +
                        "." + PartidaContract.PartidaEntry.COLUMN_PARTIDA_ID);
    }

    private static final String sBusquedaPorPartida =
            PartidaContract.PartidaEntry.TABLE_NAME +
                    "." + PartidaContract.PartidaEntry.COLUMN_PARTIDA_ID + " = ?";


    private Cursor getMovimientoPorPartida (Uri uri, String[] projection, String sortOder) {
        String partidaId = PartidaContract.PartidaEntry.getPartidaFromUri(uri);

        String [] selectionArgs = new String[]{partidaId};
        String selection = sBusquedaPorPartida;

        return sMovimientosPorPartidaBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOder);

    };

    @Override
    public boolean onCreate() {
        mOpenHelper = new PartidaDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retcursor;
        switch (sUriMatcher.match(uri)) {
            case PARTIDA: {
                retcursor = mOpenHelper.getReadableDatabase().query(
                        PartidaContract.PartidaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PARTIDA_ID: {
                String id = PartidaContract.PartidaEntry.getPartidaFromUri(uri);
                retcursor = mOpenHelper.getReadableDatabase().query(
                        PartidaContract.PartidaEntry.TABLE_NAME,
                        projection,
                        PartidaContract.PartidaEntry.COLUMN_PARTIDA_ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
//            case MOVIMIENTO_POR_PARTIDA: {
//                retcursor = getMovimientoPorPartida(uri, projection,sortOrder);
//                break;
//            }
            case MOVIMIENTO: {
                retcursor = mOpenHelper.getReadableDatabase().query(
                        PartidaContract.MovimientosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIMIENTO_ID: {
                retcursor = mOpenHelper.getReadableDatabase().query(
                        PartidaContract.MovimientosEntry.TABLE_NAME,
                        projection,
                        PartidaContract.MovimientosEntry.COLUMN_PARTIDA_ID + "=" + ContentUris.parseId(uri),
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri Desconocido: " + uri);
        }
        retcursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retcursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTIDA:
                return PartidaContract.PartidaEntry.CONTENT_TYPE;
            case PARTIDA_ID:
                return PartidaContract.PartidaEntry.CONTENT_ITEM_TYPE;
            case MOVIMIENTO:
                return PartidaContract.MovimientosEntry.CONTENT_TYPE;
            case MOVIMIENTO_ID:
                return PartidaContract.MovimientosEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
