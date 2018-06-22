package com.novaytechnologies.conspiracysquares;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Server_Dialogs {

    /*
        DESCRIPTION:
            Shows and sets up a dialog to allow a user to start a public or private cloud server.
        POST-CONDITION:
            If back was pressed, the dialog simply closes.
            If host was pressed, and all fields were empty, nothing happens.
            If host was pressed, and the password field was empty, a public server is started and joined.
            If host was pressed, and the password field was not empty, a private server is started and joined.
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

                if (!strSVRname.isEmpty()) {
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add("X");
                    params.add("ServerName");
                    params.add(strSVRname);
                    params.add("ServerPassword");
                    params.add(strSVRpassword);
                    String ParemsString = Utility_Post.GetParemsString(params);

                    Utility_Post newPost = new Utility_Post();
                    newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            Server_Error.Connect_Error(ctx);
                        }
                    });
                    newPost.SetRunnable(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            Intent newIntent = new Intent(ctx, Activity_Game.class);
                            newIntent.putExtra(Activity_Game.FIND_SERVER, false);
                            newIntent.putExtra(Activity_Game.SERVER, strSVRname);
                            newIntent.putExtra(Activity_Game.SERVER_PASS, strSVRpassword);
                            ctx.startActivity(newIntent);
                        }
                    });
                    newPost.execute("https://conspiracy-squares.appspot.com/Servlet_CreateServer", ParemsString);

                    ctx.Refresh();
                    dialog_host.dismiss();
                }
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

    /*
        DESCRIPTION:
            Shows and sets up a dialog to allow a user to join a private cloud server.
        POST-CONDITION:
            If back was pressed, the dialog simply closes.
            If join was pressed, and the password field was empty, nothing happens.
            If join was pressed, and the password field did not match, the user is notified.
            If join was pressed, and the password field did match, the server is joined.
    */
    static void Show_Dialog_Join(final Activity_Servers ctx, final String strSVRname) {
        final Dialog dialog_join;
        dialog_join = new Dialog(ctx);
        dialog_join.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_join.setContentView(R.layout.dialog_host);

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

                if (strSVRname != null && !strSVRname.isEmpty() && !strSVRpassword.isEmpty()) {
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add("X");
                    params.add("ServerName");
                    params.add(strSVRname);
                    params.add("ServerPassword");
                    params.add(strSVRpassword);
                    String ParemsString = Utility_Post.GetParemsString(params);

                    Utility_Post newPost = new Utility_Post();
                    newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            Server_Error.Connect_Error(ctx);
                        }
                    });
                    newPost.SetRunnable(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            String LastResult = GetArgs()[0];
                            if (LastResult != null && !LastResult.isEmpty())
                            {
                                if (LastResult.contains("PASSWORD_CORRECT"))
                                {
                                    Intent newIntent = new Intent(ctx, Activity_Game.class);
                                    newIntent.putExtra(Activity_Game.FIND_SERVER, false);
                                    newIntent.putExtra(Activity_Game.SERVER, strSVRname);
                                    newIntent.putExtra(Activity_Game.SERVER_PASS, strSVRpassword);
                                    ctx.startActivity(newIntent);
                                }
                                else
                                {
                                    Toast WrongPasswordToast = new Toast(ctx);
                                    WrongPasswordToast.setGravity(Gravity.CENTER,0,0);
                                    WrongPasswordToast.setText(R.string.toast_wrong_password);
                                    WrongPasswordToast.show();
                                }
                            }
                        }
                    });
                    newPost.execute("https://conspiracy-squares.appspot.com/Servlet_JoinPrivateServer", ParemsString);

                    ctx.Refresh();
                    dialog_join.dismiss();
                }
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
