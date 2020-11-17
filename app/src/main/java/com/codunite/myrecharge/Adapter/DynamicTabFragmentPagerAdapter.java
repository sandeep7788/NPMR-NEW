package com.codunite.myrecharge.Adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class DynamicTabFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final List<PageInfo> pages;
    private final ViewPager pager;
    private final ActionBar actionBar;
    private final ActionBar.TabListener tabListener;
    private final FragmentManager fragmentManager;

    public DynamicTabFragmentPagerAdapter( final Context context, final FragmentManager fm, final ViewPager pager, final ActionBar actionBar ) {
        super( fm );
        this.context = context;
        this.fragmentManager = fm;
        this.pages = new LinkedList<PageInfo>();
        this.pager = pager;
        this.actionBar = actionBar;
        this.tabListener = createTabListener();
        pager.setOnPageChangeListener( createPageChangeListener() );
        pager.setAdapter( this );

        actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
    }
    private final ActionBar.TabListener createTabListener() {
        return new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                if( pager.getCurrentItem() != tab.getPosition() ) {
                    pager.setCurrentItem( tab.getPosition() );
                }
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
        };
    }

    /**
     * @return OnPageChangeListener which update ActionBar tab position to selected pager position.
     */
    private final ViewPager.OnPageChangeListener createPageChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if( actionBar.getSelectedNavigationIndex() != position ) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        };
    }

    public void addPage( String title, Class<? extends Fragment> fragment ) {
        addPage( title, fragment, null );
    }

    public void addPage( String title, Class<? extends Fragment> fragment, Bundle arguments ) {
        if( fragment != null ) {
            pages.add( new PageInfo( fragment, title, arguments ) );

            actionBar.addTab( actionBar.newTab().setText( title ).setTabListener( tabListener ) );

            notifyDataSetChanged();
        }
    }

    public void removePage( int position ) {
        if( position >= 0 && position < pages.size() && pages.size() > 1 ) {

            pages.remove( position );

            // i don't know why but reset adapter to have no pager exception
            pager.setAdapter( this );
            notifyDataSetChanged();

            // remove tab after page was removed
            actionBar.removeTab(actionBar.getTabAt(position));
        }
    }

    public void replacePage( int position, String title, Class<? extends Fragment> fragment ) {
        replacePage( position, title, fragment );
    }

    public void replacePage( int position, String title, Class<? extends Fragment> fragment, Bundle arguments ) {
        if( position >= 0 && position < pages.size() ) {
            pages.remove( position );
            pages.add( position, new PageInfo( fragment, title, arguments ) );

            actionBar.getTabAt( position ).setText( title );

            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if( !pages.contains( (Fragment) object) ) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public long getItemId(int position) {
        return pages.get( position ).id;
    }

    @Override
    public Fragment getItem(int position) {
        if( position >= 0 && position < pages.size() ) {
            PageInfo page = pages.get( position );
            Fragment item = Fragment.instantiate(context, page.pageClass, page.arguments);
            return item;
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        // remove destroyed Fragment from FragmentManager
        if( !pages.contains( (Fragment) object ) ) {
            fragmentManager.beginTransaction().remove( (Fragment) object ).commit();
        }
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    /**
     * Load the Fragment instance from FragmentManager of given position.
     *
     * @param position
     * @return  instance of Fragment at given position
     */
    public Fragment getFragmentInstance( int position ) {
        return fragmentManager.findFragmentByTag( buildTag( getItemId( position ) ) );
    }

    /**
     * @param itemId
     * @return build a unique tag of pager id, fragment id and a prefix.
     */
    private final String buildTag( long itemId ) {
        return "android:switcher:" + pager.getId() + ":" + itemId;
    }

    public class PageInfo
    {
        public final String pageClass;
        public final String title;
        public final Bundle arguments;
        public final long id;

        public PageInfo( final Class<? extends Fragment> clss, final String title, final Bundle arguments )
        {
            this.pageClass = clss.getName();
            this.title = title;
            this.arguments = arguments;

            // create a unique id for FragmentManager
            this.id = pageClass.hashCode() ^ title.hashCode() ^ arguments.hashCode();
        }
    }
}