package com.seanshubin.builder.domain

import java.time.Instant

case class ProcessOutput(processInput:ProcessInput,
                         exitCode: Int,
                         outputLines: Seq[String],
                         errorLines: Seq[String],
                         started: Instant,
                         ended: Instant)
