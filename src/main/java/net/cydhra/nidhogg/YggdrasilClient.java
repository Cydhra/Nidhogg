package net.cydhra.nidhogg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.RequiredArgsConstructor;
import net.cydhra.nidhogg.data.AccountCredentials;
import net.cydhra.nidhogg.data.Session;
import net.cydhra.nidhogg.exception.InvalidCredentialsException;
import net.cydhra.nidhogg.exception.InvalidSessionException;
import net.cydhra.nidhogg.exception.UserMigratedException;
import net.cydhra.nidhogg.exception.YggdrasilBanException;
import net.cydhra.nidhogg.requests.AuthenticateResponse;
import net.cydhra.nidhogg.requests.ErrorResponse;
import net.cydhra.nidhogg.requests.LoginRequest;
import net.cydhra.nidhogg.requests.RefreshRequest;
import net.cydhra.nidhogg.requests.SignOutRequest;
import net.cydhra.nidhogg.requests.ValidationRequest;

import javax.ws.rs.core.MediaType;
import java.util.regex.Pattern;

/**
 *
 */
public class YggdrasilClient {
    
    private static final String DEFAULT_CLIENT_TOKEN = "Nidhogg";
    
    private static final String YGGDRASIL_HOST_SERVER = "https://authserver.mojang.com";
    private static final String ENDPOINT_AUTHENTICATE = "/authenticate";
    private static final String ENDPOINT_REFRESH = "/refresh";
    private static final String ENDPOINT_VALIDATE = "/validate";
    private static final String ENDPOINT_SIGNOUT = "/signout";
    private static final String ENDPOINT_INVALIDATE = "/invalidate";
    
    private static final Pattern errorMessageRegex = Pattern.compile(
            "\\{\"error\":\\s*\"[a-zA-Z0-9\\s\\.\\,]*\",\\s*\"errorMessage\":\\s*\"[a-zA-Z0-9\\s\\.\\,]*\"(," +
                    "\\s*\"cause\":\\s*\"[a-zA-Z0-9\\s]*\")*\\}");
    
    private final Gson gson;
    private final String nidhoggClientToken;
    
    /**
     * A default Yggdrasil REST client with client token {@value DEFAULT_CLIENT_TOKEN}
     */
    public YggdrasilClient() {
        this(DEFAULT_CLIENT_TOKEN);
    }
    
    /**
     * A Yggdrasil REST client
     *
     * @param nidhoggClientToken the client token sent to Yggdrasil. Can be arbitrary, but must be the same when refreshing a session
     */
    public YggdrasilClient(final String nidhoggClientToken) {
        this.nidhoggClientToken = nidhoggClientToken;
        this.gson = new GsonBuilder().create();
    }
    
    public Session login(final AccountCredentials credentials) {
        return this.login(credentials, YggdrasilAgent.MINECRAFT);
    }
    
    /**
     * Log in with given account credentials at given service agent
     *
     * @param credentials Yggdrasil account credentials
     * @param agent       Yggdrasil account agent
     *
     * @return a Yggdrasil session object
     *
     * @throws UserMigratedException       if the account credentials used the player name but the account is migrated
     * @throws InvalidCredentialsException if the given credentials are invalid
     * @throws YggdrasilBanException       if the client is banned from Yggdrasil
     * @throws IllegalArgumentException    if the account credentials are partially empty
     */
    public Session login(final AccountCredentials credentials, final YggdrasilAgent agent) {
        if (credentials.getUsername() == null || credentials.getPassword() == null || credentials.getUsername().equals("") || credentials
                .getPassword().equals("")) {
            throw new IllegalArgumentException("User Credentials may not be empty");
        }
        
        final LoginRequest request = new LoginRequest(
                new LoginRequest.Agent(agent.name, 1),
                credentials.getUsername(),
                credentials.getPassword(),
                this.nidhoggClientToken,
                true);
        
        final ClientResponse response = executeRequest(ENDPOINT_AUTHENTICATE, gson.toJson(request));
        throwOnError(response.getEntity(String.class));
        
        final AuthenticateResponse authenticateResponse = gson.fromJson(response.getEntity(String.class), AuthenticateResponse.class);
        return new Session(authenticateResponse.getSelectedProfile().getName(), authenticateResponse.getAccessToken(),
                authenticateResponse.getClientToken());
    }
    
    /**
     * Validate a Yggdrasil session.
     *
     * @param session A session with access and client token
     *
     * @return true, if the session is still valid. This method does not return false, but throws an appropriate exception
     *
     * @throws IllegalArgumentException if the given session has an empty access token
     * @throws InvalidSessionException  if the given session is invalid
     */
    public boolean validate(final Session session) {
        if (session.getAccessToken() == null || session.getAccessToken().equals("")) {
            throw new IllegalArgumentException("Access token may not be empty");
        }
        
        final ClientResponse response = executeRequest(ENDPOINT_VALIDATE,
                gson.toJson(new ValidationRequest(session.getAccessToken(), session.getClientToken())));
        
        // invalid session or other error
        throwOnError(response.getEntity(String.class));
        
        // session is valid
        return true;
    }
    
    /**
     * Refreshes a session at Yggdrasil
     *
     * @param session a Yggdrasil session with valid access token
     *
     * @throws IllegalArgumentException if the given session has an empty access token
     * @throws InvalidSessionException  if the given session is already invalidated / was never valid
     */
    public void refresh(final Session session) {
        if (session.getAccessToken() == null || session.getAccessToken().equals("")) {
            throw new IllegalArgumentException("Access token may not be empty");
        }
        
        final ClientResponse response = executeRequest(ENDPOINT_REFRESH,
                gson.toJson(new RefreshRequest(session.getAccessToken(), session.getClientToken(), true)));
        
        throwOnError(response.getEntity(String.class));
        
        // the refresh response is similar to authentication response, except that the available profiles are
        // missing.
        final AuthenticateResponse refreshResponse = gson.fromJson(response.getEntity(String.class), AuthenticateResponse.class);
        
        // refresh the session with latest data
        session.setAccessToken(refreshResponse.getAccessToken());
        session.setClientToken(refreshResponse.getClientToken());
        session.setAlias(refreshResponse.getSelectedProfile().getName());
    }
    
    /**
     * Sign out from an Yggdrasil account (invalidate all sessions currently associated with the given account)
     *
     * @param data account's credentials
     *
     * @throws IllegalArgumentException    if given credentials are partially empty
     * @throws UserMigratedException       if the account credentials used the player name but the account is migrated
     * @throws InvalidCredentialsException if the given credentials are invalid
     */
    public void signOut(final AccountCredentials data) {
        if (data.getUsername() == null || data.getUsername().equals("") || data.getPassword() == null || data.getPassword().equals("")) {
            throw new IllegalArgumentException("User Credentials may not be empty");
        }
        
        final ClientResponse response = executeRequest(ENDPOINT_SIGNOUT,
                gson.toJson(new SignOutRequest(data.getUsername(), data.getPassword())));
        throwOnError(response.getEntity(String.class));
    }
    
    /**
     * Invalidate a session. Yggdrasil will invalidate the access token so all sessions using it can no longer authenticate
     *
     * @param session A so far valid {@link Session}
     *
     * @throws IllegalArgumentException if the given session has an empty access token
     * @throws InvalidSessionException  if the session was already invalid
     */
    public void invalidate(final Session session) {
        if (session.getAccessToken() == null || session.getAccessToken().equals("")) {
            throw new IllegalArgumentException("Access token may not be empty");
        }
        
        // invalidation and validation requests are exactly the same
        final ClientResponse response = executeRequest(ENDPOINT_INVALIDATE,
                gson.toJson(new ValidationRequest(session.getAccessToken(), session.getClientToken())));
        
        throwOnError(response.getEntity(String.class));
    }
    
    /**
     * Executes a POST request to Yggdrasil with given request body and default settings (like User-Agent)
     *
     * @param endpoint the Yggdrasil REST service endpoint
     * @param body     the request body as JSON formatted string
     *
     * @return the JSON formatted response
     */
    private static ClientResponse executeRequest(final String endpoint, final String body) {
        assert endpoint.startsWith("/");
        final WebResource resource = Client.create().resource(YGGDRASIL_HOST_SERVER).path(endpoint);
        return resource
                .header("User-Agent", DEFAULT_CLIENT_TOKEN)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .entity(body, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);
    }
    
    /**
     * If the response is of {@link ErrorResponse} type, an appropriate exception is thrown, otherwise, nothing happens
     *
     * @param response serialized response from Yggdrasil REST service
     */
    private void throwOnError(final String response) {
        if (!errorMessageRegex.matcher(response).find()) return;
        
        final ErrorResponse errorResponse = gson.fromJson(response, ErrorResponse.class);
        
        // on user migrated error
        if (errorResponse.getCause() != null && errorResponse.getCause().equals("UserMigratedException")) {
            throw new UserMigratedException("User account has been migrated. Login with username is not allowed.");
        }
        
        // on invalid credentials
        if (errorResponse.getErrorMessage().equals("Invalid credentials. Invalid username or password.")) {
            throw new InvalidCredentialsException("Provided account credentials where invalid.");
        }
        
        // on mojang authentication ban
        if (errorResponse.getErrorMessage().equals("Invalid credentials.")) {
            throw new YggdrasilBanException("The client is currently banned from Mojang authentication service " +
                    "due to too many login attempts with invalid credentials. Last " +
                    "credentials may be valid, though");
        }
        
        // access token invalid
        if (errorResponse.getErrorMessage().equals("Invalid token")) {
            throw new InvalidSessionException("Invalid access token provided");
        }
        
        // unknown or unexpected exception
        throw new RuntimeException((errorResponse.getError() == null ? "unknown error" : errorResponse.getError()) + ":\n" +
                (errorResponse.getErrorMessage() == null ? "no description" : errorResponse.getErrorMessage()) + "\nCause: " +
                (errorResponse.getCause() == null ? "no cause" : errorResponse.getCause()));
    }
    
    @RequiredArgsConstructor
    private enum YggdrasilAgent {
        MINECRAFT("Minecraft");
        
        private final String name;
    }
}
