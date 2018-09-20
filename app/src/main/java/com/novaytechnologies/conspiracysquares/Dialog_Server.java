//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/** Dialogs for creating and joining servers.
 * @author Jesse Primiani
 */
public class Dialog_Server {

    /**
        DESCRIPTION:
            Shows and sets up a dialog to allow a user to start a public or private cloud server.
        POST-CONDITION:
            If back was pressed, the dialog simply closes.
            If host was pressed, and all fields were empty, nothing happens.
            If host was pressed, and the password field was empty, a public server is started and joined.
            If host was pressed, and the password field was not empty, a private server is started and joined.
     @author Jesse Primiani
     @param ctx The application's context handler
    */
    static void Show_Dialog_Host(final Activity_Servers ctx) {
        final Dialog dialog_host;
        dialog_host = new Dialog(ctx);
        dialog_host.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_host.setContentView(R.layout.dialog_host);

        Button dialog_btn_back = dialog_host.findViewById(R.id.ID_dialog_button_back);
        dialog_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_host.dismiss();
            }
        });

        final EditText input_name = dialog_host.findViewById(R.id.server_name_input);
        final EditText input_password = dialog_host.findViewById(R.id.password_input);

        Button dialog_btn_host = dialog_host.findViewById(R.id.ID_dialog_button_host);
        dialog_btn_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strSVRname = input_name.getText().toString();
                final String strSVRpassword = input_password.getText().toString();

                Server_ServerList.get().CreateAndJoinServer(strSVRname, strSVRpassword, ctx);
                dialog_host.dismiss();
            }
        });

        dialog_host.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface v) { }
        });
        dialog_host.show();
        if (dialog_host.getWindow() != null)
            dialog_host.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
        DESCRIPTION:
            Shows and sets up a dialog to allow a user to join a private cloud server.
        POST-CONDITION:
            If back was pressed, the dialog simply closes.
            If join was pressed, and the password field was empty, nothing happens.
            If join was pressed, and the password field did not match, the user is notified.
            If join was pressed, and the password field did match, the server is joined.
     @author Jesse Primiani
     @param ctx The application's context handler
     @param strSVRname The name of the server to attempt to join
    */
    static void Show_Dialog_Join(final Activity_Servers ctx, final String strSVRname) {
        final Dialog dialog_join;
        dialog_join = new Dialog(ctx);
        dialog_join.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_join.setContentView(R.layout.dialog_join_pw);

        Button dialog_btn_back = dialog_join.findViewById(R.id.ID_dialog_button_back);
        dialog_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_join.dismiss();
            }
        });

        final EditText input_password = dialog_join.findViewById(R.id.password_input);

        Button dialog_btn_join = dialog_join.findViewById(R.id.ID_dialog_button_join);
        dialog_btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strSVRpassword = input_password.getText().toString();

                Server_ServerList.get().JoinPrivate(strSVRname, strSVRpassword, ctx);
                dialog_join.dismiss();
            }
        });

        dialog_join.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface v) { }
        });
        dialog_join.show();
        if (dialog_join.getWindow() != null)
            dialog_join.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
