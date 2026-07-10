#!/bin/bash

APK_NAME="WwjSimpleService.apk"
APK_PATH="out/target/product/$1/system/app/WwjSimpleService/$APK_NAME"
DEVICE_APK_PATH="/data/local/tmp/$APK_NAME"
PACKAGE_NAME="com.example.simpleservice"
SERVICE_NAME="$PACKAGE_NAME/.WwjSimpleService"

echo "=========================================="
echo "  WwjSimpleService Installation Script"
echo "  (No UI - Background Service Only)"
echo "=========================================="
echo ""

if [ -z "$1" ]; then
    echo "Usage: $0 <product_name>"
    echo "Example: $0 pd2508"
    exit 1
fi

if [ ! -f "$APK_PATH" ]; then
    echo "Error: APK not found at $APK_PATH"
    echo "Please compile first: mmm vendor/androidapp_simple/"
    exit 1
fi

echo "[1/4] Pushing APK to device..."
adb push "$APK_PATH" "$DEVICE_APK_PATH"

echo ""
echo "[2/4] Installing APK..."
adb shell pm install -r "$DEVICE_APK_PATH"
if [ $? -eq 0 ]; then
    echo "APK installed successfully!"
else
    echo "Error: Failed to install APK"
    exit 1
fi

echo ""
echo "[3/4] Starting foreground service (no UI)..."
adb shell am startforegroundservice "$SERVICE_NAME"
if [ $? -eq 0 ]; then
    echo "Service started successfully!"
else
    echo "Error: Failed to start service"
    echo "Trying with explicit intent..."
    adb shell am startforegroundservice -a android.intent.action.MAIN "$SERVICE_NAME"
fi

echo ""
echo "[4/4] Showing logs..."
echo "=========================================="
echo "Press Ctrl+C to stop viewing logs"
echo "Expected output: onCreate, onStartCommand, periodic messages"
echo "=========================================="
adb logcat -s WwjSimpleService -v time
