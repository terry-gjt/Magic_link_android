package com.magic.terry.server_magic.Communication;

import android.os.Handler;
import java.net.ServerSocket;

/**
 * Created by terry on 2019-01-13.
 */

public interface ConnectAdd {
    void add(ConnectThread connectclient);
    boolean serverrunning();
    Handler gerhandler();
    ServerSocket getServerSocket();
    MyMessage getMyMessage();
}
