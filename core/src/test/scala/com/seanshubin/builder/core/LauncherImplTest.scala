package com.seanshubin.builder.core

import org.scalatest.FunSuite
import org.scalatest.mock.EasyMockSugar

class LauncherImplTest extends FunSuite with EasyMockSugar {
  test("valid configuration") {
    new Helper {
      override def expecting = () => {
        configurationFactory.validate(args).andReturn(validationSuccess)
        notifications.effectiveConfiguration(validConfiguration)
        runnerFactory.createRunner(validConfiguration).andReturn(runner)
        runner.run()
      }

      override def whenExecuting = () => {
        launcher.launch()
      }
    }
  }

  test("invalid configuration") {
    new Helper {
      override def expecting = () => {
        configurationFactory.validate(args).andReturn(validationFailure)
        notifications.configurationError(errorLines)
      }

      override def whenExecuting = () => {
        launcher.launch()
      }
    }
  }

  trait Helper {
    val args: Seq[String] = Seq("arg1")
    val validConfiguration = Configuration("world")
    val validationSuccess = Right(validConfiguration)
    val errorLines = Seq("error")
    val validationFailure = Left(errorLines)
    val configurationFactory = mock[ConfigurationFactory]
    val runnerFactory = mock[RunnerFactory]
    val notifications = mock[Notifications]
    val runner = mock[Runner]
    val launcher = new LauncherImpl(args, configurationFactory, runnerFactory, notifications)

    def expecting: () => Unit

    def whenExecuting: () => Unit

    expecting()
    EasyMockSugar.whenExecuting(configurationFactory, runnerFactory, notifications, runner) {
      whenExecuting()
    }
  }

}
