# broker.py
import zmq

def main():
    ctx = zmq.Context.instance()

    # Publishers connect here
    xsub = ctx.socket(zmq.XSUB)
    xsub.bind("tcp://127.0.0.1:5555")

    # Subscribers connect here
    xpub = ctx.socket(zmq.XPUB)
    xpub.bind("tcp://127.0.0.1:5556")
    
    print("Broker running:")
    print("  PUB -> tcp://127.0.0.1:5555")
    print("  SUB -> tcp://127.0.0.1:5556")
    zmq.proxy(xsub, xpub)

if __name__ == "__main__":
    main()
