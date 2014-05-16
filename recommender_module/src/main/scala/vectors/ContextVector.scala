package vectors

import features.Feature
import sets.AbstractVectorSet

/**
 * @author Ivan Gavrilović
 */
class ContextVector(var features: Seq[Feature[_, _]], sets: Seq[AbstractVectorSet]) extends AbstractVector {


  /**
   * Get all sets to which this vector belongs to
   * @return all vector sets associated
   */
  override def getVectorSets: Seq[AbstractVectorSet] = sets

  /**
   * Set all of the vector's features
   */
  override protected def setFeatures(feats: Seq[Feature[_, _]]): Unit = features = feats

  /**
   * Get all of the vector's features
   * @return all features
   */
  override def getFeatures: Seq[Feature[_, _]] = features
}
