package com.app.githubexplorer.util;

import java.util.concurrent.*;
public class ExecutorsProvider {
    public final Executor io = Executors.newSingleThreadExecutor();

}