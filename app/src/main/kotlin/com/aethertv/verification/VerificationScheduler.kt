package com.aethertv.verification

import com.aethertv.data.repository.ChannelRepository
import com.aethertv.domain.model.StreamQuality
import com.aethertv.domain.usecase.VerifyStreamsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerificationScheduler @Inject constructor(
    private val streamVerifier: StreamVerifier,
    private val verifyStreamsUseCase: VerifyStreamsUseCase,
    private val channelRepository: ChannelRepository,
) {
    suspend fun verifyAll() {
        val infohashes = verifyStreamsUseCase.getInfohashesToVerify()
        for (infohash in infohashes) {
            val result = streamVerifier.verify(infohash)
            val now = System.currentTimeMillis()
            when (result) {
                is VerificationResult.Alive -> {
                    channelRepository.updateVerification(
                        infohash = infohash,
                        isVerified = true,
                        quality = result.quality.label,
                        verifiedAt = now,
                        peerCount = result.peers,
                    )
                }
                is VerificationResult.Dead -> {
                    channelRepository.updateVerification(
                        infohash = infohash,
                        isVerified = false,
                        quality = null,
                        verifiedAt = now,
                        peerCount = 0,
                    )
                }
                is VerificationResult.Error -> {
                    channelRepository.updateVerification(
                        infohash = infohash,
                        isVerified = false,
                        quality = StreamQuality.UNKNOWN.label,
                        verifiedAt = now,
                        peerCount = null,
                    )
                }
            }
        }
    }
}
