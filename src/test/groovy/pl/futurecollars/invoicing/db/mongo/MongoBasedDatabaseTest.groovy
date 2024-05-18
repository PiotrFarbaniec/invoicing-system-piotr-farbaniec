package pl.futurecollars.invoicing.db.mongo

import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.process.runtime.Network
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoClients
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.TestHelper
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

@IfProfileValue(name = "spring.profile.active", values = ["mongo"])
class MongoBasedDatabaseTest extends Specification {

    MongodExecutable mongoDbExecutable
    MongoBasedDatabase database
    MongoIdProvider idProvider
    MongoCollection<Invoice> invoiceCollection

    def setup() {
        MongodStarter starter = MongodStarter.getDefaultInstance()
        String bindIp = "localhost"
        int port = 27017
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build()

        mongoDbExecutable = starter.prepare(mongodConfig)
        mongoDbExecutable.start()

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .build()

        MongoClient mongoClient = MongoClients.create(settings)

        MongoDatabase mongoDatabase = mongoClient.getDatabase("test")
        MongoCollection<Document> idCollection = mongoDatabase.getCollection("id")
        invoiceCollection = mongoDatabase.getCollection("invoices", Invoice.class)

        idProvider = new MongoIdProvider(idCollection)
        database = new MongoBasedDatabase(invoiceCollection, idProvider)
    }
    def cleanup() {
        database.all.clear()
        mongoDbExecutable.stop()
    }

    def "should save given invoice in database"() {
        given:
        Invoice firstInvoice = TestHelper.getInvoice()[0]
        Invoice secondInvoice = TestHelper.getInvoice()[1]
        Invoice thirdInvoice = TestHelper.getInvoice()[2]

        when:
        int id1 = database.save(firstInvoice)
        int id2 = database.save(secondInvoice)
        int id3 = database.save(thirdInvoice)

        then:
        Optional<Invoice> firstRetrievedInvoice = database.getById(id1)
        firstRetrievedInvoice.isPresent()
        firstRetrievedInvoice.get() == firstInvoice
        Optional<Invoice> secondRetrievedInvoice = database.getById(id2)
        secondRetrievedInvoice.isPresent()
        secondRetrievedInvoice.get() == secondInvoice

        Optional<Invoice> thirdRetrievedInvoice = database.getById(id3)
        thirdRetrievedInvoice.isPresent()
        thirdRetrievedInvoice.get() == thirdInvoice
    }

    // Dodaj więcej testów dla innych metod...
}



    /*private Database database
    private MongoCollection<Invoice> invoiceCollection
    private MongoIdProvider idProvider
    private Invoice firstInvoice = TestHelper.getInvoice()[0]
    private Invoice secondInvoice = TestHelper.getInvoice()[1]
    private Invoice thirdInvoice = TestHelper.getInvoice()[2]

    void setup() {
        invoiceCollection = Mock(MongoCollection)
        idProvider = Mock(MongoIdProvider)
        database = new MongoBasedDatabase(invoiceCollection, idProvider)
    }

    def "should save invoice"() {
        given:
        idProvider.getNextIdAndIncrement() >> 1L >> 2L >> 3L

        when:
        database.save(firstInvoice)
        database.save(secondInvoice)
        database.save(thirdInvoice)

        then:
        1 * invoiceCollection.insertOne(firstInvoice)
        1 * invoiceCollection.insertOne(secondInvoice)
        1 * invoiceCollection.insertOne(thirdInvoice)

    }

    def "should get all invoices"() {
        when:
        database.getAll()
        FindIterable findIterable = Mock(FindIterable)
        invoiceCollection.find(_) >> findIterable

        then:
        1 * invoiceCollection.find() == [3]
    }

    def "should get invoice by id"() {
        given:
        int id = 1
        invoiceCollection.find(_) >> new FindIterableImpl<>(null, null)

        when:
        database.getById(id)

        then:
        1 * invoiceCollection.find(_ as Bson)
    }

    def "should update invoice"() {
        given:
        int id = 1
        invoiceCollection.find(_) >> new FindIterableImpl<>(null, null)

        when:
        database.update(id, invoice)

        then:
        1 * invoiceCollection.findOneAndReplace(_ as Bson, invoice)
    }

    def "should delete invoice"() {
        given:
        int id = 1
        invoiceCollection.find(_) >> new FindIterableImpl<>(null, null)

        when:
        database.delete(id)

        then:
        1 * invoiceCollection.deleteOne(_ as Bson)
    }*/





/*    def "should save and retrieve invoice"() {
        given: "A running mongo container and a database instance"
        def mongoContainer = new GenericContainer('mongo:4.0.5')
                .withExposedPorts(27017)
        mongoContainer.start()

        MongoClient mongoClient = MongoClients.create(
                new ConnectionString("mongodb://" + mongoContainer.getHost() + ":" + mongoContainer.getFirstMappedPort())
        )
        *//*MongoClient mongoClient = new MongoClient(mongoContainer.containerIpAddress, mongoContainer.firstMappedPort)*//*
        MongoDatabase database = mongoClient.getDatabase("test")
        MongoCollection<Document> idCollection = database.getCollection("id")
        MongoIdProvider idProvider = new MongoIdProvider(idCollection)
        MongoCollection<Invoice> invoiceCollection = database.getCollection("invoices", Invoice.class)
        Database mongoDatabase = new MongoBasedDatabase(invoiceCollection, idProvider)

        and: "An invoice"
        def invoice = TestHelper.getInvoice()[0]*//*new Invoice(*//*/* initialize invoice *//*/*)*//*

        when: "Invoice is saved"
        int id = mongoDatabase.save(invoice)

        then: "Invoice can be retrieved by id"
        Optional<Invoice> retrievedInvoice = mongoDatabase.getById(id)
        retrievedInvoice.isPresent()
        retrievedInvoice.get() == invoice

        cleanup:
        mongoContainer.stop()
    }*/

