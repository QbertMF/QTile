package com.example.qart;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

public class QSectionSurfaceView extends SherlockFragment{

	public static final String ARG_SECTION_NUMBER = "section_number";

	private FastRenderView renderView;
	private Activity mActivity;

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
        
		/*
         * Member (state) fields
         */
		private Thread _renderThread = null;
		private SurfaceHolder _surfaceHolder;
		private volatile boolean _threadRunning = false;
		private int _screenHeight = -1;
		private int _screenWidth = -1;
		private Paint _backgroundPaint;
		private Paint _textPaint;
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
						//updateInputs();
						
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
