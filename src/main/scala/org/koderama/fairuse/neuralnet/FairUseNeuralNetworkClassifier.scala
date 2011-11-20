package org.koderama.fairuse
package neuralnet

import org.encog.engine.network.activation.ActivationSigmoid
import org.encog.neural.networks.layers.BasicLayer
import org.encog.neural.networks.BasicNetwork
import org.encog.persist.EncogDirectoryPersistence
import java.io.File
import org.encog.neural.pattern.{ElmanPattern, FeedForwardPattern}
import org.encog.neural.networks.training.{TrainingSetScore}
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing
import org.encog.ml.train.strategy.{HybridStrategy, StopTrainingStrategy}
import org.encog.neural.networks.training.propagation.back.Backpropagation
import org.encog.ml.train.strategy.end.EndMaxErrorStrategy
import util.Logging
import training.data.TrainingSetProvider
import training.ClassifierTraining

/**
 * Neural Network implementation of the [[FairUseClassifier]] interface.
 *
 * @author alejandro@koderama.com
 */
class FairUseNeuralNetworkClassifier(val network: BasicNetwork, val provider: TrainingSetProvider,
                                     val maxError: Double = 5.0,
                                     val maxIterations: Int = 10000,
                                     val saveTo: String = "./fair_use_network")
  extends FairUseClassifier with ClassifierTraining with Logging {

  override def train(): Double = {
    val trainingSet = provider.trainingSet
    val score = new TrainingSetScore(trainingSet)
    val trainAlt = new NeuralSimulatedAnnealing(network, score, 1, 10, 10)
    val trainMain = new Backpropagation(network, trainingSet, 0.000001, 0.0)

    val stop = new StopTrainingStrategy()
    trainMain.addStrategy(new HybridStrategy(trainAlt))
    trainMain.addStrategy(new EndMaxErrorStrategy(maxError));
    trainMain.addStrategy(stop)

    var epoch = 0
    info("Starting training")
    val start = System.currentTimeMillis()
    while (!stop.shouldStop()) {
      trainMain.iteration()
      debug("Training " + network.getClass.getSimpleName + ", Epoch #" + epoch + " Error:" + trainMain.getError)
      epoch += 1;
    }

    val finalCost = currentError()
    info("Ending training with network error %s in %s secs".format(finalCost, (System.currentTimeMillis() - start) / 1000))

    finalCost
  }

  override def currentError(): Double = {
    network.calculateError(provider.trainingSet)
  }

  override def compute(v: String, confidence: Double = 0.6): Boolean = {
    val output = network.compute(provider.toData(v))
    output.getData(0) >= confidence
  }

  def save() {
    info("Saving network")
    EncogDirectoryPersistence.saveObject(new File(saveTo), network)
  }
}

/**
 * Companion object to be used as factory fot [[FairUseNeuralNetworkClassifier]] instances.
 * 
 * @author alejandro@koderama.com
 */
object FairUseNeuralNetworkClassifier extends Logging {

  def apply(provider: TrainingSetProvider, maxError: Double = 5.0,
            maxIterations: Int = 10000, saveTo: String = "./fair_use_network"): FairUseNeuralNetworkClassifier = {

    new FairUseNeuralNetworkClassifier(createCustomNetwork(provider.maxSize), provider, maxError, maxIterations, saveTo)
  }

  def apply(provider: TrainingSetProvider, savedTo: String): FairUseNeuralNetworkClassifier = {
    info("Loading network")
    val network = EncogDirectoryPersistence.loadObject(new File(savedTo)).asInstanceOf[BasicNetwork]
    val fairUseNetwork = new FairUseNeuralNetworkClassifier(network, provider)
    info("Loaded network’s error is: " + fairUseNetwork.currentError())

    fairUseNetwork
  }

  // Different topologies to try...

  def createCustomNetwork(inputSize: Int) = {
    val network = new BasicNetwork()
    network.addLayer(new BasicLayer(null, true, inputSize))
    network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputSize * 5))
    network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputSize * 5))
    network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputSize * 5))
    network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1))
    network.getStructure.finalizeStructure()
    network.reset()

    network
  }

  def createElmanNetwork(inputSize: Int): BasicNetwork = {
    // construct an Elman type network
    val pattern = new ElmanPattern()
    pattern.setActivationFunction(new ActivationSigmoid())
    pattern.setInputNeurons(inputSize)
    pattern.addHiddenLayer(inputSize * 4)
    pattern.setOutputNeurons(1)

    pattern.generate().asInstanceOf[BasicNetwork]
  }

  def createFeedForwardNetwork(inputSize: Int): BasicNetwork = {
    // construct a feedforward type network
    val pattern = new FeedForwardPattern()
    pattern.setActivationFunction(new ActivationSigmoid())
    pattern.setInputNeurons(inputSize)
    pattern.addHiddenLayer(inputSize * 4)
    pattern.setOutputNeurons(1)

    pattern.generate().asInstanceOf[BasicNetwork]
  }
}