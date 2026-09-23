package aoc.y2015.core

import Light.State

final case class Light private (state: State):
  def isOn: Boolean = state == State.ON

  def turnOn: Light = Light.On

  def turnOff: Light = Light.Off

  def toggle: Light = state match
    case State.ON => Light.Off
    case State.OFF => Light.On

object Light:
  enum State:
    case OFF, ON

  val On: Light = Light(State.ON)
  val Off: Light = Light(State.OFF)

  val put: Light = Off
