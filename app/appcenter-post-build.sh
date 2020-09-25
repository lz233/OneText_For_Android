curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/sendMessage -d "chat_id=$ONETEXT_CHANNEL_ID&text=Finishing build $APPCENTER_BUILD_ID%0AVariant $APPCENTER_ANDROID_VARIANT"
if [ "$AGENT_JOBSTATUS" == "Succeeded" ]; then
    export name=$(find $APPCENTER_OUTPUT_DIRECTORY -name '*.apk')
    curl https://api.telegram.org/bot${BOT_TOKEN}/sendDocument -X POST -F chat_id="$ONETEXT_CHANNEL_ID" -F document="@$name"
fi
