To execute OrderBook application you need to specify absolute path to file.
Just find placeholder absolute_path_to_file in pom.xml and replace it by your path to file.
Then run command :
       mvn package exec:java

Also you can find integration test OrderBookApplicationTest.
I think it might be good entry point for code research.
