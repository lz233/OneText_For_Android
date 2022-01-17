PRE_BUILD_MESSAGE_RESULT=$(curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/sendMessage -d "chat_id=$ONETEXT_CHANNEL_ID&parse_mode=MarkdownV2&text=*🚀 Build $APPCENTER_BUILD_ID Starting*%0A%0A*Variant* $APPCENTER_ANDROID_VARIANT")
export PRE_BUILD_MESSAGE_ID=$(echo ${PRE_BUILD_MESSAGE_RESULT} | jq '.result.message_id')
echo $PRE_BUILD_MESSAGE_ID >PRE_BUILD_MESSAGE_ID
curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/sendMessage -d $(which java)