package com.houjie.design.skin.support.observe;

import java.util.ArrayList;

public class SkinObservable {
    private final ArrayList<SkinObserver> mObservers;

    public SkinObservable() {
        mObservers = new ArrayList<>();
    }

    public synchronized void addObserver(SkinObserver observer) {
        if (null == observer) {
            throw new NullPointerException();
        } else if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    public synchronized void removeObserver(SkinObserver observer) {
        mObservers.remove(observer);
    }

    public void notifyUpdateSkin() {
        notifyUpdateSkin(null);
    }

    public void notifyUpdateSkin(Object arg) {
        SkinObserver[] arrLocal;
        synchronized (this) {
            arrLocal = mObservers.toArray(new SkinObserver[mObservers.size()]);
        }
        for (int i = arrLocal.length - 1; i >= 0; --i) {
            arrLocal[i].updateSkin(this, arg);
        }
    }

    public synchronized void clearObservers() {
        mObservers.clear();
    }

    public synchronized int countObservers() {
        return mObservers.size();
    }
}
