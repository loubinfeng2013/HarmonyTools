package com.lbf.lib.imageloader;

import ohos.app.AbilityContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 图片请求管理类（单列）
 */
public class RequestManager {

    /**
     * 上下文对象
     */
    private AbilityContext mContext;
    /**
     * 缓存请求的队列，考虑到可能会有多个线程操作这个队列，所以这个需要用堵塞式的队列
     */
    private LinkedBlockingQueue<PixelMapRequest> mRequestQueue = new LinkedBlockingQueue<>();
    /**
     * 存放真正处理者的数组
     */
    private PixelMapDispatcher[] dispatchers;
    /**
     * 线程池
     */
    public ExecutorService executorService;

    private RequestManager() {
    }

    private static class Holder {
        private static RequestManager manager = new RequestManager();
    }

    public static RequestManager getInstance() {
        return Holder.manager;
    }

    /**
     * 初始化，建议放在MyApplication中执行
     *
     * @param abilityContext
     */
    public void init(AbilityContext abilityContext) {
        this.mContext = abilityContext;
        initThreadExecutor();
        start();
    }

    /**
     * 初始化线程池
     */
    public void initThreadExecutor() {
        int size = Runtime.getRuntime().availableProcessors();
        if (size <= 0) {
            size = 1;
        }
        size *= 2;
        executorService = Executors.newFixedThreadPool(size);
    }

    /**
     * 启动工作
     */
    public void start() {
        stop();
        startAllDispatcher();
    }

    /**
     * 启动所有处理线程
     */
    public void startAllDispatcher() {
        final int threadCount = Runtime.getRuntime().availableProcessors();//获取最大线程数
        dispatchers = new PixelMapDispatcher[threadCount];
        if (dispatchers.length > 0) {
            for (int i = 0; i < threadCount; i++) {
                PixelMapDispatcher pixelmapDispatcher = new PixelMapDispatcher(mRequestQueue);//创建处理者实例
                executorService.execute(pixelmapDispatcher);//加入线程池并启动
                dispatchers[i] = pixelmapDispatcher;
            }
        }
    }

    /**
     * 停止所有工作
     */
    public void stop() {
        if (dispatchers != null && dispatchers.length > 0) {
            for (PixelMapDispatcher pixelmapDispatcher : dispatchers) {
                if (!pixelmapDispatcher.isInterrupted()) {
                    pixelmapDispatcher.interrupt(); // 中断
                }
            }
        }
    }

    /**
     * 增加请求
     *
     * @param pixelMapRequest
     */
    public void addPixelMapRequest(PixelMapRequest pixelMapRequest) {
        if (pixelMapRequest == null) {
            return;
        }
        if (!mRequestQueue.contains(pixelMapRequest)) {
            mRequestQueue.add(pixelMapRequest); // 将请求加入队列
        }
    }
}
