#!/bin/sh
exec scala "$0" "$@"
!#

import scala.sys.process._
import scala.io.StdIn._

val usage = "You must pass an argument"

val deviceIndependantCommands = List("devices", "connect", "disconnect", "help", "version", "start-server", "kill-server")

object Runner {
  def main(args: Array[String]) {
    if (args.length == 0) println(usage)
    else {
      val argsList = args.toList
      if (deviceIndependantCommands.contains(argsList(0)) || getSerials().size == 1) deferToAdb(argsList)
      else if (getSerials().size > 1) handleMultipleSelection(argsList)
    }
  }
}

def getSerials(): List[String] = {
  val devicesProcessOutput = Seq("adb", "devices").!!
  val devicesPair: List[String] = ((devicesProcessOutput split "\n").toList).tail
  devicesPair.map(_.split("\\s")(0))
}

def getDevicesExtraInfo(): List[String] = {
  val devicesProcessOutput = Seq("adb", "devices", "-l").!!
  val devicesInformation = ((devicesProcessOutput split "\n").toList).tail
  devicesInformation
}

def deferToAdb(args: List[String]) = {
  (Seq("adb") ++ args.flatMap(Seq(_))).!
}

def runOnDevice(args: List[String], serial: String) {
  (Seq("adb", "-s", serial) ++ args.flatMap(Seq(_))).!
}

def handleMultipleSelection(args: List[String]) = {
  println("You have multple devices. On which device will this command run ?")

  val deviceInfo = getDevicesExtraInfo()

  for ((v, k) <- deviceInfo.zipWithIndex) {
    val deviceName = (v.split("\\s").toList).filter(!_.isEmpty)(3)
    println("("+ (k + 1) + ")" + " " + deviceName)
  }

  println("type 'a' to run on all devices")
  println("type 'c' to cancel")

  val optionSelected = readLine()

  if (isAllDigits(optionSelected)) {
    val index = optionSelected.toInt - 1
    println("index " + index)
    if (index < deviceInfo.length && index >= 0 ) {
      runOnDevice(args, deviceInfo(index).split("\\s").filter(!_.isEmpty)(0))
    }
    else println("Invalid Option")
  }
  else {
    optionSelected match {
      case "a" => {
        for ( i <- deviceInfo) runOnDevice(args, i.split("\\s").filter(!_.isEmpty)(0))
      }
      case "c" => println("Cancelled")
      case _ => println("Invalid Option")
    }
  }
}

def isAllDigits(x: String) = x forall Character.isDigit

Runner.main(args)
