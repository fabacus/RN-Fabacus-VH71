
package com.fabacus.vh71;

import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.handheld.UHF.UhfManager;
import com.handheld.UHFDemo.EPC;
import com.handheld.UHFDemo.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;


public class RNFabacusVh71Module extends ReactContextBaseJavaModule {


  private final ReactApplicationContext reactContext;
  private final Runnable commRunnable;
  private UhfManager manager;
  private SharedPreferences shared;
  private SharedPreferences.Editor editor;
  private int sensitive = 0;
  private int power = 0 ;//rate of work
  private int area = 0;
  private int frequency = 0;
  private boolean runFlag = true;
  private boolean startFlag = false;
  private ArrayList<EPC> listEPC;
  private ArrayList<String> listepc = new ArrayList<String>();
  private ArrayList<Map<String, Object>> listMap;
  private Handler commHandler;

  public RNFabacusVh71Module(final ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;


    commRunnable = new Runnable() {
      @Override
      public void run() {

        if(startFlag) {
          Log.e("OGTAGDEBUG::", "run: XXXXXXXXX=======> " +listEPC.toArray().length );
          for (EPC mEPC : listEPC) {
            Log.e("OGTAGDEBUG::", "run: newEPC ====> " + mEPC.getEpc());

            WritableMap map2 = Arguments.createMap();
            map2.putString(mEPC.getEpc(), mEPC.getEpc());
            sendEvent(reactContext, "FabacusOnTagReceived", map2);

          }
        }

        commHandler.postDelayed(this, 1000);

      }
    };

    listEPC = new ArrayList<EPC>();
    String powerString = "";
    switch (UhfManager.Power) {
      case SerialPort.Power_3v3:
        powerString = "power_3V3";
        break;
      case SerialPort.Power_5v:
        powerString = "power_5V";
        break;
      case SerialPort.Power_Scaner:
        powerString = "scan_power";
        break;
      case SerialPort.Power_Psam:
        powerString = "psam_power";
        break;
      case SerialPort.Power_Rfid:
        powerString = "rfid_power";
        break;
      default:
        break;
    }

    manager = UhfManager.getInstance();
    if (manager == null) {

      return;
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }




    manager.setOutputPower(power);
    manager.setWorkArea(area);
    byte[] version_bs = manager.getFirmware();
    if (version_bs!=null) {

      Log.e("OGTAGDEBUG::", "onResume: " + "(" + new String(version_bs) + ")");
    }

    shared = this.reactContext.getSharedPreferences("UhfRfPower", 0);
    editor = shared.edit();
    power = shared.getInt("power", 26);
    area = shared.getInt("area", UhfManager.WorkArea_USA);


    //start inventory thread
    Thread thread = new InventoryThread();
    thread.start();

    //React Native communication tags thread
    commHandler = new Handler(getReactApplicationContext().getMainLooper());




    commHandler.postDelayed(commRunnable, 1000);

    // init sound pool
    Util.initSoundPool(this.reactContext);

  }


  /**
   * Event sender from React Native
   * @param reactContext
   * @param eventName
   * @param params
   */
  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
  }


  @Override
  public String getName() {
    return "RNFabacusVh71";
  }

  /**
   * RN bridge mnethod to connect to selected device
   * @param deviceName
   */
  @ReactMethod
  public void connectToDevice(String deviceName) {
    Log.e("CDCDC", "connectToDevice :; attemp to connect to "+deviceName);


  }


  @ReactMethod
  public void activateScan() {
    Log.e("OGTAGDEBUG::", "activateScan: " );

    if (!startFlag) {
      startFlag = true;

      WritableMap map = Arguments.createMap();
      map.putString("scanningStatus","ON");
      sendEvent(reactContext, "onDeviceScanningStatusChanged",map);
      //buttonStart.setText(R.string.stop_inventory);
    } else {
      startFlag = false;
      WritableMap map = Arguments.createMap();
      map.putString("scanningStatus","OFF");
      sendEvent(reactContext, "onDeviceScanningStatusChanged",map);
      //buttonStart.setText(R.string.inventory);
    }
  }
  @ReactMethod
  public void resetInventory() {
    Log.e("OGTAGDEBUG::", "activateScan: " );
    listEPC.clear();

  }





  /**
   * RN bridge method to search devices already paired via Bluetooth
   */
  @ReactMethod
  public void searchDevices() {

    Log.e("OGTAGDEBUG::", "handleMessage: CONNECTING_OK" );
    WritableMap map = Arguments.createMap();
    map.putString("status","CONNECTING_OK");
    sendEvent(reactContext, "FabacusOnDeviceConnectionChanghed",map);
  }


  /**
   * Inventory EPC Thread
   */
  class InventoryThread extends Thread {
    private List<byte[]> epcList;

    @Override
    public void run() {
      super.run();
      while (runFlag) {
        if (startFlag) {
          // manager.stopInventoryMulti()
          epcList = manager.inventoryRealTime(); // inventory real time
          if (epcList != null && !epcList.isEmpty()) {
            // play sound
            Util.play(1, 0);
            for (byte[] epc : epcList) {
              String epcStr = Tools.Bytes2HexString(epc,
                      epc.length);
              addToList(listEPC, epcStr);


            }
          }
          epcList = null;
          try {
            Thread.sleep(40);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
  }

  // EPC add to LISTVIEW
  private void addToList(final List<EPC> list, final String epc) {
    this.reactContext.runOnUiQueueThread(new Runnable() {
      @Override
      public void run() {
        // The epc for the first time
        if (list.isEmpty()) {
          EPC epcTag = new EPC();
          epcTag.setEpc(epc);
          epcTag.setCount(1);
          list.add(epcTag);
          listepc.add(epc);
        } else {
          for (int i = 0; i < list.size(); i++) {
            EPC mEPC = list.get(i);
            // list contain this epc
            if (epc.equals(mEPC.getEpc())) {
              mEPC.setCount(mEPC.getCount() + 1);
              list.set(i, mEPC);




              break;
            } else if (i == (list.size() - 1)) {
              // list doesn't contain this epc
              EPC newEPC = new EPC();
              newEPC.setEpc(epc);
              newEPC.setCount(1);
              list.add(newEPC);
              listepc.add(epc);
             // Log.e("OGTAGDEBUG::", "run: getId" +newEPC.getId() );




            }
          }
        }
        // add the data to ListView
        listMap = new ArrayList<Map<String, Object>>();
        int idcount = 1;
        for (EPC epcdata : list) {
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("ID", idcount);
          map.put("EPC", epcdata.getEpc());
          map.put("COUNT", epcdata.getCount());
          idcount++;
          listMap.add(map);


        }

      }
    });
  }

}