@file:Suppress("JAVA_CLASS_ON_COMPANION")

package net.cydhra.nidhogg

import junit.framework.Assert.assertFalse
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

        lateinit var username: String
        lateinit var password: String

        lateinit var session: Session

        @BeforeClass
        @JvmStatic fun setUp() {
            val uri: URI? = this.javaClass.classLoader.getResource("credentials").toURI();
            Assume.assumeNotNull(uri)
            val file: File = File(uri)

            val credentials: List<String> = file.readText().split("\n")
            username = credentials[0]
            password = credentials[1]
        }

    }

    @Test
    fun _1_login() {
        session = client.login(AccountCredentials(username, password))
    }

    @Test
    fun _2_validate() {
        assertTrue(client.validate(session))
    }

    @Test
    fun _3_refresh() {
        client.refresh(session)
    }

    @Test(expected = InvalidSessionException::class)
    fun _4_invalidate() {
        client.invalidate(session)

        client.validate(session)
    }

    @Test
    fun _5_signOut() {
        client.signOut(AccountCredentials(username, password))
    }
}