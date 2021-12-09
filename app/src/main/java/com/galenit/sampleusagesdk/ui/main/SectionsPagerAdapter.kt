package com.galenit.sampleusagesdk.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.galenit.sampleusagesdk.R

private val TAB_TITLES = arrayOf(
    R.string.tab_face_measurement,
    R.string.tab_devices_measurement,
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a Fragment (defined as a static inner class below).
        val fragment = when(TAB_TITLES[position]) {
            R.string.tab_face_measurement -> PlaceholderFragmentFaceMeasurement.newInstance(position + 1)
            R.string.tab_devices_measurement -> PlaceholderFragmentDevicesMeasurement.newInstance(position + 1)
            else -> throw IllegalArgumentException("Tab $position does not exist")
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show total pages.
        return TAB_TITLES.count()
    }
}