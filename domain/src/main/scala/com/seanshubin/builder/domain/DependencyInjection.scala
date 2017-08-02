package com.seanshubin.builder.domain

import java.time.Clock

trait DependencyInjection {
  val buildEventQueue = new BuildEventQueue()
  val dispatcher = new Dispatcher(buildEventQueue)
  val ignoreSorter = new IgnoreSorter(
    dispatcher.ignore,
    dispatcher.doNotIgnore)
  val ignoredHandler = new IgnoredHandler
  val clock = Clock.systemUTC()
  val builder = new Checkout(clock, dispatcher.checkoutProcessFinished)
  val projectFinder = new ProjectFinder(dispatcher.projectFound)
  val attemptLimit = 3
  val checkoutSorter = new CheckoutSorter(
    dispatcher.checkoutSuccess,
    dispatcher.checkoutFailure,
    dispatcher.checkoutGiveUp,
    attemptLimit)
  val checkoutReporter = new CheckoutReporter
  val buildEventHandler = new BuildEventHandler(
    ignoreSorter,
    ignoredHandler,
    builder,
    checkoutSorter,
    checkoutReporter)
  val consumer = new Consumer(buildEventQueue, buildEventHandler)
  val runner = new Runner(projectFinder, consumer)
}
