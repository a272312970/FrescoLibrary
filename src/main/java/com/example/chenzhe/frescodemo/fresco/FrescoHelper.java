package com.example.chenzhe.frescodemo.fresco;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.chenzhe.frescodemo.fresco.listener.LoadFrescoListener;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ByteConstants;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;


import java.io.File;
import java.util.HashSet;
import java.util.Set;


/**
 * 加载工具
 * User:chenzhe
 * Date: 2018/3/29
 * Time:11:13
 */
public class FrescoHelper {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    public static final int MAX_DISK_CACHE_SIZE = 300 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    private static final String IMAGE_PIPELINE_CACHE_DIR = "wkzf_fresco_cache";
    static final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
            MAX_MEMORY_CACHE_SIZE, //  内存缓存中总图片的最大大小,以字节为
            Integer.MAX_VALUE,                    // 内存缓存中图片的最大数量
            MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
            Integer.MAX_VALUE,                 // 内存缓存中准备清除的总图片的最大数量。
            Integer.MAX_VALUE);
    private static final String TAG = FrescoHelper.class.getSimpleName();

    public static void initDefaultConfig(Context context) {
        final DiskCacheConfig sDiskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                .setMaxCacheSizeOnLowDiskSpace(30 * ByteConstants.MB)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(10 * ByteConstants.MB)//缓存的最大大小,当设备极低磁盘空间
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                .build();
        ImagePipelineConfig.Builder imageConfigBuilder = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(sDiskCacheConfig)
                .setProgressiveJpegConfig(mProgressiveJpegConfig);
        configureCaches(imageConfigBuilder, context);
        configureLoggingListeners(imageConfigBuilder);
        configureOptions(imageConfigBuilder);
        Fresco.initialize(context, imageConfigBuilder.build());
    }

    private static void configureLoggingListeners(ImagePipelineConfig.Builder imageConfigBuilder) {
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        imageConfigBuilder.setRequestListeners(requestListeners);
    }

    private static void configureCaches(ImagePipelineConfig.Builder imageConfigBuilder, Context context) {
        imageConfigBuilder
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                    @Override
                    public MemoryCacheParams get() {
                        return bitmapCacheParams;
                    }
                });
    }

    private static void configureOptions(ImagePipelineConfig.Builder imageConfigBuilder) {
        imageConfigBuilder.setDownsampleEnabled(true);
    }

    //渐进式图片
    private static ProgressiveJpegConfig mProgressiveJpegConfig = new ProgressiveJpegConfig() {
        @Override
        public int getNextScanNumberToDecode(int scanNumber) {
            return scanNumber + 2;
        }

        public QualityInfo getQualityInfo(int scanNumber) {
            boolean isGoodEnough = (scanNumber >= 5);
            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
        }
    };


    /**
     * @param imageView  图片加载控件
     * @param loadOption 加载配置构造器
     */
    public static void loadFrescoImage(FrescoImageView imageView, LoadOption loadOption) {

        loadFrescoImage(imageView, loadOption.uri, loadOption.defaultImg, loadOption.failImg, loadOption.emptyImg,
                loadOption.cornerRadius, loadOption.isCircle, loadOption.loadLocalPath, loadOption.isAnima,
                loadOption.size, loadOption.postprocessor, loadOption.controllerListener);
    }

    /**
     * fresco暂停加载
     */
    public static void pause() {
        if (Fresco.getImagePipeline().isPaused()) {
            Fresco.getImagePipeline().resume();
        }
    }

    /**
     * fresco继续加载
     */
    public static void resume() {
        if (!Fresco.getImagePipeline().isPaused()) {
            Fresco.getImagePipeline().pause();
        }

    }


    /**
     * @param imageView     图片加载控件
     * @param uri           路径或者URL
     * @param defaultImg    默认图片
     * @param cornerRadius  弧形角度
     * @param isCircle      是否为圆
     * @param loadLocalPath 是否本地资源,如果显示R.drawable.xxx,Path可以为null,前提isCircle为true
     * @param isAnima       是否显示GIF动画
     * @param size          是否再编码
     * @param postprocessor 图像显示处理
     */
    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg,
                                       int cornerRadius, boolean isCircle, boolean loadLocalPath, boolean isAnima,
                                       Point size, Postprocessor postprocessor, ControllerListener controllerListener) {
        init(imageView, cornerRadius, isCircle, isAnima, size, postprocessor, controllerListener);
        if (loadLocalPath) {
            imageView.loadLocalImage(uri, defaultImg, failImg, emptyImg);
        } else {
            imageView.loadView(uri, defaultImg, failImg, emptyImg);
        }
    }


    /**
     * 超大图片的就接口
     *
     * @param context   上下玩
     * @param imageView 图片加载控件
     * @param imageUri  图片地址
     * @param defaultId 默认失败图片
     */
    public static void loadBigImage(final Context context, final SubsamplingScaleImageView imageView, String imageUri, final int defaultId) {
        final Uri uri = Uri.parse((imageUri.startsWith("http")) ? imageUri : (imageUri.startsWith("file://")) ? imageUri : "file://" + imageUri);
        final Handler handler = new Handler();
        if (imageUri.startsWith("http")) {
            File file = FrescoHelper.getCache(context, uri);
            if (file != null && file.exists()) {
                imageView.setImage(ImageSource.uri(file.getAbsolutePath()));
            } else {
                FrescoHelper.getFrescoImg(context, imageUri, 0, 0, new LoadFrescoListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                File file = FrescoHelper.getCache(context, uri);
                                if (file != null && file.exists()) {
                                    imageView.setImage(ImageSource.uri(file.getAbsolutePath()));
                                }
                            }
                        });
                    }

                    @Override
                    public void onFail() {
                        imageView.setImage(ImageSource.resource(defaultId));
                    }
                });
            }
        } else {
            imageView.setImage(ImageSource.uri(imageUri.replace("file://", "")));
        }
    }

    private static void init(FrescoImageView imageView, int cornerRadius, boolean isCircle, boolean isAnima,
                             Point size, Postprocessor postprocessor, ControllerListener controllerListener) {
        imageView.setAnim(isAnima);
        imageView.setCornerRadius(cornerRadius);
        imageView.setFadeTime(500);
        imageView.setControllerListener(controllerListener);
        if (isCircle)
            imageView.asCircle();
        if (postprocessor != null)
            imageView.setPostProcessor(postprocessor);
        if (size != null) {
            imageView.setResize(size);
        }
    }


    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, int cornerRadius) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, cornerRadius, false, false, true, null, null, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, false, true, null, null, null);
    }

    public static void loadFrescoImageWithListener(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, ControllerListener controllerListener) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, false, true, null, null, controllerListener);
    }

    public static void loadFrescoImageWithListener(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, Point point, ControllerListener controllerListener) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, false, true, point, null, controllerListener);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, boolean loadLocalPath) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, loadLocalPath, true, null, null, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, boolean loadLocalPath, Point size) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, loadLocalPath, true, size, null, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, int cornerRadius, boolean loadLocalPath, Point size) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, cornerRadius, false, loadLocalPath, true, size, null, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, boolean loadLocalPath, Postprocessor postprocessor) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, loadLocalPath, true, null, postprocessor, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, boolean loadLocalPath, Point point, Postprocessor postprocessor) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, false, loadLocalPath, true, point, postprocessor, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, int radius, boolean loadLocalPath, Point point, Postprocessor postprocessor) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, radius, false, loadLocalPath, true, point, postprocessor, null);
    }

    public static void loadFrescoImage(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, int cornerRadius, boolean loadLocalPath) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, cornerRadius, false, loadLocalPath, true, null, null, null);
    }

    public static void loadFrescoImageCircle(FrescoImageView imageView, String uri, int defaultImg, int failImg, int emptyImg, boolean loadLocalPath) {
        loadFrescoImage(imageView, uri, defaultImg, failImg, emptyImg, 0, true, loadLocalPath, true, null, null, null);
    }

    /**
     * 清理某一条缓存
     *
     * @param url 缓存的url地址
     *            如果自定义CacheKeyFactory，使用evictFromDiskCache(ImageRequest)。
     */
    public static void clearCache(String url) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(Uri.parse(url));
        imagePipeline.evictFromDiskCache(Uri.parse(url));
    }

    /**
     * 清理所有缓存
     */
    public static void clearAllCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

    /**
     * 得到fresco所有缓存大小
     */
    public static long getAllCacheSize() {
        Fresco.getImagePipelineFactory().getMainFileCache().trimToMinimum();
        return Fresco.getImagePipelineFactory().getMainFileCache().getSize();
    }


    /**
     * 图片是否已经存在了
     */
    public static boolean isCached(Context context, Uri uri) {
        return Fresco.getImagePipelineFactory().getMainFileCache().hasKey(new SimpleCacheKey(uri.toString()));
    }

    /**
     * 本地缓存文件
     */
    public static File getCache(Context context, Uri uri) {
        /*if (!isCached(context, uri)){
            Logger.i(uri.toString() + "判断无缓存");
            return null;
        }*/
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context);
        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainFileCache().getResource(cacheKey);
        if (resource == null) {
            Log.i(TAG,uri.toString() + "判断无缓存");
            return null;
        }
        return ((FileBinaryResource) resource).getFile();
    }

    /**
     * 返回bitmap,也可以用来监听下载，bitmap会被fresco自动回收
     *
     * @param context  上下文
     * @param url      网络地址
     * @param width    宽度 可以为0
     * @param height   高度 可以为0
     * @param listener 回调
     */
    public static void getFrescoImg(Context context, String url, int width, int height, final LoadFrescoListener listener) {
        getFrescoImg(context, url, width, height, null, listener);
    }

    /**
     * 返回bitmap,也可以用来监听下载，bitmap会被fresco自动回收
     *
     * @param context   上下文
     * @param url       网络地址
     * @param width     宽度
     * @param height    高度
     * @param processor 处理图片
     * @param listener  回调
     */
    public static void getFrescoImg(Context context, final String url, final int width, final int height,
                                    BasePostprocessor processor, final LoadFrescoListener listener) {
        if(url == null){
            return;
        }
        ResizeOptions resizeOptions = null;
        if (width != 0 && height != 0) {
            resizeOptions = new ResizeOptions(width, height);
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(false)
                .setPostprocessor(processor)
                .setResizeOptions(resizeOptions)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                //图片不能是GIF
                if (null != listener) {
                    //此处是子线程，UI操作请在主线程中操作
                    Log.i(TAG,"当前线程2" + Thread.currentThread().getName());
                    listener.onSuccess(bitmap);
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (null != listener) {
                    listener.onFail();
                }
            }
        }, CallerThreadExecutor.getInstance());

    }

}