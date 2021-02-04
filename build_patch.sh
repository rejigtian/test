BASE_APK="official/release/base-app.apk"
MAPPING="official/release/base-app-mapping.apk"
R="official/release/base-app-R.apk"
APK_LOCATION="master"

echo "-- 开始构建热修复补丁包 --"
for i in "$@"; do
    echo $i
done

if [[ -n "$1" ]]; then
  BASE_APK="$1"
  else
    echo "参数1：是否开启混淆0关闭，1开启；参数2：安装包路径；[参数3：mapping文件路径]；[参数4：R文件路径]；[参数5：分支]"
    exit
fi

if [[ -n "$2" ]]; then
  APK_LOCATION="$2"
  else
    echo "参数1：是否开启混淆0关闭，1开启；参数2：安装包路径；[参数3：mapping文件路径]；[参数4：R文件路径]；[参数5：分支]"
    exit
fi

if [[ -n "$3" ]]; then
  cp -f "$3" MAPPING
fi

if [[ -n "$4" ]]; then
  cp -f "$4" R
fi

if [[ -n "$5" ]]; then
  git checkout "$5"
fi

rm -r -f official/release/base-app*
cp -f APK_LOCATION BASE_APK
./gradlew buildTinkerPatchRelease
cp -f -a app/build/outputs/patch official/release/patch
echo "-- 热修复打包完成，请复核MF文件，保证检查版本信息正确! --"