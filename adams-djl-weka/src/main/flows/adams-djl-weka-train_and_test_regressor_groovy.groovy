import adams.data.djl.networkgenerator.AbstractScript
import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.basicmodelzoo.tabular.TabNet
import ai.djl.nn.Block

class RegressionNetworkGenerator
        extends AbstractScript {

    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    public String globalInfo() {
        return "TabNet network."
    }

    /**
     * Generates the network using the supplied dataset.
     *
     * @param dataset	the dataset to generate the network for
     * @return		the network
     */
    protected Block doGenerate(TabularDataset dataset) {
        return TabNet.builder()
                .setInputDim(dataset.getFeatureSize())
                .setOutDim(dataset.getLabelSize())
                .optNumIndependent(1)
                .optNumShared(1)
                .build()
    }
}
