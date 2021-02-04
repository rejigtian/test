FIX_BRANCH="hotfix"
BASE_BRANCH="master"
BASE_APK="official/release/base-app.apk"


echo "-- 构建热修复补丁包,请确认分支存在 --"
for i in "$@"; do
    echo $i
done

if [[ -n "$1" ]]; then
  FIX_BRANCH="$1"
  if [[ -n "$2" ]]; then
      BASE_BRANCH="$2"
       else
          echo "未传入基线分支，使用当前分支作为基线"
  fi
  echo "目前使用基线分支 {$BASE_BRANCH}， 使用修复分支 {$FIX_BRANCH}"
  else
    echo "未传入修复分支与基线分支，默认使用基线分支 {$BASE_BRANCH}， 使用修复分支 {$FIX_BRANCH}"
fi

git checkout $BASE_BRANCH
rm -r -f official/release/*
./gradlew assembleRelease -P BUILD_PATCH=true
cp -f official/release/baseapp/baseapp-release.apk BASE_APK
git checkout $FIX_BRANCH
./gradlew buildTinkerPatchRelease
cp -f -a app/build/outputs/patch official/release/patch
echo "-- 热修复打包完成，请复核MF文件，保证检查版本信息正确! --"