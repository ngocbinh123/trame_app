package com.nnbinh.trame.ui.histories

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nnbinh.trame.R
import com.nnbinh.trame.databinding.ActivityHistoriesBinding
import com.nnbinh.trame.extension.setOnSingleClickListener
import com.nnbinh.trame.ui.BaseActivity
import com.nnbinh.trame.ui.record.RecordingActivity
import kotlinx.android.synthetic.main.activity_histories.rcv_histories

class HistoriesActivity : BaseActivity() {
  val viewModel: HistoriesVM by lazy {
    ViewModelProvider.AndroidViewModelFactory(application).create(HistoriesVM::class.java)
  }

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
    binding.btnRecord.setOnSingleClickListener { viewModel.createNewSession() }

    viewModel.sessions.observe(this, {
      rcv_histories.adapter = HistoriesAdapter(it)
      (rcv_histories.adapter as HistoriesAdapter).notifyDataSetChanged()
    })

    viewModel.startSession.observe(this, {
      if (it != null) {
        startActivity(RecordingActivity.getIntent(this, it.id))
      }
    })

    viewModel.checkHasRecordingSession()
  }
}