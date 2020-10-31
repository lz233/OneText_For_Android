package com.lz233.onetext.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.DeviceTypes;
import android.service.controls.actions.ControlAction;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.StatelessTemplate;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.service.controls.templates.ToggleTemplate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.lz233.onetext.activity.MainActivity;

import org.reactivestreams.FlowAdapters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.ReplayProcessor;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ExternalControlService extends ControlsProviderService {
    private ReplayProcessor updatePublisher;
    private final List<Control> mAllControls = new ArrayList<>();
    private PendingIntent mPendingIntent = PendingIntent.getActivity(getBaseContext(), 1, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherForAllAvailable() {
        return new CtsControlsPublisher(mAllControls.stream()
                .map(c -> new Control.StatelessBuilder(c).build())
                .collect(Collectors.toList()));
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> list) {
        Context context = getBaseContext();
        /* Fill in details for the activity related to this device. On long press,
         * this Intent will be launched in a bottomsheet. Please design the activity
         * accordingly to fit a more limited space (about 2/3 screen height).
         */
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        updatePublisher = ReplayProcessor.create();

        // For each controlId in controlIds

        if (list.contains("233")) {

            Control control = new Control.StatefulBuilder("233", pi)
                    // Required: The name of the control
                    .setTitle("MY-CONTROL-TITLE")
                    // Required: Usually the room where the control is located
                    .setSubtitle("MY-CONTROL-SUBTITLE")
                    // Optional: Structure where the control is located, an example would be a house
                    .setStructure("MY-CONTROL-STRUCTURE")
                    // Required: Type of device, i.e., thermostat, light, switch
                    .setDeviceType(DeviceTypes.TYPE_UNKNOWN) // For example, DeviceTypes.TYPE_THERMOSTAT
                    // Required: Current status of the device
                    .setStatus(Control.STATUS_UNKNOWN)
                    .setControlTemplate(new ToggleTemplate("2333", new ControlButton(false,"action")))// For example, Control.STATUS_OK
                    .build();

            updatePublisher.onNext(control);
        }
        // Uses the Reactive Streams API
        return FlowAdapters.toFlowPublisher(updatePublisher);
    }

    @Override
    public void performControlAction(@NonNull String s, @NonNull ControlAction controlAction, @NonNull Consumer<Integer> consumer) {

    }
    public Control buildLight(boolean isOn, float intensity) {
        RangeTemplate rt = new RangeTemplate("range", 0.0f, 100.0f, intensity, 1.0f, null);
        ControlTemplate template =
                new ToggleRangeTemplate("toggleRange", isOn, isOn ? "On" : "Off", rt);
        return new Control.StatefulBuilder("light", mPendingIntent)
                .setTitle("Light Title")
                .setSubtitle("Light Subtitle")
                .setStatus(Control.STATUS_OK)
                .setStatusText(isOn ? "On" : "Off")
                .setDeviceType(DeviceTypes.TYPE_LIGHT)
                .setStructure("Home")
                .setControlTemplate(template)
                .build();
    }

    public Control buildSwitch(boolean isOn) {
        ControlButton button = new ControlButton(isOn, isOn ? "On" : "Off");
        ControlTemplate template = new ToggleTemplate("toggle", button);
        return new Control.StatefulBuilder("switch", mPendingIntent)
                .setTitle("Switch Title")
                .setSubtitle("Switch Subtitle")
                .setStatus(Control.STATUS_OK)
                .setStatusText(isOn ? "On" : "Off")
                .setDeviceType(DeviceTypes.TYPE_SWITCH)
                .setStructure("Home")
                .setControlTemplate(template)
                .build();
    }
}
