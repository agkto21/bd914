package input

import scala.io.BufferedSource
import vectors.AbstractVector
import java.io.File

/**
 * Implement this if you want to process the input files, and create object representation of the read data
 * @author Ivan Gavrilović
 */
trait AbstractInputProcessor {
  type T <: AbstractVector

  /**
   * Parse input file and create collection of vectors from the file
   * @param input input source to read
   * @return feature vector
   */
  def processData(input: BufferedSource): T

  /**
   * Parse all files in a directory
   * @param dirName path to the directory
   * @return collection of vectors
   */
  def processInDir(dirName: String): Seq[T] = {
    val dir = new File(dirName)
    if (!dir.isDirectory) {
      throw new Exception("Directory expected")
    }
    //ignore hidden files and check file is file
    dir.listFiles.filter((x: File) => !x.getName.startsWith(".") && !x.isDirectory).filter(_.isFile()).map(
      x => processData(scala.io.Source.fromFile(x))
    )
  }
}
