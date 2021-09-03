BASE_APK="official/release/base-app.apk"
MAPPING="official/release/base-app-mapping.txt"
R="official/release/base-app-R.txt"
APK_LOCATION="official/release/base-app.apk"
BUILD_TYPE=0;

echo "-- 开始构建热修复补丁包 --"
for i in "$@"; do
    echo $i
done

if [[ -n "$1" ]]; then
  BUILD_TYPE="$1"
  else
    echo "参数1：使用url或路径，0=路径，1=url；参数2：安装包；[参数3：R文件]；[参数4：mapping文件]；[参数5：分支]"
    exit
fi

if [[ -n "$2" ]]; then
  APK_LOCATION="$2"
  else
    echo "参数1：使用url或路径，0=路径，1=url；参数2：安装包；[参数3：R文件]；[参数4：mapping文件]；[参数5：分支]"
    exit
fi

rm -r -f official/release/base-app*

if [ $BUILD_TYPE == 0 ]; then {
    cp -f $APK_LOCATION $BASE_APK
    if [[ -n "$3" ]]; then
      cp -f "$3" $R
    fi

    if [[ -n "$4" ]]; then
      cp -f "$4" $MAPPING
    fi
  }
  else {
    curl $APK_LOCATION > $BASE_APK
    if [[ -n "$3" ]]; then
      curl "$3" > $R
    fi

    if [[ -n "$4" ]]; then
      curl "$4" > $MAPPING
    fi
  }
fi

if [[ -n "$5" ]]; then
  git checkout "$5"
fi

./gradlew buildTinkerPatchRelease
cp -f -a app/build/outputs/patch official/release
echo "-- 热修复打包完成，请复核MF文件，保证检查版本信息正确! --"