package com.nnbinh.trame.ui.histories

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nnbinh.trame.R
import com.nnbinh.trame.databinding.ActivityHistoriesBinding
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.extension.setOnSingleClickListener
import com.nnbinh.trame.ui.BaseActivity
import com.nnbinh.trame.ui.record.RecordingActivity
import kotlinx.android.synthetic.main.activity_histories.rcv_histories
import java.util.Date

class HistoriesActivity : BaseActivity() {
  private val viewModel: HistoriesVM by lazy {
    ViewModelProvider.AndroidViewModelFactory(application).create(HistoriesVM::class.java) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding: ActivityHistoriesBinding = DataBindingUtil.setContentView(this,
        R.layout.activity_histories)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel
    binding.rcvHistories.layoutManager = with(LinearLayoutManager(this)) {
        this.initialPrefetchItemCount = 10
        this.isSmoothScrollbarEnabled = true
        this
    }
    binding.rcvHistories.setHasFixedSize(true)
    binding.rcvHistories.setItemViewCacheSize(20)

    binding.btnRecord.setOnSingleClickListener {
      this.startActivity(RecordingActivity.getIntent(this))
    }

    viewModel.sessions.observe(this, {
      rcv_histories.adapter = HistoriesAdapter(it)
      (rcv_histories.adapter as HistoriesAdapter).notifyDataSetChanged()
    })

  }
}