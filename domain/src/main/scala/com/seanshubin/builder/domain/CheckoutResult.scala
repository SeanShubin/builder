package com.seanshubin.builder.domain

import com.seanshubin.builder.process.ProcessOutput

case class CheckoutResult(sshUrl: String, previousAttempts: Int, processOutput: ProcessOutput)
