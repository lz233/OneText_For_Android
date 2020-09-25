curl -X POST https://api.telegram.org/bot${BOT_TOKEN}/sendMessage -d "chat_id=$ONETEXT_CHANNEL_ID&text=Starting build $APPCENTER_BUILD_ID\nVariant $APPCENTER_ANDROID_VARIANT"
