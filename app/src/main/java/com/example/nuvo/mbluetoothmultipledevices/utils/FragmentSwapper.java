package com.example.nuvo.mbluetoothmultipledevices.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class FragmentSwapper {

    private static FragmentManager mFragmentManager;
    private static FragmentSwapper fragmentSwapper;

    private FragmentSwapper(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }


    public static FragmentSwapper getInstance(FragmentManager fragmentManager) {
        fragmentSwapper = new FragmentSwapper(fragmentManager);
        return fragmentSwapper;
    }

    public Fragment swapToFragment(Class<? extends Fragment> fragmentClass, Bundle arguments, int containerResourceId, boolean isShouldAddToBackStack, boolean add) {

        String fragmentTag = fragmentClass.getSimpleName();
        Fragment newFragment = null;
        FragmentTransaction mFt = null;

        newFragment = mFragmentManager.findFragmentByTag(fragmentTag);
        mFt = mFragmentManager.beginTransaction();

//        if(newFragment == null){
        try {
            newFragment = fragmentClass.newInstance();
            newFragment.setArguments(arguments);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        }


        if (add) {
            mFt.add(containerResourceId, newFragment, fragmentTag);
            if (isShouldAddToBackStack) {
                mFt.addToBackStack(fragmentTag);
            }
        } else {
            mFt.replace(containerResourceId, newFragment, fragmentTag);
        }
        try {
            mFt.commit();
        } catch (Exception e) {

        }

        return newFragment;
    }

    // fragment still dont exist in FragmentManager
/*    public Fragment addInitialFragment(Fragment newFragment, Bundle arguments, int containerResourceId, boolean isShouldAddToBackStack, String fragmentTag) {

        FragmentTransaction mFt = null;
        newFragment.setArguments(arguments);
        mFt = mFragmentManager.beginTransaction();

        if (isShouldAddToBackStack) {
            mFt.add(containerResourceId, newFragment, fragmentTag);
            mFt.addToBackStack(fragmentTag);
        } else {
            mFt.replace(containerResourceId, newFragment, fragmentTag);
        }
        mFt.commit();

        return newFragment;
    }*/

    public void clearFragments() {

        if (mFragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = mFragmentManager.getBackStackEntryAt(0);
            mFragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void clearFragmentsToPosition(int i_position) {
        if (mFragmentManager != null) {
            int stackCount = mFragmentManager.getBackStackEntryCount();
            if (stackCount > 0 && stackCount > i_position) {
                FragmentManager.BackStackEntry first = mFragmentManager.getBackStackEntryAt(i_position);
                mFragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    public void popFragment() {
        mFragmentManager.popBackStack();
    }

    public int getFragmentsCount() {
        return mFragmentManager.getBackStackEntryCount();
    }
}
