# Based on:
# https://pythonhosted.org/Pyro4/intro.html#with-a-name-server
import Pyro4
import json
import numpy as np
import traceback
from sklearn.linear_model import LinearRegression

MODELS = {}
DEBUG = False

@Pyro4.expose
class ScikitLearnProxy(object):

    def train(self, data):
        global MODELS

        try:
            input = json.loads(data)
            name = input["Model"]
            if DEBUG:
                print("[train] Model", name)

            X = np.array(input["Train"]["X"])
            y = np.array(input["Train"]["y"])
            if DEBUG:
                print("[train] X", X)
                print("[train] y", y)

            reg = LinearRegression().fit(X, y)
            MODELS[name] = reg
            if DEBUG:
                print("[train] models", models)

            return "OK"
        except:
            error = traceback.format_exc()
            print(error)
            return "Error: " + error

    def predict(self, data):
        global MODELS

        try:
            input = json.loads(data)
            name = input["Model"]
            if DEBUG:
                print("[predict] Model", name)

            x = np.array([input["x"]])
            if DEBUG:
                print("[predict] x", x)

            pred = MODELS[name].predict(x).tolist()
            if DEBUG:
                print("[predict] pred", pred)
            result = {"Prediction": pred}
        except:
            error = traceback.format_exc()
            result = {"Error": error}

        return json.dumps(result)

daemon = Pyro4.Daemon()
ns = Pyro4.locateNS(host="localhost", port=9090)
uri = daemon.register(ScikitLearnProxy)
ns.register("sklearn", uri)

print("Ready.")
daemon.requestLoop()
