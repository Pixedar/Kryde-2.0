package pixedar.com.kryde;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Bluetooth {
    private static Context context;
    private BluetoothAdapter bluetoothAdapter;
    private static BluetoothSocket btSocket;
    private static boolean sw = true;

    // private Parser Parser = new Parser();
    OnBluetoothConnectedListener listener;

    public Bluetooth(Context context, OnBluetoothConnectedListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public void sendString(String data) throws IOException {
        btSocket.getOutputStream().write(data.getBytes());
    }

    public void writeBytes(byte[] data) throws IOException {
        btSocket.getOutputStream().write(data);
    }

    public void write(byte data) throws IOException {
        btSocket.getOutputStream().write(data);
    }

    public int getData(byte[] buffer) throws IOException {
        return btSocket.getInputStream().read(buffer);
    }

    public void connect(final ProgressBar progressBar) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (!isEnabled) {
            bluetoothAdapter.enable();
        } else {
            if (sw) {
                new DeviceConnectionTask(progressBar).execute();
            } else {
                //  new LedconnectionTask(progressBar).execute();
            }
        }
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:

                            break;
                        case BluetoothAdapter.STATE_ON:
                         //   if (sw) {
                                new DeviceConnectionTask(progressBar).execute();
            /*                    sw = false;
                            } else {
                                Log.d("GG", "ledTaskST");
                                //    new LedconnectionTask(progressBar).execute();
                            }*/
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                    }
                }
            }
        };

        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    public static void disconnect() {
        // LocalBroadcastManager.getInstance(context).unregisterReceiver(mReceiver);
        if (btSocket != null) {
            if (btSocket.isConnected()) {
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private class DeviceConnectionTask extends AsyncTask<Object, Object, int[]> {
        private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
         // private final UUID myUUID = UUID.fromString("00001526-1212-efde-1523-785feabcd123");
        //name: MI Band 2address: D1:41:F4:F3:89:A9
        ProgressBar progressBar;

        public DeviceConnectionTask(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            //    progressBar = (ProgressBar) ((Activity)context).findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            // progressBar.setScrollBarFadeDuration(100);
            progressBar.setProgress(1);
        }

        @Override
        protected int[] doInBackground(Object... devices) {
            final byte[] buffer = new byte[43];
            int[] unsignedBuffer = new int[buffer.length];
            try {
                //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
              //  String address = "D1:41:F4:F3:89:A9";
                  String address = "00:14:01:03:38:8B";
             //   String address = "00:14:01:03:38:8B";
                BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(address);
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
          //      btSocket = dispositivo.createRfcommSocketToServiceRecord(myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();


                //  btSocket.getOutputStream().close();
                write((byte) 0);
                Thread.sleep(220);
                getData(buffer);

                for (int i = 0; i < buffer.length; i++) {
                    unsignedBuffer[i] = buffer[i] & 0xFF;
                }

                return unsignedBuffer;

            } catch (Exception e) {
                e.printStackTrace();
                msg(e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPostExecute(int[] result) {
            super.onPostExecute(result);
            listener.connected(result);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void msg(final String s) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        };
        mainHandler.post(myRunnable);

    }
}
