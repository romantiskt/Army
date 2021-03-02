package com.rolan.army;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rolan.Wang on 1/28/21.7:04 PM
 * 描述：
 */
public class SupportRequestManagerFragment extends Fragment {
    private static final String TAG = "SupportRMFragment";
    @Nullable private RequestManager requestManager;
    private final ActivityFragmentLifecycle lifecycle;
    private final RequestManagerTreeNode requestManagerTreeNode =
            new SupportFragmentRequestManagerTreeNode();
    private Fragment parentFragmentHint;
    private SupportRequestManagerFragment rootRequestManagerFragment;
    private final Set<SupportRequestManagerFragment> childRequestManagerFragments = new HashSet<>();

    public SupportRequestManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }

    @VisibleForTesting
    @SuppressLint("ValidFragment")
    public SupportRequestManagerFragment(@NonNull ActivityFragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }
    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void setRequestManager(@Nullable RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    ActivityFragmentLifecycle getArmyLifecycle() {
        return lifecycle;
    }

    public RequestManagerTreeNode getRequestManagerTreeNode() {
        return requestManagerTreeNode;
    }

    Set<SupportRequestManagerFragment> getDescendantRequestManagerFragments() {
        if (rootRequestManagerFragment == null) {
            return Collections.emptySet();
        } else if (equals(rootRequestManagerFragment)) {
            return Collections.unmodifiableSet(childRequestManagerFragments);
        } else {
            Set<SupportRequestManagerFragment> descendants = new HashSet<>();
            for (SupportRequestManagerFragment fragment : rootRequestManagerFragment
                    .getDescendantRequestManagerFragments()) {
                if (isDescendant(fragment.getParentFragmentUsingHint())) {
                    descendants.add(fragment);
                }
            }
            return Collections.unmodifiableSet(descendants);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
        unregisterFragmentWithRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            registerFragmentWithRoot(getActivity());
        } catch (IllegalStateException e) {
            // OnAttach can be called after the activity is destroyed, see #497.
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to register fragment with root", e);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentFragmentHint = null;
        unregisterFragmentWithRoot();
    }

    private Fragment getParentFragmentUsingHint() {
        Fragment fragment = getParentFragment();
        return fragment != null ? fragment : parentFragmentHint;
    }

    void setParentFragmentHint(@Nullable Fragment parentFragmentHint) {
        this.parentFragmentHint = parentFragmentHint;
        if (parentFragmentHint != null && parentFragmentHint.getActivity() != null) {
            registerFragmentWithRoot(parentFragmentHint.getActivity());
        }
    }

    private void registerFragmentWithRoot(@NonNull FragmentActivity activity) {
        unregisterFragmentWithRoot();
        rootRequestManagerFragment =
                Army.get(activity).getRequestManagerRetriever().getSupportRequestManagerFragment(activity);
        if (!equals(rootRequestManagerFragment)) {
            rootRequestManagerFragment.addChildRequestManagerFragment(this);
        }
    }

    private void unregisterFragmentWithRoot() {
        if (rootRequestManagerFragment != null) {
            rootRequestManagerFragment.removeChildRequestManagerFragment(this);
            rootRequestManagerFragment = null;
        }
    }

    private void addChildRequestManagerFragment(SupportRequestManagerFragment child) {
        childRequestManagerFragments.add(child);
    }

    private void removeChildRequestManagerFragment(SupportRequestManagerFragment child) {
        childRequestManagerFragments.remove(child);
    }

    private boolean isDescendant(@NonNull Fragment fragment) {
        Fragment root = getParentFragmentUsingHint();
        Fragment parentFragment;
        while ((parentFragment = fragment.getParentFragment()) != null) {
            if (parentFragment.equals(root)) {
                return true;
            }
            fragment = fragment.getParentFragment();
        }
        return false;
    }

    private class SupportFragmentRequestManagerTreeNode implements RequestManagerTreeNode {

        SupportFragmentRequestManagerTreeNode() { }

        @NonNull
        @Override
        public Set<RequestManager> getDescendants() {
            Set<SupportRequestManagerFragment> descendantFragments =
                    getDescendantRequestManagerFragments();
            Set<RequestManager> descendants = new HashSet<>(descendantFragments.size());
            for (SupportRequestManagerFragment fragment : descendantFragments) {
                if (fragment.getRequestManager() != null) {
                    descendants.add(fragment.getRequestManager());
                }
            }
            return descendants;
        }

        @Override
        public String toString() {
            return super.toString() + "{fragment=" + SupportRequestManagerFragment.this + "}";
        }
    }
}
