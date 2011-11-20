package org.koderama.fairuse
package neuralnet

import org.specs2.Specification
import training.data.{TrainingSetGenerator, InMemoryTrainingSetProvider}

/**
 * Specifications for the [[FairUseNeuralNetworkClassifier]] implementation
 * 
 * @author alejandro@koderama.com
 */
class FairUseNeuralNetworkClassifierSpec extends Specification {

  val generator = TrainingSetGenerator(List("tomorrow", "hello", "today", "goodbye", "sometimes"),
    List("fuck", "stupid", "fucker"), List("the", "when", "now", "what", "there"))

  val badWordsTs = generator.generateWords(8, profanePer = 1, obfuscationProb = 0.2)
  val normalWordsTs = generator.generateWords(20, profanePer = 0, obfuscationProb = 0.0)
  val trainingSet = badWordsTs ::: normalWordsTs

  val provider = InMemoryTrainingSetProvider(trainingSet)

  val network = FairUseNeuralNetworkClassifier(provider, maxError = 0.01)

  def is =
    "This is a specification to check the general behavior of the FairUseNeuralNetworkClassifier class" ^
      p ^
      "The FairUseNeuralNetworkClassifier with a training set of %s should".format(trainingSet) ^
      "Return an error lower than 0.01" ! e1 ^
      "Save the state and load the same network" ! e2 ^
      "Classify 'fuck' as bad" ! e3 ^
      "Classify 'hello' as good" ! e4 ^
      "Classify 'tomorrow' as good" ! e5 ^
      "Classify 'f*ck' as bad" ! e6 ^
      "Classify unkonwn words as good" ! e7

  end


  def e1 = network.train() must lessThan(1.0d)

  def e2 = {
    val e = network.currentError()
    network.save()
    val persistedNetwork = FairUseNeuralNetworkClassifier(provider, "./fair_use_network")
    (persistedNetwork.currentError() - e).abs must lessThan(0.01)
  }

  def e3 = network.compute("fuck") must beTrue

  def e4 = network.compute("hello") must beFalse

  def e5 = network.compute("tomorrow") must beFalse

  def e6 = network.compute("f*ck") must beTrue

  def e7 = network.compute("tuesday?") must beFalse
}