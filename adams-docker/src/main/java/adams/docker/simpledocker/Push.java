/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Push.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Pushes the specified image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Push
  extends AbstractAsyncCapableDockerCommand
  implements AsyncCapableDockerCommand {

  private static final long serialVersionUID = -3235247889827794116L;

  /** the image to push. */
  protected String m_Image;

  /** whether to push all tags. */
  protected boolean m_AllTags;

  /** whether to disable content trust. */
  protected boolean m_DisableContentTrust;

  /** whether to be less verbose. */
  protected boolean m_Quiet;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Pushes the specified image."
      + "Use non-blocking mode for better progress updates.\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/push/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image", "image",
      "");

    m_OptionManager.add(
      "all-tags", "allTags",
      false);

    m_OptionManager.add(
      "disable-content-trust", "disableConentTrust",
      false);

    m_OptionManager.add(
      "quiet", "quiet",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "image", m_Image, ", image: ");
    result += QuickInfoHelper.toString(this, "allTags", m_AllTags, "all tags", ", ");
    result += QuickInfoHelper.toString(this, "disableContentTrust", m_DisableContentTrust, "no content trust", ", ");
    result += QuickInfoHelper.toString(this, "quiet", m_Quiet, "quiet", ", ");

    return result;
  }

  /**
   * Sets the image to push.
   *
   * @param value	the image
   */
  public void setImage(String value) {
    m_Image = value;
    reset();
  }

  /**
   * Returns the image to push.
   *
   * @return		the image
   */
  public String getImage() {
    return m_Image;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTipText() {
    return "The name of the image to push.";
  }

  /**
   * Sets whether to push all tags.
   *
   * @param value	true if all tags
   */
  public void setAllTags(boolean value) {
    m_AllTags = value;
    reset();
  }

  /**
   * Returns whether to push all tags.
   *
   * @return		true if all tags
   */
  public boolean getAllTags() {
    return m_AllTags;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allTagsTipText() {
    return "Whether to push all tags of this image.";
  }

  /**
   * Sets whether to disable content trust, i.e., skip signing images.
   *
   * @param value	true if to disable
   */
  public void setDisableContentTrust(boolean value) {
    m_DisableContentTrust = value;
    reset();
  }

  /**
   * Returns whether to disable content trust, i.e., skip signing images.
   *
   * @return		true if to disable
   */
  public boolean getDisableContentTrust() {
    return m_DisableContentTrust;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String disableContentTrustTipText() {
    return "Whether to skip image signing.";
  }

  /**
   * Sets whether to be less verbose in the output.
   *
   * @param value	true if less verbose
   */
  public void setQuiet(boolean value) {
    m_Quiet = value;
    reset();
  }

  /**
   * Returns whether to be less verbose in the output.
   *
   * @return		true if less verbose
   */
  public boolean getQuiet() {
    return m_Quiet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String quietTipText() {
    return "If enabled, the output is less verbose.";
  }

  /**
   * Assembles the command to run. Docker executable gets added separately.
   *
   * @return		the command
   */
  @Override
  protected List<String> buildCommand() {
    List<String> 	result;

    result = new ArrayList<>();
    result.add("push");
    if (m_AllTags)
      result.add("--all-tags");
    if (m_DisableContentTrust)
      result.add("--disable-content-trust");
    if (m_Quiet)
      result.add("--quiet");
    result.add(m_Image);

    return result;
  }
}
