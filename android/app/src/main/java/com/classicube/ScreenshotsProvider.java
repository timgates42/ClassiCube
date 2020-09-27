package com.classicube;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ScreenshotsProvider extends ContentProvider {
	@Override
	public boolean onCreate() { return true; }
	static String[] defaultCols = { OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE };

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		File file = uriToFile(uri);
		if (projection == null) projection = defaultCols;

		ArrayList<String> cols = new ArrayList<String>();
		ArrayList<Object> vals = new ArrayList<Object>();

		for (String col : projection) {
			if (OpenableColumns.DISPLAY_NAME.equals(col)) {
				cols.add(OpenableColumns.DISPLAY_NAME);
				vals.add(file.getName());
			} else if (OpenableColumns.SIZE.equals(col)) {
				cols.add(OpenableColumns.SIZE);
				vals.add(file.length());
			}
		}
		
		String columns[] = new String[cols.size()];
		columns = cols.toArray(columns);

		MatrixCursor cursor = new MatrixCursor(columns, 1);
		cursor.addRow(vals.toArray());
		return cursor;
	}

	@Override
	public String getType(Uri uri) { return "image/png"; }

	@Override
	public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException("No inserting screenshots"); }

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		File file = uriToFile(uri);
		return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
	}
	
	public static String getAuthority() { return "com.classicube.android.provider"; }

	public static Uri fileToUri(File file) {
		String path = Uri.encode(file.getPath(), "/");
		return new Uri.Builder().scheme("content")
				.authority(getAuthority()).encodedPath(path).build();
	}
		
	public static File uriToFile(Uri uri) {
		return new File(uri.getPath());
	}
}