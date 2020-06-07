package net.cydhra.nidhogg

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.runBlocking
import net.cydhra.nidhogg.batch.batchRequestProfilesByUuid
import net.cydhra.nidhogg.mojang.MojangClient
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class BatchRequestsTest {

    companion object {
        lateinit var client: MojangClient

        @JvmStatic
        @BeforeClass
        fun setup() {
            client = MojangClient()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            client.close()
        }
    }

    @Test
    fun getProfileByUUID() {
        runBlocking {
            val profiles = client.batchRequestProfilesByUuid(
                    uuids = setOf(
                            Uuid.fromString("fdba166c-4eab-43ea-b0e8-8d7d62e1c417"),
                            Uuid.fromString("66040a6b-3484-4dbd-9e86-10c82fc56b65"),
                            Uuid.fromString("a2080281-c278-4181-b961-d99ed2f3347c")
                    )
            )

            Assert.assertEquals(3, profiles.size)
        }
    }
}