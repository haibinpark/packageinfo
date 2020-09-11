package com.sctuopuyi.packageinfos.updateApk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.sctuopuyi.packageinfos.R;

import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by fengmlo on 2018/3/1.
 */

public class Updater {

    private static WeakReference<AlertDialog> updateDialog;
    private static WeakReference<AlertDialog> downloadDialog;

    public static void check(final Context context, final String url, final CheckUpdateRequest request,final String packageName) {
        if (downloadDialog != null && downloadDialog.get() != null) return;
        Net.init(context, url,packageName);
        Net.getInstance().checkUpdate(request.toQueryMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseHttpResponse<CheckUpdateResponse>>() {
                    @Override
                    public void call(BaseHttpResponse<CheckUpdateResponse> checkUpdateResponseBaseHttpResponse) {
                        if (checkUpdateResponseBaseHttpResponse.getCode() == 0) {
                            CheckUpdateResponse result = checkUpdateResponseBaseHttpResponse.getResult();
                            if (result == null) {
//                                Toast.makeText(context, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                            } else if (result.getVersionCode() > request.getVersionCode()) {
                                showUpdateDialog(context, result);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(context, "检查更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void check(final Context context, final String url, final CheckUpdateRequest request, final CheckUpdateListener listener,final String packageTag) {
        if (listener == null) return;
        Net.init(context, url,packageTag);
        Net.getInstance().checkUpdate(request.toQueryMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseHttpResponse<CheckUpdateResponse>>() {
                    @Override
                    public void call(BaseHttpResponse<CheckUpdateResponse> checkUpdateResponseBaseHttpResponse) {
                        if (checkUpdateResponseBaseHttpResponse.getCode() == 0) {
                            CheckUpdateResponse result = checkUpdateResponseBaseHttpResponse.getResult();
                            if (result == null) {
//                                Toast.makeText(context, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                                listener.onCheckUpdate(false, null);
                            } else if (result.getVersionCode() > request.getVersionCode()) {
//                                showUpdateDialog(context, result);
                                listener.onCheckUpdate(true, result);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(context, "检查更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void showUpdateDialog(final Context context, final CheckUpdateResponse checkUpdateResponse) {
        AlertDialog oldDialog;
        if (updateDialog != null
                && (oldDialog = updateDialog.get()) != null
                && oldDialog.isShowing()) {
            oldDialog.dismiss();
        }
        String message = "版本名称：" + checkUpdateResponse.getVersion() + "\n" +
                "文件大小：" + String.format(Locale.getDefault(), "%.2fMB", checkUpdateResponse.getFileSizeBytes() / (1024.0f * 1024.0f)) + "\n" +
                "更新日期：" + checkUpdateResponse.getCurrentVersionReleaseDate() + "\n" +
                "更新日志：" + "\n" +
                checkUpdateResponse.getReleaseNotes();
        final boolean isForce = "1".equals(checkUpdateResponse.getUpdateStrategy());
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("有新的版本")
                .setMessage(message)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDownloadDialog(context, checkUpdateResponse.getUpdateUrl(), isForce);
                    }
                });
        if (isForce) {
            builder.setCancelable(false);
        } else {
            builder.setCancelable(true)
                    .setNegativeButton("暂不更新", null);
        }
        final AlertDialog alertDialog = builder.show();
        if ("1".equals(checkUpdateResponse.getUpdateMode())) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(checkUpdateResponse.getUpdateUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (isIntentAvailable(context, intent)) {
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "没有可用的浏览器", Toast.LENGTH_SHORT).show();
                    }
                    if (!isForce) alertDialog.dismiss();
                }
            });
        }
        Updater.updateDialog = new WeakReference<>(alertDialog);
    }

    public static void showDownloadDialog(final Context context, String url, final boolean isForce) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_porgress, new LinearLayout(context), false);
        final ProgressBar progressBar = view.findViewById(R.id.pb_progress);
        final TextView tvProgress = view.findViewById(R.id.tv_progress);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("正在更新...")
                .setView(view)
                .setCancelable(false)
                .show();

        downloadDialog = new WeakReference<>(dialog);

        final Handler handler = new Handler(Looper.getMainLooper());

        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, final boolean done) {
//                Logger.d("bytesRead: " + bytesRead + "contentLength: " + contentLength + "done: " + done);
                final float progress = bytesRead * 100.0f / contentLength;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress((int) progress);
                        tvProgress.setText(String.format(Locale.getDefault(), "%.2f%%", progress));
                        if (done) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        };

        updateApp(context, url, isForce, progressListener);
    }

    public static void updateApp(final Context context, String url, final boolean isForce, ProgressListener progressListener) {
        FileDownloader.download(url, progressListener)
                .map(new Func1<Response, File>() {
                    @Override
                    public File call(Response response) {
                        try {
                            return saveFile(response, context);
                        } catch (IOException e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        if (file != null) {
                            install(context, file, isForce);
                        } else {
                            downloadFail(context, isForce);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        downloadFail(context, isForce);
                    }
                });
    }

    private static void downloadFail(Context context, final boolean isForce) {
        AlertDialog dialog;
        if (downloadDialog != null && (dialog = downloadDialog.get()) != null) {
            dialog.dismiss();
        }
        Toast.makeText(context, isForce ? "下载文件出错，即将退出" : "下载文件出错", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isForce) {
                    System.exit(0);
                }
            }
        }, 2000);

    }

    private static File saveFile(Response response, Context context) throws IOException {
        ResponseBody responseBody = response.body();
        ensureExternalCacheDir(context);
        File appFile = new File(context.getExternalCacheDir(), context.getPackageName() + ".apk");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] cache = new byte[4096];
            inputStream = responseBody.byteStream();
            outputStream = new FileOutputStream(appFile);
            int read;
            while ((read = inputStream.read(cache)) > 0) {
                outputStream.write(cache, 0, read);
            }
            outputStream.flush();
            return appFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void install(Context context, File file, boolean force) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        } else {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".downloadedfileprovider", file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
//        if (force) {
//            System.exit(0);
//        }
    }

    private static void ensureExternalCacheDir(Context context) {
        File file = context.getExternalCacheDir();
        if (file == null) {
            file = new File(context.getExternalFilesDir("").getParentFile(), "cache");
        }
        if (file != null) {
            file.mkdirs();
        }
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER);
        return list.size() > 0;
    }

}
