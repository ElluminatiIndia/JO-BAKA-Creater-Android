package com.elluminati.jobaka;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;
import com.revmob.ads.fullscreen.RevMobFullscreen;
import com.revmob.ads.link.RevMobLink;
import com.revmob.ads.popup.RevMobPopup;
import com.revmob.internal.RMLog;

public class MainActivity extends Activity implements OnClickListener {
	private EditText etName;
	private EditText etSentance;
	private ImageButton btnShare, btnFb, btnTwitter, btnEllu, btnGPlus;
	private LinearLayout clipLayout;
	private ImageView ivRainbow;
	private ImageView ivChoose;
	float hsv[];
	private String uniqueId;
	private String current = null;
	private File imgPath;
	private String tempDir;
	private final int CHOOSE_IMAGE = 1;
	private final int TAKE_PHOTO = 2;
	private Bitmap bitmap;
	private String path;

	private File mFileTemp;
	private final String URL_FB = "https://www.facebook.com/ElluminatiIndia";
	private final String URL_TWITTER = "https://twitter.com/Elluminatiindia";
	private final String URL_GOOGLE = "https://plus.google.com/+ElluminatiIn/";
	private final String URL_ELLU = "http://elluminati.in/";
	RevMob revmob;
	boolean useUIThread = true;
	Activity currentActivity; // for anonymous classes
	RevMobFullscreen fullscreen;
	RevMobBanner banner;
	RevMobPopup popup;
	RevMobLink link;
	private File dafaultFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		etName = (EditText) findViewById(R.id.etName);
		btnShare = (ImageButton) findViewById(R.id.btnShare);
		btnFb = (ImageButton) findViewById(R.id.btnFb);
		btnGPlus = (ImageButton) findViewById(R.id.btnGplus);
		btnTwitter = (ImageButton) findViewById(R.id.btnTwitter);
		btnEllu = (ImageButton) findViewById(R.id.btnEllu);
		btnShare.setOnClickListener(this);
		btnEllu.setOnClickListener(this);
		btnFb.setOnClickListener(this);
		btnGPlus.setOnClickListener(this);
		btnTwitter.setOnClickListener(this);
		etSentance = (EditText) findViewById(R.id.etSentance);
		clipLayout = (LinearLayout) findViewById(R.id.clipView);
		clipLayout.setBackgroundColor(getResources().getColor(
				R.color.background));
		ivRainbow = (ImageView) findViewById(R.id.ivRainbow);
		ivChoose = (ImageView) findViewById(R.id.ivChoose);
		ivChoose.setOnClickListener(this);
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mFileTemp = new File(Environment.getExternalStorageDirectory(),
					Const.TEMP_PHOTO_FILE_NAME);
			dafaultFile = new File(Environment.getExternalStorageDirectory(),
					Const.TEMP_DEFAULT_FILE_NAME + System.currentTimeMillis()
							+ ".jpg");
		} else {
			mFileTemp = new File(getFilesDir(), Const.TEMP_PHOTO_FILE_NAME);
			dafaultFile = new File(getFilesDir(), Const.TEMP_DEFAULT_FILE_NAME
					+ System.currentTimeMillis() + ".jpg");
		}
		setAutoResizeSize();
		setRainbowPicker();
		revmob = RevMob.start(this);
		fullscreen = revmob.createFullscreen(this, null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setAutoResizeSize() {
		etName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					etName.setGravity(Gravity.LEFT | Gravity.TOP);

					etName.setBackgroundDrawable(null);
				} else {
					etName.setGravity(Gravity.CENTER);
					etName.setBackgroundResource(R.drawable.edit_text);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		final float size1 = 25, size2 = 15, size3 = 10;

		etSentance.setTextSize(size1);

		etSentance.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					etSentance.setGravity(Gravity.LEFT | Gravity.TOP);
					etSentance.setBackgroundDrawable(null);
					LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, 1);
					etSentance.setLayoutParams(param);
					etSentance.invalidate();
				} else {
					etSentance.setGravity(Gravity.CENTER);
					etSentance.setBackgroundResource(R.drawable.edit_text);
					LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT, 1);
					etSentance.setLayoutParams(param);
					etSentance.invalidate();
				}
				// if (s.length() > 15 && s.length() < 30) {
				// etSentance.setTextSize(size2);
				// } else if (s.length() > 30) {
				// etSentance.setTextSize(size3);
				// } else {
				// etSentance.setTextSize(size1);
				// }
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void setRainbowPicker() {
		hsv = new float[3];
		hsv[1] = 255;
		hsv[2] = 255;
		ivRainbow.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float y = event.getX();
					if (y < 0.f)
						y = 0.f;
					if (y > ivRainbow.getMeasuredWidth())
						y = ivRainbow.getMeasuredWidth() - 0.001f;

					float hue = 360.f - 360.f / ivRainbow.getMeasuredWidth()
							* y;
					if (hue == 360.f)
						hue = 0.f;
					hsv[0] = (360.f - hue);
					int col = Color.HSVToColor(hsv);
					clipLayout.setBackgroundColor(col);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnShare:
			if (TextUtils.isEmpty(etName.getText().toString())) {
				Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT)
						.show();
			} else if (TextUtils.isEmpty(etName.getText().toString())) {
				Toast.makeText(this, "Please enter description",
						Toast.LENGTH_SHORT).show();
			} else {
				save(clipLayout);
			}

			break;
		case R.id.ivChoose:
			showChooseImageDialog();
			break;
		case R.id.btnFb:
			openBrowser(URL_FB);
			break;
		case R.id.btnTwitter:
			openBrowser(URL_TWITTER);
			break;
		case R.id.btnGplus:
			openBrowser(URL_GOOGLE);
			break;
		case R.id.btnEllu:
			openBrowser(URL_ELLU);
			break;
		default:
			break;
		}
	}

	private void openBrowser(final String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	private void showChooseImageDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		String items[] = { "From Gallary", "From Camera" };
		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					pickImage();
					break;
				case 1:
					takePicture();
					break;

				}

			}
		});
		dialog.show();
	}

	public void save(View v) {
		if (TextUtils.isEmpty(path)) {
			path = dafaultFile.getAbsolutePath();
		}
		// return;
		v.setDrawingCacheEnabled(true);
		tempDir = Environment.getExternalStorageDirectory() + "/"
				+ getResources().getString(R.string.external_dir) + "/";
		Bitmap mBitmap = null;
		if (mBitmap == null) {
			mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
					Bitmap.Config.RGB_565);
			;
		}
		File directory = getDir(
				getResources().getString(R.string.external_dir),
				Context.MODE_PRIVATE);

		prepareDirectory();
		uniqueId = System.currentTimeMillis() + "_" + Math.random();
		// current = Environment.getExternalStorageDirectory() + "/"
		// + "tmp_sign.png";

		current = uniqueId + ".png";
		// mypath = new File(directory, current);
		imgPath = new File(path);
		if (!imgPath.exists())
			imgPath.delete();
		try {
			imgPath.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Canvas canvas = new Canvas(mBitmap);
		try {
			FileOutputStream mFileOutStream = new FileOutputStream(imgPath);
			v.draw(canvas);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
			mFileOutStream.flush();
			mFileOutStream.close();
			String url = Images.Media.insertImage(getContentResolver(),
					mBitmap, "title", null);
			Log.v("log_tag", "url: " + url);
			// Toast.makeText(this, imgPath.getAbsolutePath(),
			// Toast.LENGTH_SHORT)
			// .show();
			System.out.println(imgPath);
			if (imgPath != null) {
				initShareIntent();
			}
			// In case you want to delete the file
			// boolean deleted = mypath.delete();
			// Log.v("log_tag","deleted: " + mypath.toString() + deleted);
			// If you want to convert the image to string use base64
			// converter

		} catch (Exception e) {
			Log.v("log_tag", e.toString());
		}
	}

	private void initShareIntent() {

		// internal storage
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(new File(imgPath.getAbsolutePath()))); // optional//use
		// this when
		// you want to send an image
		shareIntent.setType("image/jpeg");
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivity(Intent.createChooser(shareIntent, "Share Image.."));

	}

	private boolean prepareDirectory() {
		try {
			if (makedirs()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(
					this,
					"Could not initiate File System.. Is Sdcard mounted properly?",
					1000).show();
			return false;
		}
	}

	private boolean makedirs() {
		File tempdir = new File(tempDir);
		if (!tempdir.exists())
			tempdir.mkdirs();

		if (tempdir.isDirectory()) {
			File[] files = tempdir.listFiles();
			for (File file : files) {
				if (!file.delete()) {
					System.out.println("Failed to delete " + file);
				}
			}
		}
		return (tempdir.isDirectory());
	}

	public void pickImage() {

		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, CHOOSE_IMAGE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		String picturePath = "";
		switch (requestCode) {
		case CHOOSE_IMAGE:
			switch (resultCode) {
			case Activity.RESULT_OK:

				if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK
						&& null != data) {
					if (bitmap != null)
						bitmap.recycle();
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					picturePath = cursor.getString(columnIndex);
					path = picturePath;
					cursor.close();
					bitmap = BitmapFactory.decodeFile(picturePath);
					ivChoose.setImageBitmap(bitmap);

				}
				break;
			}
			break;
		case TAKE_PHOTO:
			switch (resultCode) {
			case Activity.RESULT_OK:
				System.out.println(mFileTemp.getAbsolutePath());
				if (mFileTemp == null)
					return;

				if (bitmap != null)
					bitmap.recycle();
				picturePath = mFileTemp.getAbsolutePath();
				path = picturePath;
				bitmap = BitmapFactory.decodeFile(picturePath);
				ivChoose.setImageBitmap(bitmap);
				break;
			}
			break;

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (fullscreen != null) {
			fullscreen.show();
		}
		super.onBackPressed();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

	}

	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(mFileTemp);
			} else {
				/*
				 * The solution is taken from here:
				 * http://stackoverflow.com/questions
				 * /10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
				// mImageCaptureUri =
				// InternalStorageContentProvider.CONTENT_URI;
				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, TAKE_PHOTO);

		} catch (ActivityNotFoundException e) {

			e.printStackTrace();
		}
	}

	RevMobAdsListener revmobListener = new RevMobAdsListener() {
		@Override
		public void onRevMobSessionIsStarted() {

		}

		@Override
		public void onRevMobSessionNotStarted(String message) {

		}

		@Override
		public void onRevMobAdReceived() {

		}

		@Override
		public void onRevMobAdNotReceived(String message) {

		}

		@Override
		public void onRevMobAdDismiss() {

		}

		@Override
		public void onRevMobAdClicked() {

		}

		@Override
		public void onRevMobAdDisplayed() {

		}

		@Override
		public void onRevMobEulaIsShown() {
			RMLog.i("[RevMob Sample App] Eula is shown.");
		}

		@Override
		public void onRevMobEulaWasAcceptedAndDismissed() {
			RMLog.i("[RevMob Sample App] Eula was accepeted and dismissed.");
		}

		@Override
		public void onRevMobEulaWasRejected() {
			RMLog.i("[RevMob Sample App] Eula was rejected.");

		}
	};

}
