import adams.flow.core.Actor
import adams.flow.core.AdditionalOptions
import adams.flow.core.AdditionalOptionsHandler
import ai.djl.basicdataset.tabular.TabularDataset
import ai.djl.nn.Block
import ai.djl.zero.Performance
import adams.data.djl.networkgenerator.NetworkGenerator
import adams.data.djl.networkgenerator.TabularRegressionGenerator


public class TabNetGenerator implements NetworkGenerator, AdditionalOptionsHandler {

    protected Actor m_FlowContext

    protected AdditionalOptions m_AdditionalOptions

    /**
     * Returns whether flow context is required.
     *
     * @return true if required
     */
    @Override
    boolean requiresFlowContext() {
        return false
    }

    /**
     * Sets the flow context.
     *
     * @param value	the actor
     */
    @Override
    void setFlowContext(Actor value) {
        m_FlowContext = value
    }

    /**
     * Returns the flow context, if any.
     *
     * @return the actor, null if none available
     */
    @Override
    Actor getFlowContext() {
        return m_FlowContext
    }

    /**
     * Sets the additional options.
     *
     * @param options the options (name &lt;-&gt;value relation)
     */
    @Override
    void setAdditionalOptions(AdditionalOptions options) {
        m_AdditionalOptions = options
    }

    /**
     * Returns the additional options.
     *
     * @return the options (name &lt;-&gt;value relation)
     */
    @Override
    AdditionalOptions getAdditionalOptions() {
        return m_AdditionalOptions
    }

    /**
     * Generates the network using the supplied dataset.
     *
     * @param dataset	the dataset to generate the network for
     * @return		the network
     */
    Block generate(TabularDataset dataset) {
        TabularRegressionGenerator  generator

        generator = new TabularRegressionGenerator()
        generator.setPerformance(Performance.FAST)

        return generator.generate(dataset)
    }
}