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
 * RatsTextHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import java.io.StringWriter;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;

import adams.core.Utils;
import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.data.text.TextContainer;
import adams.flow.webservice.WebserviceUtils;

/**
 * Helper class for converting text.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2085 $
 */
public class RatsTextHelper {

  /**
   * Converts a {@link TextContainer} into a Webservice Text objecct.
   * 
   * @param input	the {@link TextContainer}
   * @return		the Webservice Text object
   */
  public static nz.ac.waikato.adams.webservice.rats.text.Text containerToWebservice(TextContainer input) {
    nz.ac.waikato.adams.webservice.rats.text.Text	result;
    nz.ac.waikato.adams.webservice.rats.text.Properties	props;
    nz.ac.waikato.adams.webservice.rats.text.Property	prop;
    adams.data.report.Report				report;
    
    result = new nz.ac.waikato.adams.webservice.rats.text.Text();
    
    // text
    result.setData(new DataHandler(new ByteArrayDataSource(input.getContent().getBytes(), WebserviceUtils.MIMETYPE_APPLICATION_OCTETSTREAM)));
    
    // report
    props = new nz.ac.waikato.adams.webservice.rats.text.Properties();
    if (input.hasReport()) {
      report = input.getReport();
      for (AbstractField field: report.getFields()) {
	prop = new nz.ac.waikato.adams.webservice.rats.text.Property();
	prop.setKey(field.getName());
	prop.setType(nz.ac.waikato.adams.webservice.rats.text.DataType.valueOf(field.getDataType().toRaw()));
	prop.setValue("" + report.getValue(field));
	props.getProp().add(prop);
      }
    }
    
    result.setProps(props);
    
    return result;
  }

  /**
   * Converts a Webservice Text object into a {@link TextContainer}.
   * 
   * @param input	the Text object
   * @return		the {@link TextContainer}
   */
  public static TextContainer webserviceToContainer(nz.ac.waikato.adams.webservice.rats.text.Text input) {
    TextContainer		result;
    adams.data.report.Report	report;
    Field			field;
    StringWriter 		writer;
    
    result = new TextContainer();
    
    // text
    writer = new StringWriter();
    try {
      IOUtils.copy(input.getData().getInputStream(), writer);
      result.setContent(writer.toString());
    }
    catch (Exception e) {
      result.getNotes().addError(RatsTextHelper.class, Utils.throwableToString(e));
    }
    
    // report
    report = new adams.data.report.Report();
    if (input.getProps() != null) {
      for (nz.ac.waikato.adams.webservice.rats.text.Property prop: input.getProps().getProp()) {
	field = new Field(prop.getKey(), adams.data.report.DataType.valueOf(prop.getType().toString()));
	report.addField(field);
	report.setValue(
	    field, 
	    prop.getValue());
      }
    }
    
    result.setReport(report);
    
    return result;
  }
}
