package net.cydhra.nidhogg

import net.cydhra.nidhogg.data.NameEntry
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.time.Instant
import java.util.*

/* an epoch second, where the UUID of account Cydhra was valid */
private const val EPOCH_SECOND = 1492297560L

class MojangClientTest {

    companion object {
        lateinit var client: MojangClient

        @JvmStatic
        @BeforeClass
        fun setup() {
            client = MojangClient()
        }
    }

    @Test
    fun checkStatus() {
    }

    @Test
    fun getUUIDbyUsername() {
        Assert.assertEquals(
                "fdba166c-4eab-43ea-b0e8-8d7d62e1c417",
                client.getUUIDbyUsername("Cydhra", Instant.ofEpochSecond(EPOCH_SECOND)).toString()
        )
    }

    @Test
    fun getNameHistoryByUUID() {
        Assert.assertEquals(
                client.getNameHistoryByUUID(UUID.fromString("fdba166c-4eab-43ea-b0e8-8d7d62e1c417"))[0],
                NameEntry("Cydhra", null)
        )
    }

    @Test
    fun getUUIDsByNames() {
    }

    @Test
    fun getProfileByUUID() {
    }

    @Test
    fun changeSkin() {
    }

    @Test
    fun uploadSkin() {
    }

    @Test
    fun resetSkin() {
    }
}