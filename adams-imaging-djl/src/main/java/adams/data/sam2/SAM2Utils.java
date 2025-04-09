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
 * SAM2Utils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.sam2;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.modality.cv.output.Mask;
import ai.djl.modality.cv.translator.Sam2Translator.Sam2Input;
import ai.djl.modality.cv.translator.Sam2Translator.Sam2Input.Builder;
import ai.djl.modality.cv.translator.Sam2TranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.github.fracpete.javautils.struct.Struct2;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for Segment Anything 2.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SAM2Utils {

  /** the available models. */
  public final static String[] MODEL_NAMES = new String[]{"sam2-hiera-tiny", "sam2-hiera-large"};

  /**
   * Loads the model and predictor.
   *
   * @param modelName 	the name of the model to load
   * @throws Exception	if model loading fails
   * @see		#MODEL_NAMES
   * @throws Exception	if loading fails
   */
  public static Struct2<ZooModel<Sam2Input, DetectedObjects>, Predictor<Sam2Input, DetectedObjects>> loadModel(String modelName) throws Exception {
    Criteria<Sam2Input, DetectedObjects> 	criteria;
    ZooModel<Sam2Input, DetectedObjects>	model;
    Predictor<Sam2Input, DetectedObjects>	predictor;

    criteria = Criteria.builder()
		 .setTypes(Sam2Input.class, DetectedObjects.class)
		 .optModelUrls("djl://ai.djl.pytorch/" + modelName)
		 .optTranslatorFactory(new Sam2TranslatorFactory())
		 .optProgress(new ProgressBar())
		 .build();

    model     = criteria.loadModel();
    predictor = model.newPredictor();

    return new Struct2<>(model, predictor);
  }

  /**
   * Applies SAM2 to the image and points.
   *
   * @param predictor 	the predictor instance to use
   * @param img 	the image to analyze
   * @param points 	the prompt points
   * @return 		the detected objects
   * @throws Exception	if detection fails
   */
  public static DetectedObjects detectObjects(Predictor<Sam2Input, DetectedObjects> predictor, BufferedImage img, List<Point> points) throws Exception {
    Image 		image;
    Builder 		builder;
    Sam2Input 		input;

    image   = ImageFactory.getInstance().fromImage(img);
    builder = Sam2Input.builder(image);
    for (Point p: points)
      builder.addPoint((int) p.getX(), (int) p.getY());
    input = builder.build();

    return predictor.predict(input);
  }

  /**
   * Extracts the (full-image) probability distributions from the detected objects.
   *
   * @param detection	the detected objects to process
   * @param minProb	the minimum probability that detections require
   * @return		the distributions
   */
  public static List<float[][]> probabilityDistributions(DetectedObjects detection, double minProb) {
    List<float[][]>	result;
    int			i;
    DetectedObject 	detected;
    Mask		mask;

    result = new ArrayList<>();

    for (i = 0; i < detection.getNumberOfObjects(); i++) {
      detected = detection.item(i);
      if (detected.getProbability() < minProb)
	continue;
      if (detected.getBoundingBox() instanceof Mask) {
	mask = (Mask) detected.getBoundingBox();
	if (mask.isFullImageMask())
	  result.add(mask.getProbDist());
      }
    }

    return result;
  }

  /**
   * Combines the probability distribution masks into a single image segmenation layer.
   *
   * @param probDists	the distributions to combine
   * @param minProb	the probability threshold fot the masks
   * @param color	the color to use in the image
   * @return		the generated ARGB image, null if no masks provided
   */
  public static BufferedImage combineProbabilityDistributions(List<float[][]> probDists, float minProb, Color color) {
    BufferedImage 	result;
    int[]		pixels;
    int			w;
    int			h;
    int			x;
    int			y;
    int			active;
    int 		black;

    if (probDists.isEmpty())
      return null;

    h      = probDists.get(0).length;
    w      = probDists.get(0)[0].length;
    pixels = new int[w * h];
    active = color.getRGB();
    black  = new Color(0, 0, 0, 0).getRGB();
    Arrays.fill(pixels, black);

    for (float[][] probDist: probDists) {
      for (y = 0; y < h; y++) {
	for (x = 0; x < w; x++) {
	  if (probDist[y][x] >= minProb)
	    pixels[y * w + x] = active;
	}
      }
    }

    result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    result.setRGB(0, 0, w, h, pixels, 0, w);

    return result;
  }
}
