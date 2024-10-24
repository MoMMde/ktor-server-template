package template.group.name.repository

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

interface DocumentedById {
    val id: ObjectId
}

// needs to stay protected for abstract instances of this class to access this field
abstract class CollectableRepository<T : DocumentedById>(protected val collection: MongoCollection<T>) {
    suspend fun save(document: T) {
        collection.insertOne(document)
    }
    suspend fun remove(document: T) {
        collection.deleteOne(filter = Filters.eq("_id", document.id))
    }
    suspend fun findAll(): List<T> {
        val results = mutableListOf<T>()
        collection.find().collect { results.add(it) }
        return results
    }
    suspend fun findByObjectId(id: ObjectId): T? {
        return collection.find(Filters.eq("_id", id)).firstOrNull()
    }
}