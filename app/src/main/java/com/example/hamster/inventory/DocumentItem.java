package com.example.hamster.inventory;

import android.content.Context;
import android.net.Uri;
import com.example.hamster.utils.FileUtils;

public class DocumentItem {

    public Uri localUri;
    public String remoteUrl;
    public String existingId;

    public String name;
    public String type;


    public DocumentItem(Uri localUri, String name, String type) {
        this.localUri = localUri;
        this.name = name;
        this.type = type;
    }

    public DocumentItem(String remoteUrl, String existingId, String name, String type) {
        this.remoteUrl = remoteUrl;
        this.existingId = existingId;
        this.name = name;
        this.type = type;
    }

    public boolean isExisting() {
        return remoteUrl != null && !remoteUrl.isEmpty();
    }

    public String getFileName(Context context) {
        if (name != null && !name.isEmpty()) {
            return name;
        }

        if (localUri != null) {
            return FileUtils.getFileName(context, localUri);
        }
        return "Unnamed Document";
    }
}