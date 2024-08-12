package net.minevn.minigames

import java.awt.Color
import kotlin.math.*

/**
 * Allows generation of a multi-part gradient with a defined number of steps
 */
class Gradient(colors: List<Color>, steps: Int) {
	private val gradients: MutableList<TwoStopGradient>
	private val steps: Int
	private var step: Long

	init {
		require(colors.size >= 2) { "Must provide at least 2 colors" }
		gradients = ArrayList()
		this.steps = steps
		step = 0
		val increment = (this.steps - 1).toFloat() / (colors.size - 1)
		for (i in 0 until colors.size - 1) gradients.add(
			TwoStopGradient(
				colors[i],
				colors[i + 1], increment * i, increment * (i + 1)
			)
		)
	}

	fun nextColor(): Color {
		// Do some wizardry to get a function that bounces back and forth between 0 and a cap given an increasing input
		// Thanks to BomBardyGamer for assisting with this
		val adjustedStep = abs(2 * asin(sin(step * (Math.PI / (2 * steps)))) / Math.PI * steps).roundToInt()
		val color: Color = if (gradients.size < 2) {
			gradients[0].colorAt(adjustedStep)
		} else {
			val segment = steps.toFloat() / gradients.size
			val index = floor((adjustedStep / segment).toDouble()).coerceAtMost((gradients.size - 1).toDouble()).toInt()
			gradients[index].colorAt(adjustedStep)
		}
		step++
		return color
	}
}

class TwoStopGradient(
	private val startColor: Color,
	private val endColor: Color,
	private val lowerRange: Float,
	private val upperRange: Float
) {
	/**
	 * Gets the color of this gradient at the given step
	 *
	 * @param step The step
	 * @return The color of this gradient at the given step
	 */
	fun colorAt(step: Int): Color {
		return Color(
			calculateHexPiece(step, startColor.getRed(), endColor.getRed()),
			calculateHexPiece(step, startColor.getGreen(), endColor.getGreen()),
			calculateHexPiece(step, startColor.getBlue(), endColor.getBlue())
		)
	}

	private fun calculateHexPiece(step: Int, channelStart: Int, channelEnd: Int): Int {
		val range = upperRange - lowerRange
		if (range == 0f) // No range, don't divide by 0
			return channelStart
		val interval = (channelEnd - channelStart) / range
		return Math.min(Math.max(Math.round(interval * (step - lowerRange) + channelStart), 0), 255)
	}
}