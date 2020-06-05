@file:Suppress("JAVA_CLASS_ON_COMPANION")

package net.cydhra.nidhogg

import kotlinx.coroutines.runBlocking
import net.cydhra.nidhogg.data.AccountCredentials
import net.cydhra.nidhogg.data.Session
import org.junit.*
import org.junit.runners.MethodSorters
import java.io.File
import java.net.URI

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class YggdrasilClientTest {

    companion object {
        const val CLIENT_TOKEN = "unit-tests"
        val client = YggdrasilClient(clientToken = CLIENT_TOKEN)

        var uri: URI? = null
        lateinit var username: String
        lateinit var password: String

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
        }

    }

    @Test
    fun _1_authenticate() {
        Assume.assumeNotNull(username)
        Assume.assumeNotNull(password)

        runBlocking {
            val response = client.authenticate(AccountCredentials(username, password), MinecraftAgent, true)
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
            client.signOut(AccountCredentials(username, password))
        }
    }
}