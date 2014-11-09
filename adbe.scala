import scala.sys.process._
import scala.io.StdIn._

val usage = "You must pass a valid argument"

val deviceIndependantCommands = List("devices", "connect", "disconnect", "help", "version", "start-server", "kill-server")

object Runner {
  def main(args: Array[String]) {
    if (args.length == 0) println(usage)
    else {
      val argsList = args.toList
      debug(argsList(0))
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

def deferToAdb(args: List[String]) = {
  (Seq("adb") ++ args.flatMap(Seq(_))).!
}

def handleMultipleSelection(args: List[String]) {
  println("On which device will this command run ?")
  val optionSelected = readChar()
  println(optionSelected)
}

def debug(command: String) = {
  println("Command Name is " + command)
  println("serials size " + getSerials().length)
  println("Command is Device Independant : " + deviceIndependantCommands.contains(command))
}

Runner.main(args)
