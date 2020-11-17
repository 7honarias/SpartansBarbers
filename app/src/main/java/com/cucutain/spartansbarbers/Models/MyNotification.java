package com.cucutain.spartansbarbers.Models;

import com.google.firebase.firestore.FieldValue;

public class MyNotification {
    private String uid, title, context;
    private boolean read;
    private FieldValue serverTimestap;

    public MyNotification() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public FieldValue getServerTimestap() {
        return serverTimestap;
    }

    public void setServerTimestap(FieldValue serverTimestap) {
        this.serverTimestap = serverTimestap;
    }
}
