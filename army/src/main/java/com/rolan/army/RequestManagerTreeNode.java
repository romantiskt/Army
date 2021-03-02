package com.rolan.army;

import androidx.annotation.NonNull;

import java.util.Set;

/**
 * Created by Rolan.Wang on 1/28/21.6:18 PM
 * 描述：
 */
public interface RequestManagerTreeNode {
    @NonNull
    Set<RequestManager> getDescendants();
}