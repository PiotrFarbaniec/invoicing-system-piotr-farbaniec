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
        idProvider.postConstruct()
    }

    def cleanup() {
        database.getAll().stream()
        .forEach(invoice-> database.delete(invoice.id))
        mongoDbExecutable.stop()
    }

    def "should return no invoices if database is empty"() {
        when:
        def invoices = database.getAll()

        then:
        invoices.size() == 0
    }

    def "should save given invoice in database"() {
        given:
        Invoice firstInvoice = new Invoice()
        Invoice secondInvoice = new Invoice()
        Invoice thirdInvoice = new Invoice()

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

        cleanup:
        database.getAll().stream()
        .forEach(invoice-> database.delete(invoice.id))
    }

    def "should get all invoices stored in database"() {
        given:
        Invoice firstInvoice = TestHelper.getInvoice()[0]
        Invoice secondInvoice = TestHelper.getInvoice()[1]
        Invoice thirdInvoice = TestHelper.getInvoice()[2]
        database.save(firstInvoice)
        database.save(secondInvoice)
        database.save(thirdInvoice)

        when:
        def invoices = database.getAll()

        then:
        invoices.size() == 3
        invoices.get(0) == firstInvoice
        invoices.get(1) == secondInvoice
        invoices.get(2) == thirdInvoice

        cleanup:
        database.getAll().stream()
                .forEach(invoice-> database.delete(invoice.id))
    }

    def "should return no content if searched invoice does not exist"() {
        when:
        def searchedInvoice = database.getById(1)

        then:
        searchedInvoice == Optional.empty()
    }

    def "should delete an invoice with specified id if exist"() {
        given:
        Invoice firstInvoice = TestHelper.getInvoice()[0]
        Invoice secondInvoice = TestHelper.getInvoice()[1]
        database.save(firstInvoice)
        database.save(secondInvoice)

        when:
        database.delete(firstInvoice.getId())
        database.delete(secondInvoice.getId())

        then:
        database.getAll() == []
        database.getById(firstInvoice.id) == Optional.empty()
        database.getById(secondInvoice.id) == Optional.empty()
    }

    def "should not delete if invoice with specified id does not exist"() {
        given:
        Invoice firstInvoice = TestHelper.getInvoice()[0]
        database.save(firstInvoice)

        when:
        database.delete(2)
        def originalSize = database.getAll().size()

        then:
        database.getAll().size() == originalSize

        cleanup:
        database.getAll().stream()
                .forEach(invoice-> database.delete(invoice.id))
    }

    def "should update existed invoice"() {
        given:
        Invoice firstInvoice = new Invoice()
        Invoice secondInvoice = new Invoice()

        database.save(firstInvoice)
        database.save(secondInvoice)
        def updatedInvoice = TestHelper.getInvoice()[2]

        when:
        database.update(secondInvoice.id, updatedInvoice)
        def resultInvoice =  database.getById(secondInvoice.getId()).get()


        then:
        resultInvoice.number == updatedInvoice.number
        resultInvoice.seller == updatedInvoice.seller
        resultInvoice.buyer == updatedInvoice.buyer
        resultInvoice.entries.size() == updatedInvoice.entries.size()

        cleanup:
        database.getAll().stream()
                .forEach(invoice-> database.delete(invoice.id))
    }

    def "should not update if invoice with specified id does not exist"() {
        given:
        def updatedInvoice = TestHelper.getInvoice()[2]
        updatedInvoice.setId(1)

        when:
        database.update(1, updatedInvoice)

        then:
        database.getById(1) == Optional.empty()

        cleanup:
        database.getAll().stream()
                .forEach(invoice-> database.delete(invoice.id))
    }
}
