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
 * CoNLLHelper.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for
 * <a href="http://ilk.uvt.nl/conll/#dataformat" target="_blank">CoNLL</a>
 * format related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CoNLLHelper {

  /** the CoNLL headers. */
  public final static String[] HEADERS = new String[]{
    "ID", "FORM", "LEMMA", "CPOSTAG", "POSTAG", "FEATS", "HEAD", "DEPREL", "PHEAD", "PDEPREL"
  };

  /**
   * Parses the given string in CoNLL format (one token per line).
   *
   * @param content	the content with the tokens
   * @return		the generated spreadsheet
   */
  public static SpreadSheet parse(String content) {
    SpreadSheet 		result;
    CsvSpreadSheetReader	reader;
    StringReader 		sreader;

    reader  = new CsvSpreadSheetReader();
    reader.setMissingValue(new BaseRegExp(""));
    reader.setSeparator("\\t");
    reader.setNoHeader(true);
    reader.setCustomColumnHeaders(Utils.flatten(HEADERS, ","));
    sreader = new StringReader(content);
    result = reader.read(sreader);

    return result;
  }

  /**
   * Groups the lines read from a file containing one or more CoNLL token
   * sequences into separate groups, represented by a single string.
   *
   * @param lines	the lines read from file
   * @return		the groups
   */
  public static List<String> group(List<String> lines) {
    List<String>	result;
    int			i;
    StringBuilder	content;

    result  = new ArrayList<>();
    content = new StringBuilder();
    for (i = 0; i < lines.size(); i++) {
      if (lines.get(i).isEmpty()) {
	if (content.length() > 0) {
	  result.add(content.toString());
	  content = new StringBuilder();
	}
      }
      else {
	if (content.length() > 0)
	  content.append("\n");
	content.append(lines.get(i));
      }
    }
    if (content.length() > 0)
      result.add(content.toString());

    return result;
  }
}
