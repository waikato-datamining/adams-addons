package adams.gui.visualization.annotator;

/**
 * an interface that describes a listener for an annotation event
 *
 * @author sjb90
 * @version $Revision$
 */
public interface AnnotationListener {
  /**
   * Receives an AnnotationEvent that contains a Step
   * @param e the event containing the Step
   */
  void annotationOccurred(AnnotationEvent e);
}
