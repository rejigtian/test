package com.huiwan.base.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.R;
import com.huiwan.base.interfaces.SingleCallback;
import com.huiwan.base.str.ResUtil;
import com.huiwan.platform.ThreadUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by zhuguangwen on 15/3/3.
 * email 979343670@qq.com
 */
public class FileUtil {

    private static final int NOT_FOUND = -1;
    public static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final String TAG = FileUtil.class.getName();
    private static final int MAX_READ_STR_LEN = 1024 * 1024;
    private static final long MB_SIZE = 1024 * 1024; //1M
    private static final long KB_SIZE = 1024; //1K
    public static final long GB_SIZE = 1024 * 1024 * 1024; //1G
    public static final long HALF_GB_SIZE = 1024 * 1024 * 512; //1G



    private static Gson gson = new Gson();

    public static void mkdir(String dirStr) throws Exception {
        File dir = new File(dirStr);

        if (!dir.isAbsolute()) {
            throw new Exception("dir is not valid -->" + dirStr);
        }

        if (dir.exists()) {
            return;
        }

        if (!dir.mkdirs()) {
            throw new Exception("mkdir failed");
        }
    }

    public static boolean safeMkdir(String dir) {
        try {
            mkdir(dir);
            return true;
        } catch (Exception e) {
            printe(e.getMessage());
            return false;
        }
    }

    public static void createFile(String fileStr) throws Exception {
        File file = new File(fileStr);

        if (!file.isAbsolute()) {
            throw new Exception("file is not valid -->" + fileStr);
        }

        if (file.exists()) {
            return;
        }

        mkdir(file.getParentFile().getAbsolutePath());

        if (!file.createNewFile()) {
            throw new Exception("create file failed");
        }
    }

    public static boolean safeCreateFile(String fileStr) {
        try {
            createFile(fileStr);
            return true;
        } catch (Exception e) {
            printe(e.getMessage());
            return false;
        }
    }

    public static void deleteFile(String fileStr) throws Exception {
        File file = new File(fileStr);

        if (!file.isAbsolute()) {
            throw new Exception("file is not valid -->" + fileStr);
        }

        if (!file.exists()) {
            return;
        }

        if (!file.delete()) {
            throw new Exception("delete file failed");
        }
    }

    public static boolean safeDeleteFile(String fileStr) {
        try {
            deleteFile(fileStr);
            return true;
        } catch (Exception e) {
            printe(e.getMessage());
            return false;
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws IOException              in case deletion is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        cleanDirectory(directory);

        if (!directory.delete()) {
            final String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException              in case cleaning is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                printe( ioe.getMessage());
            }
        }


    }

    /**
     * Lists files in a directory, asserting that the supplied directory satisfies exists and is a directory
     *
     * @param directory The directory to list
     * @return The files in the directory, never null.
     * @throws IOException if an I/O error occurs
     */
    public static File[] verifiedListFiles(File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    public static List<String> listFileName(String path) {
        String[] files = null;
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                final String message = directory + " does not exist";
                throw new IllegalArgumentException(message);
            }

            if (!directory.isDirectory()) {
                final String message = directory + " is not a directory";
                throw new IllegalArgumentException(message);
            }

             files = directory.list();
            if (files == null) {  // null if security restricted
                throw new IOException("Failed to list contents of " + directory);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        if (files != null) {
            return Arrays.asList(files);
        } else return new ArrayList<>();
    }

    //-----------------------------------------------------------------------

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     * (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file file or directory to delete, must not be {@code null}
     * @throws NullPointerException  if the directory is {@code null}
     * @throws FileNotFoundException if the file was not found
     * @throws IOException           in case deletion is unsuccessful
     */
    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void copyFile(InputStream inputStream, String destFilePath) throws Exception {
        final int BUFFER_SIZE = 1024;
        try {
            createFile(destFilePath);
            FileOutputStream fos = new FileOutputStream(new File(destFilePath));
            byte[] buffer = new byte[BUFFER_SIZE];
            int readByte;
            while ((readByte = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, readByte);
            }
            inputStream.close();
            fos.close();

        } catch (Exception e) {
            safeDeleteFile(destFilePath);
            throw new Exception("copy failed, " + e.getMessage());
        }
    }

    public static void copyFile(String srcFilePath, String destFilePath) throws Exception {
        File srcFile = new File(srcFilePath);
        File destFile = new File(destFilePath);

        if (!srcFile.isAbsolute() || !destFile.isAbsolute()) {
            throw new Exception("source file or dest file is not valid ,"
                    + " source : " + srcFilePath
                    + " dest : " + destFilePath);
        }

        if (!srcFile.exists()) {
            throw new Exception("source file does not exist");
        }

        if (!destFile.exists()) {
            File parentDir = destFile.getParentFile();
            if (parentDir == null) {
                throw new IllegalArgumentException("文件路径异常");
            } else {
                if (!parentDir.exists()) {
                    makeDirs(parentDir);
                }
            }
        }

        FileChannel inChannel = new FileInputStream(srcFile).getChannel();
        FileChannel outChannel = new FileOutputStream(destFile).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            throw new Exception("copy failed, " + e.getMessage());
        } finally {
            inChannel.close();
            outChannel.close();
        }
    }

    public static String getExtensionName(String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        return lastUnixPos;
    }

    public static boolean safeCopyFile(String srcFilePath, String destFilePath) {
        try {
            copyFile(srcFilePath, destFilePath);
            return true;
        } catch (Exception e) {
            printe(e.getMessage());
            return false;
        }
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.canRead();
    }

    /**
     * 遍历一个目录下的文件
     *
     * @param folderPath 目录路径
     * @param callback   遍历到某个文件的回调
     */
    public static void listFilesInFolder(String folderPath, ListFilesCallback callback) {
        File dir = new File(folderPath);
        if (!dir.isDirectory()) {
            throw new RuntimeException(folderPath + "is not a folder");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            throw new RuntimeException("can not list files :" + folderPath);
        }
        for (File f : files) {
            callback.onGetFile(f);
        }
    }

    public interface ListFilesCallback {
        public void onGetFile(File f);
    }


    public static void safeDeleteFile(File file) {
        try {
            deleteFile(file);
        } catch (Exception e) {
            printe("safe delete file failed");
        }
    }

    /**
     * delete a file
     *
     * @param file the file to delete
     * @throws RuntimeException RuntimeException when delete or delete failed
     */
    public static void deleteFile(File file) throws RuntimeException {
        if (!file.isAbsolute()) {
            throw new RuntimeException(file.getPath() + " is not a valid file path");
        }
        if (!file.exists()) {
            return;
        }
        if (!file.delete()) {
            throw new RuntimeException("delete file failed");
        }
    }

    /**
     * create a file
     *
     * @param file the file to create
     * @throws RuntimeException file is not a valid file, or creation failed
     */
    public static void createFile(File file) throws RuntimeException {
        if (!file.isAbsolute()) {
            throw new RuntimeException(file.getPath() + " is not a valid file path");
        }

        if (file.exists()) {
            return;
        }

        makeDirs(file.getParentFile());

        boolean ret;
        try {
            ret = file.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException("");
        }
        if (!ret) {
            throw new RuntimeException("create file failed -->" + file.getPath());
        }
    }

    /**
     * create a directory file
     *
     * @param dir the dir file to create
     * @throws RuntimeException dir is not valid, or creation failed
     */
    public static void makeDirs(File dir) throws RuntimeException {
        if (!dir.isAbsolute()) {
            throw new RuntimeException(dir.getPath() + "is not a valid directory path");
        }

        if (dir.exists()) {
            return;
        }

        if (!dir.mkdirs()) {
            throw new RuntimeException("mkdirs failed -->" + dir.getPath());
        }
    }

    public static void writeFileSync(String fileName, String content) {
        writeFile(fileName, content);
    }

    public static String readFileSync(String fileName) {
        return readFile(fileName);
    }

    public static byte[] readInputStream(InputStream is) {
        try {
            byte[] bytes = new byte[4096];
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            int len;
            while ((len = is.read(bytes)) > 0) {
                bao.write(bytes, 0, len);
            }
            return bao.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static String readAssetSync(String assetName) {
        AssetManager assetManager = LibBaseUtil.getApplication().getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(assetName);
            byte[] bytes = readInputStream(is);
            if (bytes == null) {
                return "";
            } else {
                return new String(bytes);
            }
        } catch (IOException e) {
            printe("error open asset file: " + e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    printe("error close asset file: " + e);
                }
            }
        }
        return "";
    }

    public static void writeFileAsync(final String fileName, final String content) {
        ThreadUtil.runInOtherThread(new Runnable() {
            @Override
            public void run() {
                writeFileSync(fileName, content);
            }
        });
    }

    public static void readFileAsync(final String fileName, final CacheCallback callback) {
        ThreadUtil.runInOtherThread(new Runnable() {
            @Override
            public void run() {
                final String json = readFileSync(fileName);
                Log.d("FileUtil cache ---->", "fileName : " + fileName + "\n json :\n " + json);
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) callback.onFinish(json);
                    }
                });
            }
        });
    }

    public static void readFileAsync(final File file, final CacheCallback callback) {
        ThreadUtil.runInOtherThread(new Runnable() {
            @Override
            public void run() {
                final String json = readFile(file);
                Log.d("FileUtil cache ---->", "fileName : " + file.getAbsolutePath() + "\n json :\n " + json);
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) callback.onFinish(json);
                    }
                });
            }
        });
    }

    @Nullable
    public static <T> T loadEntityFromFile(@NonNull final String fileName, @NonNull final Class<T> c) {
        String json = readFileSync(fileName);
        T cache = null;
        if (!TextUtils.isEmpty(json)) {
            try {
                cache = gson.fromJson(json, c);
            } catch (Exception e) {
                Log.e(TAG, "parse json entity failed: " + json, e);
            }
        }
        return cache;
    }

    @Nullable
    public static <T> List<T> loadEntityListFromFile(@NonNull final String fileName, @NonNull final Class<T> entityClass) {
        String json = readFileSync(fileName);
        if (!TextUtils.isEmpty(json)) {
            try {
                JsonParser parser = new JsonParser();
                JsonArray ja = parser.parse(json).getAsJsonArray();
                int size = ja.size();
                List<T> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(gson.fromJson(ja.get(i), entityClass));
                }
                return list;
            } catch (Exception e) {
                Log.e(TAG, "parse json array failed: " + json, e);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T loadEntityFromAsset(@NonNull final String fileName, @NonNull final Class<T> c) {
        String json = readAssetSync(fileName);
        T cache = null;
        if (!TextUtils.isEmpty(json)) {
            try {
                cache = gson.fromJson(json, c);
            } catch (Exception e) {
                Log.e(TAG, "parse json entity failed: " + json, e);
            }
        }
        return cache;
    }

    @Nullable
    public static <T> List<T> loadEntityListFromAsset(@NonNull final String fileName, @NonNull final Class<T> entityClass) {
        String json = readAssetSync(fileName);
        if (!TextUtils.isEmpty(json)) {
            try {
                JsonParser parser = new JsonParser();
                JsonArray ja = parser.parse(json).getAsJsonArray();
                int size = ja.size();
                List<T> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(gson.fromJson(ja.get(i), entityClass));
                }
                return list;
            } catch (Exception e) {
                Log.e(TAG, "parse json array failed: " + json, e);
            }
        }
        return null;
    }

    /**
     * 单线程顺序将非空 entity 写入应用文件夹下文件
     *
     * @param fileName 文件名
     * @param entity   实体信息
     */
    public static void writeEntityAsync(final String fileName, final Object entity) {
        if (entity != null) {
            ThreadUtil.runInOtherThread(new Runnable() {
                @Override
                public void run() {
                    writeFileSync(fileName, gson.toJson(entity));
                }
            });
        }
    }

    public static String getFileNameFromPath(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    public interface CacheCallback {
        void onFinish(String json);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @Nullable
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            printe(e.getMessage());
        } finally {
            try {
                if (cursor != null) cursor.close();
            } catch (Exception e) {
                printe(e.getMessage());
            }
        }
        return null;
    }

    public static String getFileName(Context context, Uri uri){
        String fileName = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            printe(e.getMessage());
        } finally {
            try {
                if (cursor != null) cursor.close();
            } catch (Exception e) {
                printe(e.getMessage());
            }
        }

        return fileName;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static long getDirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    result += getDirSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }

    public static boolean zipFile(String src, String dest) {
        ZipOutputStream out = null;
        try {
            File outFile = new File(dest);
            File fileOrDirectory = new File(src);
            out = new ZipOutputStream(new FileOutputStream(outFile));
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                File[] files = fileOrDirectory.listFiles();
                for (File f : files) {
                    zipFileOrDirectory(out, f, "");
                }
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    private static void zipFileOrDirectory(ZipOutputStream out,
                                           File fileOrDirectory, String curPath) throws IOException {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            if (!fileOrDirectory.isDirectory()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                in = new FileInputStream(fileOrDirectory);
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                out.putNextEntry(entry);
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.closeEntry();
            } else {
                File[] files = fileOrDirectory.listFiles();
                for (File file : files) {
                    zipFileOrDirectory(out, file, curPath + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            printe("error zip file");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    printe("");
                }
            }
        }
    }

    /**
     * 获取某个目录下的最老文件
     *
     * @param folderPath 目录路径
     */
    public static File findOldestFileInFolder(String folderPath) {
        File dir = new File(folderPath);
        if (!dir.isDirectory()) {
            throw new RuntimeException(folderPath + "is not a folder");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            throw new RuntimeException("can not list files :" + folderPath);
        }
        File maxfile=files[0];
        for (File f : files) {
            maxfile = maxfile.lastModified() < f.lastModified() ? maxfile:f;
        }
        return maxfile;
    }

    public static Uri getUriFromRaw(@RawRes int rawInt) {
        return Uri.parse("android.resource://" + LibBaseUtil.getApplication().getPackageName() + "/" + rawInt);
    }

    public static Uri idToUri(Context context, int resourceId) {
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
    }

    public static void copyFileFromAssets(String assetsPath, String destFilePath) {
        final int BUFFER_SIZE = 1024;
        InputStream inputStream = null;
        try {
            inputStream = LibBaseUtil.getApplication().getResources().getAssets().open(assetsPath);
            createFile(destFilePath);
            FileOutputStream fos = new FileOutputStream(new File(destFilePath));
            byte[] buffer = new byte[BUFFER_SIZE];
            int readByte;
            while ((readByte = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, readByte);
            }
            inputStream.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            safeDeleteFile(destFilePath);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Nullable
    public static String readFile(String fileName) {
        String path = getFullPath(fileName);
        return readFile(new File(path));
    }

    public static String readFile(File file) {
        BufferedReader br = null;
        try {
            if (!file.exists()) return null;
            br = new BufferedReader(new FileReader(file));

            String data;
            StringBuilder sb = new StringBuilder();
            while ((data = br.readLine()) != null) {
                sb.append(data);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeFile(String fileName, String content) {
        String path = getFullPath(fileName);
        BufferedWriter out = null;
        try {
            if (content == null) content = "";
            File file = new File(path);
            file.getParentFile().mkdirs();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(File file, String content) {
        BufferedWriter out = null;
        try {
            if (content == null) content = "";
            file.getParentFile().mkdirs();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前最大读取 1 Mb 大小字符串, 推测当前文件最可能的编码方式
     * @param path 文件全路径
     * @return 字符串
     */
    @Nullable
    public static String detectEncoderAndRead(String path) {
        InputStream i = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            int readLen = (int)file.length();
            if (readLen > MAX_READ_STR_LEN) {
                printe("file length over 1MB, ignore overload");
                readLen = MAX_READ_STR_LEN;
            }
            byte[] b = new byte[readLen];
            i = new FileInputStream(file);
            int len = i.read(b);
            if (len < readLen) {
                printe("read error for no fully read file");
            }
            int code = EncodingDetect.get().detectEncoding(b);
            String codingName = EncodingDetect.get().getCodingById(code);
            if (TextUtils.isEmpty(codingName)) {
                codingName = "utf8";
            }
            return new String(b, 0, len, codingName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (i != null) {
                    i.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isValid(File file) {
        if (file.exists()) {
            if (file.isFile() && file.length() != 0) {
                return true;
            } else if (file.isDirectory()) {
                File[] list = file.listFiles();
                return list != null && list.length != 0;
            }
        }
        return false;
    }

    public static void unpackSync(InputStream is, String dirPath) throws IOException {
        ZipInputStream zipIs = null;
        try {
            zipIs = new ZipInputStream(is);
            ZipEntry ze;
            while ((ze = zipIs.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    File dirFile = new File(dirPath);
                    if (!dirFile.exists()) {
                        dirFile.mkdir();
                    }
                } else {
                    cpyEntry(zipIs, new File(dirPath, ze.getName()));
                    zipIs.closeEntry();
                }
            }
        } finally {
            if (zipIs != null) {
                try {
                    zipIs.close();
                } finally {
                    ;
                }
            }
        }
    }

    private static void cpyEntry(InputStream is, File file) throws IOException {
        FileOutputStream fos = null;
        try {
            FileUtil.createFile(file);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int readByte;
            while ((readByte = is.read(buffer)) != -1) {
                fos.write(buffer, 0, readByte);
            }
        } finally {
            try {
                if (fos != null) fos.close();
            } finally {
                ;
            }
        }
    }

    @NonNull
    public static String getFullPath(String fileName){
        return LibBaseUtil.getApplication().getFilesDir() + "/" + getCacheFolderName() + "/" + fileName;
    }

    @NonNull
    private static String getCacheFolderName() {
        /* 用 final 做值时切换账户并不会重新赋值 */
        return LibBaseUtil.envDebug() ? "cache_debug" : "cache_release";
    }

    public interface UserCacheCallback {
        void onFinish(String json);
    }

    public interface ISaveCallback {
        void onSave(boolean success, String path);
    }

    public static void printe(String s){
        if (s == null) {
            return;
        }
        if (LibBaseUtil.envDebug()) {
            Log.e(TAG, s);
        }
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        // 判斷是否為Android 4.4之後的版本
        final boolean after44 = Build.VERSION.SDK_INT >= 19;
        if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是Android 4.4之後的版本，而且屬於文件URI
            final String authority = uri.getAuthority();
            // 判斷Authority是否為本地端檔案所使用的
            if ("com.android.externalstorage.documents".equals(authority)) {
                // 外部儲存空間
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                if ("primary".equals(type)) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(divide[1]);
                    return path;
                } else {
                    String path = "/storage/".concat(type).concat("/").concat(divide[1]);
                    return path;
                }
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                // 下載目錄
                final String docId = DocumentsContract.getDocumentId(uri);
                if (docId.startsWith("raw:")) {
                    final String path = docId.replaceFirst("raw:", "");
                    return path;
                }
                final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                String path = queryAbsolutePath(context, downloadUri);
                return path;
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // 圖片、影音檔案
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];
                Uri mediaUri = null;
                if ("image".equals(type)) {
                    mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    return null;
                }
                mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
                String path = queryAbsolutePath(context, mediaUri);
                return path;
            }
        } else {
            // 如果是一般的URI
            final String scheme = uri.getScheme();
            String path = null;
            if ("content".equals(scheme)) {
                // 內容URI
                path = queryAbsolutePath(context, uri);
            } else if ("file".equals(scheme)) {
                // 檔案URI
                path = uri.getPath();
            }
            return path;
        }
        return null;
    }

    public static String queryAbsolutePath(final Context context, final Uri uri) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static String inputStream2Base64(InputStream inputStream) {
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            data = new byte[inputStream.available()];
            inputStream.read(data);
            //用默认的编码格式进行编码
            result = android.util.Base64.encodeToString(data, Base64.NO_CLOSE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void saveImgToAndroidAsync(String path, String destFullPath, SingleCallback<String> callback) {
        ThreadUtil.runInOtherThread(()->{
            String res = saveImgToAndroid(path, destFullPath);
            if (callback != null) {
                ThreadUtil.runOnUiThread(()->{
                    if (TextUtils.isEmpty(res)) {
                        callback.onCall(res);
                    } else {
                        callback.onErr(-1, ResUtil.getStr(R.string.save_failed));
                    }
                });
            }
        });
    }

    public static void saveImgToAndroid(Bitmap bitmap, String destFullPath, SingleCallback<String> callback) {
        ThreadUtil.runInOtherThread(()->{
            String res = saveImgToAndroid(bitmap, destFullPath);
            if (callback != null) {
                ThreadUtil.runOnUiThread(()->{
                    if (!TextUtils.isEmpty(res)) {
                        callback.onCall(res);
                    } else {
                        callback.onErr(-1, ResUtil.getStr(R.string.save_failed));
                    }
                });
            }
        });
    }

    public static String saveImgToAndroid(Bitmap bitmap, String destFullPath) {
        try {
            if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String name = simpleDateFormat.format(System.currentTimeMillis()) + (System.currentTimeMillis() % 1000);
                String destName = "image-" + name + ".jpg";
                if (saveImgToAndroid10(bitmap, destName)) {
                    return destName;
                } else {
                    return null;
                }
            } else {
                FileUtil.createFile(new File(destFullPath));
                FileOutputStream fos = new FileOutputStream(destFullPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.close();
                updateSystemGallery(LibBaseUtil.getApplication(), destFullPath);
                return destFullPath;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String saveImgToAndroid(String path, String destFullPath) {
        try {
            if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String name = simpleDateFormat.format(System.currentTimeMillis()) + (System.currentTimeMillis() % 1000);
                String destName = "image-" + name + ".jpg";
                if (saveImgToAndroid10(path, destName)) {
                    return destName;
                } else {
                    return null;
                }
            } else {
                saveFileToAndroidExclude10(path, destFullPath);
                updateSystemGallery(LibBaseUtil.getApplication(), destFullPath);
                return destFullPath;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void updateSystemGallery(Context context, final String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(fileName)));
        context.sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static boolean saveImgToAndroid10(String path, String destName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, destName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        Uri uri = LibBaseUtil.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try {
                InputStream is = new FileInputStream(path);
                OutputStream os = LibBaseUtil.getApplication().getContentResolver().openOutputStream(uri);
                FileUtils.copy(is, os);
                is.close();
                os.close();
                return true;
            } catch (Exception e) {
                LibBaseUtil.logInfo("FileUtil", "save file {}", e);
            }
        }
        return false;
    }

    private static boolean saveImgToAndroid10(Bitmap bitmap, String destName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, destName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        Uri uri = LibBaseUtil.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try {
                OutputStream os = LibBaseUtil.getApplication().getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
                os.close();
                return true;
            } catch (Exception e) {
                LibBaseUtil.logInfo("FileUtil", "save file {}", e);
            }
        }
        return false;
    }

    private static void saveFileToAndroidExclude10(String path, String destFullPath) throws Exception {
        FileUtil.copyFile(path, destFullPath);
    }

    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getAllFilesInDir(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    /**
     *
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getAllFilesInDir(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getAllFilesInDir(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static String convertSizeUnit(long size){
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (size > MB_SIZE){
            return decimalFormat.format(size/(float)MB_SIZE) + "MB";
        } else if (size > KB_SIZE){
            return decimalFormat.format(size/(float)KB_SIZE) + "KB";
        } else {
            return size + "B";
        }
    }
}
