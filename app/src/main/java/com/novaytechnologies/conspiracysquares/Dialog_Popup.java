package com.novaytechnologies.conspiracysquares;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Dialog_Popup
{
    // Shows a simple message popup
    private static void ShowPopupDialog(final Context ctx, final String strText)
    {
        final Dialog dialog_join;
        dialog_join = new Dialog(ctx);
        dialog_join.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_join.setContentView(R.layout.dialog_popup);

        Button dialog_btn_back = dialog_join.findViewById(R.id.ID_dialog_button_back);
        dialog_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_join.dismiss();
            }
        });

        final TextView WarningText = dialog_join.findViewById(R.id.ID_dialog_text);
        WarningText.setText(strText);

        dialog_join.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface v) { }
        });
        dialog_join.show();
        if (dialog_join.getWindow() != null)
            dialog_join.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // Shows a simple no server found message popup
    public static void Find_Error(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_server_noauto));
    }

    // Shows a simple connection error message popup
    public static void Connect_Error(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_connection_error));
    }

    // Shows a simple server full message popup
    public static void Server_Full(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_server_full));
    }

    // Shows a simple wrong password message popup
    public static void Wrong_Password(Context ctx)
    {
        ShowPopupDialog(ctx, ctx.getResources().getString(R.string.toast_wrong_password));
    }
}
