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

/**
 * OutgoingFileBasedCallback.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;

/**
 * Callback class for outgoing logging.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutgoingFileBasedCallback
  extends AbstractOutgoingCallback {

  /** the file to write the collected data to. */
  protected File m_File;

  /**
   * Initializes the callback.
   * 
   * @param file	the file to write the data to
   * @param msg		the message to process
   * @param os		the output stream
   */
  public OutgoingFileBasedCallback(final File file, final Message msg, final OutputStream os) {
    super(msg, os);
    m_File = file;
  }

  /**
   * Outputs the buffer using its logger.
   * 
   * @param buffer	the buffer with the collected data
   */
  @Override
  protected void write(LoggingMessage buffer) {
    BufferedWriter	writer;
    
    try {
      writer = new BufferedWriter(new FileWriter(m_File.getAbsoluteFile(), true));
      writer.write(buffer.toString());
      writer.newLine();
      writer.flush();
      writer.close();
    }
    catch (Exception e) {
      System.err.println("Failed to write message data to: " + m_File);
      e.printStackTrace();
    }
  }
}