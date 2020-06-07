@file:Suppress("JAVA_CLASS_ON_COMPANION")

package net.cydhra.nidhogg

import kotlinx.coroutines.runBlocking
import net.cydhra.nidhogg.data.AccountCredentials
import net.cydhra.nidhogg.data.Session
import net.cydhra.nidhogg.exception.InvalidAccessTokenException
import net.cydhra.nidhogg.exception.InvalidCredentialsException
import net.cydhra.nidhogg.exception.UserMigratedException
import net.cydhra.nidhogg.yggdrasil.MinecraftAgent
import net.cydhra.nidhogg.yggdrasil.YggdrasilClient
import org.junit.*
import org.junit.rules.ExpectedException
import org.junit.runners.MethodSorters
import java.io.File
import java.net.URI

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class YggdrasilClientTest {

    companion object {
        const val CLIENT_TOKEN = "unit-tests"
        val client = YggdrasilClient(clientToken = CLIENT_TOKEN)

        var uri: URI? = null
        var username: String? = null
        var password: String? = null
        var alias: String? = null

        var session: Session? = null

        @BeforeClass
        @JvmStatic
        fun setUp() {
            uri = this.javaClass.classLoader.getResource("credentials")?.toURI();
            Assume.assumeNotNull(uri)
            val file = File(uri)

            val credentials: List<String> = file.readText().split(":")
            username = credentials[0]
            password = credentials[1]
            alias = if (credentials.size > 2) credentials[2] else null
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            client.close()
        }
    }

    @Rule
    @JvmField
    val ruleThrown = ExpectedException.none()!!

    @Test
    fun _1_authenticate() {
        Assume.assumeNotNull(username)
        Assume.assumeNotNull(password)

        runBlocking {
            val response = client.authenticate(AccountCredentials(username!!, password!!),
                    MinecraftAgent, true
            )
            Assert.assertEquals(CLIENT_TOKEN, response.session.clientToken)

            session = response.session
        }
    }

    @Test
    fun _2_validate() {
        Assume.assumeNotNull(session)

        runBlocking {
            Assert.assertTrue(client.validate(session!!, true))
            Assert.assertFalse(client.validate(Session("invalid session", "invalid token")))
        }
    }

    @Test
    fun _3_refresh() {
        Assume.assumeNotNull(session)

        runBlocking {
            val response = client.refresh(session!!, null, false)

            Assert.assertEquals(session!!.clientToken, response.session.clientToken)
            session = response.session
        }
    }

    @Test
    fun _4_invalidate() {
        Assume.assumeNotNull(session)

        runBlocking {
            client.invalidate(session!!)
            Assert.assertFalse(client.validate(session!!, true))
        }
    }

    @Test
    fun _5_signOut() {
        Assume.assumeNotNull(username)
        Assume.assumeNotNull(password)

        runBlocking {
            client.signOut(AccountCredentials(username!!, password!!))
        }
    }

    /**
     * Test whether a [UserMigratedException] is thrown when logging into a migrated account that has been using the
     * old authentication scheme before. Note: if the account was never migrated (but created after migration due),
     * this test will fail.
     */
    @Test
    fun _6_accountMigratedTest() {
        Assume.assumeNotNull(alias)
        Assume.assumeNotNull(password)

        runBlocking {
            ruleThrown.expect(UserMigratedException::class.java)
            client.authenticate(AccountCredentials(alias!!, password!!))
        }
    }

    @Test
    fun _7_invalidCredentialsTest() {
        runBlocking {
            ruleThrown.expect(InvalidCredentialsException::class.java)
            client.authenticate(AccountCredentials("definitely not a username", "this password is secure"))
        }
    }

    @Test
    fun _8_invalidAccessTokenTest() {
        runBlocking {
            ruleThrown.expect(InvalidAccessTokenException::class.java)
            client.refresh(Session("definitely not a token", "Nidhogg sees you"))
        }
    }
}