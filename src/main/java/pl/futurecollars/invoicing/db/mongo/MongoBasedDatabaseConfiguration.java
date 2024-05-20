package pl.futurecollars.invoicing.db.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(name = {"invoicing-system.database"}, havingValue = "mongo")
public class MongoBasedDatabaseConfiguration {

  @Bean
  public MongoDatabase mongoDB(
      @Value("${invoicing-system.database.database-name}") String databaseName) {
    CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoClientSettings settings = MongoClientSettings.builder()
        .codecRegistry(pojoCodecRegistry)
        .build();
    MongoClient client = MongoClients.create(settings);
    log.debug("MONGO DATABASE CODECS REGISTERING");
    return client.getDatabase(databaseName);
  }

  @Bean
  public MongoIdProvider invoiceIdProvider(
      @Value("${invoicing-system.database.invoice-counter-name}")
      String invoiceCounterName, MongoDatabase mongoDB) {
    MongoCollection<Document> collection = mongoDB.getCollection(invoiceCounterName);
    log.debug("CREATING ID PROVIDER FOR MONGO DATABASE");
    return new MongoIdProvider(collection);
  }

  @Bean
  public Database<Invoice> invoiceMongoDatabase(
      @Value("${invoicing-system.database.invoice-collection-name}") String collectionName,
      MongoDatabase mongoDB, MongoIdProvider invoiceIdProvider) {
    MongoCollection<Invoice> collection = mongoDB.getCollection(collectionName, Invoice.class);
    log.info("CURRENTLY THE APPLICATION WORKS WITH MONGO DATABASE");
    return new MongoBasedDatabase<>(collection, invoiceIdProvider);
  }

  @Bean
  public MongoIdProvider companyIdProvider(
      @Value("${invoicing-system.database.company-counter-name}")
      String companyCounterName, MongoDatabase mongoDB) {
    MongoCollection<Document> collection = mongoDB.getCollection(companyCounterName);
    log.debug("CREATING ID PROVIDER FOR MONGO DATABASE");
    return new MongoIdProvider(collection);
  }

  @Bean
  public Database<Company> companyMongoDatabase(
      @Value("${invoicing-system.database.company-collection-name}") String collectionName,
      MongoDatabase mongoDB, MongoIdProvider companyIdProvider) {
    MongoCollection<Company> collection = mongoDB.getCollection(collectionName, Company.class);
    log.info("CURRENTLY THE APPLICATION WORKS WITH MONGO DATABASE");
    return new MongoBasedDatabase<>(collection, companyIdProvider);
  }
}
