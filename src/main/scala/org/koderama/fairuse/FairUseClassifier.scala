package org.koderama.fairuse

/**
 * A template trait to define the main interface for words classification.
 *
 * @author alejandro@koderama.com
 */
trait FairUseClassifier {

  /**
   * Defines whenever the given word is profane or not.
   *
   * @return true if the word is profane. False otherwise
   */
  protected def compute(word: String, confidence: Double = 0.6): Boolean

  /**
   * Classifies the given phrase. The phrase will be tokenized in words using the space as separator.
   * Words lower than 4 characters are not considered.
   *
   * @return true if the phrase contains profane words. False otherwise.
   */
  def classify(phrase: String, confidence: Double = 0.6, inner: Boolean = true): Boolean = {
    if (phrase.length > 3 && inner) {
      if (phrase.contains(" ")) {
        phrase.split(" ").map(p => classify(p.trim, confidence, false)).reduce(_ || _)
      } else {
        compute(phrase, confidence)
      }
    } else if (phrase.length > 3 && !inner) {
      compute(phrase)
    } else {
      false
    }
  }

}