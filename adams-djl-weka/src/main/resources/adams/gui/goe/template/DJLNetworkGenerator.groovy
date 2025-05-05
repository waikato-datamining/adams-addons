/*
 * Template of a Groovy network generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */

import adams.data.djl.networkgenerator.AbstractScript
import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.nn.Block

class TemplateNetworkGenerator
        extends AbstractScript {

    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    public String globalInfo() {
        return "FIXME."
    }

    /**
     * Generates the network using the supplied dataset.
     *
     * @param dataset	the dataset to generate the network for
     * @return		the network
     */
    protected Block doGenerate(TabularDataset dataset) {
        // FIXME
        return null
    }
}
