For this project an up and running **MongoDb** instance and a **Java 8** jre is required.

Then send a post request to **localhost:8080/makeThisShorter/** with the long url in its body as plain text. The 
response will be a short url. Hitting the short url cause a redirect to long url.

###################
Some Information
###################
A Url shortener needs a database which is high speed in read operations.
And overally it needs a architecture supporting large scale of simultaneous requests.
So I went for a fully reactive stack and Redis as database.
Redis is a key-value database so it would be great choice for a Url Shortener.

But I couldn't manage to use Spring-data-redis-reactive. I tried ReactiveRedisTemplate, ReactiveRedisOperations, 
ReactiveCrudRepository but none of them gave me successful crud operation. I will search more about this issue later.

Then I used MongoDb. It has a acceptable read speed. And Also it has Random Id generator which is very useful for creating a short Url.
 Although in this method the results are not perfectly short, they sound acceptable.

Firstly, I tried to use an algorithm called Bijective for creating short Urls. But it required a reliable source of random integers.
 And I coudln't find neither a clean way to create true random integers nor clean way to use them.
Then I tried to change the Bijective code to work with BigInetegers(MongoDb provides Biginteger random Ids) but It turned out to be a failure.
Maybe later I will try to find a way to create a stream of true random integers.

Hash functions are also an option for creating shor urls but a hash function with a short enough output is subject to
 large number of duplicates. 

I couldn't manage to take every kinds of URL as a GET endpoint input, so I used a POST one.
