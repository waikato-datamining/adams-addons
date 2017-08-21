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
 * AbstractBrainScriptModelGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelgenerator;

/**
 * Just outputs a single manually defined model.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBrainScriptModelGenerator
  extends AbstractModelGenerator {

  private static final long serialVersionUID = -4683549348343064989L;

  /**
   * Returns information on BrainScript.
   *
   * @return 		the info
   */
  protected String getBrainScriptInfo() {
    return
	"More information on BrainScript:\n"
	+ "https://docs.microsoft.com/en-us/cognitive-toolkit/BrainScript-Full-Function-Reference\n"
	+ "https://docs.microsoft.com/en-us/cognitive-toolkit/BrainScript-Layers-Reference\n"
	+ "https://docs.microsoft.com/en-us/cognitive-toolkit/BrainScript-SGD-Block";
  }
}
