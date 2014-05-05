package com.example.bluetoothclassicdemo;

import com.example.bluetoothclassicdemo.util.*;

import java.util.UUID;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends Activity {
	
	final String activityName = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button enableBTButton = (Button) findViewById(R.id.enable_bluetooth);
        enableBTButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		handleEnableBluetooth();
        	}
        });
        
        Button discoverableButton = (Button) findViewById(R.id.make_discoverable);
        discoverableButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		handleMakeDiscoverable();
        	}
        });
    }
    
    private void handleEnableBluetooth() {
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (mBluetoothAdapter == null) {
    		Log.e(activityName, "Device no support BTClassic!");
    		return;
    	}
    	
    	if (!mBluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, 1);
    	} else {
    		Log.i(activityName, "BTClassic already enabled.");
    	}
    }
    
    private void handleMakeDiscoverable() {
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
		startActivity(discoverableIntent);
    }
    
    
    
    private BTServerThread btServerThread = null;
    private void restartServerThread() {
    	if(btServerThread != null) {
    		btServerThread.cancel();
    	}
    	
    	final String btServiceName = "MyService";
    	final UUID myUUID = new UUID(0xDEADBEEFFACEFEEDl, 0xBADDCAFEB105F00Dl);
    	
    	btServerThread = new BTServerThread(btServiceName, myUUID);
    	btServerThread.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
