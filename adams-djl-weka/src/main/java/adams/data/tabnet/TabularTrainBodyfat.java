package adams.data.tabnet;

import ai.djl.basicdataset.tabular.ListFeatures;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.zero.Performance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.djl.InstancesDataset;

import java.nio.file.Path;

public class TabularTrainBodyfat {

  public static void main(String[] args) throws Exception {
    Instances instances = DataSource.read("src/main/flows/data/bodyfat.arff");
    InstancesDataset dataset = InstancesDataset.builder()
			    .data(instances)
			    .setSampling(32, true)
			    .classIsLast()
			    .addAllFeatures()
			    .build();
    dataset.prepare();
    dataset.toJson(Path.of("src/main/flows/output/bodyfat.json"));
    System.out.println(dataset.getData().relationName());
    // required at least 3 input features?
    ZooModel<ListFeatures, Float> model = TabularRegression.train(dataset, Performance.ACCURATE);
    System.out.println(model);
    model.save(Path.of("src/main/flows/output"), "bodyfat");
    model.close();
  }
}
