package com.nnbinh.trame.ui.histories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.nnbinh.trame.R
import com.nnbinh.trame.databinding.CellSessionBinding
import com.nnbinh.trame.db.table.Session

class HistoriesAdapter(private val data: List<Session> = arrayListOf()) : ListAdapter<Session, SessionCell>(
    SessionsDiffCallback()) {

  override fun getItemCount() = data.size
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionCell {
    val inflater = LayoutInflater.from(parent.context)
    return SessionCell(CellSessionBinding.inflate(inflater, parent, false))
  }

  override fun onBindViewHolder(holder: SessionCell, position: Int) {
    holder.onBind(data[position])
  }
}

class SessionCell(private val binding: CellSessionBinding) : RecyclerView.ViewHolder(binding.root), OnMapReadyCallback {
  fun onBind(item: Session) {
    binding.item = item
    loadMapFragment()
    binding.executePendingBindings()
  }

  private fun loadMapFragment() {
    (itemView.context as? HistoriesActivity)?.let { activity ->
      val mapFragment = SupportMapFragment()
      activity.supportFragmentManager.beginTransaction()
          .add(R.id.lout_route, mapFragment)
          .commit()
      mapFragment.getMapAsync(this)
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    binding.item?.let {
      (itemView.context as? HistoriesActivity)?.viewModel?.loadRouteMapForSession(it, googleMap)
    }
  }
}

private class SessionsDiffCallback : DiffUtil.ItemCallback<Session>() {
  override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
    return oldItem == newItem
  }
}