package com.nnbinh.trame.data

/**
 * SessionState show state of session
 * */
enum class SessionState {
  NEW, // session just have created
  RECORDING, // user is tracking location
  PAUSE // user is stopping/pausing to track location
}