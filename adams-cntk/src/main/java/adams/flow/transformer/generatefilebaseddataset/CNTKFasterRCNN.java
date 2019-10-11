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
 * CNTKFasterRCNN.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.generatefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.data.RoundingType;
import adams.data.SharedStringsTable;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.transformer.subimages.Grid;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.objectfilter.Scale;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.Report;
import adams.flow.container.FileBasedDatasetContainer;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a CNTK dataset for Faster-RCNN in the specified directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKFasterRCNN
  extends AbstractFileBasedDatasetGeneration<String> {

  private static final long serialVersionUID = 1428730724419661949L;

  /** train sub-dir. */
  public final static String SUB_DIR_TRAIN = "train";

  /** test sub-dir. */
  public final static String SUB_DIR_TEST = "test";

  /** train sub-dir. */
  public final static String SUB_DIR_NEGATIVE = "negative";

  /** the sub-directories. */
  public final static String[] SUB_DIRS = {
    SUB_DIR_TRAIN,
    SUB_DIR_TEST,
    SUB_DIR_NEGATIVE,
  };

  public static final String BACKGROUND = "__background__";

  public static final String CLASS_MAP = "class_map.txt";

  /** the output directory for the dataset. */
  protected PlaceholderDirectory m_OutputDir;

  /** the image reader for the train/test images. */
  protected AbstractImageReader m_ImageReader;

  /** the regular expression to identify images to enlarge. */
  protected BaseRegExp m_RegExpEnlarge;

  /** the object finder to use. */
  protected ObjectFinder m_ObjectFinder;

  /** the label translations. */
  protected BaseKeyValuePair[] m_LabelTranslations;

  /** whether to output object location spreadsheet. */
  protected boolean m_OutputObjectLocations;

  /** whether to make use of annotations for the negative images. */
  protected boolean m_UseNegativeAnnotations;

  /** the image reader for the negative images. */
  protected AbstractImageReader m_NegativeImageReader;

  /** the object finder to use for the negative images. */
  protected ObjectFinder m_NegativeObjectFinder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a CNTK dataset for Faster-RCNN in the specified directory.\n"
      + "Expects reports with annotations to be present with the same name (but with .report extension).\n"
      + "Via the 'regExpEnlarge' expression, iamges can be identified that should be split into 2x2 grid "
      + "and blown up to original size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-dir", "outputDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "regexp-enlarge", "regExpEnlarge",
      new BaseRegExp(".*-2x2.*"));

    m_OptionManager.add(
      "image-reader", "imageReader",
      new JAIImageReader());

    m_OptionManager.add(
      "object-finder", "objectFinder",
      new AllFinder());

    m_OptionManager.add(
      "label-translation", "labelTranslations",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "output-object-locations", "outputObjectLocations",
      false);

    m_OptionManager.add(
      "use-negative-annotations", "useNegativeAnnotations",
      false);

    m_OptionManager.add(
      "negative-image-reader", "negativeImageReader",
      new JAIImageReader());

    m_OptionManager.add(
      "negative-object-finder", "negativeObjectFinder",
      new AllFinder());
  }

  /**
   * Sets the output directory.
   *
   * @param value	the directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the output directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The output directory for the generated dataset.";
  }

  /**
   * Sets the regular expression for identifying train/test images that need
   * to get enlarged (2x2 grid).
   *
   * @param value	the directory
   */
  public void setRegExpEnlarge(BaseRegExp value) {
    m_RegExpEnlarge = value;
    reset();
  }

  /**
   * Returns the regular expression for identifying train/test images that need
   * to get enlarged (2x2 grid).
   *
   * @return		the expression
   */
  public BaseRegExp getRegExpEnlarge() {
    return m_RegExpEnlarge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpEnlargeTipText() {
    return "The regular expression for identifying train/test images that need to get enlarged (2x2 grid).";
  }

  /**
   * Sets the reader for the train/test images.
   *
   * @param value	the reader
   */
  public void setImageReader(AbstractImageReader value) {
    m_ImageReader = value;
    reset();
  }

  /**
   * Returns the reader for the train/test images.
   *
   * @return		the reader
   */
  public AbstractImageReader getImageReader() {
    return m_ImageReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageReaderTipText() {
    return "The image reader to use for the train/test images.";
  }

  /**
   * Sets the object finder to use for the train/test annotations.
   *
   * @param value	the finder
   */
  public void setObjectFinder(ObjectFinder value) {
    m_ObjectFinder = value;
    reset();
  }

  /**
   * Returns the object finder to use for the train/test annotations.
   *
   * @return		the finder
   */
  public ObjectFinder getObjectFinder() {
    return m_ObjectFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectFinderTipText() {
    return "The object finder to use for the train/test annotations.";
  }

  /**
   * Sets the label translations to apply.
   *
   * @param value	the translations
   */
  public void setLabelTranslations(BaseKeyValuePair[] value) {
    m_LabelTranslations = value;
    reset();
  }

  /**
   * Returns the label translations to apply.
   *
   * @return		the translations
   */
  public BaseKeyValuePair[] getLabelTranslations() {
    return m_LabelTranslations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelTranslationsTipText() {
    return "For translation labels: key=to find, value=replacement.";
  }

  /**
   * Sets whether to output the objects loactions and their associated
   * label and file in a spreadsheet.
   *
   * @param value	true if to use annotations
   */
  public void setOutputObjectLocations(boolean value) {
    m_OutputObjectLocations = value;
    reset();
  }

  /**
   * Returns whether to output the objects loactions and their associated
   * label and file in a spreadsheet.
   *
   * @return		true if to output
   */
  public boolean getOutputObjectLocations() {
    return m_OutputObjectLocations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputObjectLocationsTipText() {
    return "If enabled, the object locations and their associated label and "
      + "file are output in a spreadsheet for training/test set.";
  }

  /**
   * Sets whether to make use of the annotations of the negative images,
   * extracting them as actual negative images, or just copying the images
   * as is.
   *
   * @param value	true if to use annotations
   */
  public void setUseNegativeAnnotations(boolean value) {
    m_UseNegativeAnnotations = value;
    reset();
  }

  /**
   * Returns whether to make use of the annotations of the negative images,
   * extracting them as actual negative images, or just copying the images
   * as is.
   *
   * @return		true if to use annotations
   */
  public boolean getUseNegativeAnnotations() {
    return m_UseNegativeAnnotations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useNegativeAnnotationsTipText() {
    return "If enabled, the located annotations of the negative images get "
      + "extracted as actual negative images; if disabled, just copies the "
      + "images as is into the output directory.";
  }

  /**
   * Sets the reader for the negative images.
   *
   * @param value	the reader
   */
  public void setNegativeImageReader(AbstractImageReader value) {
    m_NegativeImageReader = value;
    reset();
  }

  /**
   * Returns the reader for the negative images.
   *
   * @return		the reader
   */
  public AbstractImageReader getNegativeImageReader() {
    return m_NegativeImageReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String negativeImageReaderTipText() {
    return "The image reader to use for the negative images.";
  }

  /**
   * Sets the object finder to use for the negative annotations.
   *
   * @param value	the finder
   */
  public void setNegativeObjectFinder(ObjectFinder value) {
    m_NegativeObjectFinder = value;
    reset();
  }

  /**
   * Returns the object finder to use for the negative annotations.
   *
   * @return		the finder
   */
  public ObjectFinder getNegativeObjectFinder() {
    return m_NegativeObjectFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String negativeObjectFinderTipText() {
    return "The object finder to use for the negative annotations.";
  }

  /**
   * Returns the class that gets generated.
   *
   * @return		the generated class
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * The keys of the values that need to be present in the container.
   *
   * @return		the keys
   */
  @Override
  protected String[] requiredValues() {
    return new String[]{
      FileBasedDatasetContainer.VALUE_TRAIN,
      FileBasedDatasetContainer.VALUE_TEST,
      FileBasedDatasetContainer.VALUE_NEGATIVE,
    };
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "objectFinder", m_ObjectFinder, ", finder: ");
    result += QuickInfoHelper.toString(this, "outputDir", m_OutputDir, ", output: ");
    if (m_UseNegativeAnnotations) {
      result += QuickInfoHelper.toString(this, "negativeImageReader", m_NegativeImageReader, ", neg image reader: ");
      result += QuickInfoHelper.toString(this, "negativeObjectFinder", m_NegativeObjectFinder, ", neg finder: ");
    }

    return result;
  }

  /**
   * Performs checks on the container.
   *
   * @param cont	the container to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(FileBasedDatasetContainer cont) {
    String	result;
    String[]	files;

    result = super.check(cont);

    if (result == null) {
      if (m_OutputDir.exists()) {
        if (!m_OutputDir.isDirectory()) {
	  result = "Already exists, but isn't a directory: " + m_OutputDir;
	}
        else {
          files = m_OutputDir.list();
          if (files != null) {
            for (String file: files) {
              if (file.equals(".") || file.equals(".."))
                continue;
              result = "Output directory is non-empty: " + m_OutputDir;
              break;
	    }
	  }
	}
      }
    }

    return result;
  }

  /**
   * Translates the label using the translation matrix.
   *
   * @param label	the label to translate
   * @return		the (potentially) translated label
   */
  protected String translateLabel(String label) {
    String	result;

    result = label;

    if (m_LabelTranslations.length > 0) {
      for (BaseKeyValuePair translation: m_LabelTranslations) {
        if (translation.getPairKey().equals(result))
          result = translation.getPairValue();
      }
    }

    return result;
  }

  /**
   * Generates the directory structure.
   *
   * @return		null if successful, otherwise error message
   */
  protected String createDirStructure() {
    String			result;
    PlaceholderDirectory	dir;

    result = null;

    for (String subdir: SUB_DIRS) {
      dir = new PlaceholderDirectory(m_OutputDir.getAbsolutePath() + File.separator + subdir);
      if (!dir.mkdirs()) {
        result = "Failed to create sub-directory: " + dir;
        break;
      }
    }

    return result;
  }

  /**
   * Reads the report file associated with the image.
   *
   * @param file	the image filename
   * @return		the report, null if failed to read
   */
  protected Report readAssociatedReport(String file) {
    Report			result;
    String[]			reportFiles;
    DefaultSimpleReportReader	reader;
    List<Report> 		reports;

    result = null;
    reader = new DefaultSimpleReportReader();
    reportFiles = new String[]{
      FileUtils.replaceExtension(file, ".report"),
      FileUtils.replaceExtension(file, ".report.gz"),
    };
    for (String reportFile: reportFiles) {
      if (FileUtils.fileExists(reportFile)) {
	reader.setInput(new PlaceholderFile(reportFile));
	reports = reader.read();
	if ((reports != null) && (reports.size() > 0))
	  result = reports.get(0);
	break;
      }
    }

    return result;
  }

  /**
   * Splits the image into a 2x2 grid and blows up the individual images
   * to the original size. Object locations get adjusted accordingly.
   *
   * @param imgCont	the image to process
   * @return		the generated
   */
  protected BufferedImageContainer[] enlarge(AbstractImageContainer imgCont) {
    List<BufferedImageContainer>	result;
    Grid				grid;
    List<BufferedImageContainer>	subImages;
    ParameterBlock 			params;
    RenderedOp 				imNew;
    BufferedImageContainer		contNew;
    Scale				scale;
    LocatedObjects			objects;

    result = new ArrayList<>();

    // split
    grid = new Grid();
    grid.setNumCols(2);
    grid.setNumRows(2);
    grid.setPartial(false);
    grid.setFixInvalid(true);
    grid.setPrefix(m_ObjectFinder.getPrefix());
    subImages = grid.process(BufferedImageHelper.toBufferedImageContainer(imgCont));

    for (BufferedImageContainer subImage: subImages) {
      // scale image
      params = new ParameterBlock();
      params.addSource(subImage.toBufferedImage());
      params.add(2.0F);
      params.add(2.0F);
      params.add(0.0F);  // x translate
      params.add(0.0F);  // y translate
      params.add(new InterpolationNearest());
      imNew = JAI.create("scale", params);
      contNew = new BufferedImageContainer();
      contNew.setContent(imNew.getAsBufferedImage());

      // scale annotations
      objects = LocatedObjects.fromReport(subImage.getReport(), m_ObjectFinder.getPrefix());
      scale = new Scale();
      scale.setScaleX(2.0);
      scale.setScaleY(2.0);
      scale.setRoundingType(RoundingType.ROUND);
      objects = scale.filter(objects);
      contNew.setReport(objects.toReport(m_ObjectFinder.getPrefix()));

      result.add(contNew);
    }

    return result.toArray(new BufferedImageContainer[0]);
  }

  /**
   * Outputs the annotations.
   *
   * @param cont	the file container
   * @param train 	whether to output train or test
   * @param labels 	for recording labels
   * @return		null if successful, otherwise error message
   */
  protected String processAnnotations(FileBasedDatasetContainer cont, boolean train, SharedStringsTable labels) {
    String				result;
    String[]				files;
    PlaceholderDirectory		outDir;
    int					i;
    PlaceholderFile			file;
    PlaceholderFile			subfile;
    Report				report;
    LocatedObjects			objects;
    List<String> 			imgList;
    List<String> 			roiList;
    List<String>			objList;
    StringBuilder			rois;
    AbstractImageContainer		imgCont;
    boolean				enlarge;
    BufferedImageContainer[] 		imgLarge;
    int					n;
    int					imgIndex;

    result  = null;
    files   = cont.getValue(train ? FileBasedDatasetContainer.VALUE_TRAIN : FileBasedDatasetContainer.VALUE_TEST, String[].class);
    outDir  = new PlaceholderDirectory(m_OutputDir.getAbsolutePath() + File.separator + (train ? SUB_DIR_TRAIN : SUB_DIR_TEST));
    imgList = new ArrayList<>();
    roiList = new ArrayList<>();
    objList = new ArrayList<>();
    objList.add("file,x0,y0,x1,y1,label");

    imgIndex = 0;
    for (i = 0; i < files.length; i++) {
      file    = new PlaceholderFile(files[i]);
      enlarge = m_RegExpEnlarge.isMatch(files[i]);
      try {
        // load report
	report = readAssociatedReport(file.getAbsolutePath());
	if (report == null) {
	  result = "Failed to read associated report for " + (train ? "training" : "test") + " image: " + file;
	  break;
	}

	// load image
	imgCont = m_ImageReader.read(file);
	if (imgCont == null) {
	  result = "Failed to read " + (train ? "training" : "test") + " image: " + file;
	  break;
	}
	imgCont.getReport().mergeWith(report);

	if (enlarge) {
	  imgLarge = enlarge(imgCont);
	  for (n = 0; n < imgLarge.length; n++) {
	    subfile = new PlaceholderFile(outDir.getAbsolutePath() + File.separator + FileUtils.replaceExtension(file.getName(), "-" + (n+1) + ".png"));
	    // store image
	    result = BufferedImageHelper.write(imgLarge[n].toBufferedImage(), subfile);
	    if (result != null)
	      break;
	    // generate annotations
	    rois    = new StringBuilder();
	    objects = m_ObjectFinder.findObjects(imgLarge[n].getReport());
	    if (objects.size() > 0) {
	      for (LocatedObject object : objects) {
		object.makeFit(imgLarge[n].getWidth(), imgLarge[n].getHeight());
		// roi
		rois.append(" " + object.getX());
		rois.append(" " + object.getY());
		rois.append(" " + (object.getX() + object.getWidth() - 1));
		rois.append(" " + (object.getY() + object.getHeight() - 1));
		rois.append(" " + labels.getIndex(translateLabel("" + object.getMetaData().get("type"))));
		// location
		if (m_OutputObjectLocations) {
		  objList.add(
		    FileUtils.replaceExtension(subfile.getName(), "") + ","
		      + object.getX() + ","
		      + object.getY() + ","
		      + (object.getX() + object.getWidth() - 1) + ","
		      + (object.getY() + object.getHeight() - 1) + ","
		      + translateLabel("" + object.getMetaData().get("type")));
		}
	      }
	    }
	    imgList.add(imgIndex + "\t" + (train ? SUB_DIR_TRAIN : SUB_DIR_TEST) + "/" + subfile.getName() + "\t" + "0");
	    roiList.add(imgIndex + " |roiAndLabel " + rois);
	    imgIndex++;
	  }
	  if (result != null)
	    break;
	}
	else {
	  // store image
	  if (!FileUtils.copy(file, outDir)) {
	    result = "Failed to copy image '" + file + "' to: " + outDir;
	    break;
	  }

	  // generate annotations
	  rois    = new StringBuilder();
	  objects = m_ObjectFinder.findObjects(imgCont.getReport());
	  if (objects.size() > 0) {
	    for (LocatedObject object : objects) {
	      object.makeFit(imgCont.getWidth(), imgCont.getHeight());
	      // roi
	      rois.append(" " + object.getX());
	      rois.append(" " + object.getY());
	      rois.append(" " + (object.getX() + object.getWidth() - 1));
	      rois.append(" " + (object.getY() + object.getHeight() - 1));
	      rois.append(" " + labels.getIndex(translateLabel("" + object.getMetaData().get("type"))));
	      // location
	      if (m_OutputObjectLocations) {
		objList.add(
		  FileUtils.replaceExtension(file.getName(), "") + ","
		    + object.getX() + ","
		    + object.getY() + ","
		    + (object.getX() + object.getWidth() - 1) + ","
		    + (object.getY() + object.getHeight() - 1) + ","
		    + translateLabel("" + object.getMetaData().get("type")));
	      }
	    }
	  }
	  imgList.add(imgIndex + "\t" + (train ? SUB_DIR_TRAIN : SUB_DIR_TEST) + "/" + file.getName() + "\t" + "0");
	  roiList.add(imgIndex + " |roiAndLabel " + rois);
	  imgIndex++;
	}
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to process " + (train ? "training" : "test") + " '" + file + "!", e);
      }
    }

    if (result == null) {
      result = FileUtils.saveToFileMsg(imgList, new File(m_OutputDir.getAbsolutePath() + File.separator + (train ? "train" : "test") + "_img_file.txt"), null);
      if (result == null)
	result = FileUtils.saveToFileMsg(roiList, new File(m_OutputDir.getAbsolutePath() + File.separator + (train ? "train" : "test") + "_roi_file.txt"), null);
      if ((result == null) && m_OutputObjectLocations)
	result = FileUtils.saveToFileMsg(objList, new File(m_OutputDir.getAbsolutePath() + File.separator + (train ? "train" : "test") + ".csv"), null);
    }

    return result;
  }

  /**
   * Outputs the annotations.
   *
   * @param cont	the file container
   * @return		null if successful, otherwise error message
   */
  protected String processNegative(FileBasedDatasetContainer cont) {
    String			result;
    String[]			files;
    PlaceholderDirectory	outDir;
    PlaceholderFile		file;
    AbstractImageContainer 	imgCont;
    BufferedImage 		img;
    BufferedImage		subImg;
    String			subFile;
    Report			report;
    LocatedObjects		objects;
    int				count;

    result = null;

    files = cont.getValue(FileBasedDatasetContainer.VALUE_NEGATIVE, String[].class);
    outDir = new PlaceholderDirectory(m_OutputDir.getAbsolutePath() + File.separator + SUB_DIR_NEGATIVE);

    if (m_UseNegativeAnnotations) {
      for (String fileStr: files) {
        file = new PlaceholderFile(fileStr);
	try {
	  report = readAssociatedReport(fileStr);
	  if (report == null) {
	    result = "Failed to read associated report for negative image: " + file;
	    break;
	  }
	  imgCont = m_NegativeImageReader.read(file);
	  if (imgCont == null) {
	    result = "Failed to read negative image: " + file;
	    break;
	  }
	  objects = m_NegativeObjectFinder.findObjects(report);
	  if (objects.size() > 0) {
	    img   = imgCont.toBufferedImage();
	    count = 0;
	    for (LocatedObject object: objects) {
	      count++;
	      object.makeFit(img.getWidth(), img.getHeight());
	      subImg = img.getSubimage(object.getX(), object.getY(), object.getWidth(), object.getHeight());
	      subFile = m_OutputDir.getAbsolutePath() + File.separator + SUB_DIR_NEGATIVE + File.separator + FileUtils.replaceExtension(file.getName(), "-" + count + ".png");
	      result = BufferedImageHelper.write(subImg, new PlaceholderFile(subFile));
	      if (result != null)
	        break;
	    }
	  }
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to extract negative regions from '" + file + "' to: " + outDir, e);
	}
      }
    }
    else {
      try {
        for (String fileStr: files) {
	  if (!FileUtils.copy(new PlaceholderFile(fileStr), outDir)) {
	    result = "Failed to copy '" + fileStr + "' to: " + outDir;
	    break;
	  }
	}
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to copy negative images to: " + outDir, e);
      }
    }

    return result;
  }

  /**
   * Generates the class map file.
   *
   * @param labels	the collected labels
   * @return		null if successful, otherwise error message
   */
  protected String processClassMap(SharedStringsTable labels) {
    String		result;
    List<String>	content;
    int			i;

    result = null;
    if (labels.size() == 0)
      result = "No labels collected!";

    if (result == null) {
      content = new ArrayList<>();
      for (i = 0; i < labels.size(); i++)
	content.add(labels.getString(i) + "\t" + i);
      // write
      result = FileUtils.saveToFileMsg(
        content,
	new File(m_OutputDir.getAbsolutePath() + File.separator + CLASS_MAP),
	null);
    }

    return result;
  }

  /**
   * Generates the dataset.
   *
   * @param cont	the container to use
   * @return		the generated output
   */
  @Override
  protected String doGenerate(FileBasedDatasetContainer cont) {
    String    		msg;
    SharedStringsTable labels;

    msg = createDirStructure();

    if (msg == null) {
      labels = new SharedStringsTable();
      labels.getIndex(BACKGROUND);
      msg = processAnnotations(cont, true, labels);
      if (msg == null)
	msg = processAnnotations(cont, false, labels);
      if (msg == null)
	msg = processNegative(cont);
      if (msg == null)
	msg = processClassMap(labels);
    }

    if (msg != null)
      throw new IllegalStateException(msg);

    return m_OutputDir.getAbsolutePath();
  }
}
