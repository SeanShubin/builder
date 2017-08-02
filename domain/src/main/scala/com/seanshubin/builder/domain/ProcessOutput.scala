package com.seanshubin.builder.domain

import java.time.Instant

case class ProcessOutput(exitCode: Int,
                         outputLines: Seq[String],
                         errorLines: Seq[String],
                         started: Instant,
                         ended: Instant)
