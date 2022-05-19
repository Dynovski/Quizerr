package pl.dynovski.quizerr.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fActivity: FragmentActivity) : FragmentStateAdapter(fActivity) {

    private val fragments: MutableList<Fragment> = mutableListOf()
    private val fragmentsTitles: MutableList<String> = mutableListOf()

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentsTitles.add(title)
    }

    fun getTabTitle(position: Int): String {
        return fragmentsTitles[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getItem(i: Int): Fragment {
        return fragments[i]
    }

    fun removeAllFragments() {
        fragments.clear()
        fragmentsTitles.clear()
        notifyDataSetChanged()
    }
}
