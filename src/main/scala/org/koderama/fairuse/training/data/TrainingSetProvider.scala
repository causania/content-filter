package org.koderama.fairuse
package training.data

import org.encog.ml.data.{MLData, MLDataSet}
import org.encog.ml.data.basic.{BasicMLData, BasicMLDataSet}

/**
 * Provides training data for a network.
 * Data is defined using doubles.
 *
 * @author alejandro@koderama.com
 */
trait TrainingSetProvider {

  /**
   * @return a List of list containing all the training data (X)
   */
  def input: List[List[Double]]

  /**
   * @return the ideal output y for each input X.
   */
  def ideal: List[Double]

  /**
   * Adds a new training example.
   *
   * @param newTrainEntry contains the list of inputs and the value of y
   */
  def +=(newTrainEntry: (List[Double], Double)): TrainingSetProvider

  /**
   * @return the size of the input with more X values.
   */
  def maxSize = input.max(Ordering[Int].on[List[Double]](_.size)).size

  /**
   * Creates a training set based on the input and ideal
   *
   * @return a normalized training set (all the input with the same length)
   */
  def trainingSet: MLDataSet = {
    new BasicMLDataSet(input.map(_.toArray).toArray, ideal.map(Array(_)).toArray)
  }

  /**
   * Transforms the given string to a data representation.
   * @return a normalized Data considering the max size of the current inputs.
   */
  def toData(s: String): MLData = {
    new BasicMLData(transform(s).toArray)
  }

  /**
   * Transforms the string to a collection of double.
   * The string will be normalized to consider the max size of this training set.
   *
   * @returns a list where each double value represents the character code.
   */
  def transform(s: String): List[Double] = {
    s.take(maxSize).map(_.toDouble).toList ::: List.fill(maxSize - s.length)(0d)
  }
}