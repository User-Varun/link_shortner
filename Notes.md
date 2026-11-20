Features Implemented 
- short url generation 
- url redirect
- url validation
- caching

Features to Implemented 
- user accounts 
- (done) add update and delete routes for urls (in future will need to verify if shortcode belongs to that user, when implement user accounts)
- analytics (clicks)
- (done) caching, indexing
- (done) check if the implementation is scalable (unique code generation -- it is scalable, but it doesn't look good )

bottlenecks
- (Fixed) 30% request failing coz of duplicate short code generation  ( fixed by removing the random no. gen, and generating base62String with only id)