# Based on:
# https://pythonhosted.org/Pyro4/intro.html#with-a-name-server
import Pyro4
import json

@Pyro4.expose
class DummyProxy(object):
    def train(self, data):
        print("train", data)

    def predict(self, data):
        print("predict", data)
        input = json.loads(data)
        name = input["names"][0]
        result = {"outputs": {name: {"class": [[3.1415]]}}}
        return json.dumps(result)

daemon = Pyro4.Daemon()
ns = Pyro4.locateNS()
uri = daemon.register(DummyProxy)
ns.register("dummy", uri)

print("Ready.")
daemon.requestLoop()
