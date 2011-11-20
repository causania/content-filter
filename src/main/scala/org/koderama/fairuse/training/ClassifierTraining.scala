package org.koderama.fairuse
package training

import data.TrainingSetProvider

/**
 * A template trait to define the main interface for classifier training.
 *
 * @author alejandro@koderama.com
 */
trait ClassifierTraining {

  /**
   * Performs the training of the classifier.
   * This method must be called before start classifying phrases. Otherwise classification might not work as expected.
   *
   * @return a double representing the error after the training.
   */
  def train(): Double

  /**
   * The error of the classifier is a generic number and it's
   * only useful to be compared after the classifier is trained.
   *
   * @return Gets the current error of the classifier.
   */
  def currentError(): Double

  /**
   * @return the training set used to train this classifier
   */
  def provider: TrainingSetProvider

}