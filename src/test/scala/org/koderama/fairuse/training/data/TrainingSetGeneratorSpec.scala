package org.koderama.fairuse
package training.data

import org.specs2.Specification

/**
 * Specification for the [[TrainingSetGenerator]] implementation
 * 
 * @author alejandro@koderama.com
 */
class TrainingSetGeneratorSpec extends Specification {

  val tsg = TrainingSetGenerator("src/test/resources/english-all-words.txt",
    "src/test/resources/english-profane-words.txt", "src/test/resources/english-most-used-words.txt")

  def is =
    "This is a specification to check the general behavior of the TrainingSetGenerator class" ^
      p ^
      "The TrainingSetGenerator with a list of all words should" ^
      "Generate a list of 10 words" ! e1 ^
      "Contains \"tomorrow\" or \"computer\" in a list of 50" ! e2 ^
      "Contains an obfusctated word" ! e3

  end

  def e1 = tsg.generateWords(10).length must equalTo(10)

  def e2 = tsg.generateWords(50, profanePer = 0, mostUsedPer = 0.01) must contain(("tomorrow", false)) or contain(("computer", false))

  def e3 = tsg.generateWords(10, profanePer = 1, obfuscationProb = 1).find(
    w => w._1.find(c => tsg.obfuscationCharacters.contains(c.toString)) != None) must not equalTo (None)
}