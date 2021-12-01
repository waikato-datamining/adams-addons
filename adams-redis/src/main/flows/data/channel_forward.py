import redis
import sys

channel_in = "in"
channel_out = "out"
r = None
p = None


def custom_handler(message):
    print(message)
    r.publish(channel_out, message['data'].decode() + "-processed")


if __name__ == '__main__':
    if len(sys.argv) >= 2:
        channel_in = sys.argv[1]
    if len(sys.argv) >= 3:
        channel_out = sys.argv[2]
    print("Listen on: %s" % channel_in)
    print("Publish on: %s" % channel_out)
    r = redis.Redis(host='localhost', port=6379, db=0)
    p = r.pubsub()
    p.psubscribe(**{channel_in:custom_handler})
    p.run_in_thread(sleep_time=0.001)