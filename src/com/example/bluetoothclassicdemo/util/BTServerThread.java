package com.example.bluetoothclassicdemo.util;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BTServerThread extends Thread {
	private BluetoothAdapter mBluetoothAdapter;
    private final BluetoothServerSocket mmServerSocket;
    private BTDataThread btDataThread;
 
    public BTServerThread(String name, UUID uuid) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
        	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }
 
    // TODO(ivany) This should automatically be relaunched
    public void run() {
    	Log.v("BTServerThread", "Starting run()");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
        	Log.v("BTServerThread", "In run() while loop.");
            try {
                socket = mmServerSocket.accept();
                Log.v("BTServerThread", "accept() returned!");
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
            	Log.v("BTServerThread", "Launching manageConnectedSocket()");
            	// Blocks until manageConnectedSocket() finishes.
                manageConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                	Log.e("BTServerThread", e.toString());
                }
                
    	    	try {
    	    		btDataThread.join();
    	    	} catch (InterruptedException e) {
    	    		Log.w("BTServerThread", "Got InterruptedException!");
    	    	}
                
                Log.v("BTServerThread", "run() joined BTDataThread, looping again..");
            }
        }
    }
    
    private void manageConnectedSocket(BluetoothSocket socket) {
    	if (btDataThread != null) {
    		Log.v("BTServerThread", "manageConnectedSocket() killing current btDataThread");
    		btDataThread.cancel();
    		btDataThread = null;
    	}
    	
    	Log.v("BTServerThread", "manageConnectedSocket() creating and run()ning new btDataThread");
    	btDataThread = new BTDataThread(socket);
    	btDataThread.run();
    	Log.v("BTServerThread", "manageConnectedSocket() returning.");
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
        	if (btDataThread != null) {
        		btDataThread.cancel();
        		btDataThread = null;
        	}
        	
            mmServerSocket.close();
        } catch (IOException e) { 
        	Log.e("BTServerThread", e.toString());
        }
        
        Log.v("BTServerThread", "cancel() complete.");
    }
}
