# Based on:
# https://pythonhosted.org/Pyro4/intro.html#with-a-name-server
import Pyro4

@Pyro4.expose
class CntkProxy(object):
    def predict(self, data):
        result = "[0.314]"
        return result

daemon = Pyro4.Daemon()
ns = Pyro4.locateNS()
uri = daemon.register(CntkProxy)
ns.register("cntk", uri)

print("Ready.")
daemon.requestLoop()
