package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Server_Error
{
    // Shows a simple connection error message popup
    public static void Connect_Error(Context ctx)
    {
        Toast ConnectionErrorToast = new Toast(ctx);
        ConnectionErrorToast.setGravity(Gravity.CENTER,0,0);
        ConnectionErrorToast.setText(R.string.toast_connection_error);
        ConnectionErrorToast.show();
    }
}
