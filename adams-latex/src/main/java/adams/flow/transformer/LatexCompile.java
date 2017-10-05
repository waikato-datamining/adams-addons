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
 * LatexCompile.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.management.ProcessUtils;
import adams.doc.latex.LatexHelper;
import adams.flow.core.ActorUtils;
import adams.flow.source.filesystemsearch.LocalFileSearch;
import adams.flow.standalone.LatexSetup;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Compiles the incoming LaTeX document (file name). Outputs the error if failed to compile.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LatexCompile
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LatexCompile
  extends AbstractTransformer {

  private static final long serialVersionUID = -2873331108505370869L;

  /** outlines need a rerun. */
  public final static String RERUN_OUTLINES = "Rerun to get outlines right";

  /** cross-references need a rerun. */
  public final static String RERUN_CROSSREF = "Rerun to get cross-references right";

  /** citations need a rerun. */
  public final static String RERUN_CITATIONS = "Rerun to get citations correct";

  /** emergency stop text in log. */
  public static final String EMERGENCY_STOP = "Emergency stop";

  /** extensions of temp files to delete before compilation (incl dot). */
  public final static String[] TMP_EXT = new String[]{
    ".aux",
    ".lof",
    ".out",
    ".toc",
    ".bbl",
  };

  /** the latex setup. */
  protected LatexSetup m_LatexSetup;

  /** for executing latex. */
  protected transient CollectingProcessOutput m_ProcessOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Compiles the incoming LaTeX document (file name). Outputs the error if failed to compile.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null)
      m_LatexSetup = (LatexSetup) ActorUtils.findClosestType(this, LatexSetup.class, true);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    String			exec;
    String			options;
    List<String>		cmdline;
    String			latex;
    String			tmp;
    String			log;
    String			bibtex;
    PlaceholderDirectory	cwd;
    LocalFileSearch		search;
    List<String> 		list;
    boolean			compiling;

    result = null;

    // files
    if (m_InputToken.getPayload() instanceof File)
      latex = ((File) m_InputToken.getPayload()).getAbsolutePath();
    else
      latex = new PlaceholderFile((String) m_InputToken.getPayload()).getAbsolutePath();
    cwd = new PlaceholderDirectory(new PlaceholderFile(latex).getParentFile());
    log = FileUtils.replaceExtension(latex, ".log");

    // executables
    if (m_LatexSetup == null) {
      exec    = LatexHelper.getBinariesDir() + File.separator + LatexHelper.getExecutable();
      bibtex  = LatexHelper.getBinariesDir() + File.separator + LatexHelper.getBibtex();
      options = LatexHelper.getExecutableOptions();
    }
    else {
      exec    = m_LatexSetup.executablePath();
      bibtex  = m_LatexSetup.bibtexPath();
      options = m_LatexSetup.getExecutableOptions();
    }

    // delete tmp files
    for (String ext: TMP_EXT) {
      tmp = FileUtils.replaceExtension(latex, ext);
      if (FileUtils.fileExists(tmp))
	FileUtils.delete(tmp);
    }

    // do we have a bibtex file?
    search = new LocalFileSearch();
    search.setDirectory(new PlaceholderDirectory(new File(latex).getParentFile()));
    search.setRegExp(new BaseRegExp(".*\\.bib"));
    search.setRecursive(false);
    try {
      list = search.search();
      for (String file: list) {
	m_ProcessOutput = ProcessUtils.execute(new String[]{bibtex, file}, cwd);
	if (!m_ProcessOutput.hasSucceeded()) {
	  result = ProcessUtils.toErrorOutput(m_ProcessOutput);
	  break;
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to check for bibtex file(s)!", e);
    }
    m_ProcessOutput = null;

    // compile latex document
    if (result == null) {
      // assemble commandline
      cmdline = new ArrayList<>();
      cmdline.add(exec);
      if (!options.isEmpty())
	cmdline.addAll(Arrays.asList(options.split(" ")));
      cmdline.add(latex);

      compiling = true;
      while (compiling) {
	try {
	  m_ProcessOutput = ProcessUtils.execute(cmdline.toArray(new String[cmdline.size()]), cwd);
	  compiling = false;
	  if (m_ProcessOutput.hasSucceeded()) {
	    if (FileUtils.fileExists(log)) {
	      list = FileUtils.loadFromFile(new PlaceholderFile(log));
	      for (String line : list) {
		if (line.contains(RERUN_CITATIONS) || line.contains(RERUN_CROSSREF) || line.contains(RERUN_OUTLINES)) {
		  compiling = true;
		}
		else if (line.contains(EMERGENCY_STOP)) {
		  compiling = false;
		  result = ProcessUtils.toErrorOutput(m_ProcessOutput);
		}
	      }
	    }
	  }
	}
	catch (Exception e) {
	  compiling = false;
	  result = handleException("Failed to compile LaTeX document: " + latex, e);
	}
	m_ProcessOutput = null;
      }
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_ProcessOutput != null)
      m_ProcessOutput.destroy();
    super.stopExecution();
  }
}
