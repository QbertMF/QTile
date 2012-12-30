package com.example.qart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;

public class QSectionList extends SherlockListFragment {

    public static final String ARG_SECTION_NUMBER = "section_number";

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
		Log.i("QSectionList", "onResume");
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, MainActivity.SELECTION));
	
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
//		adapter.notifyDataSetChanged();
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
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, MainActivity.SELECTION));
		Log.i("QSectionList", "onActivityCreated");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("QSectionList", "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("QSectionList", "Item clicked: " + id);
		MainActivity activity = (MainActivity)getActivity();
				
		switch ((int)id){
		case 0:
			if (activity.getDifficulty() != 0){
				activity.setDifficulty(0);
				activity.setSettingsChanged(true);
			}
			break;
		case 1:
			if (activity.getDifficulty() != 1){
				activity.setDifficulty(1);
				activity.setSettingsChanged(true);
			}
			break;
		case 2:
			if (activity.getDifficulty() != 2){
				activity.setDifficulty(2);
				activity.setSettingsChanged(true);
			}
			break;
		case 3:
			activity.setShowNumbers(!activity.isShowNumbers());
			break;
		case 4:
			activity.setShowGrid(!activity.isShowGrid());
			break;
		}
	}
	
	public void invalidate(){
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
		adapter.notifyDataSetChanged();
	}
}
