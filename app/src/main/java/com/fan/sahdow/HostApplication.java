package com.fan.sahdow;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.StrictMode;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.load_parameters.LoadParameters;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.runtime.container.ContentProviderDelegateProviderHolder;
import com.tencent.shadow.core.runtime.container.DelegateProviderHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Description:
 * Data：2023/9/18-16:44
 * Author: ly
 */
public class HostApplication extends Application {


    private static Application sApp;

    public final static String PART_MAIN = "partMain";

    public final static String PART_Two = "partTwo";

    private static final PreparePluginApkBloc sPluginPrepareBloc
            = new PreparePluginApkBloc(
            "plugin.apk"
    );

    static {
        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(new SLoggerFactory());
    }

    private ShadowPluginLoader mPluginLoader;

    private final Map<String, InstalledApk> mPluginMap = new HashMap<>();

    public void loadPlugin(final String partKey, final Runnable completeRunnable) {
        InstalledApk installedApk = mPluginMap.get(partKey);
        if (installedApk == null) {
            throw new NullPointerException("partKey == " + partKey);
        }

        if (mPluginLoader.getPluginParts(partKey) == null) {
            // 插件访问宿主类的白名单
            String[] hostWhiteList = new String[]{
                    "androidx.test.espresso",//这个包添加是为了general-cases插件中可以访问测试框架的类
                    "com.tencent.shadow.test.lib.plugin_use_host_code_lib.interfaces"//测试插件访问宿主白名单类
            };
            LoadParameters loadParameters = new LoadParameters(null,
                    partKey,
                    null,
                    hostWhiteList);

            Parcel parcel = Parcel.obtain();
            loadParameters.writeToParcel(parcel, 0);
            final InstalledApk plugin = new InstalledApk(
                    installedApk.apkFilePath,
                    installedApk.oDexPath,
                    installedApk.libraryPath,
                    parcel.marshall()
            );
            parcel.recycle();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ShadowPluginLoader pluginLoader = mPluginLoader;
                    Future<?> future = null;
                    try {
                        future = pluginLoader.loadPlugin(plugin);
                        future.get(10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("加载失败", e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mPluginLoader.callApplicationOnCreate(partKey);
                    completeRunnable.run();
                }
            }.execute();
        } else {
            completeRunnable.run();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        ShadowPluginLoader loader = mPluginLoader = new SamplePluginLoader(getApplicationContext());
        loader.onCreate();
        DelegateProviderHolder.setDelegateProvider(loader.getDelegateProviderKey(), loader);
        ContentProviderDelegateProviderHolder.setContentProviderDelegateProvider(loader);

        InstalledApk installedApk = sPluginPrepareBloc.preparePlugin(this.getApplicationContext());
        mPluginMap.put(PART_MAIN, installedApk);
    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        boolean isRunningEspressoTest;
        try {
            Class.forName("android.support.test.espresso.Espresso");
            isRunningEspressoTest = true;
        } catch (Exception ignored) {
            isRunningEspressoTest = false;
        }
        if (isRunningEspressoTest) {
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    public static Application getApp() {
        return sApp;
    }

    public ShadowPluginLoader getPluginLoader() {
        return mPluginLoader;
    }
}
