package com.ointerface.oconnect.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ointerface.oconnect.R;

/**
 * Created by AnthonyDoan on 5/6/17.
 */

public class ActivityConnectionsCarouselPreviewBinding extends android.databinding.ViewDataBinding  {

    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.carousel_horizontal, 1);
    }
    // views
    public android.support.v7.widget.RecyclerView carousel_horizontal;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityConnectionsCarouselPreviewBinding(android.databinding.DataBindingComponent bindingComponent, View root) {
        super(bindingComponent, root, 0);
        final Object[] bindings = mapBindings(bindingComponent, root, 6, sIncludes, sViewsWithIds);
        this.carousel_horizontal = (RecyclerView) bindings[1];
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
            mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean setVariable(int variableId, Object variable) {
        /*
        switch(variableId) {
            case BR.test :
                return true;
        }
        */

        return false;
    }

    public void setTest(java.lang.Byte test) {
        // not used, ignore
    }
    public java.lang.Byte getTest() {
        return null;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;

    public static ActivityConnectionsCarouselPreviewBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityConnectionsCarouselPreviewBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot, android.databinding.DataBindingComponent bindingComponent) {
        return android.databinding.DataBindingUtil.<ActivityConnectionsCarouselPreviewBinding>inflate(inflater, R.layout.activity_connections, root, attachToRoot, bindingComponent);
    }
    public static ActivityConnectionsCarouselPreviewBinding inflate(android.view.LayoutInflater inflater) {
        return inflate(inflater, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityConnectionsCarouselPreviewBinding inflate(android.view.LayoutInflater inflater, android.databinding.DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(R.layout.activity_connections, null, false), bindingComponent);
    }
    public static ActivityConnectionsCarouselPreviewBinding bind(android.view.View view) {
        return bind(view, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static ActivityConnectionsCarouselPreviewBinding bind(android.view.View view, android.databinding.DataBindingComponent bindingComponent) {
        if (!"layout/activity_carousel_preview_0".equals(view.getTag())) {
            throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
        }
        return new ActivityConnectionsCarouselPreviewBinding(bindingComponent, view);
    }
    /* flag mapping
        flag 0 (0x1L): test
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}