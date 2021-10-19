Had a some technical challenges related to compiling and executing binaries from the terminal. Once I got all the binaries downloaded and paths sorted out it worked. Below is a short summary of how I got it working.

From packages' parent folder
```
export CP=.:amqp-client-5.13.1.jar:slf4j-api-1.7.9.jar:slf4j-simple-1.7.9.jar
```
Compile
```
javac -cp $CP <package>/*.java
```
Run
```
java -cp $CP <package>/<class>
```
- **Work Queues**, each task is delivered to exactly one worker.
- **Publish/Subscribe**, a message is deliver to multiple consumers.

## Link to code

- [Experiment 2: Hello World](RabbitMQ/src/main/java/experiment2)
- [Experiment 3: Work Queues](RabbitMQ/src/main/java/experiment3)
- [Experiment 4: Publish Subscribe](RabbitMQ/src/main/java/experiment4)
