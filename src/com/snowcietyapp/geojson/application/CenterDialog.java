package com.snowcietyapp.geojson.application;

import com.snowcietyapp.geojson.application.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CenterDialog extends DialogFragment {
	
	private Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	public CenterDialog() {
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setView(view);
		builder.setTitle(R.string.center_dialog_title);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.set, null);
		final AlertDialog d = builder.create();
		
		/*
		 * Below we are overriding the behavior of the positive
		 * button in an AlertDialog so that the dialog doesn't
		 * disappear if non-valid values are inserted.
		 */
		d.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				Button positive = d.getButton(DialogInterface.BUTTON_POSITIVE);
				positive.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						EditText latTxt = (EditText) d.findViewById(R.id.center_lat);
						EditText lonTxt = (EditText) d.findViewById(R.id.center_lon);
						
						double lat;
						try {
							lat = Double.parseDouble(latTxt.getText().toString());
						} catch (NumberFormatException e) {
							Toast.makeText(mActivity, "Latitude must be in the form xx.xx!", Toast.LENGTH_SHORT).show();
							return;
						}
						double lon;
						try {
							lon = Double.parseDouble(lonTxt.getText().toString());
						} catch (NumberFormatException e) {
							Toast.makeText(mActivity, "Longitude must be in the form xx.xx!", Toast.LENGTH_SHORT).show();
							return;
						}
						((MainActivity) mActivity).setCenter(lat, lon);
						d.dismiss();
					}
				});
				
			}
		});
		return d;
	}

}
