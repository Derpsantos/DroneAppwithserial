package com.drone.app.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.drone.app.R;

public class LoadingHelper {
    Context context;

    public LoadingHelper(Context context) {
        this.context = context;
    }
    public Dialog openNetLoaderDialog() {
       Dialog dialogP=new Dialog(context);
        dialogP.setContentView(R.layout.dialog_loading);
        dialogP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogP.setCancelable(false);
        dialogP.show();
        return dialogP;
    }
}
