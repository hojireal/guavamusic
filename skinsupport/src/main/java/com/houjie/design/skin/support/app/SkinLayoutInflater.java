package com.houjie.design.skin.support.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.jar.Attributes;

/**
 * @date on 2018/12/18
 * @author Jeffrey Hou(Hou jie)
 * @email p_jiehhou@tencent.com
 * @describe TODO
 **/
public interface SkinLayoutInflater {
    View createView(@NonNull Context context, final String name, @NonNull Attributes attrs);
}
