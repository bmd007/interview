package ir.tiroon.cabonline.codechallenge.restWebservice

import ir.tiroon.cabonline.codechallenge.model.UrlRecord
import ir.tiroon.cabonline.codechallenge.repository.UrlRecordRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse


import static org.springframework.web.reactive.function.server.RequestPredicates.GET
import static org.springframework.web.reactive.function.server.RequestPredicates.POST
import static org.springframework.web.reactive.function.server.RequestPredicates.accept
import static org.springframework.web.reactive.function.server.RouterFunctions.route

@Configuration
class RestWebService {

    @Autowired
    UrlRecordRepository urlRecordRepository
    String perfectUrlValidationRegex = "(?i)^(http://|https://)?(www.)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?\$"
    String theRegexThatIImproved = "^(http://|https://)?(www.)?([a-zA-Z0-9,-_]+)(.([a-zA-Z0-9])*)*.[a-z]{3,9}(/[a-zA-Z0-9]*)*\$"

    @Bean
    RouterFunction<ServerResponse> monoRouterFunction() {

        //This endpoint takes a long url as a path variable that brings some limitations.
        //For example, the longUrl can not include 'http' or path variables ('?q=13', /13')
        //So I developed a Post alternative for it that consumes plain text
        route(GET("/makeThisShorter/{longUrl}"), { req ->

            def longUrl = String.valueOf(req.pathVariable("longUrl"))

            if (longUrl.matches(perfectUrlValidationRegex))
                urlRecordRepository.save(new UrlRecord(longUrl))
                .flatMap{ ServerResponse.ok().syncBody("http://localhost:8080/"+it.id) }
            else
                ServerResponse.badRequest().syncBody("bad url")

        })

        .andRoute(POST("/makeThisShorter").and(accept(MediaType.TEXT_PLAIN)), { req ->

            req.bodyToMono(String.class).name("longUrl")
            .flatMap{ longUrl ->
                if (longUrl.matches(perfectUrlValidationRegex))
                    urlRecordRepository.save(new UrlRecord(longUrl)).flatMap{ ServerResponse.ok().syncBody("http://localhost:8080/"+it.id) }
                else
                    ServerResponse.badRequest().syncBody("bad url")
            }
        })



        .andRoute(GET("/{shortUrl}"), { req ->

            def id = String.valueOf(req.pathVariable("shortUrl"))

            urlRecordRepository.findById(id).flatMap{
                ServerResponse
                        .temporaryRedirect(URI.create(it.longUrl.contains("http")? it.longUrl : "http://"+it.longUrl))
                        .build()
            }
        })

    }

}