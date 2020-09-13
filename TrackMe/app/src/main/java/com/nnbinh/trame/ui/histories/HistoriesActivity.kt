package com.nnbinh.trame.ui.histories

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.nnbinh.trame.R
import com.nnbinh.trame.databinding.ActivityHistoriesBinding
import com.nnbinh.trame.db.table.Session
import com.nnbinh.trame.extension.setOnSingleClickListener
import com.nnbinh.trame.ui.BaseActivity
import com.nnbinh.trame.ui.record.RecordingActivity

class HistoriesActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding: ActivityHistoriesBinding = DataBindingUtil.setContentView(this,
        R.layout.activity_histories)
    binding.lifecycleOwner = this
    binding.rcvHistories.layoutManager = with(LinearLayoutManager(this)) {
        this.initialPrefetchItemCount = 10
        this
    }
    binding.rcvHistories.adapter = HistoriesAdapter(dumpSessions)
    (binding.rcvHistories.adapter as HistoriesAdapter).notifyDataSetChanged()

    binding.btnRecord.setOnSingleClickListener {
      this.startActivity(RecordingActivity.getIntent(this))
    }
  }


  private val dumpSessions = arrayListOf(
      Session(
          id = 1,
          createdAt = "03/01/2020",
          distance = 5.6f,
          averageSpeed = 12.5f,
          duration = "01:32:00"
      ),
      Session(
          id = 2,
          createdAt = "03/01/2020",
          distance = 10.8f,
          averageSpeed = 25.5f,
          duration = "02:14:23"
      ),
      Session(
          id = 3,
          createdAt = "03/01/2020",
          distance = 15.23f,
          averageSpeed = 27.8f,
          duration = "04:23:26"
      ),
      Session(
          id = 4,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
      Session(
          id = 5,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
      Session(
          id = 6,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
      Session(
          id = 7,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
      Session(
          id = 8,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
      Session(
          id = 9,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
      Session(
          id = 10,
          createdAt = "03/01/2020",
          distance = 22.34f,
          averageSpeed = 26.7f,
          duration = "05:31:16"
      ),
  )
}