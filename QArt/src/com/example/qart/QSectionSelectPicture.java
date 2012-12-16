package com.example.qart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.qactionbar3.R;

public class QSectionSelectPicture extends SherlockFragment{
	
    public static final String ARG_SECTION_NUMBER = "section_number";
    
    private Button mLoadButton;
    private ImageView mImageView;
    //private Uri mSelectedImageUri;

    /* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		//renderView.onStop();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		MainActivity activity = (MainActivity)getActivity();

		View VPicSelect = inflater.inflate(R.layout.section_one, container, false);
		
//		TextView pwtextOne = (TextView) VPicSelect.findViewById(R.id.textView1);
//		Bundle args = getArguments();
//		pwtextOne.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
		
		mImageView = (ImageView) VPicSelect.findViewById(R.id.imageView);
		if (activity.getSelectedImageUri() != null){
			mImageView.setImageURI(activity.getSelectedImageUri());
		}
		
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
		
		return VPicSelect;
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
