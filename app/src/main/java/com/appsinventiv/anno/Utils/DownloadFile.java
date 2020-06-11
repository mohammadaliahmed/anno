package com.appsinventiv.anno.Utils;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * Created by AliAh on 03/05/2018.
 */

public class DownloadFile {

    private static long downloadID;

    private DownloadFile() {
    }

    public static void fromUrl(String Url, String filename) {
//        String string = Long.toHexString(Double.doubleToLongBits(Math.random()));

        DownloadManager downloadManager = (DownloadManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        Long referene = downloadManager.enqueue(request);
    }

    public static void fromUrll(String Url, String filename) {
//        String string = Long.toHexString(Double.doubleToLongBits(Math.random()));

        DownloadManager downloadManager = (DownloadManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
//        Long referene = downloadManager.enqueue(request);
//        DownloadManager downloadManagerr = (DownloadManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);

    }



}
