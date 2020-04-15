if [ "$AGENT_JOBSTATUS" == "Succeeded" ]; then
    export name=$(find $APPCENTER_OUTPUT_DIRECTORY -name '*.apk')
    curl https://api.telegram.org/bot${BOT_TOKEN}/sendDocument -X POST -F chat_id="$SMCI_CHANNEL_ID" -F document="@$name"
    curl https://api.telegram.org/bot${BOT_TOKEN}/sendDocument -X POST -F chat_id="$ONETEXT_CHANNEL_ID" -F document="@$name"
fi