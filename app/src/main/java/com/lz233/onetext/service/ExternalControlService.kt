package com.lz233.onetext.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.DeviceTypes
import android.service.controls.actions.ControlAction
import android.service.controls.templates.StatelessTemplate
import androidx.annotation.RequiresApi
import com.lz233.onetext.OneTextApp
import com.lz233.onetext.R
import com.lz233.onetext.activity.MainActivity
import com.lz233.onetext.tools.utils.CoreUtil
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.ReplayProcessor
import org.reactivestreams.FlowAdapters
import java.util.*
import java.util.concurrent.Flow
import java.util.function.Consumer
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.R)
class ExternalControlService : ControlsProviderService() {
    val context = OneTextApp.context
    val i = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra("fromExternalControl", true)
    }
    val pi: PendingIntent =
            PendingIntent.getActivity(
                    context, 2333, i,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
    val updatePublisher = ReplayProcessor.create<Control>()
    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        val controls = mutableListOf(buildOnetext())
        // Create more controls here if needed and add it to the ArrayList

        // Uses the RxJava 2 library
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls))
    }

    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        if (controlIds.contains("onetext")) {
            val control = buildOnetext()

            updatePublisher.onNext(control)
        }

        // If you have other controls, check that they have been selected here

        // Uses the Reactive Streams API
        return FlowAdapters.toFlowPublisher(updatePublisher)
    }

    override fun performControlAction(controlId: String, action: ControlAction, consumer: Consumer<Int>) {
        /* First, locate the control identified by the controlId. Once it is located, you can
         * interpret the action appropriately for that specific device. For instance, the following
         * assumes that the controlId is associated with a light, and the light can be turned on
         * or off.
         */
        if (controlId == "onetext") {

            // Inform SystemUI that the action has been received and is being processed
            consumer.accept(ControlAction.RESPONSE_OK)
            // In this example, action.getNewState() will have the requested action: true for “On”,
            // false for “Off”.

            /* This is where application logic/network requests would be invoked to update the state of
             * the device.
             * After updating, the application should use the publisher to update SystemUI with the new
             * state.
             */
            // This is the publisher the application created during the call to createPublisherFor()
            updatePublisher.onNext(buildOnetext(true))
        }
    }

    private fun buildOnetext(forceFresh: Boolean = false): Control {
        val coreUtil = CoreUtil(context)
        var hashMap: HashMap<Any, Any> = try {
            coreUtil.getOneText(forceFresh, true)
        }catch (e: Throwable){
            e.printStackTrace()
            HashMap<Any, Any>().apply {
                put("text", getString(R.string.control_long_press))
                put("by", "")
            }
        }
        //更新小部件
        context.sendBroadcast(Intent("com.lz233.onetext.widget").apply { setPackage(packageName) })
        return Control.StatefulBuilder("onetext", pi)
                .setTitle(hashMap["text"] as String)
                .setSubtitle(hashMap["by"] as String)
                .setStructure(getString(R.string.app_name))
                .setDeviceType(DeviceTypes.TYPE_UNKNOWN)
                .setStatus(Control.STATUS_OK)
                .setStatusText(getString(R.string.app_name))
                .setControlTemplate(StatelessTemplate("onetext"))
                .setCustomIcon(Icon.createWithResource(OneTextApp.context, R.drawable.ic_notification).setTint(getColor(R.color.colorWhiteWidget)))// For example, DeviceTypes.TYPE_THERMOSTAT
                .build()
    }
}