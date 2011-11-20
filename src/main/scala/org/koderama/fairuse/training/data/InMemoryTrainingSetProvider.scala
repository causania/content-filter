package org.koderama.fairuse
package training.data

/**
 * Extends the [[TrainingSetProvider]] trait be storing  the elements in memory.
 *
 * @author alejandro@koderama.com
 */
class InMemoryTrainingSetProvider(val X: List[List[Double]], val y: List[Double]) extends TrainingSetProvider {

  override def input: List[List[Double]] = X

  override def ideal: List[Double] = y

  override def +=(newTrainEntry: (List[Double], Double)): TrainingSetProvider = {
    new InMemoryTrainingSetProvider(newTrainEntry._1 :: X, newTrainEntry._2 :: y)
  }
}

object InMemoryTrainingSetProvider {
  implicit def bool2Double(b: Boolean): Double = if (b) 1d else 0d

  /**
   * Convenient factory method
   *
   * @param X the list of input training set
   * @param y the actual value for each training entry X[i]
   */
  def apply(X: List[List[Double]], y: List[Double]): InMemoryTrainingSetProvider = {
    new InMemoryTrainingSetProvider(X, y)
  }

  /**
   * Convenient factory method
   *
   * @param X the list of input training set
   */
  def apply(rawTrainingSet: List[(String, Boolean)]): InMemoryTrainingSetProvider = {
    val trainingSet: List[(List[Double], Double)] = rawTrainingSet.map(
      t => (t._1.map(_.toDouble).toList, bool2Double(t._2)))

    val tmpTs = new InMemoryTrainingSetProvider(trainingSet.map(_._1), trainingSet.map(_._2))


    val normalizedX = tmpTs.input.map(i => i ::: List.fill(tmpTs.maxSize - i.length)(0d))
    new InMemoryTrainingSetProvider(normalizedX, tmpTs.ideal)
  }

}