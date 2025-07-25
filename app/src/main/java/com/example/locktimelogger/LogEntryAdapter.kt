package com.example.locktimelogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class LogEntryAdapter(private val entries: List<LogEntry>) :
    RecyclerView.Adapter<LogEntryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.findViewById(R.id.dateView)
        val eventView: TextView = view.findViewById(R.id.eventView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_entry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.dateView.text = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(entry.timestamp)
        holder.eventView.text = entry.event
    }

    override fun getItemCount(): Int = entries.size
}
