BASE_APK="official/release/baseapp.apk"
MAPPING="official/release/app-release-mapping.apk"
R="official/release/app-release-R.apk"
APK_URL="master"

echo "-- 开始构建热修复补丁包 --"
for i in "$@"; do
    echo $i
done

if [[ -n "$1" ]]; then
  BASE_APK="$1"
  else
    echo "参数1：是否开启混淆0关闭，1开启；参数2：安装包url；[参数3：mapping文件url]；[参数4：R文件url]；[参数5：分支]"
    exit
fi

if [[ -n "$2" ]]; then
  APK_URL="$2"
  else
    echo "参数1：是否开启混淆0关闭，1开启；参数2：安装包url；[参数3：mapping文件url]；[参数4：R文件url]；[参数5：分支]"
    exit
fi

if [[ -n "$3" ]]; then
  curl "$3" > MAPPING
fi

if [[ -n "$4" ]]; then
  curl "$4" > R
fi

if [[ -n "$5" ]]; then
  git checkout "$5"
fi

rm -r -f BASE_APK
curl APK_URL > BASE_APK
./gradlew buildTinkerPatchRelease
cp -f -a app/build/outputs/patch official/release/patch
echo "-- 热修复打包完成，请复核MF文件，保证检查版本信息正确! --"