package ir.tiroon.cabonline.codechallenge.repository

import ir.tiroon.cabonline.codechallenge.model.UrlRecord
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UrlRecordRepository extends ReactiveMongoRepository<UrlRecord,String>{
    Mono<UrlRecord> findById(String id)
}
