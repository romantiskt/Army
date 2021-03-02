package com.rolan.army;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Rolan.Wang on 1/28/21.7:00 PM
 * 描述：
 */
final class EmptyRequestManagerTreeNode implements RequestManagerTreeNode {
    @NonNull
    @Override
    public Set<RequestManager> getDescendants() {
        return Collections.emptySet();
    }
}