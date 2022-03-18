package ir.tiroon.cabonline.codechallenge.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "UrlRecord")
class UrlRecord implements Serializable{

    @Id
    String id
    String longUrl

    UrlRecord(String longUrl) {
        this.longUrl = longUrl
    }
}
