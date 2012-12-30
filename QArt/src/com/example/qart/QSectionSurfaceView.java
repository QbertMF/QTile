package com.example.qart;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class QSectionSurfaceView extends SherlockFragment{

    // Constants
    static final int SHUFFEL_ITERATIONS = 1000;

    static final int STATE_NORMAL = 1;
    static final int STATE_HINT = 2;

    // Menu identifiers
    static final int RESTART_ID = Menu.FIRST;
    static final int HINT_ID = Menu.FIRST+1;

	public static final String ARG_SECTION_NUMBER = "section_number";

	private FastRenderView renderView;
	private Activity mActivity;
	
	private int mState = STATE_NORMAL;
	
	private int[][] mMap;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		// We have a menu item to show in action bar.
        setHasOptionsMenu(true);

		super.onActivityCreated(savedInstanceState);
	}

	//-------------------------------------
	// GETTER AND SETTER
	//-------------------------------------

	/**
	 * @return the mState
	 */
	public int getState() {
		return mState;
	}

	/**
	 * @param mState the mState to set
	 */
	public void setState(int mState) {
		this.mState = mState;
	}
	
	//-------------------------------------
	// OPTIONS MENU
	//-------------------------------------

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem populateItem = menu.add(Menu.NONE, RESTART_ID, 0, "Restart");
        populateItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        MenuItem clearItem = menu.add(Menu.NONE, HINT_ID, 0, "Hint");
        clearItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
    	case RESTART_ID:
    		renderView.shuffelMap(mMap, SHUFFEL_ITERATIONS);
        	return true;
    	case HINT_ID:	
    		if (renderView.get_state() == FastRenderView.STATE_RUNNING)
    			renderView.set_state(FastRenderView.STATE_SHOWHINT);
    		else
    			renderView.set_state(FastRenderView.STATE_RUNNING);
        	return true;
    	default:
        	return super.onOptionsItemSelected(item);
    	}    
	}
	
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
		renderView.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		renderView.onResume();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		renderView.onStart();
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
//        TextView textView = new TextView(getActivity());
//        textView.setGravity(Gravity.CENTER);
//        Bundle args = getArguments();
//        textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
//        return textView;
		renderView = new FastRenderView(getActivity());
		return renderView;
//		setContentView(renderView);

    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	/****************************************
	 * FastRenderView Class
	 ****************************************/	
	class FastRenderView extends SurfaceView 
	implements OnTouchListener, Runnable{

		public static final int MAX_TOUCH_EVENTS = 10;
		private int[]   _touchX  = new int[MAX_TOUCH_EVENTS];
		private int[]   _touchY  = new int[MAX_TOUCH_EVENTS];
		private boolean[] _touched = new boolean[10];
		
		/*
		 * State-tracking constants
		 */
		public static final int STATE_LOSE = 1;
		public static final int STATE_PAUSE = 2;
		public static final int STATE_READY = 3;
		public static final int STATE_RUNNING = 4;
		public static final int STATE_WIN = 5;
		public static final int STATE_SHOWHINT = 6;
        
		private static final float TILE_NUMBER_TEXT_SIZE = 20f;
		/*
         * Member (state) fields
         */
		private int _state = STATE_RUNNING;
		private Thread _renderThread = null;
		private SurfaceHolder _surfaceHolder;
		private volatile boolean _threadRunning = false;
		private int _screenHeight = -1;
		private int _screenWidth = -1;
		private Paint _backgroundPaint;
		private Paint _textPaint;
		private Paint _numberPaint;
		private Paint _touchOnePaint;
		private Paint _touchTwoPaint;
		private Paint _touchThreePaint;
		
		private long _elapsed = 0;
				
		/* This is the canvas of the SurfaceHolder */
		private Canvas _surfaceHolderCanvas;

		/* Used to figure out elapsed time between frames */
		private long _lastTime;
		private long _accumulateSecond = 0;
		private int _currentFrame = 0;
		private int _backupFrame = 0;
		private int _fps; 
		
		private int _shipPosX;
		private int _shipPosY;
		
		private Random rand;
		
		private int _scrollPrescaler = 0;
		// debug only
		private int _shipTile;
		
		private SherlockFragment _activity;

		/**
		 * @return the _state
		 */
		public int get_state() {
			return _state;
		}

		/**
		 * @param _state the _state to set
		 */
		public void set_state(int _state) {
			this._state = _state;
		}

		/*
         * Methods
         */
		public FastRenderView(Context context) {
			super(context);
//			 _activity = (SherlockFragment)context;
			 
			 setOnTouchListener(this);

			_surfaceHolder = getHolder();
			
			rand = new Random();
		}

		/*
		 * Activity state machine functions
		 */
		
		protected void onStart(){
			Log.w(this.getClass().getName(), "FastRenderView.onStart");
			MainActivity activity = (MainActivity)getActivity();
			
			mMap = createMap(activity.getDifficulty());
			shuffelMap(mMap, SHUFFEL_ITERATIONS);			
		}
		
		private int[][] createMap(int difficulty){
			
			int tiles;
			switch (difficulty){
			case 0:
				tiles = 5;
				break;
			case 1:
				tiles = 10;
				break;
			default:
				tiles = 15;
				break;
			}
			int[][] myMap = new int[tiles][tiles];
			
			int curTile = 0;
			for (int x=0;x<tiles;x++){
				for (int y=0;y<tiles;y++){
					myMap[x][y] = curTile;
					curTile++;
				}				
			}
			
			myMap[tiles-1][tiles-1] = -1;
			
			return myMap;
		}

		private void shuffelMap(int[][] map, int iterations){
			for (int i=0; i<iterations; i++){

				int size = map[0].length;
				
				boolean found = false;
				int x = 0;
				int y = 0;

				for (int z=0;z<(size*size);z++){
					x = z % size;
					y = z / size;
					if (map[x][y] == -1){
						break;
					}
				}
				
				// x and y contain the empty tile position
				int dir = rand.nextInt(4);
				
				switch (dir){
				case 0:
					// top
					swapMapTiles(map, x, y, x, y-1);
					break;
				case 1:
					// right
					swapMapTiles(map, x, y, x+1, y);
					break;
				case 2:
					// bottom
					swapMapTiles(map, x, y, x, y+1);
					break;
				case 3:
					// left
					swapMapTiles(map, x, y, x-1, y);
					break;
				}
			}
		}
		
		/**
		 * Copy map tile from x1, y1 to x2, y2. If the source
		 * is outside the map no action is performed. The tiles
		 * are not swapped.
		 * @param map
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 */
		private void swapMapTiles(int[][] map, 
				                  int x1, int y1, 
				                  int x2, int y2){
			int size = map[0].length;

			if ((x2>=0) && (x2<size) && (y2>=0) && (y2<size)) {
				map[x1][y1] = map[x2][y2];
				map [x2][y2] = -1;
			}
		}
		
		protected void onReStart(){
			Log.v(this.getClass().getName(), "FastRenderView.onReSart");
		}
		
		protected void onResume() {
			Log.w(this.getClass().getName(), "FastRenderView.onResume");
			_threadRunning = true;
			_renderThread = new Thread(this);
			_renderThread.start();
			startGame();
		}

		protected void onPause() {
			Log.w(this.getClass().getName(), "FastRenderView.onPause");
			_threadRunning = false;
			boolean retry = true;
			while (retry) {
				try {
					_renderThread.join();
					retry = false;
					Log.w(this.getClass().getName(), "pause - join");
				} catch (InterruptedException e) {
					Log.w(this.getClass().getName(), "pause - catch");
					// retry
				}
			}
		}

		public void run() {
			Log.w(this.getClass().getName(), "FastRenderView.run");

			_surfaceHolderCanvas = null;

			while (_threadRunning) {
				if (!_surfaceHolder.getSurface().isValid())
					continue;

				try {
					_surfaceHolderCanvas = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						updateInputs();
						
						updatePhysics();
						
						doDraw(_surfaceHolderCanvas);
						_currentFrame++;
						
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (_surfaceHolderCanvas != null) {
						_surfaceHolder.unlockCanvasAndPost(_surfaceHolderCanvas);
					}
				}
			}
		}

		private void startGame() {
			_lastTime = System.currentTimeMillis() + 100;
		}

		private void doDraw(Canvas canvas){
			// Get the screen resolution
			if (_screenHeight == -1){
				Log.w(this.getClass().getName(), "run - create scrollPane");

				_screenHeight = canvas.getHeight();
				_screenWidth = canvas.getWidth();

				
				_backgroundPaint = new Paint();
				_backgroundPaint.setColor(0x770000FF);
				
				_textPaint = new Paint();
				_textPaint.setColor(Color.WHITE);
				
				_numberPaint = new Paint();
				_numberPaint.setColor(Color.WHITE);
				_numberPaint.setTextSize(TILE_NUMBER_TEXT_SIZE);
				_numberPaint.setShadowLayer(TILE_NUMBER_TEXT_SIZE/2f, // radius 
						                    TILE_NUMBER_TEXT_SIZE/8f, // dx
						                    TILE_NUMBER_TEXT_SIZE/8f, // dy
						                    Color.BLACK);
				
				
				_touchOnePaint = new Paint();
				_touchOnePaint.setColor(Color.MAGENTA);
				
				_touchTwoPaint = new Paint();
				_touchTwoPaint.setColor(Color.RED);

				_touchThreePaint = new Paint();
				_touchThreePaint.setColor(Color.CYAN);								
			}

			// Touch input
			if (_touched[0] == true)
				canvas.drawCircle(_touchX[0], _touchY[0], 15, _touchOnePaint);
			if (_touched[1] == true)
				canvas.drawCircle(_touchX[1], _touchY[1], 15, _touchTwoPaint);
			if (_touched[2] == true)
				canvas.drawCircle(_touchX[2], _touchY[2], 15, _touchThreePaint);

			// Text Output
			canvas.drawRect(20, 0, 100, 180, _backgroundPaint);
			

			canvas.drawText(String.valueOf(_screenHeight), 20, 50, _textPaint);
			canvas.drawText(String.valueOf(_screenWidth), 20, 60, _textPaint);

			canvas.drawText(String.valueOf(_fps), 20, 80, _textPaint);
			
			doDrawTiles(canvas);
		}
		
		private void updateInputs(){
			MainActivity activity = (MainActivity)getActivity();
			Bitmap bmp = activity.getSelectedImageBitmap();
			
			if ((mMap == null) || (bmp == null))
				return;

			int numTiles = mMap[0].length;

			int viewHeight = this.getHeight();
			int viewWidth = this.getWidth();
			
			int tileWidth = viewWidth / numTiles;
			int tileHeight = viewHeight / numTiles;
					
			int bmpHeight = bmp.getHeight();
			int bmpWidth = bmp.getWidth();
			
			float xFactor = (float)viewWidth / (float)bmpWidth;
			float yFactor = (float)viewHeight / (float)bmpHeight;			

			if (_touched[0] == true){
				
				int x = _touchX[0] / tileWidth;
				int y = _touchY[0] / tileHeight;
					
				// Check if -1 is above
				if  ((y-1 >= 0) && (mMap[x][y-1] == -1)){
					swapMapTiles(mMap, x, y-1, x, y); 
				}
				// Check if -1 is below
				if  ((y+1 < numTiles) && (mMap[x][y+1] == -1)){
					swapMapTiles(mMap, x, y+1, x, y); 
				}
				// Check if -1 is left
				if  ((x-1 >= 0) && (mMap[x-1][y] == -1)){
					swapMapTiles(mMap, x-1, y, x, y); 
				}
				// Check if -1 is right
				if  ((x+1 < numTiles) && (mMap[x+1][y] == -1)){
					swapMapTiles(mMap, x+1, y, x, y); 
				}
			}
		}
		
		private void doDrawTiles(Canvas canvas){

			MainActivity activity = (MainActivity)getActivity();
			Bitmap bmp = activity.getSelectedImageBitmap();
			
			if ((mMap == null) || (bmp == null))
				return;
			
			// Draw the tiles			
			int viewHeight = this.getHeight();
			int viewWidth = this.getWidth();
			
			int numTiles = mMap[0].length;
			
			int bmpHeight = bmp.getHeight();
			int bmpWidth = bmp.getWidth();
			
			float xFactor = (float)viewWidth / (float)bmpWidth;
			float yFactor = (float)viewHeight / (float)bmpHeight;			
			
			int tileWidth = bmpWidth / numTiles;
			int tileHeight = bmpHeight / numTiles;
			
			Rect rectScreen = new Rect();
			Rect rectAtlas = new Rect();
			
			if (get_state() == STATE_SHOWHINT){
				rectAtlas.top = 0;
				rectAtlas.bottom = bmpHeight;
				rectAtlas.left = 0;
				rectAtlas.right = bmpWidth;
			
				rectScreen.top = 0;
				rectScreen.bottom = viewHeight;
				rectScreen.left = 0;
				rectScreen.right = viewWidth;
				
				canvas.drawBitmap(bmp, rectAtlas, rectScreen, null);				
			}
			else
			{
			boolean isEmpty;
			
			if (mMap != null){
				int size = mMap[0].length;
				for (int x=0;x<size;x++){
					for (int y=0;y<size;y++){
						
						int tile = mMap[x][y];
						
						isEmpty = (tile == -1)?true:false;
						
						if (!isEmpty){
							int sX = tile % size;
							int sY = tile / size;
							rectAtlas.top = sY * tileHeight;
							rectAtlas.bottom = sY * tileHeight + tileHeight;
							rectAtlas.left = sX * tileWidth;
							rectAtlas.right = sX * tileWidth + tileWidth;
						}
						
						rectScreen.top = (int)((float)(y*tileHeight) * yFactor);
						rectScreen.bottom = (int)((float)(y*tileHeight + tileHeight) * yFactor);
						rectScreen.left = (int)((float)(x*tileWidth) * xFactor);
						rectScreen.right = (int)((float)(x*tileWidth + tileWidth) * xFactor);
						
						if (isEmpty)
							canvas.drawRect(rectScreen, _backgroundPaint);
						else
							canvas.drawBitmap(bmp, rectAtlas, rectScreen, null);

						canvas.drawText(String.valueOf(mMap[x][y]), 
								rectScreen.left + TILE_NUMBER_TEXT_SIZE/8, 
								rectScreen.top + TILE_NUMBER_TEXT_SIZE, 
								_numberPaint);
					}
				}
			}
			}
		}
		
		private void doProcess(){
		}
		
		private void updatePhysics() {
			long now = System.currentTimeMillis();

			// Do nothing if mLastTime is in the future.
			// This allows the game-start to delay the start of the physics
			// by 100ms or whatever.
			if (_lastTime > now) return;

			_elapsed = now - _lastTime;
			
			// One second elapsed
			long oldSecond = _accumulateSecond / 1000;
			_accumulateSecond += _elapsed;
			long newSecond = _accumulateSecond / 1000;
			
			if(newSecond > oldSecond){
				_fps = _currentFrame - _backupFrame;
				_backupFrame = _currentFrame;
			}

			_lastTime = now;
		}

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (_surfaceHolder) {
            	Log.w(this.getClass().getName(), "setSurfaceSize - new Screen Dimensions");
            }
        }
        
    	/*
    	 * OnTouchListerner Interface
    	 */	
        //@Override
    	public boolean onTouch(View view, MotionEvent event) {
    		int action = event.getAction() & MotionEvent.ACTION_MASK;
    		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    		int pointerId = event.getPointerId(pointerIndex);
    		
    		switch (action){
    		case MotionEvent.ACTION_DOWN:
    		case MotionEvent.ACTION_POINTER_DOWN:
    			_touched[pointerId] = true;
    			_touchX[pointerId] = (int)event.getX(pointerIndex);
    			_touchY[pointerId] = (int)event.getY(pointerIndex);
    			break;
    		case MotionEvent.ACTION_UP:
    		case MotionEvent.ACTION_POINTER_UP:
    		case MotionEvent.ACTION_CANCEL:
    			_touched[pointerId] = false;
    			_touchX[pointerId] = (int)event.getX(pointerIndex);
    			_touchY[pointerId] = (int)event.getY(pointerIndex);
    			break;
    		case MotionEvent.ACTION_MOVE:
    			int pointerCount = event.getPointerCount();
    			for (int i=0; i<pointerCount; i++){
    				pointerIndex = i;
    				pointerId = event.getPointerId(pointerIndex);
    				_touchX[pointerId] = (int)event.getX(pointerIndex);
    				_touchY[pointerId] = (int)event.getY(pointerIndex);
    			}
    			break;
    		}
    		return true;
    	}

		/**
		 * @return the _touchX
		 */
		public int[] get_touchX() {
			return _touchX;
		}

		/**
		 * @return the _touchY
		 */
		public int[] get_touchY() {
			return _touchY;
		}

		/**
		 * @return the _touched
		 */
		public boolean[] get_touched() {
			return _touched;
		}

	} // class FastRenderView

}
