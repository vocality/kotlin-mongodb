import io.github.cdimascio.dotenv.dotenv
import io.github.cdimascio.dotenv.Dotenv
import com.mongodb.MongoCredential
import com.mongodb.MongoException
import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters.eq
import fr.vocality.tutorials.KotlinMongoDB
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertNotNull

class KotlinMongoDbTest {
    private lateinit var kotlinMongoDB: KotlinMongoDB
    private lateinit var credential: MongoCredential
    private lateinit var dotenv: Dotenv

    @BeforeEach
    fun setUp() {
        kotlinMongoDB = KotlinMongoDB()
        dotenv = dotenv()
        credential = MongoCredential.createScramSha256Credential(dotenv["MONGODB_USER"], "admin", dotenv["MONGODB_PASSWORD"].toCharArray())
    }

    @Test
    fun testConnect() {
        lateinit var mongoClient: MongoClient

        try {
            mongoClient = kotlinMongoDB.connect(dotenv["MONGODB_HOST"], credential)!!
            assertNotNull(mongoClient)

        } catch (e: MongoException) {
            e.printStackTrace()
        } finally {
            mongoClient.close()
        }
    }

    @Test
    fun testInsertOne() {
        lateinit var mongoClient: MongoClient

        try {
            mongoClient = kotlinMongoDB.connect(dotenv["MONGODB_HOST"], credential)!!
            assertNotNull(mongoClient)

            val db = mongoClient.getDatabase(dotenv["MONGODB_DBNAME"])
            val collection = db.getCollection("positions")
            val document = Document()
                .append("coordinates", listOf(1.5, 45.78))
                .append("timestamp", Instant.now())

            val resultId = kotlinMongoDB.insertOne(collection, document)
            println("result: $resultId")

            // search document with resultId
            val findDocument = collection.find(eq("_id", ObjectId(resultId))).first()
            println(findDocument?.toJson())
            assertNotNull(findDocument)

        } catch (e: MongoException) {
            e.printStackTrace()
        } finally {
            mongoClient.close()
        }
    }
}