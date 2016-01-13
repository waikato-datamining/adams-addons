package adams.gui.visualization.annotator;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public interface TickListener {

  void tickHappened(TickEvent e);

  long getInterval();
}
