@file:Suppress("unused")

package net.cydhra.nidhogg

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import javax.ws.rs.core.MediaType

internal const val DEFAULT_CLIENT_TOKEN = "Nidhogg"

abstract class NidhoggClient(private val userAgent: String) {

    protected val gson: Gson = GsonBuilder().create()

    /**
     * Executes a RESTful GET request at a given host and resource.
     * @param Hostname of requested service
     * @param endpoint resource endpoint that is requested
     *
     * @return a [ClientResponse] object of the Jersey Rest API
     */
    internal fun getRequest(host: String, endpoint: String, vararg queryParam: Pair<String, String>): ClientResponse {
        assert(endpoint.startsWith("/"))
        val resource = Client.create().resource(host).path(endpoint)
        for (param in queryParam) resource.queryParam(param.first, param.second)

        return buildRequest(resource)
                .get<ClientResponse>(ClientResponse::class.java)
    }

    /**
     * Execute a RESTful POST request at a given host and resource providing a given body.
     * @param host Hostname of requested service
     * @param endpoint resource endpoint that is requested
     * @param body request body entity
     *
     * @return a [ClientResponse] object of Jersey Rest API
     */
    internal fun postRequest(host: String, endpoint: String, body: Any, mediaType: MediaType = MediaType.APPLICATION_JSON_TYPE): ClientResponse {
        assert(endpoint.startsWith("/"))
        val resource = Client.create().resource(host).path(endpoint)
        return buildRequest(resource)
                .type(mediaType)
                .post<ClientResponse>(ClientResponse::class.java, body)
    }

    /**
     * Build up a request with default settings.
     * @return a builder for Jersey requests with default settings
     */
    private fun buildRequest(resource: WebResource): WebResource.Builder =
            resource.header("User-Agent", DEFAULT_CLIENT_TOKEN)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
}
