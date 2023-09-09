package com.aatorque.stats;


import android.util.SparseArray;

import com.google.android.apps.auto.sdk.MenuAdapter;
import com.google.android.apps.auto.sdk.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMenuAdapter extends MenuAdapter {
    private final List<MenuItem> mMenuItems = new ArrayList<>();
    private final Map<String, Integer> mMenuItemsByNames = new HashMap<>();
    private final SparseArray<String> mMenuItemNames = new SparseArray<>();
    private final SparseArray<MenuAdapter> mSubmenus = new SparseArray<>();
    private MenuCallbacks mCallbacks;

    public interface MenuCallbacks {
        void onMenuItemClicked(String name);
        void onEnter();
        void onExit();
    }

    @Override
    public MenuItem getMenuItem(int i) {
        return mMenuItems.get(i);
    }

    public MenuItem getMenuItem(String name) {
        return mMenuItems.get(mMenuItemsByNames.get(name));
    }

    @Override
    public int getMenuItemCount() {
        return mMenuItems.size();
    }

    @Override
    public MenuAdapter onLoadSubmenu(int i) {
        return mSubmenus.get(i);
    }

    public void addMenuItem(String name, MenuItem menuItem) {
        mMenuItemsByNames.put(name, mMenuItems.size());
        mMenuItemNames.put(mMenuItems.size(), name);
        mMenuItems.add(menuItem);
    }

    public void addSubmenu(String name, MenuAdapter submenu) {
        if (!mMenuItemsByNames.containsKey(name)) {
            throw new IllegalArgumentException("Unknown menu to add submenu");
        }
        int index = mMenuItemsByNames.get(name);
        if (mSubmenus.get(index) != null) {
            throw new IllegalArgumentException("Submenu already present");
        }
        if (mMenuItems.get(index).getType() != MenuItem.Type.SUBMENU) {
            throw new IllegalArgumentException("Submenu can be attached only to SUBMENU item type");
        }
        mSubmenus.put(index, submenu);
    }

    public void setCallbacks(MenuCallbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    @Override
    public void onMenuItemClicked(int i) {
        if (mCallbacks != null) {
            mCallbacks.onMenuItemClicked(mMenuItemNames.get(i));
        }
    }

    @Override
    public void onEnter() {
        if (mCallbacks != null) {
            mCallbacks.onEnter();
        }
    }

    @Override
    public void onExit() {
        if (mCallbacks != null) {
            mCallbacks.onExit();
        }
    }
}