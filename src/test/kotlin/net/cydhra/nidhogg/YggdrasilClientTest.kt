@file:Suppress("JAVA_CLASS_ON_COMPANION")

package net.cydhra.nidhogg

import junit.framework.Assert.assertTrue
import net.cydhra.nidhogg.data.AccountCredentials
import net.cydhra.nidhogg.data.Session
import net.cydhra.nidhogg.exception.InvalidSessionException
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.File
import java.net.URI

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class YggdrasilClientTest {

    companion object {
        val client = YggdrasilClient()

        var uri: URI? = null
        lateinit var username: String
        lateinit var password: String

        var session: Session? = null

        @BeforeClass
        @JvmStatic fun setUp() {
            uri = this.javaClass.classLoader.getResource("credentials")?.toURI();
            Assume.assumeNotNull(uri)
            val file = File(uri)

            val credentials: List<String> = file.readText().split(":")
            username = credentials[0]
            password = credentials[1]
        }

    }

    @Test
    fun _1_login() {
        Assume.assumeNotNull(uri)
        session = client.login(AccountCredentials(username, password))
    }

    @Test
    fun _2_validate() {
        Assume.assumeNotNull(uri)
        Assume.assumeNotNull(session)
        assertTrue(client.validate(session!!))
    }

    @Test
    fun _3_refresh() {
        Assume.assumeNotNull(uri)
        Assume.assumeNotNull(session)
        client.refresh(session!!)
    }

    @Test(expected = InvalidSessionException::class)
    fun _4_invalidate() {
        Assume.assumeNotNull(uri)
        Assume.assumeNotNull(session)

        client.invalidate(session!!)

        client.validate(session!!)
    }

    @Test
    fun _5_signOut() {
        Assume.assumeNotNull(uri)
        client.signOut(AccountCredentials(username, password))
    }
}