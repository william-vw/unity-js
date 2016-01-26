The unity-js project contains a simple JavaScript API to access the Unity web services. 

<b>Important note</b>: when trying to access the Unity API from JavaScript, one will likely get a Cross-Origin Resource Sharing-related exception raised by the browser, along the lines of 

	"Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource. Origin '*******' is therefore not allowed access. The response 	had HTTP status code 405."

This error refers to the much more general issue of <a href="https://en.wikipedia.org/wiki/Cross-origin_resource_sharing">Cross-Origin Resource Sharing (CORS)</a>, and the fact that the Unity API doesn't support it. Regrettably, and afaik, this means that without going through an intermediary, you cannot access Unity APIs from the browser. Therefore, the repository also includes an intermediary "mobile-proxy" that, on behalf of the browser script, forwards requests to the Unity web service, whereby it sets the appropriate CORS headers in the response and then returns it to the client. Some basic authentication is being applied at the proxy side to avoid abuse (see mobile-proxy/proxy.properties). So, this requires this authentication information to be passed in each request as well.

The unity-js project currently relies on this proxy to access the Unity web services.
