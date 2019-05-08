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
 * RabbitMQLocalJobRunner.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.multiprocess.rabbitmq;

import adams.multiprocess.Job;
import adams.multiprocess.LocalJobRunner;
import adams.multiprocess.RabbitMQJobRunner;

/**
 * Dummy JobRunner for a single run used internally by {@link RabbitMQJobRunner},
 * encapsulates the original job index.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQLocalJobRunner<T extends Job>
  extends LocalJobRunner<T> {

  private static final long serialVersionUID = 3556786234167433375L;

  /** the job index. */
  protected int m_Index;

  /**
   * Initializes the job runner.
   *
   * @param index	the original job index
   */
  public RabbitMQLocalJobRunner(int index) {
    super();
    m_Index = index;
  }

  /**
   * Returns the index.
   *
   * @return		the index
   */
  public int getIndex() {
    return m_Index;
  }
}
