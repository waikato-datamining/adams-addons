import Pyro4
import argparse
import json
import numpy as np
import os
import sys
import traceback
from cntk.ops import load_model

MODELS = {}
""" the models loaded from the model directory. """

METADATA = {}
""" the metadata for the models loaded from the model directory. """


@Pyro4.expose
class CntkProxy(object):
    """
    Pyro4 proxy class for CNTK models.
    To be used in conjunction with:
    - weka.classifiers.functions.CNTKPyroProxy
    - adams.data.cntkpyroproxy.JsonAttributeBlocksCommunicationProcessor
    """

    def predict(self, data):
        """
        Performs a prediction using the model specified in the data.

        :param data: the JSON data, containing blocked data and model name
        :type: type: str
        :return: the JSON object with the predictions or error message
        :rtype: str
        """

        global MODELS
        global METADATA

        try:
            input = json.loads(data)
            name = input["Model"]
            blocks = input["Blocks"]

            # ensure that model/metadata is known
            if name not in MODELS:
                raise Exception("Unknown model: %s" % name)
            model = MODELS[name]
            if name not in METADATA:
                raise Exception("No meta-data for model: %s" % name)
            inputs = METADATA[name]["Inputs"]

            # check whether all blocks are known to model
            for block in blocks:
                if block not in inputs:
                    raise Exception("Model %s does not know block %s" % (name, block))

            # assemble input data
            inputdata = {}
            for i, inputname in enumerate(inputs):
                array = [float(x) for x in blocks[inputname].split(",")]
                inputdata[model.arguments[i]] = np.array(array)

            # make prediction
            prediction = model.eval(inputdata)[0][0]
            result = {"Prediction": [float(prediction)]}
        except:
            error = traceback.format_exc()
            result = {"Error": error}

        return json.dumps(result)


def load_models(model_dir):
    """
    Loads the models from the model directory.

    :param model_dir: the model directory
    :type model_dir: str
    """

    global MODELS
    global METADATA

    print("Loading models from: %s" % model_dir)
    for f in os.listdir(model_dir):
        if not f.endswith(".model"):
            continue
        n = f.replace(".model", "")
        j = n + ".json"
        if not os.path.exists(os.path.join(model_dir, j)):
            continue
        try:
            model = load_model(os.path.join(model_dir, f))
            with open(os.path.join(model_dir, j), "r") as jf:
                meta = json.load(jf)
            MODELS[n] = model
            METADATA[n] = meta
            print("Loaded: %s" % n)
        except:
            print("Failed to load: %s" % n)
            print(traceback.format_exc())


def main(args):
    parser = argparse.ArgumentParser(
        description='Makes CNTK models available via their filename across the network through Pyro4.')
    parser.add_argument("--ns_host", dest="ns_host", metavar="address", help="Pyro4 nameserver to connect to", default="localhost")
    parser.add_argument("--ns_port", dest="ns_port", metavar="port", help="Port of Pyro4 nameserver", default=9090)
    parser.add_argument("--model_dir", dest="model_dir", metavar="dir", help="directory with CNTK models", default=".")
    parsed = parser.parse_args(args)

    if not os.path.exists(parsed.model_dir):
        raise Exception("Model dir does not exist: %s" % parsed.model_dir)
    load_models(parsed.model_dir)

    daemon = Pyro4.Daemon()
    print("Connecting to nameserver: %s:%i" % (parsed.ns_host, parsed.ns_port))
    ns = Pyro4.locateNS(host=parsed.ns_host, port=parsed.ns_port)

    print("Registering...")
    uri = daemon.register(CntkProxy)
    ns.register("cntk", uri)

    print("Ready.")
    daemon.requestLoop()


if __name__ == '__main__':
    main(sys.argv[1:])
