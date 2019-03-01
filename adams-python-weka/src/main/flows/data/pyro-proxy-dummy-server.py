# Based on:
# https://pythonhosted.org/Pyro4/intro.html#with-a-name-server
import Pyro4

@Pyro4.expose
class DummyProxy(object):
    def train(self, data):
        print("train", data)

    def predict(self, data):
        print("predict", data)
        result = "{\"Prediction\": [0.314]}"
        return result

daemon = Pyro4.Daemon()
ns = Pyro4.locateNS()
uri = daemon.register(DummyProxy)
ns.register("dummy", uri)

print("Ready.")
daemon.requestLoop()
