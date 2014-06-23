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
 * RatsBlobHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import gnu.trove.list.array.TByteArrayList;

import java.io.InputStream;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import adams.core.Utils;
import adams.data.blob.BlobContainer;
import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.flow.webservice.WebserviceUtils;

/**
 * Helper class for converting blobs.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2085 $
 */
public class RatsBlobHelper {

  /**
   * Converts a {@link BlobContainer} into a Webservice Blob objecct.
   * 
   * @param input	the {@link BlobContainer}
   * @return		the Webservice Blob object
   */
  public static nz.ac.waikato.adams.webservice.rats.blob.Blob containerToWebservice(BlobContainer input) {
    nz.ac.waikato.adams.webservice.rats.blob.Blob	result;
    nz.ac.waikato.adams.webservice.rats.blob.Properties	props;
    nz.ac.waikato.adams.webservice.rats.blob.Property	prop;
    adams.data.report.Report				report;
    
    result = new nz.ac.waikato.adams.webservice.rats.blob.Blob();
    
    // blob
    result.setData(new DataHandler(new ByteArrayDataSource(input.getContent(), WebserviceUtils.MIMETYPE_APPLICATION_OCTETSTREAM)));
    
    // report
    props = new nz.ac.waikato.adams.webservice.rats.blob.Properties();
    if (input.hasReport()) {
      report = input.getReport();
      for (AbstractField field: report.getFields()) {
	prop = new nz.ac.waikato.adams.webservice.rats.blob.Property();
	prop.setKey(field.getName());
	prop.setType(nz.ac.waikato.adams.webservice.rats.blob.DataType.valueOf(field.getDataType().toRaw()));
	prop.setValue("" + report.getValue(field));
	props.getProp().add(prop);
      }
    }
    
    result.setProps(props);
    
    return result;
  }

  /**
   * Converts a Webservice Blob object into a {@link BlobContainer}.
   * 
   * @param input	the Blob object
   * @return		the {@link BlobContainer}
   */
  public static BlobContainer webserviceToContainer(nz.ac.waikato.adams.webservice.rats.blob.Blob input) {
    BlobContainer		result;
    adams.data.report.Report	report;
    Field			field;
    TByteArrayList		bytes;
    InputStream			in;
    int				read;
    
    result = new BlobContainer();
    
    // blob
    bytes = new TByteArrayList();
    try {
      in = input.getData().getInputStream();
      while ((read = in.read()) != -1)
	bytes.add((byte) read);
      result.setContent(bytes.toArray());
    }
    catch (Exception e) {
      result.getNotes().addError(RatsBlobHelper.class, Utils.throwableToString(e));
    }
    
    // report
    report = new adams.data.report.Report();
    for (nz.ac.waikato.adams.webservice.rats.blob.Property prop: input.getProps().getProp()) {
      field = new Field(prop.getKey(), adams.data.report.DataType.valueOf(prop.getType().toString()));
      report.addField(field);
      report.setValue(
	  field, 
	  prop.getValue());
    }
    
    result.setReport(report);
    
    return result;
  }
}
