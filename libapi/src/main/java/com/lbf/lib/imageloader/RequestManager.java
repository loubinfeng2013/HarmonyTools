package com.lbf.lib.imageloader;

import ohos.app.AbilityContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 请求管理类
 */
public class RequestManager {

    //上下文对象
    private AbilityContext mContext;
    //缓存请求的队列
    private LinkedBlockingQueue<PixelMapRequest> mRequestQueue = new LinkedBlockingQueue<>();
    //真正处理者的数组
    private PixelMapDispatcher[] dispatchers;
    //线程池管理线程
    public ExecutorService executorService;

    private RequestManager() {
    }

    private static class Holder {
        private static RequestManager manager = new RequestManager();
    }

    public static RequestManager getInstance() {
        return Holder.manager;
    }

    public void init(AbilityContext abilityContext) {
        this.mContext = abilityContext;
        // 初始化线程池
        initThreadExecutor();
        // 只有一个管理者，所有在这里启动最合适
        start();
    }

    public void initThreadExecutor() {
        int size = Runtime.getRuntime().availableProcessors();
        if (size <= 0) {
            size = 1;
        }
        size *= 2;
        executorService = Executors.newFixedThreadPool(size);
    }

    public void start() {
        stop();
        startAllDispatcher();
    }

    // 处理并开始所有的线程
    public void startAllDispatcher() {
        // 获取线程最大数量
        final int threadCount = Runtime.getRuntime().availableProcessors();
        dispatchers = new PixelMapDispatcher[threadCount];
        if (dispatchers.length > 0) {
            for (int i = 0; i < threadCount; i++) {
                // 线程数量开辟的请求分发去抢请求资源对象，谁抢到了，就由谁去处理
                PixelMapDispatcher pixelmapDispatcher = new PixelMapDispatcher(mRequestQueue);
                executorService.execute(pixelmapDispatcher);
                // 将每个dispatcher放到数组中，方便统一处理
                dispatchers[i] = pixelmapDispatcher;
            }
        }
    }

    // 停止所有的线程
    public void stop() {
        if (dispatchers != null && dispatchers.length > 0) {
            for (PixelMapDispatcher pixelmapDispatcher : dispatchers) {
                if (!pixelmapDispatcher.isInterrupted()) {
                    pixelmapDispatcher.interrupt(); // 中断
                }
            }
        }
    }

    // 这里收集所有请求
    public void addBitmapRequest(PixelMapRequest pixelMapRequest) {
        if (pixelMapRequest == null) {
            return;
        }
        if (!mRequestQueue.contains(pixelMapRequest)) {
            mRequestQueue.add(pixelMapRequest); // 将请求加入队列
        }
    }
}
