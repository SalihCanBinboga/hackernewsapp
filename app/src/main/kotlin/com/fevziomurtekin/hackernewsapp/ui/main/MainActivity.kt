package com.fevziomurtekin.hackernewsapp.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.fevziomurtekin.hackernewsapp.R
import com.fevziomurtekin.hackernewsapp.data.domain.ItemModel
import com.fevziomurtekin.hackernewsapp.ui.adapter.NewsAdapter
import com.fevziomurtekin.hackernewsapp.ui.news.NewsFragment
import com.fevziomurtekin.hackernewsapp.util.FragmentExt
import com.fevziomurtekin.hackernewsapp.util.LoadingEvent
import com.fevziomurtekin.hackernewsapp.util.SuccessEvent
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber


class MainActivity : AppCompatActivity(), NewsAdapter.OnItemClickListener,FragmentExt{

    /***
     * @param holderId -> FrameLayout that the fragment will be placed in
     * @param fragment -> Fragment instance
     * @param retain -> Whether or not previous fragment will be destroyed of will retain its state
     */

    override fun replaceFragment(retain: Boolean, holderId: Int,fragment:Fragment) {
        if (retain) {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_rigth,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .add(holderId,fragment,fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
                .commit();
        } else {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_rigth,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(holderId,fragment,fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
                .commit()
        }

    }

    /**
     * replace new Activity when onClick in News adapter
     */

    override fun onItemClick(item: ItemModel) {
        Timber.d("${item.text}")
    }

    //Declare MainViewModel with Koin and allow constructor di.
    private val viewModel by viewModel<MainViewModel> ()
    private var onItemClick : NewsAdapter.OnItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomappbar.replaceMenu(R.menu.bottomappbar_menu)
        onItemClick = this
        //recyclerview.layoutManager = LinearLayoutManager(applicationContext)


        replaceFragment(false,R.id.framelayout,NewsFragment())

        viewModel.events.observe(this, Observer {
            it?.let {
                when(it){
                    LoadingEvent -> viewModel.showSplash(splashscreen as ConstraintLayout)
                    SuccessEvent -> viewModel.hideSplash(splashscreen as ConstraintLayout)
                    else         -> showError()
                }
            }
        })

        viewModel.getNews()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.appbar_user->{
                //viewModel.getUsers()
            }
            R.id.appbar_jobs->{
                //viewModel.getJobs()
            }
            R.id.appbar_ask->{
                //viewModel.getAsk()
            }
        }

        return true
    }


    fun showError(){
        Snackbar.make(
            main_layout,
            getString(R.string.loading_error),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.retry, {
                viewModel.getNews()
            })
            .show()
    }


    /**
     * Defined and created EventBus.
     * Fetch Items and Update Recyclerview from usage EventBus.
     * @return MutableList<ItemModel>
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventItems(items : MutableList<ItemModel>){
        Timber.d("${items.toString()}")
        //recyclerview.adapter = NewsAdapter(applicationContext,items,onItemClick)

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}
