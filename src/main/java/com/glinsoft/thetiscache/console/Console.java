package com.glinsoft.thetiscache.console;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Console {

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void println(String str){
        executorService.execute(()-> System.out.println(str));
    }
}
