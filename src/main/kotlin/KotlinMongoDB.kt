package fr.vocality.tutorials

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.result.InsertOneResult
import com.mongodb.connection.ClusterSettings
import org.bson.Document
import java.util.concurrent.TimeUnit

class KotlinMongoDB {
    fun connect(host: String, credential: MongoCredential): MongoClient? {
        try {
            val settings = MongoClientSettings.builder()
                .applyToClusterSettings{ ClusterSettings.builder()
                    //.codecRegistry(codecRegistry)
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

    fun insertOne(collection: MongoCollection<Document>, document: Document): String {
        val result: InsertOneResult
        try {
            result = collection.insertOne(document)
            return result?.insertedId?.asObjectId()?.value.toString()
        } catch (e: MongoException) {
            e.printStackTrace()
        }
        return "InsertOne failed !"
    }
}