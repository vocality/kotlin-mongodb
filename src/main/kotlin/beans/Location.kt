package fr.vocality.tutorials.beans

import org.bson.types.ObjectId

class Location {
    private lateinit var coordinates: Array<Double>

    //@BsonProperty(value = "user_id")
    private lateinit var userId: ObjectId

    private lateinit var id: ObjectId



}
