# Based on:
# https://pythonhosted.org/Pyro4/intro.html#with-a-name-server
import Pyro4
import json
import numpy as np
import traceback
from sklearn.linear_model import LinearRegression

models = {}

@Pyro4.expose
class ScikitLearnProxy(object):

    def train(self, data):
        global models

        try:
            input = json.loads(data)
            name = input["Model"]
            print("[train] Model", name)

            X = np.array(input["Train"]["X"])
            y = np.array(input["Train"]["y"])
            print("[train] X", X)
            print("[train] y", y)

            reg = LinearRegression().fit(X, y)
            models[name] = reg
            print("[train] models", models)

            return "OK"
        except:
            error = traceback.format_exc()
            print(error)
            return "Error: " + error

    def predict(self, data):
        global models

        try:
            input = json.loads(data)
            name = input["Model"]
            print("[predict] Model", name)

            x = np.array([input["x"]])
            print("[predict] x", x)

            pred = models[name].predict(x).tolist()
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
