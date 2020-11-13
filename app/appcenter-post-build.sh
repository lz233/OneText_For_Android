PRE_BUILD_MESSAGE_ID=$(cat PRE_BUILD_MESSAGE_ID)
METADATA_NAME=$(find $APPCENTER_OUTPUT_DIRECTORY -name 'output-metadata.json')
VERSION_NAME=$(cat METADATA_NAME | jq .elements[0].versionName | sed 's/\./\\\./g' | sed 's/\-/\\-/g')
VERSION_CODE=$(cat METADATA_NAME | jq .elements[0].versionCode)
curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/deleteMessage -d "chat_id=$ONETEXT_CHANNEL_ID&message_id=$PRE_BUILD_MESSAGE_ID"
if [ "$AGENT_JOBSTATUS" == "Succeeded" ]; then
  export FILE_NAME=$(find $APPCENTER_OUTPUT_DIRECTORY -name '*.apk')
  curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/sendMessage -d "chat_id=$ONETEXT_CHANNEL_ID&parse_mode=MarkdownV2&text=*🚀 Build $APPCENTER_BUILD_ID Succeeded*%0A%0A*Variant* ${APPCENTER_ANDROID_VARIANT}%0A*Version Name* ${VERSION_NAME}%0A*Version Code* ${VERSION_CODE}"
  curl https://api.telegram.org/bot${BOT_TOKEN}/sendDocument -X POST -F chat_id="$ONETEXT_CHANNEL_ID" -F document="@$FILE_NAME"
else
  curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/sendMessage -d "chat_id=$ONETEXT_CHANNEL_ID&parse_mode=MarkdownV2&text=*🚀 Build $APPCENTER_BUILD_ID Failed*%0A%0A*Variant* $APPCENTER_ANDROID_VARIANT"
fi
