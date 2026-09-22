package aoc.utils

import Light.State

final case class Light private (state: State):
  def turnOn: Light = Light(State.ON)
  def turnOff: Light = Light(State.OFF)

  def toggle: Light = state match
    case State.ON => Light(State.OFF)
    case State.OFF => Light(State.ON)

object Light:
  enum State:
    case OFF, ON

  val put: Light = Light(State.OFF)
