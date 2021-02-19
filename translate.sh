opencc -c s2t.json -i app/src/main/res/values-zh/strings.xml -o app/src/main/res/values-zh-rMO/strings.xml
opencc -c s2hk.json -i app/src/main/res/values-zh/strings.xml -o app/src/main/res/values-zh-rHK/strings.xml
opencc -c s2twp.json -i app/src/main/res/values-zh/strings.xml -o app/src/main/res/values-zh-rTW/strings.xml
cp app/src/main/res/values-zh-rMO/strings.xml? app/src/main/res/values-zh-rMO/strings.xml
rm app/src/main/res/values-zh-rMO/strings.xml?
cp app/src/main/res/values-zh-rHK/strings.xml? app/src/main/res/values-zh-rHK/strings.xml
rm app/src/main/res/values-zh-rHK/strings.xml?
