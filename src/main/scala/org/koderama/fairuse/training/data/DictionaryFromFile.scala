package org.koderama.fairuse
package training.data

/**
 * Defines a dictionary where the words are read from a file.
 * Each file expects a different format. But, in general it's just one line per word.
 *
 * @author alejandro@koderama.com
 */
trait DictionaryFromFile {

  /**
   * The path to the file where all words of the language are listed.
   * Format: one line per word
   */
  def allWordsFile: String

  /**
   * The path to the file where profane words of the language are listed.
   * Format: one line per word
   */
  def profaneWordsFile: String

  /**
   * The path to the file where most used words of the language are listed.
   * Format per line: rank, word, abs, r, mod
   */
  def mostUsedWordsFile: String

  /**
   * All the words of the language
   */
  lazy val allWords = io.Source.fromFile(allWordsFile).getLines().toList

  /**
   * All the profane words of the language
   */
  lazy val profaneWords = io.Source.fromFile(profaneWordsFile).getLines().toList

  /**
   * The most used words of the language. Position 0 is the most used, followed by 1, etc
   */
  lazy val mostUsedWords =
    io.Source.fromFile(mostUsedWordsFile).getLines().map(line => {
      line.split(",") match {
        case Array(rank, word, abs, r, mod) => {
          word.trim
        }
      }
    }).toList
}