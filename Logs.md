Log (nov 24-2026, 2:45 am) 
- improved shortCode generation using redis counter combined with off-set (62 ^ 6) for guanted 7 char short-codes.
- fixed the redirect issue caused by non-normalized urls, added normalizeUrl method
- added caching after saving a resource so that first time look up is also fast.
- added lombok for boilerplate code reduction
- added h2 database for testing along with postgres(commented out)
- added doc for the encode method used internally for generating short codes.

log (20 nov 2026 9:48AM)
- (Fixed) 30% request failing coz of duplicate short code generation  ( fixed by removing the random no. gen, and generating base62String with only id)


## Features Implemented 
- short url generation 
- url redirect
- update , delete routes
- url validation
- redis caching

## Features to Implemented 
- user accounts (also verify if shortcode belongs to that user)
- analytics (clicks)


