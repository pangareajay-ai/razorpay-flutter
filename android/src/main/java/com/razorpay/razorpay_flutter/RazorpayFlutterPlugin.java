package com.razorpay.razorpay_flutter;

import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class RazorpayFlutterPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

    private RazorpayDelegate razorpayDelegate;
    private ActivityPluginBinding pluginBinding;
    private static final String CHANNEL_NAME = "razorpay_flutter";
    Map<String, Object> _arguments;
    String customerMobile;
    String color;
    private MethodChannel channel;

    public RazorpayFlutterPlugin() {}

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "open":
                razorpayDelegate.openCheckout((Map<String, Object>) call.arguments, result);
                break;
            case "setPackageName":
                razorpayDelegate.setPackageName((String) call.arguments);
                break;
            case "resync":
                razorpayDelegate.resync(result);
                break;
            case "setKeyID":
                razorpayDelegate.setKeyID(call.arguments.toString(), result);
                break;
            case "linkNewUpiAccount":
                _arguments = (Map<String, Object>) call.arguments;
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.linkNewUpiAccount(customerMobile, color, result);
                break;
            case "manageUpiAccounts":
                _arguments = (Map<String, Object>) call.arguments;
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.manageUpiAccounts(customerMobile, color, result);
                break;
            case "isTurboPluginAvailable":
                razorpayDelegate.isTurboPluginAvailable(result);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.razorpayDelegate = new RazorpayDelegate(binding.getActivity());
        this.pluginBinding = binding;
        binding.addActivityResultListener(razorpayDelegate);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (pluginBinding != null) {
            pluginBinding.removeActivityResultListener(razorpayDelegate);
            pluginBinding = null;
        }
    }
}
