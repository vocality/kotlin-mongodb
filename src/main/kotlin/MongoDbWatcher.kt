package fr.vocality.tutorials

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCursor
import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.connection.ClusterSettings
import org.bson.Document
import java.util.concurrent.TimeUnit
import io.github.cdimascio.dotenv.dotenv

object MongoDbWatcher {
    @JvmStatic
    fun main(args: Array<String>) {
        println("[Watching for change streams] positions collection...")

        val dotenv = dotenv()
        val credentials = MongoCredential.createScramSha256Credential(dotenv["MONGODB_USER"], "admin", dotenv["MONGODB_PASSWORD"].toCharArray())
        lateinit var mongoClient: MongoClient
        lateinit var watchCursor: MongoCursor<ChangeStreamDocument<Document>>

        try {
            mongoClient = connect(dotenv["MONGODB_HOST"], credentials)!!

            val db = mongoClient.getDatabase(dotenv["MONGODB_DBNAME"])
            val collection = db.getCollection("positions")

            watchCursor = collection.watch().iterator()
            while (watchCursor.hasNext()) {
                val changedDocument = watchCursor.next()

                when(changedDocument.operationType.value) {
                    "insert" -> println(changedDocument.fullDocument?.toJson())
                    "delete" -> println(changedDocument.documentKey)
                }
            }
        } catch (e: MongoException) {
            e.printStackTrace()
        } finally {
            watchCursor.close()
            mongoClient.close()
        }
    }

    private fun connect(host: String, credential: MongoCredential): MongoClient? {
        try {
            val settings = MongoClientSettings.builder()
                .applyToClusterSettings{ ClusterSettings.builder()
                    .hosts(listOf(ServerAddress(host)))
                    .serverSelectionTimeout(10000, TimeUnit.MILLISECONDS)
                    .requiredReplicaSetName("rs0")
                    .build()
                }
                .credential(credential)
                .build()

            return (MongoClients.create(settings))
        } catch(e: MongoException) {
            e.printStackTrace()
        }
        return null
    }
}