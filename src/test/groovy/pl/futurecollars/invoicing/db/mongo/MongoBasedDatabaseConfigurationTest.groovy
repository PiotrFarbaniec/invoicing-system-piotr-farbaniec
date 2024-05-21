package pl.futurecollars.invoicing.db.mongo

import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = MongoBasedDatabaseConfiguration.class)
@ActiveProfiles("mongo")
class MongoBasedDatabaseConfigurationTest extends Specification {
//
//    @Autowired
//    MongoDatabase mongoDB
//
//    def "should create MongoDatabase bean"() {
//        expect:
//        mongoDB != null
//    }
}
