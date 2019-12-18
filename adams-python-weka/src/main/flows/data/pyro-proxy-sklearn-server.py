# Based on:
# https://pythonhosted.org/Pyro4/intro.html#with-a-name-server
import Pyro4
import json
import numpy as np
import traceback
from sklearn.linear_model import LinearRegression

MODELS = {}
DEBUG = True

@Pyro4.expose
class ScikitLearnProxy(object):

    def train(self, data):
        global MODELS
        global DEBUG

        try:
            input = json.loads(data)
            name = input["names"][0]
            if DEBUG:
                print("[train] Model", name)

            X = np.array(input["inputs"]["input"])
            cls = []
            for row in input["inputs"]["class"]:
                cls.append(row[0])
            y = np.array(cls)

            reg = LinearRegression().fit(X, y)
            MODELS[name] = reg
            if DEBUG:
                print("[train] models", MODELS)

            return "OK"
        except:
            error = traceback.format_exc()
            print(error)
            return "Error: " + error

    def predict(self, data):
        global MODELS
        global DEBUG

        try:
            input = json.loads(data)
            name = input["names"][0]
            if DEBUG:
                print("[predict] Model", name)

            pred = []
            for row in input["inputs"]["input"]:
                x = np.array([row])
                npp = MODELS[name].predict(x)
                p = npp.tolist()
                pred.append(p)
            if DEBUG:
                print("[predict] pred", pred)
            result = {"outputs": {name: {"class": pred}}}
        except:
            error = traceback.format_exc()
            result = {"error": error}

        return json.dumps(result)

daemon = Pyro4.Daemon()
ns = Pyro4.locateNS(host="localhost", port=9090)
uri = daemon.register(ScikitLearnProxy)
ns.register("sklearn", uri)

print("Ready.")
daemon.requestLoop()
