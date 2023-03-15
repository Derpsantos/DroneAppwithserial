package com.drone.app.serial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drone.app.R;
import com.drone.app.utility.DatabaseHelper;
import com.hoho.android.usbserial.driver.SerialTimeoutException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener {
    DatabaseHelper database;
    private final BroadcastReceiver broadcastReceiver;
    private int deviceId, portNum, baudRate;
    private UsbSerialPort usbSerialPort;
    private SerialService service;
    private TextView receiveText;
    private TextView sendText;
    private TextView motor1;
    private TextView motor2;
    private TextView motor3;
    private TextView motor4;
    private TextView humidity;
    private TextView temperature;
    private TextView altitude;
    private ArrayList<Double> motor1_temps;
    private ArrayList<Double> motor2_temps;
    private ArrayList<Double> motor3_temps;
    private ArrayList<Double> motor4_temps;
    private ArrayList<Double> humidities;
    private ArrayList<Double> battery_temps;
    private ArrayList<Double> altitudes;
    private ArrayList<Double> latitudes;
    private ArrayList<Double> longitudes;
    private Button upload;

    private ControlLines controlLines;
    private TextUtil.HexWatcher hexWatcher;
    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean controlLinesEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    public TerminalFragment() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    connect(granted);
                }
            }
        };
    }

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceId = getArguments().getInt("device");
        portNum = getArguments().getInt("port");
        baudRate = getArguments().getInt("baud");

        motor1_temps=new ArrayList<>();
        motor2_temps=new ArrayList<>();
        motor3_temps=new ArrayList<>();
        motor4_temps=new ArrayList<>();
        humidities=new ArrayList<>();
        battery_temps=new ArrayList<>();
        altitudes=new ArrayList<>();
        latitudes=new ArrayList<>();
        longitudes=new ArrayList<>();
        database=new DatabaseHelper();
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change

        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String id = UUID.randomUUID().toString();
                double motor1max=findmax(motor1_temps);
                double motor2max=findmax(motor2_temps);
                double motor3max=findmax(motor3_temps);
                double motor4max=findmax(motor4_temps);
                double humiditymax=findmax(humidities);
                double batterymax=findmax(battery_temps);
                double recentaltitude=altitudes.get(altitudes.size() -1);
                double recentlatitude=latitudes.get(latitudes.size() -1 );
                double recentlongitude=longitudes.get(longitudes.size() - 1);
                database.add_flight(id, motor1max,motor2max,motor3max,motor4max,humiditymax,batterymax,recentaltitude, recentlatitude, recentlongitude, System.currentTimeMillis());
                database.add_flight_recordings(id,motor1_temps,motor2_temps, motor3_temps, motor4_temps, humidities, battery_temps, altitudes, System.currentTimeMillis());
                clear_arrays();
            }
        });
    }

    @Override
    public void onStop() {
        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        if (initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
        if (controlLinesEnabled && controlLines != null && connected == Connected.True)
            controlLines.start();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        if (controlLines != null)
            controlLines.stop();
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if (initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        motor1=view.findViewById(R.id.motor1text);
        motor2=view.findViewById(R.id.motor2text);
        motor3=view.findViewById(R.id.motor3text);
        motor4=view.findViewById(R.id.motor4text);
        humidity=view.findViewById(R.id.ambient_humid_text);
        temperature=view.findViewById(R.id.ambient_temp_text);
        altitude=view.findViewById(R.id.altitude_text);
        upload=view.findViewById(R.id.uploadbutton);

        sendText = view.findViewById(R.id.send_text);
        hexWatcher = new TextUtil.HexWatcher(sendText);
        hexWatcher.enable(hexEnabled);
        sendText.addTextChangedListener(hexWatcher);
        sendText.setHint(hexEnabled ? "HEX mode" : "");

        View sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()));
        controlLines = new ControlLines(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terminal, menu);
        menu.findItem(R.id.hex).setChecked(hexEnabled);
        menu.findItem(R.id.controlLines).setChecked(controlLinesEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            receiveText.setText("");
            return true;
        } else if (id == R.id.newline) {
            String[] newlineNames = getResources().getStringArray(R.array.newline_names);
            String[] newlineValues = getResources().getStringArray(R.array.newline_values);
            int pos = java.util.Arrays.asList(newlineValues).indexOf(newline);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Newline");
            builder.setSingleChoiceItems(newlineNames, pos, (dialog, item1) -> {
                newline = newlineValues[item1];
                dialog.dismiss();
            });
            builder.create().show();
            return true;
        } else if (id == R.id.hex) {
            hexEnabled = !hexEnabled;
            sendText.setText("");
            hexWatcher.enable(hexEnabled);
            sendText.setHint(hexEnabled ? "HEX mode" : "");
            item.setChecked(hexEnabled);
            return true;
        } else if (id == R.id.controlLines) {
            controlLinesEnabled = !controlLinesEnabled;
            item.setChecked(controlLinesEnabled);
            if (controlLinesEnabled) {
                controlLines.start();
            } else {
                controlLines.stop();
            }
            return true;
        } else if (id == R.id.sendBreak) {
            try {
                usbSerialPort.setBreak(true);
                Thread.sleep(100);
                status("send BREAK");
                usbSerialPort.setBreak(false);
            } catch (Exception e) {
                status("send BREAK failed: " + e.getMessage());
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Serial + UI
     */
    private void connect() {
        connect(null);
    }

    private void connect(Boolean permissionGranted) {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        for (UsbDevice v : usbManager.getDeviceList().values())
            if (v.getDeviceId() == deviceId)
                device = v;
        if (device == null) {
            status("connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if (driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        if (driver.getPorts().size() < portNum) {
            status("connection failed: not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(portNum);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if (usbConnection == null && permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }

        connected = Connected.Pending;
        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect();
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        controlLines.stop();
        service.disconnect();
        usbSerialPort = null;
    }

    private void send(String str) {
        if (connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;
            if (hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            service.write(data);
        } catch (SerialTimeoutException e) {
            status("write timeout: " + e.getMessage());
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }


    public StringBuffer buffer = new StringBuffer();
    public void handleBuffer()
    {
        int startIndex = this.buffer.indexOf("\n");
        if (startIndex == -1) {
            // no data yet
            return;
        }
        int endIndex = this.buffer.indexOf("\n", startIndex + 1);
        if (endIndex == -1) {
            // if no message space or newline, can't build full value
            return;
        }

        this.handlePart(this.buffer.substring(startIndex + 1, endIndex));
        this.buffer = new StringBuffer(this.buffer.substring(endIndex));

        handleBuffer();
    }

    public void handlePart(String part)
    {
        String[] fragments = part.split(" ");
        String key = fragments[0];
        Double value = Double.parseDouble(fragments[1]);

        switch (key) {
            default:
                StringBuilder msg = new StringBuilder();
                msg.append("Key: ")
                        .append(key)
                        .append(" | Value: ")
                        .append(value)
                        .append("\n");

                receiveText.append(msg);
                break;
        }
    }
    private void receive(ArrayDeque<byte[]> datas) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        for (byte[] data : datas) {
            if (hexEnabled) {
                spn.append(TextUtil.toHexString(data)).append('\n');
            } else {
                String msg = new String(data);
                if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                    // don't show CR as ^M if directly before LF
                    msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                    // special handling if CR and LF come in separate fragments
                    if (pendingNewline && msg.charAt(0) == '\n') {
                        if (spn.length() >= 2) {
                            spn.delete(spn.length() - 2, spn.length());
                        } else {
                            Editable edt = receiveText.getEditableText();
                            if (edt != null && edt.length() >= 2)
                                edt.delete(edt.length() - 2, edt.length());
                        }
                    }
                    pendingNewline = msg.charAt(msg.length() - 1) == '\r';
                }
                spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
            }
        }
        //********************************************************************************************************************
       // receiveText.append(spn);

        this.buffer.append(spn.toString());
        handleBuffer();



        /*
        byte[] tag = datas.getFirst();
        String tagstring=tag.toString();
        datas.pop();
        String data=datas.toString();
        Double temp = Double.parseDouble(data);

        switch (tagstring){
            case "a":
                motor1.setText("Motor 1 temperature: " + temp +"C");
                //above 70 risk, 90 very bad, 100 fucked up
                if(temp>69 && temp<90){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 1 temperature too high, please check motor as soon as possible", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=90 && temp < 100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 1 temperature is very high, continued operation will cause permanent damage", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 1 temperature critical, land drone and perform maintenance immediately", Toast.LENGTH_SHORT);
                    toast.show();
                }
                motor1_temps.add(temp);
                break;
            case "b":
                motor2.setText("Motor 2 temperature: " + temp +"C");
                if(temp>69 && temp<90){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 2 temperature too high, please check motor as soon as possible", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=90 && temp < 100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 2 temperature is very high, continued operation will cause permanent damage", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 2 temperature critical, land drone and perform maintenance immediately", Toast.LENGTH_SHORT);
                    toast.show();
                }
                motor2_temps.add(temp);
                break;
            case "c":
                motor3.setText("Motor 3 temperature: " + temp +"C");
                if(temp>69 && temp<90){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 3 temperature too high, please check motor as soon as possible", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=90 && temp < 100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 3 temperature is very high, continued operation will cause permanent damage", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 3 temperature critical, land drone and perform maintenance immediately", Toast.LENGTH_SHORT);
                    toast.show();
                }
                motor3_temps.add(temp);
                break;
            case "d":
                motor4.setText("Motor 4 temperature: " + temp +"C");
                if(temp>69 && temp<90){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 4 temperature too high, please check motor as soon as possible", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=90 && temp < 100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 4 temperature is very high, continued operation will cause permanent damage", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>=100) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Motor 4 temperature critical, land drone and perform maintenance immediately", Toast.LENGTH_SHORT);
                    toast.show();
                }
                motor4_temps.add(temp);
                break;
            case "e":
                humidity.setText("Ambient Humidity: " + temp +"%");
                humidities.add(temp);
                break;
            case "f":
                temperature.setText("Battery temperature: " + temp +"C");
                if(temp<10){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Battery temperature too low, please end operation and check battery", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (temp>50) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Battery temperature too high, please end operation and check battery", Toast.LENGTH_SHORT);
                    toast.show();
                }
                //below 10 and above 50
                battery_temps.add(temp);
                break;
            case "g":
                altitude.setText("Altitude: " + temp +"");
                if(temp>12000){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Reduce altitude, you are above legal flight limit", Toast.LENGTH_SHORT);
                    toast.show();
                }
                altitudes.add(temp);
                break;
            case"h":
                latitudes.add(temp);
                break;
            case "i":
                longitudes.add(temp);
                break;
            case"j":
                if(temp<1000){
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Quadcopter approaching object, please operate with caution", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            default:
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"packet unrecognized:" + tagstring + temp + "" , Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
*/

    }
//***********************************************************************************************************************************************************


    public void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
        if (controlLinesEnabled)
            controlLines.start();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

    private enum Connected {False, Pending, True}


    class ControlLines {
        private static final int refreshInterval = 200; // msec

        private final Handler mainLooper;
        private final Runnable runnable;
        private final LinearLayout frame;
        private final ToggleButton rtsBtn, ctsBtn, dtrBtn, dsrBtn, cdBtn, riBtn;

        ControlLines(View view) {
            mainLooper = new Handler(Looper.getMainLooper());
            runnable = this::run; // w/o explicit Runnable, a new lambda would be created on each postDelayed, which would not be found again by removeCallbacks

            frame = view.findViewById(R.id.controlLines);
            rtsBtn = view.findViewById(R.id.controlLineRts);
            ctsBtn = view.findViewById(R.id.controlLineCts);
            dtrBtn = view.findViewById(R.id.controlLineDtr);
            dsrBtn = view.findViewById(R.id.controlLineDsr);
            cdBtn = view.findViewById(R.id.controlLineCd);
            riBtn = view.findViewById(R.id.controlLineRi);
            rtsBtn.setOnClickListener(this::toggle);
            dtrBtn.setOnClickListener(this::toggle);
        }

        private void toggle(View v) {
            ToggleButton btn = (ToggleButton) v;
            if (connected != Connected.True) {
                btn.setChecked(!btn.isChecked());
                Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String ctrl = "";
            try {
                if (btn.equals(rtsBtn)) {
                    ctrl = "RTS";
                    usbSerialPort.setRTS(btn.isChecked());
                }
                if (btn.equals(dtrBtn)) {
                    ctrl = "DTR";
                    usbSerialPort.setDTR(btn.isChecked());
                }
            } catch (IOException e) {
                status("set" + ctrl + " failed: " + e.getMessage());
            }
        }

        private void run() {
            if (connected != Connected.True)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getControlLines();
                rtsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RTS));
                ctsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CTS));
                dtrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DTR));
                dsrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DSR));
                cdBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CD));
                riBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RI));
                mainLooper.postDelayed(runnable, refreshInterval);
            } catch (IOException e) {
                status("getControlLines() failed: " + e.getMessage() + " -> stopped control line refresh");
            }
        }

        void start() {
            frame.setVisibility(View.VISIBLE);
            if (connected != Connected.True)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getSupportedControlLines();
                if (!controlLines.contains(UsbSerialPort.ControlLine.RTS))
                    rtsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CTS))
                    ctsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DTR))
                    dtrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DSR))
                    dsrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CD))
                    cdBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.RI))
                    riBtn.setVisibility(View.INVISIBLE);
                run();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "getSupportedControlLines() failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        void stop() {
            frame.setVisibility(View.GONE);
            mainLooper.removeCallbacks(runnable);
            rtsBtn.setChecked(false);
            ctsBtn.setChecked(false);
            dtrBtn.setChecked(false);
            dsrBtn.setChecked(false);
            cdBtn.setChecked(false);
            riBtn.setChecked(false);
        }
    }
//*************************************************************************************************************************************************************
    private double findmax(ArrayList<Double> array){
        double max=0;
        for(int i =0; i<array.size();i++){
            if(max< array.get(i)){
                max=array.get(i);
            }
        }
        return max;
    }



    private void clear_arrays(){
        motor1_temps.clear();
        motor2_temps.clear();
        motor3_temps.clear();
        motor4_temps.clear();
        humidities.clear();
        battery_temps.clear();
        altitudes.clear();

        motor1_temps=new ArrayList<>();
        motor2_temps=new ArrayList<>();
        motor3_temps=new ArrayList<>();
        motor4_temps=new ArrayList<>();
        humidities=new ArrayList<>();
        battery_temps=new ArrayList<>();
        altitudes=new ArrayList<>();

    }

}
