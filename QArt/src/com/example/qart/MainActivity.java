package com.example.qart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	public static final int REQUEST_CHOOSE_IMAGE  = 1;

	public static final String RETAIN_IMAGE_URI  = "image_uri";

	// Settings
	private int mDifficulty = 0;
	private boolean mShowNumbers = true;
	private boolean mShowGrid = false;
	private boolean mSettingsChanged = false;
	private boolean mSettingsChange = false;
	
    public static final String[] SELECTION = 
    {
            "Easy 3x3",
            "Medium 4x4",
            "Difficult 5x5",
            "Toggle numbers",
            "Toggle grid"
    };
	// will be set when chooser activity returns
//	private String mSelectedImagePath;
	
    private Uri mSelectedImageUri;

    private Bitmap mSelectedImageBitmap;

    
	/**
	 * @return the mShowGrid
	 */
	public boolean isShowGrid() {
		return mShowGrid;
	}

	/**
	 * @param mShowGrid the mShowGrid to set
	 */
	public void setShowGrid(boolean mShowGrid) {
		this.mShowGrid = mShowGrid;
	}

	/**
	 * @return the mSettingsChange
	 */
	public boolean isSettingsChange() {
		return mSettingsChange;
	}

	/**
	 * @param mSettingsChange the mSettingsChange to set
	 */
	public void setSettingsChange(boolean mSettingsChange) {
		this.mSettingsChange = mSettingsChange;
	}

	/**
	 * @return the mSettingsChanged
	 */
	public boolean isSettingsChanged() {
		return mSettingsChanged;
	}

	/**
	 * @param mSettingsChanged the mSettingsChanged to set
	 */
	public void setSettingsChanged(boolean changed) {
		this.mSettingsChanged = changed;
	}

	/**
	 * @return the mShowNumbers
	 */
	public boolean isShowNumbers() {
		return mShowNumbers;
	}

	/**
	 * @param mShowNumbers the mShowNumbers to set
	 */
	public void setShowNumbers(boolean mShowNumbers) {
		this.mShowNumbers = mShowNumbers;
	}

	/**
	 * @return the mSelectedImageBitmap
	 */
	public Bitmap getSelectedImageBitmap() {
		return mSelectedImageBitmap;
	}

	/**
	 * @return the mDifficulty
	 */
	public int getDifficulty() {
		return mDifficulty;
	}

	/**
	 * @param mDifficulty the mDifficulty to set
	 */
	public void setDifficulty(int mDifficulty) {
		this.mDifficulty = mDifficulty;
	}

	/**
	 * @return the mSelectedImageUri
	 */
	public Uri getSelectedImageUri() {
		return mSelectedImageUri;
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(RETAIN_IMAGE_URI, mSelectedImageUri.toString());
	}
	
	/**
	 * @param mSelectedImageUri the mSelectedImageUri to set
	 */
	public void setSelectedImageUri(Uri selectedImageUri) {
		this.mSelectedImageUri = selectedImageUri;
		if (mSelectedImageBitmap != null)
			mSelectedImageBitmap.recycle();
		mSelectedImageBitmap = loadBitmap(mSelectedImageUri);
	}
	
	  private Bitmap loadBitmap(Uri imageFileUri) {
		    Display currentDisplay = getWindowManager().getDefaultDisplay();

		    float dw = currentDisplay.getWidth();
		    float dh = currentDisplay.getHeight();

		    Bitmap returnBmp = Bitmap.createBitmap((int) dw, (int) dh,
		        Bitmap.Config.ARGB_8888);
		    try {
		      BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		      bmpFactoryOptions.inJustDecodeBounds = true;
		      returnBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
		      bmpFactoryOptions.inSampleSize = 2;
		      bmpFactoryOptions.inJustDecodeBounds = false;
		      returnBmp = BitmapFactory.decodeStream(getContentResolver()
		          .openInputStream(imageFileUri), null, bmpFactoryOptions);
		    } catch (Exception e) {
		      Log.v("ERROR", e.toString());
		    }
		    return returnBmp;
		  }

		/* (non-Javadoc)
		 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onCreateOptionsMenu(com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
//		    MenuInflater inflater = this.getSupportMenuInflater();
//		    inflater.inflate(R.menu.activity_main, menu);
		    return true;
		}

	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
      SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        if (savedInstanceState != null){
        	String str = savedInstanceState.getString(RETAIN_IMAGE_URI);
        	mSelectedImageUri = Uri.parse(str);
        	mSelectedImageBitmap = this.loadBitmap(mSelectedImageUri);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
    	
        /* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#finishUpdate(android.view.View)
		 */
		@Override
		public void finishUpdate(ViewGroup container) {
			Log.v(this.getClass().getName(), "SectionsPagerAdapter.finishUpdate");
			super.finishUpdate(container);
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#startUpdate(android.view.View)
		 */
		@Override
		public void startUpdate(ViewGroup container) {
			Log.v(this.getClass().getName(), "SectionsPagerAdapter.startUpdate");
			super.startUpdate(container);
		}

		public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	switch (i) {
        	case 0:
                Fragment fragmentOne = new QSectionSelectPicture();
                Bundle argsSecOne = new Bundle();
                argsSecOne.putInt(QSectionSelectPicture.ARG_SECTION_NUMBER, i + 1);
                fragmentOne.setArguments(argsSecOne);
                return fragmentOne;
        	case 1:
                Fragment fragmentTwo = new QSectionSurfaceView();
                Bundle argsSecTwo = new Bundle();
                argsSecTwo.putInt(QSectionSurfaceView.ARG_SECTION_NUMBER, i + 1);
                fragmentTwo.setArguments(argsSecTwo);
                return fragmentTwo;        		
        	default:
        		Fragment fragmentThree = new QSectionList();
        		Bundle argsSecThree = new Bundle();
        		argsSecThree.putInt(QSectionList.ARG_SECTION_NUMBER, i + 1);
        		fragmentThree.setArguments(argsSecThree);
        		return fragmentThree;
        	} 	
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();
                case 2: return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
    }

	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		int pos = tab.getPosition();
        mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		
	}

	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
	}

}
