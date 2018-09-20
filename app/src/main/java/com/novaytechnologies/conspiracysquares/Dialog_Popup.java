//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Warning popup dialogs.
 * @author Jesse Primiani
 */
public class Dialog_Popup
{
    /**
     * Shows a simple popup message.
     * @author Jesse Primiani
     * @param ctx The application's context handler
     * @param strText The popup box's text
     */
    private static void ShowPopupDialog(final Context ctx, final String strText)
    {
        final Dialog dialog_popup;
        dialog_popup = new Dialog(ctx);
        dialog_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_popup.setContentView(R.layout.dialog_popup);

        Button dialog_btn_back = dialog_popup.findViewById(R.id.ID_dialog_button_back);
        dialog_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_popup.dismiss();
            }
        });

        final TextView WarningText = dialog_popup.findViewById(R.id.ID_dialog_text);
        WarningText.setText(strText);

        dialog_popup.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface v) { }
        });
        dialog_popup.show();
        if (dialog_popup.getWindow() != null)
            dialog_popup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * Shows a simple connection error message popup.
     * @author Jesse Primiani
     * @param ctx The application's context handler
     */
    public static void Connect_Error(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_connection_error));
    }

    /**
     * Shows a simple server full message popup
     * @author Jesse Primiani
     * @param ctx The application's context handler
     */
    public static void Server_Full(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_server_full));
    }

    /**
     * Shows a simple wrong password message popup.
     * @author Jesse Primiani
     * @param ctx The application's context handler
     */
    public static void Wrong_Password(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_wrong_password));
    }
}
