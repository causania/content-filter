package org.koderama.fairuse
package training.data

import collection.mutable.{Set}
import scala.util.Random

/**
 * Generates random set of words to be used as training set.
 * The result collection might contains a combination of normal and profane words.
 * Additionally, profane words might be obfuscated in order to produce a more diverse set.
 *
 * @author alejandro@koderama.com
 */
abstract class TrainingSetGenerator {

  /**
   * @return all the words related with the language. Profane words are not included.
   */
  def allWords: List[String]

  /**
   * @return only the words that are considered profane.
   */
  def profaneWords: List[String]

  /**
   * @return a list of the most used words from most used (head) to less used (last).
   * This collection does not necessarily includes all the words of the language.
   */
  def mostUsedWords: List[String]

  lazy val obfuscationCharacters = List("-", "*", "+", ".", "!")

  /**
   * Generate phrases using the words from the dictionaries.
   *
   * @param required how many phrases are required
   * @param wordsPerPhrase how many words per phrase
   * @param profanePer the % of profane words to include from 0 to 1. Default 0.4
   * @param obfuscationProb the probability of obfuscation for profane words to include from 0 to 1. Default 0.5
   * @param mostUsedPer the % of most used words to include (from the total of words) from 0 to 1. Default 0.5
   *
   * @see #generateWords(Int, Int, Double, Double, Double)
   * @return a List of pairs with the phrases and a boolean value indicating if the phrase has a profane word
   */
  def generatePhrases(required: Int, wordsPerPhrase: Int = 5, profanePer: Double = 0.4,
                      obfuscationProb: Double = 0.5, mostUsedPer: Double = 0.5): List[(String, Boolean)] = {
    val words = generateWords(required * wordsPerPhrase,
      profanePer = profanePer, obfuscationProb = obfuscationProb, mostUsedPer = mostUsedPer)

    words.grouped(wordsPerPhrase).map(p => (
      p.reduceLeft((a, b) => (a._1 + " " + b._1, a._2 && b._2)))).toList
  }

  /**
   * Peeks random words from the dictionaries.
   *
   * @param required how many phrases are required
   * @param profanePer the % of profane words to include from 0 to 1. Default 0.4
   * @param obfuscationProb the probability of obfuscation for profane words to include from 0 to 1. Default 0.5
   * @param mostUsedPer the % of most used words to include (from the total of words) from 0 to 1. Default 0.5
   *
   * @return a List of pairs with the words and a boolean value indicating if the word is profane
   */
  def generateWords(required: Int, profanePer: Double = 0.4, obfuscationProb: Double = 0.5,
                    mostUsedPer: Double = 0.5): List[(String, Boolean)] = {
    require(required > 0)
    require(profanePer >= 0.0)
    require(obfuscationProb >= 0.0)

    val totalProfane = (required * profanePer).floor.toInt
    val totalMostUsed = ((required - totalProfane) * mostUsedPer).floor.intValue()
    val totalOther = required - totalProfane - totalMostUsed

    val profane = peekRandomly(totalProfane, profaneWords, obfuscationProb = obfuscationProb)
    val mostUsed = peekRandomly(totalMostUsed, mostUsedWords, useGaussian = true)
    val other = peekRandomly(totalOther, allWords)

    mostUsed.map(s => (s, false)) ::: profane.map(s => (s, true)) ::: other.map(s => (s, false))
  }

  private def peekRandomly(required: Int, words: List[String], useGaussian: Boolean = false,
                           obfuscationProb: Double = 0.0): List[String] = {
    val selectedWords = Set[String]()

    while (selectedWords.size < required && (selectedWords.size < words.size || obfuscationProb > 0.0)) {
      val index =
        if (useGaussian) (Random.nextGaussian().abs * 0.3 * words.size).intValue() else Random.nextInt(words.size)

      if (index < words.size) {
        if (obfuscationProb > 0.0 && Random.nextDouble <= obfuscationProb) {
          selectedWords += obfuscate(words(index))
        } else {
          selectedWords += words(index)
        }
      }
    }

    selectedWords.toList
  }

  private def obfuscate(s: String): String = {
    val obChar = obfuscationCharacters(Random.nextInt(obfuscationCharacters.size))

    Random.nextDouble() match {
      case n if n <= 0.5 => obChar + s + obChar
      case n if n > 0.5 && n <= 0.8 => s.map(_.toString).reduceLeft {
        (acc, n) =>
          acc + obChar + n
      }
      case _ => s.replaceFirst(s.charAt(Random.nextInt(s.size)).toString, obChar)
    }
  }

}

object TrainingSetGenerator {

  /**
   * Creates a [[TrainingSetGenerator]] using dictionaries from a file
   *
   * @param allWordsFilePath the path to the dictionary that contains all the language words
   * @param profaneWordsFilePath the path to the dictionary that contains profane words
   * @param mostUsedWordsFilePath the path to the file that contains information about the most used words (rank, etc)
   *
   * @see DictionaryFromFile
   * @see TrainingSetGenerator
   */
  def apply(allWordsFilePath: String, profaneWordsFilePath: String, mostUsedWordsFilePath: String): TrainingSetGenerator = {
    new TrainingSetGenerator()
      with DictionaryFromFile {
      val allWordsFile = allWordsFilePath
      val profaneWordsFile = profaneWordsFilePath
      val mostUsedWordsFile = mostUsedWordsFilePath
    };
  }

  /**
   * Creates a [[TrainingSetGenerator]] using list dictionaries.
   *
   * @param allWordsList a list containing all the language words. Not including profane words
   * @param profaneWordsList a list containing all the profane words.
   * @param mostUsedWordsList a list with the most used words. head -> most used, last less used.
   *
   * @see TrainingSetGenerator
   */
  def apply(allWordsList: List[String], profaneWordsList: List[String],
            mostUsedWordsList: List[String]): TrainingSetGenerator = {
    new TrainingSetGenerator() {
      val allWords = allWordsList
      val profaneWords = profaneWordsList
      val mostUsedWords = mostUsedWordsList
    };
  }
}