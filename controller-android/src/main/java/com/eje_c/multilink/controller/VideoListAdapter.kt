package com.eje_c.multilink.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.eje_c.multilink.controller.db.VideoEntity

/**
 * Adapter for [DeviceEntity].
 */
class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    var list: List<VideoEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val video = list[position]
        holder.text.text = video.name

        // TODO Needs to be refactored
        holder.itemView.setOnClickListener {
            PlayerActivity_.intent(holder.itemView.context)
                    .videoPath(video.path)
                    .videoLength(video.length)
                    .start()
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    class ViewHolder(container: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(container.context).inflate(R.layout.video_list_item, container, false)) {
        val text: TextView = itemView.findViewById(R.id.text)
    }
}