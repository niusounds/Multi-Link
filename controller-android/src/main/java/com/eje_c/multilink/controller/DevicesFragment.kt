package com.eje_c.multilink.controller

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.eje_c.multilink.db.DeviceEntity
import com.eje_c.multilink.udp.MultiLinkUdpMessenger
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.ViewById
import kotlin.concurrent.thread

@EFragment(R.layout.fragment_devices)
open class DevicesFragment : Fragment() {

    @ViewById
    lateinit var recyclerView: RecyclerView
    @ViewById
    lateinit var deviceCount: TextView

    @AfterViews
    fun init() {

        val adapter = DeviceListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        App.db.deviceDao().query().observe(this, Observer<List<DeviceEntity>> { data ->
            if (data != null) {
                adapter.list = data
                deviceCount.text = data.size.toString()
            }
        })
    }

    @Click
    fun reloadButton() {
        thread {
            App.db.deviceDao().clear()
            App.db.videoDao().clear()

            MultiLinkUdpMessenger.ping()
        }
    }
}
