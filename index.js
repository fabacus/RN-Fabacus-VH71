'use strict';
import React, {Component, PropTypes} from 'react';
var {
    Platform,
    DeviceEventEmitter,
    requireNativeComponent
} = require('react-native');


import { NativeModules } from 'react-native';
var RNReactNativeRfid = NativeModules.RNFabacusVh71;


class RFID {

    /**
     * Initial method to star search of paired devices,
     * @param res , callback receiber of array of paired devices
     */
    static getDevices(res) {
        RNReactNativeRfid.searchDevices();
        DeviceEventEmitter.addListener('FabacusOnDeviceDetection', (e) => {res(e)});
    }

    /**
     * Receive tag data from device
     * @param res callback method receiving Tags payloads
     */
    static onTagReceived(res){
        DeviceEventEmitter.addListener('FabacusOnTagReceived', (e) => {res(e)});
    }

    /**
     * Handheld trigger event
     * @param res
     */
    static FabacusOnHandheldTriggerEvent(res){
        DeviceEventEmitter.addListener('FabacusOnHandheldTriggerEvent', (e) => {res(e)});
    }

    /**
     * Connect , engage  BT device
     * @param device
     */
    static connect(device) {
        console.log('rect side attemp to connect to '+device)
        RNReactNativeRfid.connectToDevice(device)
    }

    /**
     * Device connection status update events
     * @param res
     */
    static onDeviceConnectionStatusChanged(res){
        DeviceEventEmitter.addListener('FabacusOnDeviceConnectionChanghed', (e) => {res(e)})


    }


    /**
     * Device connection status update events
     * @param res
     */
    static onDeviceScanningStatusChanged(res){
        DeviceEventEmitter.addListener('onDeviceScanningStatusChanged', (e) => {res(e)})


    }

    /**
     * RSSI power signal RFID tag value changed handle event
     * @param res
     */
    static onRSSIValuechanged(res){
        DeviceEventEmitter.addListener('FabacusOnRSSINewValue', (e) => {res(e)})
    }

    /**
     * Reset the inventory hash array.
     */
    static resetInventory(){
        RNReactNativeRfid.resetInventory();
    }

    /**
     * Start/Stop RDIF tags scan
     * @param active bool
     */
    static activateScan(){
        console.log('activate scan called')
        RNReactNativeRfid.activateScan()
    }

}




module.exports = RFID;
