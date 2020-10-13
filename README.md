# excavator
挖掘机

## start serviceProvider 

    java -jar serviceProvider/target/serviceProvider-1.0-SNAPSHOT.jar --server.port=8090 --rpc.server.port=9090 --zkConnection=127.0.0.1:2181
    
## start serviceConsumer
    java -jar serviceConsumer/target/serviceConsumer-1.0-SNAPSHOT.jar --server.port=8010

## security 

Ref: https://github.com/advisories/GHSA-6phf-73q6-gh87


