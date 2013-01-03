package com.example.qart;

import java.util.Random;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;

public class QSectionSelectPicture extends SherlockFragment{
	
    public static final String ARG_SECTION_NUMBER = "section_number";
    
    private Button mLoadButton;
    private Button mRandomButton;
    private Button mExampleButton;
    private ImageView mImageView;
    
    private Random mRand;

    /* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.w(this.getClass().getName(), "onDestroy");
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		Log.w(this.getClass().getName(), "onPause");
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		MainActivity activity = (MainActivity)getActivity();
		Log.w(this.getClass().getName(), "onResume");

		if ((mImageView!=null) && (activity.getSelectedImageBitmap() != null)){
			mImageView.setImageBitmap(activity.getSelectedImageBitmap());
		}

		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.w(this.getClass().getName(), "onStart");
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.w(this.getClass().getName(), "onStop");
		super.onStop();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	super.onCreateView(inflater, container, savedInstanceState);
    	
		Log.w(this.getClass().getName(), "onCreateView");
    	
		mRand = new Random();
    	
		View VPicSelect = inflater.inflate(R.layout.section_one, container, false);
				
		mImageView = (ImageView) VPicSelect.findViewById(R.id.imageView);
		
		mLoadButton = (Button) VPicSelect.findViewById(R.id.btnLoadImage);
		
		mLoadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        Intent intent = new Intent();
		        intent.setType("image/*");
		        intent.setAction(Intent.ACTION_GET_CONTENT);
		        startActivityForResult(Intent.createChooser(intent,
		                "Select Picture"), MainActivity.REQUEST_CHOOSE_IMAGE);
		        
		        Log.d("DEBUG:", "Code: " + MainActivity.REQUEST_CHOOSE_IMAGE);
			}
		});

		mRandomButton = (Button) VPicSelect.findViewById(R.id.btnRandomImage);
		
		mRandomButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setRandomImage();
		        Log.d("DEBUG:", "random image");
			}
		});

		mExampleButton = (Button) VPicSelect.findViewById(R.id.btnExampleImage);
		
		mExampleButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setExampleImage();
		        Log.d("DEBUG:", "example image");
			}
		});

		return VPicSelect;
    }
    
    private void setExampleImage(){
    	Uri uri = Uri.parse("android.resource://com.example.qart/" + R.drawable.cartoon_star);
    	MainActivity activity = (MainActivity)getActivity();
    	activity.setSelectedImageUri(uri);
    	mImageView.setImageURI(uri);
    }
    
    public void setRandomImage(){
		ContentResolver cr = getActivity().getContentResolver();

		String[] columns = new String[] {
		                ImageColumns._ID,
		                ImageColumns.TITLE,
		                ImageColumns.DATA,
		                ImageColumns.MIME_TYPE,
		                ImageColumns.SIZE };
		Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		                columns, null, null, null);

		// if there are no images we give up
		if (cur == null)
			return;
		
		int numImages = cur.getCount();
		int selImage = mRand.nextInt(numImages);
		
		cur.move(selImage);
		
		int    imageID = cur.getInt(0);
//		String strTitle = cur.getString(1);
//		String strData = cur.getString(2);
		
		Uri uri = Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Integer.toString(imageID) );

		cur.close();
		
    	mImageView.setImageURI(uri);
    	
    	MainActivity activity = (MainActivity)getActivity();
    	activity.setSelectedImageUri(uri);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == MainActivity.REQUEST_CHOOSE_IMAGE) {
	        if (resultCode == Activity.RESULT_OK) {

	        	MainActivity activity = (MainActivity)getActivity();

	        	// onActivityResult is called before any other Activity function.
	        	// Just get the selected path from the selected picture
	        	activity.setSelectedImageUri(data.getData());
	        	//String mSelectedImagePath = getPath(selectedImageUri);
	        	
	        	mImageView.setImageURI(activity.getSelectedImageUri());
	            
	            // Execution continues in onRestart()       
	        }
	    }
	}
	
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}

}
