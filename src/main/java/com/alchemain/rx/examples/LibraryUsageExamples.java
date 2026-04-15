package com.alchemain.rx.examples;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
// MongoDB imports commented out - requires mongodb-driver dependency
// import com.mongodb.MongoClient;
// import com.mongodb.client.MongoCollection;
// import com.mongodb.client.MongoDatabase;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
// Lombok imports commented out - requires lombok dependency
// import lombok.Data;
// import lombok.Builder;
import redis.clients.jedis.Jedis;

/**
 * Simple examples showing how to use all the major libraries in the Reactor project.
 * This demonstrates basic usage patterns for each dependency.
 */
public class LibraryUsageExamples {
    private static final Logger log = LoggerFactory.getLogger(LibraryUsageExamples.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        LibraryUsageExamples examples = new LibraryUsageExamples();
        
        try {
            examples.demonstrateJacksonUsage();
            examples.demonstrateGuavaUsage();
            examples.demonstrateCommonsIOUsage();
            examples.demonstrateLombokUsage();
            examples.demonstrateLogging();
            examples.demonstrateMetrics();
            examples.demonstrateAkkaActors();
            // Uncomment these if you have the services running
            // examples.demonstrateRedisUsage();
            // examples.demonstrateMongoUsage();
            // examples.demonstrateElasticsearchUsage();
        } catch (Exception e) {
            log.error("Error running examples", e);
        }
    }

    /**
     * Jackson JSON processing examples
     */
    public void demonstrateJacksonUsage() throws IOException {
        log.info("=== Jackson JSON Examples ===");
        
        // Create JSON object
        ObjectNode person = mapper.createObjectNode();
        person.put("name", "John Doe");
        person.put("age", 30);
        person.put("city", "New York");
        
        // Convert to string
        String jsonString = mapper.writeValueAsString(person);
        log.info("Created JSON: {}", jsonString);
        
        // Parse JSON string
        JsonNode parsed = mapper.readTree(jsonString);
        log.info("Parsed name: {}", parsed.get("name").asText());
        log.info("Parsed age: {}", parsed.get("age").asInt());
    }

    /**
     * Google Guava utilities examples
     */
    public void demonstrateGuavaUsage() {
        log.info("=== Guava Examples ===");
        
        // String utilities
        String nullString = null;
        String safeString = Strings.nullToEmpty(nullString);
        log.info("Null to empty: '{}'", safeString);
        
        boolean isEmpty = Strings.isNullOrEmpty("");
        log.info("Is empty string null or empty: {}", isEmpty);
        
        // List utilities
        java.util.List<String> list = Lists.newArrayList("apple", "banana", "cherry");
        log.info("Created list with Guava: {}", list);
        
        // Sleep utility
        log.info("Sleeping for 1 second...");
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        log.info("Awake!");
    }

    /**
     * Apache Commons IO examples
     */
    public void demonstrateCommonsIOUsage() throws IOException {
        log.info("=== Commons IO Examples ===");
        
        // Create a temporary file
        File tempFile = File.createTempFile("example", ".txt");
        tempFile.deleteOnExit();
        
        // Write to file
        String content = "Hello, Commons IO!";
        FileUtils.writeStringToFile(tempFile, content, "UTF-8");
        log.info("Wrote to file: {}", tempFile.getAbsolutePath());
        
        // Read from file
        String readContent = FileUtils.readFileToString(tempFile, "UTF-8");
        log.info("Read from file: {}", readContent);
        
        // File size
        
                long size = tempFile.length();
        
                log.info("File size: {} bytes", size);
    }

    /**

         * Lombok examples (using @Data and @Builder annotations)

         * NOTE: Requires lombok dependency to be added to pom.xml

         */

        public void demonstrateLombokUsage() {

            log.info("=== Lombok Examples ===");

            log.warn("Lombok dependency not available - example skipped");

            

            // Using regular Java class instead

            Person person = new Person();

            person.setName("Jane Smith");

            person.setAge(25);

            person.setEmail("jane@example.com");

            

            log.info("Created person: {}", person);

            

            // Using generated getters/setters

            person.setAge(26);

            log.info("Updated age: {}", person.getAge());

        }
    /**
     * SLF4J Logging examples
     */
    public void demonstrateLogging() {
        log.info("=== Logging Examples ===");
        
        log.trace("This is a TRACE message");
        log.debug("This is a DEBUG message");
        log.info("This is an INFO message");
        log.warn("This is a WARN message");
        log.error("This is an ERROR message");
        
        // Parameterized logging
        String user = "admin";
        int attempts = 3;
        log.info("User {} failed login after {} attempts", user, attempts);
        
        // Exception logging
        try {
            throw new RuntimeException("Example exception");
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    /**
     * Micrometer metrics examples
     */
    public void demonstrateMetrics() {
        log.info("=== Micrometer Metrics Examples ===");
        
        // Counter example
        Counter requestCounter = Metrics.counter("requests.total", "endpoint", "/api/users");
        requestCounter.increment();
        requestCounter.increment(5);
        log.info("Request counter value: {}", requestCounter.count());
        
        // Timer example
        
                Timer responseTimer = Metrics.timer("response.time", "endpoint", "/api/users");
        
                Timer.Sample sample = Timer.start(Metrics.globalRegistry);
        // Simulate some work
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        
        sample.stop(responseTimer);
        log.info("Response timer count: {}, total time: {} ms", 
            responseTimer.count(), responseTimer.totalTime(TimeUnit.MILLISECONDS));
    }

    /**
     * Akka Actor System examples
     */
    public void demonstrateAkkaActors() {
        log.info("=== Akka Actor Examples ===");
        
        ActorSystem system = ActorSystem.create("ExampleSystem");
        
        try {
            // Create a simple actor
            ActorRef greeter = system.actorOf(Props.create(GreeterActor.class), "greeter");
            
            // Send messages
            greeter.tell("Hello", ActorRef.noSender());
            greeter.tell("Akka", ActorRef.noSender());
            greeter.tell("World", ActorRef.noSender());
            
            // Give actors time to process
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            
        } finally {
            
                    system.shutdown();
            
                }
    }

    /**

         * Redis (Jedis) examples - requires Redis server running

         */

        public void demonstrateRedisUsage() {

            log.info("=== Redis (Jedis) Examples ===");

            

            Jedis jedis = null;

            try {

                jedis = new Jedis("localhost", 6379);

                

                // String operations

                jedis.set("greeting", "Hello Redis!");

                String value = jedis.get("greeting");

                log.info("Redis get result: {}", value);

                

                // List operations

                jedis.lpush("fruits", "apple");

                jedis.lpush("fruits", "banana");

                jedis.lpush("fruits", "cherry");

                java.util.List<String> fruits = jedis.lrange("fruits", 0, -1);

                log.info("Redis list: {}", fruits);

                

                // Set expiration

                jedis.expire("greeting", 60); // Expire in 60 seconds

                Long ttl = jedis.ttl("greeting");

                log.info("TTL for greeting: {} seconds", ttl);

                

            } catch (Exception e) {

                log.warn("Could not connect to Redis: {}", e.getMessage());

            } finally {

            

                            if (jedis != null) {

            

                                // Older Jedis versions use disconnect() instead of close()

                                jedis.disconnect();

            

                            }

            

                        }
        }
    /**

         * MongoDB examples - requires MongoDB server running

         * NOTE: Requires mongodb-driver dependency to be added to pom.xml

         */

        public void demonstrateMongoUsage() {

            log.info("=== MongoDB Examples ===");

            log.warn("MongoDB driver dependency not available - example skipped");

            

            // Uncomment when mongodb-driver dependency is added:

            /*

            try (MongoClient mongoClient = new MongoClient("localhost", 27017)) {

                MongoDatabase database = mongoClient.getDatabase("testdb");

                MongoCollection<org.bson.Document> collection = database.getCollection("testcollection");

                

                // Insert document

                org.bson.Document doc = new org.bson.Document("name", "MongoDB Example")

                    .append("type", "database")

                    .append("version", "4.0");

                

                collection.insertOne(doc);

                log.info("Inserted document into MongoDB");

                

                // Find document

                org.bson.Document found = collection.find(new org.bson.Document("name", "MongoDB Example")).first();

                if (found != null) {

                    log.info("Found document: {}", found.toJson());

                }

                

            } catch (Exception e) {

                log.warn("Could not connect to MongoDB: {}", e.getMessage());

            }

            */

        }
    /**
     * Elasticsearch examples - requires Elasticsearch server running
     */
    public void demonstrateElasticsearchUsage() {
        log.info("=== Elasticsearch Examples ===");
        
        try {
            Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();
            
            // Note: TransportClient is deprecated in ES 7.x+
            
                        // TransportClient is abstract and cannot be instantiated directly in ES 5.x+
            
                        // Would need PreBuiltTransportClient or similar concrete implementation
            
                        
            
                        @SuppressWarnings("deprecation")
            
                        TransportClient client = null; // Cannot instantiate abstract TransportClient
            
                        
            
                        // In real usage, would use:
            
                        // TransportClient client = new PreBuiltTransportClient(settings);
            
                        // client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
            // Index a document
            ObjectNode source = mapper.createObjectNode();
            source.put("title", "Elasticsearch Example");
            source.put("content", "This is a test document");
            source.put("timestamp", System.currentTimeMillis());
            
            // Note: This is for older ES versions, newer versions have different APIs
            
                        log.info("Would index document: {}", source);
            
                        
            
                        if (client != null) {
            
                            client.close();
            
                        }
        } catch (Exception e) {
            log.warn("Could not connect to Elasticsearch: {}", e.getMessage());
        }
    }

    /**
     * Simple Actor example for Akka demonstration
     */
    public static class GreeterActor extends UntypedActor {
        private final Logger log = LoggerFactory.getLogger(GreeterActor.class);
        
        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                String msg = (String) message;
                log.info("GreeterActor received: {}", msg);
                log.info("GreeterActor says: Hello, {}!", msg);
            } else {
                unhandled(message);
            }
        }
    }

    /**

         * Example data class (would use Lombok @Data and @Builder if dependency available)

         */

        public static class Person {

            private String name;

            private int age;

            private String email;

            

            public String getName() {

                return name;

            }

            

            public void setName(String name) {

                this.name = name;

            }

            

            public int getAge() {

                return age;

            }

            

            public void setAge(int age) {

                this.age = age;

            }

            

            public String getEmail() {

                return email;

            }

            

            public void setEmail(String email) {

                this.email = email;

            }

            

            @Override

            public String toString() {

                return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";

            }

        }
}